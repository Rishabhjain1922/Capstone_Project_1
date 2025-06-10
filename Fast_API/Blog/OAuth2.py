from fastapi import Depends, HTTPException,status
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session
from sqlalchemy.testing import db

from Blog import JWTtoken, model
from Blog.JWTtoken import verify_token
from Blog.databases import get_db

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="login")

def get_current_user(
        token: str = Depends(oauth2_scheme),
        db: Session = Depends(get_db)  # Add this line
):
    credentials_exception = HTTPException(
        status_code=401,
        detail="Invalid credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    email = verify_token(token, credentials_exception)
    user = db.query(model.User).filter(model.User.email == email).first()
    if not user:
        raise credentials_exception
    return user

