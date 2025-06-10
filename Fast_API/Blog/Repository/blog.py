from fastapi import HTTPException, Depends
from sqlalchemy.orm import Session
from starlette import status

from .. import model, schemas
from ..databases import get_db


def get_all(db: Session):
    blogs=db.query(model.Blog).all()
    return blogs

def create(request, db: Session):
    new_blog=model.Blog(title=request.title,body=request.body, user_id=1)
    db.add(new_blog)
    db.commit()
    db.refresh(new_blog)
    return new_blog

def show(id:int, db: Session):
    blog=db.query(model.Blog).filter(model.Blog.id==id).first()
    if not blog:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,detail=f"Blog with id {id} not found")
    return blog

def delete(id:int, db: Session):
    blog=db.query(model.Blog).filter(model.Blog.id==id)
    if not blog.first():
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"Blog with id {id} not found")
    blog.delete(synchronize_session=False)
    db.commit()
    return {'done'}

def update(id,request:schemas.Blog,db:Session=Depends(get_db)):
    blog=db.query(model.Blog).filter(model.Blog.id==id)
    if not blog.first():
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"Blog with id {id} not found")
    blog.update(request.dict())
    db.commit()
    return 'updated Successfully'
