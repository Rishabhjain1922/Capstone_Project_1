from passlib.context import CryptContext
pwd_cxt=CryptContext(schemes=["bcrypt"], deprecated="auto")

def bcrypt(password):
    hashed_password=pwd_cxt.encrypt(password)
    return hashed_password