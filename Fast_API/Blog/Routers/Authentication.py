from datetime import timedelta

from fastapi import HTTPException

from fastapi import APIRouter, Depends
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session
from starlette import status

from .. import schemas, databases, model, Hashing
from ..JWTtoken import create_access_token, ACCESS_TOKEN_EXPIRE_MINUTES
from ..databases import get_db
from ..schemas import Token

router = APIRouter(
    tags=["Authentication"]
)

@router.post('/login', response_model=schemas.Token)  # Add response model
def login(request: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    user = db.query(model.User).filter(model.User.email == request.username).first()
    if not user:
        raise HTTPException(status_code=404, detail="Invalid credentials")

    if not Hashing.verify(user.password, request.password):
        raise HTTPException(status_code=400, detail="Incorrect password")

    access_token = create_access_token(data={"sub": user.email})
    return {"access_token": access_token, "token_type": "bearer"}
