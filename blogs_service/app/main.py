from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routes import blogs, comments

app = FastAPI(title="Blog Service", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(blogs.router, prefix="/api/blogs", tags=["blogs"])
app.include_router(comments.router, prefix="/api/blogs", tags=["comments"])


@app.get("/health")
async def health():
    return {"status": "ok"}