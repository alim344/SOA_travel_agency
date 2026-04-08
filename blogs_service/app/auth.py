from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
#from jose import jwt, JWTError
import jwt
import os

SECRET_KEY = os.getenv("JWT_SECRET", "somesecret-key-for-jwt-token-has-to-be-512-bits-long-1234567890123456789")
ALGORITHM = "HS512"

bearer_scheme = HTTPBearer()


class CurrentUser:
    def __init__(self, email: str):
        self.email = email
        self.user_id = email


async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(bearer_scheme),
) -> CurrentUser:
    token = credentials.credentials
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM], options={"verify_aud": False})
        email: str = payload.get("sub") #email
        if email is None:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED,
                                detail="Token is not valid")
        return CurrentUser(email=email)
    
    except jwt.ExpiredSignatureError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token has expired",
        )
    
    except jwt.InvalidTokenError as e:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=f"Token is not valid: {str(e)}",
        )