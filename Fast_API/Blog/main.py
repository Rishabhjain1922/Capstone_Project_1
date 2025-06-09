

from typing import List
from fastapi import FastAPI, Depends,status, Response, HTTPException
from starlette import status

from . import schemas, model
from .model import Base
from .databases import engine, SessionLocal
from sqlalchemy.orm import Session
from .Hashing import bcrypt
from .schemas import ShowUser



app = FastAPI()

# Create tables
Base.metadata.create_all(bind=engine)
def get_db():
    db=SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.post('/blog',status_code=status.HTTP_201_CREATED)
def create(request: schemas.Blog,db:Session=Depends(get_db)):
   new_blog=model.Blog(title=request.title,body=request.body)
   db.add(new_blog)
   db.commit()
   db.refresh(new_blog)
   return new_blog

@app.get('/blog', response_model=List[schemas.ShowBlog])
def all(db: Session=Depends(get_db)):
    blogs=db.query(model.Blog).all()
    return blogs

@app.get('/blog/{id}',status_code=status.HTTP_200_OK, response_model=schemas.ShowBlog)
def show(id:int,response: Response, db: Session=Depends(get_db)):
    blog=db.query(model.Blog).filter(model.Blog.id==id).first()
    if not blog:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,detail=f"Blog with id {id} not found")
    return blog

@app.delete('/blog/{id}',status_code=status.HTTP_204_NO_CONTENT)
def destroy(id,db:Session=Depends(get_db)):
    blog=db.query(model.Blog).filter(model.Blog.id==id)
    if not blog.first():
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"Blog with id {id} not found")
    blog.delete(synchronize_session=False)
    db.commit()
    return {'done'}


@app.put('/blog/{id}',status_code=status.HTTP_202_ACCEPTED)
def update(id,request:schemas.Blog,db:Session=Depends(get_db)):
    blog=db.query(model.Blog).filter(model.Blog.id==id)
    if not blog.first():
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"Blog with id {id} not found")
    blog.update(request.dict())
    db.commit()
    return 'updated Successfully'

@app.post('/user', response_model=schemas.ShowUser, status_code=status.HTTP_201_CREATED)
def create_user(request: schemas.User, db: Session = Depends(get_db)):
    new_user = model.User(name=request.name, email=request.email, password=bcrypt(request.password))
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    return {'name': new_user.name, 'email': new_user.email}

@app.get('/user/{id}',response_model=schemas.ShowUser, status_code=status.HTTP_200_OK)
def get_user(id:int,db:Session=Depends(get_db)):
    user=db.query(model.User).filter(model.User.id == id).first()
    if not user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"User with id {id} not found")
    return user