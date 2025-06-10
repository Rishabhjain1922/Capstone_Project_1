from typing import Optional,List

from pydantic import BaseModel

class User(BaseModel):
    name:str
    email:str
    password:str

class Blog(BaseModel):
    title: str
    body: str
    class Config:
        orm_mode = True

class ShowUser(BaseModel):
    name: str
    email: str
    blogs: Optional[List[Blog]]
    class Config:
        orm_mode = True


class ShowBlog(BaseModel):
    title: str
    body: str
    creator: Optional[ShowUser]
    class Config:
        orm_mode = True


class Login(BaseModel):
    username: str
    password: str

class Token(BaseModel):
    access_token: str
    token_type: str


class TokenData(BaseModel):
    email: str | None = None


