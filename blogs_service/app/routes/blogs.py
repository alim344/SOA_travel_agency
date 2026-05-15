from xmlrpc import client

from fastapi import APIRouter, Depends, HTTPException, status
from bson import ObjectId
from datetime import datetime, timezone
import httpx
from app.database import blogs_collection, FOLLOWER_SERVICE_URL
from app.models import BlogCreate
from app.auth import get_current_user, CurrentUser

router = APIRouter()


def blog_to_response(doc: dict, current_email: str | None = None) -> dict:
    return {
        "id": str(doc["_id"]),
        "title": doc["title"],
        "description": doc["description"],
        "created_at": doc["created_at"],
        "images": doc.get("images", []),
        "author_id": doc["author_id"],
        "author_email": doc["author_email"],
        "like_count": len(doc.get("likes", [])),
        "liked_by_me": current_email in doc.get("likes", [])
                       if current_email else False,
    }

@router.post("/", status_code=status.HTTP_201_CREATED)
async def create_blog(
    body: BlogCreate,
    user: CurrentUser = Depends(get_current_user),
):
    doc = {
        "title": body.title,
        "description": body.description,
        "images": body.images,
        "created_at": datetime.now(timezone.utc),
        "author_id": user.user_id,
        "author_email": user.email,
        "likes": [],
    }
    result = await blogs_collection.insert_one(doc)
    doc["_id"] = result.inserted_id
    return blog_to_response(doc, user.email)

@router.get("/")
async def get_blogs(user: CurrentUser = Depends(get_current_user)):
    try:
        async with httpx.AsyncClient() as client:
            response = await client.get(f"{FOLLOWER_SERVICE_URL}/following/{user.user_id}")
            following_ids = response.json() if response.status_code == 200 else []
    except httpx.RequestError:
        following_ids = []

    following_ids.append(user.user_id)

    cursor = blogs_collection.find({"author_id": {"$in": following_ids}}).sort("created_at", -1)
    blogs = await cursor.to_list(length=100)
    
    return [blog_to_response(b, user.email) for b in blogs]


@router.get("/{blog_id}")
async def get_blog(
    blog_id: str,
    user: CurrentUser = Depends(get_current_user),
):
    doc = await blogs_collection.find_one({"_id": ObjectId(blog_id)})
    if not doc:
        raise HTTPException(status_code=404, detail="Blog not found")
    
    if doc["author_id"] == user.user_id:
        return blog_to_response(doc, user.email)
    
    is_following = await check_if_following(user.user_id, doc["author_id"])
    if not is_following:
        raise HTTPException(status_code=403, detail="You must follow the author to view this blog")

    return blog_to_response(doc, user.email)


@router.post("/{blog_id}/like")
async def toggle_like(
    blog_id: str,
    user: CurrentUser = Depends(get_current_user),
):
    doc = await blogs_collection.find_one({"_id": ObjectId(blog_id)})
    if not doc:
        raise HTTPException(status_code=404, detail="Blog not found")

    likes: list = doc.get("likes", [])

    if user.email in likes:
        #ukloni lajk
        await blogs_collection.update_one(
            {"_id": ObjectId(blog_id)},
            {"$pull": {"likes": user.email}},
        )
        return {"liked": False, "like_count": len(likes) - 1}
    else:
        # dodaj lajk
        await blogs_collection.update_one(
            {"_id": ObjectId(blog_id)},
            {"$addToSet": {"likes": user.email}},
        )
        return {"liked": True, "like_count": len(likes) + 1}
    

async def check_if_following(follower_id : str, followee_id : str) -> bool:
    try:
        async with httpx.AsyncClient() as client:
            url = f"{FOLLOWER_SERVICE_URL}/is-following/{follower_id}/{followee_id}"
            response = await client.get(url)
            
            if response.status_code == 200:
                return response.json() 
            return False
    except httpx.RequestError:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Follower service is temporarily unavailable"
        )