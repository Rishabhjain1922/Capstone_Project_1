from fastapi import APIRouter
from Blog import schemas, OAuth2
from ..OAuth2 import get_current_user
from ..databases import get_db
from typing import List
from fastapi import  Depends
from starlette import status
from sqlalchemy.orm import Session
from ..Repository import blog


router = APIRouter(
    tags=['Blogs'],
    prefix="/blog",
)

@router.get('/', response_model=List[schemas.ShowBlog])
def all(db: Session=Depends(get_db),current_user:schemas.User=Depends(OAuth2.get_current_user)):
    return blog.get_all(db)

@router.post('/',status_code=status.HTTP_201_CREATED)
def create(request: schemas.Blog,db:Session=Depends(get_db),current_user:schemas.User=Depends(OAuth2.get_current_user)):
   return blog.create(request, db)

@router.get('/{id}',status_code=status.HTTP_200_OK, response_model=schemas.ShowBlog)
def show(id:int, db: Session=Depends(get_db),current_user:schemas.User=Depends(OAuth2.get_current_user)):
   return blog.show(id, db)

@router.delete('/{id}',status_code=status.HTTP_204_NO_CONTENT)
def destroy(id,db:Session=Depends(get_db),current_user:schemas.User=Depends(OAuth2.get_current_user)):
    return blog.delete(id, db)
@router.put('/{id}',status_code=status.HTTP_202_ACCEPTED)
def update(id,request:schemas.Blog,db:Session=Depends(get_db),current_user:schemas.User=Depends(OAuth2.get_current_user)):
    return blog.update(id, request, db)