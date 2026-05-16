from fastapi import APIRouter, Depends, HTTPException, status
from bson import ObjectId
from datetime import datetime, timezone

from app.database import blogs_collection, comments_collection
from app.models import CommentCreate
from app.auth import get_current_user, CurrentUser

router = APIRouter()


@router.post("/{blog_id}/comments", status_code=status.HTTP_201_CREATED)
async def add_comment(
    blog_id: str,
    body: CommentCreate,
    user: CurrentUser = Depends(get_current_user),
):
    blog = await blogs_collection.find_one({"_id": ObjectId(blog_id)})
    if not blog:
        raise HTTPException(status_code=404, detail="Blog not found")

    now = datetime.now(timezone.utc)
    doc = {
        "blog_id": blog_id,
        "text": body.text,
        "created_at": now,
        "updated_at": now,
        "author_email": user.email,
    }
    result = await comments_collection.insert_one(doc)
    return {
        "id": str(result.inserted_id),
        "blog_id": blog_id,
        "text": body.text,
        "created_at": now,
        "updated_at": now,
        "author_email": user.email,
    }


@router.get("/{blog_id}/comments")
async def get_comments(
    blog_id: str,
    user: CurrentUser = Depends(get_current_user),
):
    cursor = comments_collection.find({"blog_id": blog_id}).sort("created_at", 1)
    comments = await cursor.to_list(length=200)
    return [
        {
            "id": str(c["_id"]),
            "blog_id": c["blog_id"],
            "text": c["text"],
            "created_at": c["created_at"],
            "updated_at": c["updated_at"],
            "author_email": c["author_email"],
        }
        for c in comments
    ]

@router.put("/{blog_id}/comments/{comment_id}")
async def update_comment(
    blog_id: str,
    comment_id: str,
    body: CommentCreate,
    user: CurrentUser = Depends(get_current_user),
):
    comment = await comments_collection.find_one({"_id": ObjectId(comment_id)})
    if not comment:
        raise HTTPException(status_code=404, detail="Comment not found")
    if comment["author_email"] != user.email:
        raise HTTPException(status_code=403, detail="Not your comment")

    await comments_collection.update_one(
        {"_id": ObjectId(comment_id)},
        {"$set": {"text": body.text, "updated_at": datetime.now(timezone.utc)}},
    )
    return {"message": "Comment updated successfully"}

@router.put("/{blog_id}/comments/{comment_id}")
async def update_comment(
    blog_id: str,
    comment_id: str,
    body: CommentCreate,
    user: CurrentUser = Depends(get_current_user),
):
    blog = await blogs_collection.find_one({"_id": ObjectId(blog_id)})
    if not blog:
        raise HTTPException(status_code=404, detail="Blog not found")

    comment = await comments_collection.find_one({"_id": ObjectId(comment_id)})
    if not comment:
        raise HTTPException(status_code=404, detail="Comment not found")
    
    if comment.get("author_email") != user.email:
        raise HTTPException(
            status_code=403, 
            detail="You do not have permission to update this comment"
        )
    
    now = datetime.now(timezone.utc)
    
    await comments_collection.update_one(
        {"_id": ObjectId(comment_id)},
        {
            "$set": {
                "text": body.text,
                "updated_at": now
            }
        }
    )
    
    return {
        "id": comment_id,
        "blog_id": blog_id,
        "text": body.text,
        "created_at": comment["created_at"],
        "updated_at": now,
        "author_email": user.email,
    }


@router.delete("/{blog_id}/comments/{comment_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_comment(
    blog_id: str,
    comment_id: str,
    user: CurrentUser = Depends(get_current_user),
):
    blog = await blogs_collection.find_one({"_id": ObjectId(blog_id)})
    if not blog:
        raise HTTPException(status_code=404, detail="Blog not found")

    comment = await comments_collection.find_one({"_id": ObjectId(comment_id)})
    if not comment:
        raise HTTPException(status_code=404, detail="Comment not found")
    
    if comment.get("author_email") != user.email:
        raise HTTPException(
            status_code=403, 
            detail="You do not have permission to delete this comment"
        )
    
    await comments_collection.delete_one({"_id": ObjectId(comment_id)})
    return None