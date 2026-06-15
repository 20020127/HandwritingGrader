from pydantic_settings import BaseSettings
from typing import Optional
import os
from dotenv import load_dotenv

load_dotenv()

class Settings(BaseSettings):
    APP_NAME: str = "手写作业批改系统"
    DEBUG: bool = True
    
    DATABASE_URL: str = "sqlite+aiosqlite:///./handwriting_grader.db"
    
    PADDLEOCR_LANG: str = "ch"
    PADDLEOCR_USE_ANGLE_CLS: bool = True
    
    LLM_PROVIDER: str = "zhipu"
    LLM_API_KEY: str = os.getenv("LLM_API_KEY", "")
    LLM_MODEL: str = "glm-4"
    LLM_BASE_URL: str = "https://open.bigmodel.cn/api/paas/v4"
    
    MAX_IMAGE_SIZE: int = 10 * 1024 * 1024
    ALLOWED_IMAGE_TYPES: list = ["image/jpeg", "image/png", "image/bmp"]
    
    class Config:
        env_file = ".env"

settings = Settings()
