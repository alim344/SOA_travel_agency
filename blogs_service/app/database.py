from motor.motor_asyncio import AsyncIOMotorClient
import os

MONGO_URL = os.getenv("MONGO_URL", "mongodb://localhost:27017")
DB_NAME = os.getenv("DB_NAME", "blog_db")

client = AsyncIOMotorClient(MONGO_URL)
db = client[DB_NAME]

blogs_collection = db["blogs"]
comments_collection = db["comments"]