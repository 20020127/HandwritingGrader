from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, update, delete
from typing import List, Optional
from datetime import datetime
from app.models.database import get_db
from app.models.schemas import WrongQuestion
from pydantic import BaseModel

router = APIRouter()

class WrongQuestionCreate(BaseModel):
    question_content: str
    correct_answer: str
    student_answer: str
    question_type: str
    subject: str
    error_reason: Optional[str] = None
    question_number: Optional[int] = None
    homework_title: Optional[str] = None

class WrongQuestionUpdate(BaseModel):
    is_mastered: Optional[bool] = None
    review_count: Optional[int] = None

class WrongQuestionResponse(BaseModel):
    id: int
    question_content: str
    correct_answer: str
    student_answer: str
    question_type: str
    subject: str
    error_reason: Optional[str]
    question_number: Optional[int]
    homework_title: Optional[str]
    create_time: datetime
    review_count: int
    is_mastered: bool
    last_review_time: Optional[datetime]
    
    class Config:
        from_attributes = True

@router.post("/", response_model=WrongQuestionResponse)
async def create_wrong_question(
    wrong_question: WrongQuestionCreate,
    db: AsyncSession = Depends(get_db)
):
    db_wrong_question = WrongQuestion(**wrong_question.model_dump())
    db.add(db_wrong_question)
    await db.commit()
    await db.refresh(db_wrong_question)
    return db_wrong_question

@router.get("/", response_model=List[WrongQuestionResponse])
async def get_wrong_questions(
    subject: Optional[str] = None,
    question_type: Optional[str] = None,
    is_mastered: Optional[bool] = None,
    skip: int = 0,
    limit: int = 100,
    db: AsyncSession = Depends(get_db)
):
    query = select(WrongQuestion)
    
    if subject:
        query = query.where(WrongQuestion.subject == subject)
    if question_type:
        query = query.where(WrongQuestion.question_type == question_type)
    if is_mastered is not None:
        query = query.where(WrongQuestion.is_mastered == is_mastered)
    
    query = query.order_by(WrongQuestion.create_time.desc()).offset(skip).limit(limit)
    
    result = await db.execute(query)
    return result.scalars().all()

@router.get("/{wrong_question_id}", response_model=WrongQuestionResponse)
async def get_wrong_question(
    wrong_question_id: int,
    db: AsyncSession = Depends(get_db)
):
    query = select(WrongQuestion).where(WrongQuestion.id == wrong_question_id)
    result = await db.execute(query)
    wrong_question = result.scalar_one_or_none()
    
    if not wrong_question:
        raise HTTPException(status_code=404, detail="错题不存在")
    
    return wrong_question

@router.put("/{wrong_question_id}", response_model=WrongQuestionResponse)
async def update_wrong_question(
    wrong_question_id: int,
    wrong_question_update: WrongQuestionUpdate,
    db: AsyncSession = Depends(get_db)
):
    query = select(WrongQuestion).where(WrongQuestion.id == wrong_question_id)
    result = await db.execute(query)
    wrong_question = result.scalar_one_or_none()
    
    if not wrong_question:
        raise HTTPException(status_code=404, detail="错题不存在")
    
    update_data = wrong_question_update.model_dump(exclude_unset=True)
    
    if "is_mastered" in update_data and update_data["is_mastered"]:
        update_data["last_review_time"] = datetime.utcnow()
    
    if "review_count" in update_data:
        update_data["last_review_time"] = datetime.utcnow()
    
    for key, value in update_data.items():
        setattr(wrong_question, key, value)
    
    await db.commit()
    await db.refresh(wrong_question)
    return wrong_question

@router.delete("/{wrong_question_id}")
async def delete_wrong_question(
    wrong_question_id: int,
    db: AsyncSession = Depends(get_db)
):
    query = select(WrongQuestion).where(WrongQuestion.id == wrong_question_id)
    result = await db.execute(query)
    wrong_question = result.scalar_one_or_none()
    
    if not wrong_question:
        raise HTTPException(status_code=404, detail="错题不存在")
    
    await db.delete(wrong_question)
    await db.commit()
    
    return {"message": "错题已删除"}

@router.post("/{wrong_question_id}/review")
async def review_wrong_question(
    wrong_question_id: int,
    db: AsyncSession = Depends(get_db)
):
    query = select(WrongQuestion).where(WrongQuestion.id == wrong_question_id)
    result = await db.execute(query)
    wrong_question = result.scalar_one_or_none()
    
    if not wrong_question:
        raise HTTPException(status_code=404, detail="错题不存在")
    
    wrong_question.review_count += 1
    wrong_question.last_review_time = datetime.utcnow()
    
    await db.commit()
    await db.refresh(wrong_question)
    
    return {"message": "复习记录已更新", "review_count": wrong_question.review_count}
