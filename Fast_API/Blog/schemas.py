
from pydantic import BaseModel

class User(BaseModel):
    name:str
    email:str
    password:str

class Blog(BaseModel):
    title: str
    body: str

class ShowBlog(Blog):
    title:str
    class Config:
        orm_mode = True

class ShowUser(BaseModel):
    name: str
    email: str
    class Config:
        orm_mode = True


