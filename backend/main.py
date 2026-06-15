from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api import ocr, grading, wrong_questions, statistics
from app.core.config import settings
from app.models.database import engine, Base

app = FastAPI(
    title="手写作业批改系统",
    description="基于PaddleOCR和LLM的手写作业批改API",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(ocr.router, prefix="/api/ocr", tags=["OCR识别"])
app.include_router(grading.router, prefix="/api/grading", tags=["作业批改"])
app.include_router(wrong_questions.router, prefix="/api/wrong-questions", tags=["错题本"])
app.include_router(statistics.router, prefix="/api/statistics", tags=["成绩统计"])

@app.on_event("startup")
async def startup():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)

@app.get("/")
async def root():
    return {"message": "手写作业批改系统API", "version": "1.0.0"}

@app.get("/health")
async def health_check():
    return {"status": "healthy"}
