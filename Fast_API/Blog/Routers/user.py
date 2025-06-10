from fastapi import Depends, APIRouter
from sqlalchemy.orm import Session
from starlette import status

from Blog import schemas

from Blog.Repository import user
from Blog.databases import get_db

router = APIRouter(
    tags=["Users"],
    prefix="/user"
)
@router.post('/', response_model=schemas.ShowUser, status_code=status.HTTP_201_CREATED, )
def create_user(request: schemas.User, db: Session = Depends(get_db)):
    return user.create(request, db)

@router.get('/{id}', response_model=schemas.ShowUser,)
def get_user(id: int, db: Session = Depends(get_db)):
    return user.get_user(id, db)