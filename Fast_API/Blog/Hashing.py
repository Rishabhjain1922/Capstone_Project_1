from passlib.context import CryptContext
pwd_cxt=CryptContext(schemes=["bcrypt"], deprecated="auto")

def bcrypt(password):
    hashed_password=pwd_cxt.encrypt(password)
    return hashed_password

def verify(hashed_password, plain_password):
    return pwd_cxt.verify(plain_password, hashed_password)