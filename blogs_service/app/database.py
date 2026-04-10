import os
from motor.motor_asyncio import AsyncIOMotorClient
from dotenv import load_dotenv

load_dotenv()

MONGO_URL = os.getenv("MONGO_URL", "mongodb://localhost:27017/blog")
STAKEHOLDERS_URL = os.getenv("STAKEHOLDERS_URL", "http://localhost:8080")

client = AsyncIOMotorClient(MONGO_URL)
db = client.blog

blogs_collection = db["blogs"]
comments_collection = db["comments"]