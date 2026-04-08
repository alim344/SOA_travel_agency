from fastapi import FastAPI
from app.routes import blogs, comments

app = FastAPI(title="Blog Service", version="1.0.0")

app.include_router(blogs.router, prefix="/api/blogs", tags=["blogs"])
app.include_router(comments.router, prefix="/api/blogs", tags=["comments"])


@app.get("/health")
async def health():
    return {"status": "ok"}