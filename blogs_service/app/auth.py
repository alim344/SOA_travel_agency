from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
import httpx
import os

STAKEHOLDERS_URL = os.getenv("STAKEHOLDERS_URL", "http://localhost:8080")

bearer_scheme = HTTPBearer()

class CurrentUser:
    def __init__(self, email: str, enabled: bool):
        self.email = email
        self.user_id = email
        self.enabled = enabled

async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(bearer_scheme),
) -> CurrentUser:
    token = credentials.credentials
    
    try:
        async with httpx.AsyncClient() as client:
            response = await client.get(
                f"{STAKEHOLDERS_URL}/auth/userEnabled",
                headers={"Authorization": f"Bearer {token}"}
            )
        
        if response.status_code == 401:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Token is not valid"
            )
        
        if response.status_code == 404:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="User not found or not enabled"
            )
        
        # Success - get user data from response
        user_data = response.json()
        return CurrentUser(
            email=user_data["email"],
            enabled=user_data["enabled"]
        )
    
    except httpx.RequestError:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Stakeholders service unavailable"
        )