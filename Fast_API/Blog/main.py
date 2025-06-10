

from fastapi import FastAPI
from .model import Base
from .databases import engine
from .Routers import blog, user, Authentication

app = FastAPI()

# Create tables
Base.metadata.create_all(bind=engine)
app.include_router(Authentication.router)
app.include_router(blog.router)
app.include_router(user.router)
# def get_db():
#     db=SessionLocal()
#     try:
#         yield db
#     finally:
#         db.close()

# @app.post('/blog',status_code=status.HTTP_201_CREATED, tags=['blogs'])
# def create(request: schemas.Blog,db:Session=Depends(get_db)):
#    new_blog=model.Blog(title=request.title,body=request.body, user_id=1)
#    db.add(new_blog)
#    db.commit()
#    db.refresh(new_blog)
#    return new_blog

# @app.get('/blog', response_model=List[schemas.ShowBlog], tags=['blogs'])
# def all(db: Session=Depends(get_db)):
#     blogs=db.query(model.Blog).all()
#     return blogs

# @app.get('/blog/{id}',status_code=status.HTTP_200_OK, response_model=schemas.ShowBlog, tags=['blogs'])
# def show(id:int,response: Response, db: Session=Depends(get_db)):
#     blog=db.query(model.Blog).filter(model.Blog.id==id).first()
#     if not blog:
#         raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,detail=f"Blog with id {id} not found")
#     return blog

# @app.delete('/blog/{id}',status_code=status.HTTP_204_NO_CONTENT, tags=['blogs'])
# def destroy(id,db:Session=Depends(get_db)):
#     blog=db.query(model.Blog).filter(model.Blog.id==id)
#     if not blog.first():
#         raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"Blog with id {id} not found")
#     blog.delete(synchronize_session=False)
#     db.commit()
#     return {'done'}

# @app.put('/blog/{id}',status_code=status.HTTP_202_ACCEPTED, tags=['blogs'])
# def update(id,request:schemas.Blog,db:Session=Depends(get_db)):
#     blog=db.query(model.Blog).filter(model.Blog.id==id)
#     if not blog.first():
#         raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"Blog with id {id} not found")
#     blog.update(request.dict())
#     db.commit()
#     return 'updated Successfully'


# @app.post('/user', response_model=schemas.ShowUser, status_code=status.HTTP_201_CREATED,tags=['users'])
# def create_user(request: schemas.User, db: Session = Depends(get_db)):
#     new_user = model.User(name=request.name, email=request.email, password=bcrypt(request.password))
#     db.add(new_user)
#     db.commit()
#     db.refresh(new_user)
#     return {'name': new_user.name, 'email': new_user.email}
#
# @app.get('/user/{id}', response_model=schemas.ShowUser,tags=['users'])
# def get_user(id: int, db: Session = Depends(get_db)):
#     user = db.query(model.User).filter(model.User.id == id).first()
#     if not user:
#         raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
#                             detail=f"User with id {id} not found")
#     return user