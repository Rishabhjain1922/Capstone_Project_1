from fastapi import HTTPException
from sqlalchemy.orm import Session
from starlette import status

from Blog import model
from Blog.Hashing import bcrypt


def create(request,db:Session):
    new_user = model.User(name=request.name, email=request.email, password=bcrypt(request.password))
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    return new_user

def get_user(id:int,db:Session):
    user = db.query(model.User).filter(model.User.id == id).first()
    if not user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
                            detail=f"User with id {id} not found")
    return user