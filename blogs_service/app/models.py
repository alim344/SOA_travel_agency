from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime

class BlogCreate(BaseModel):
    title: str
    description: str
    images: Optional[list[str]] = []


class BlogResponse(BaseModel):
    id: str
    title: str
    description: str
    created_at: datetime
    images: list[str]
    author_id: str
    author_username: str
    like_count: int
    liked_by_me: Optional[bool] = False


class CommentCreate(BaseModel):
    text: str


class CommentResponse(BaseModel):
    id: str
    blog_id: str
    text: str
    created_at: datetime
    updated_at: datetime
    author_id: str
    author_username: str