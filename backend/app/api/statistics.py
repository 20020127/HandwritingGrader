from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func, and_
from typing import Optional, List
from datetime import datetime, timedelta
from app.models.database import get_db
from app.models.schemas import Statistics, Submission, Question, Homework, WrongQuestion
from pydantic import BaseModel

router = APIRouter()

class StatisticsResponse(BaseModel):
    subject: str
    total_questions: int
    correct_count: int
    wrong_count: int
    accuracy_rate: float
    average_score: float

class SubjectStatistics(BaseModel):
    subject: str
    total_questions: int
    correct_count: int
    wrong_count: int
    accuracy_rate: float

class DailyStatistics(BaseModel):
    date: str
    total_questions: int
    correct_count: int
    accuracy_rate: float

class QuestionTypeStatistics(BaseModel):
    question_type: str
    total_questions: int
    correct_count: int
    wrong_count: int
    accuracy_rate: float

@router.get("/overview")
async def get_overview_statistics(
    days: int = 30,
    db: AsyncSession = Depends(get_db)
):
    start_date = datetime.utcnow() - timedelta(days=days)
    
    total_query = select(func.count(Submission.id)).where(
        Submission.submit_time >= start_date
    )
    total_result = await db.execute(total_query)
    total_submissions = total_result.scalar() or 0
    
    correct_query = select(func.count(Submission.id)).where(
        and_(
            Submission.submit_time >= start_date,
            Submission.is_correct == True
        )
    )
    correct_result = await db.execute(correct_query)
    correct_count = correct_result.scalar() or 0
    
    wrong_query = select(func.count(Submission.id)).where(
        and_(
            Submission.submit_time >= start_date,
            Submission.is_correct == False
        )
    )
    wrong_result = await db.execute(wrong_query)
    wrong_count = wrong_result.scalar() or 0
    
    accuracy_rate = (correct_count / total_submissions * 100) if total_submissions > 0 else 0
    
    avg_score_query = select(func.avg(Submission.score)).where(
        Submission.submit_time >= start_date
    )
    avg_score_result = await db.execute(avg_score_query)
    average_score = avg_score_result.scalar() or 0
    
    wrong_questions_query = select(func.count(WrongQuestion.id)).where(
        WrongQuestion.is_mastered == False
    )
    wrong_questions_result = await db.execute(wrong_questions_query)
    unmastered_wrong = wrong_questions_result.scalar() or 0
    
    return {
        "period_days": days,
        "total_submissions": total_submissions,
        "correct_count": correct_count,
        "wrong_count": wrong_count,
        "accuracy_rate": round(accuracy_rate, 2),
        "average_score": round(average_score, 2),
        "unmastered_wrong_questions": unmastered_wrong
    }

@router.get("/by-subject", response_model=List[SubjectStatistics])
async def get_subject_statistics(
    days: int = 30,
    db: AsyncSession = Depends(get_db)
):
    start_date = datetime.utcnow() - timedelta(days=days)
    
    query = (
        select(
            Question.subject,
            func.count(Submission.id).label("total_questions"),
            func.sum(func.cast(Submission.is_correct, int)).label("correct_count"),
            func.sum(func.cast(~Submission.is_correct, int)).label("wrong_count")
        )
        .join(Submission, Question.id == Submission.question_id)
        .where(Submission.submit_time >= start_date)
        .group_by(Question.subject)
    )
    
    result = await db.execute(query)
    rows = result.all()
    
    statistics = []
    for row in rows:
        total = row.total_questions or 0
        correct = row.correct_count or 0
        wrong = row.wrong_count or 0
        accuracy = (correct / total * 100) if total > 0 else 0
        
        statistics.append(SubjectStatistics(
            subject=row.subject,
            total_questions=total,
            correct_count=correct,
            wrong_count=wrong,
            accuracy_rate=round(accuracy, 2)
        ))
    
    return statistics

@router.get("/daily", response_model=List[DailyStatistics])
async def get_daily_statistics(
    days: int = 30,
    subject: Optional[str] = None,
    db: AsyncSession = Depends(get_db)
):
    start_date = datetime.utcnow() - timedelta(days=days)
    
    query = (
        select(
            func.date(Submission.submit_time).label("date"),
            func.count(Submission.id).label("total_questions"),
            func.sum(func.cast(Submission.is_correct, int)).label("correct_count")
        )
        .where(Submission.submit_time >= start_date)
    )
    
    if subject:
        query = query.join(Question, Question.id == Submission.question_id).where(
            Question.subject == subject
        )
    
    query = query.group_by(func.date(Submission.submit_time)).order_by("date")
    
    result = await db.execute(query)
    rows = result.all()
    
    statistics = []
    for row in rows:
        total = row.total_questions or 0
        correct = row.correct_count or 0
        accuracy = (correct / total * 100) if total > 0 else 0
        
        statistics.append(DailyStatistics(
            date=str(row.date),
            total_questions=total,
            correct_count=correct,
            accuracy_rate=round(accuracy, 2)
        ))
    
    return statistics

@router.get("/by-question-type", response_model=List[QuestionTypeStatistics])
async def get_question_type_statistics(
    days: int = 30,
    subject: Optional[str] = None,
    db: AsyncSession = Depends(get_db)
):
    start_date = datetime.utcnow() - timedelta(days=days)
    
    query = (
        select(
            Question.question_type,
            func.count(Submission.id).label("total_questions"),
            func.sum(func.cast(Submission.is_correct, int)).label("correct_count"),
            func.sum(func.cast(~Submission.is_correct, int)).label("wrong_count")
        )
        .join(Submission, Question.id == Submission.question_id)
        .where(Submission.submit_time >= start_date)
    )
    
    if subject:
        query = query.where(Question.subject == subject)
    
    query = query.group_by(Question.question_type)
    
    result = await db.execute(query)
    rows = result.all()
    
    statistics = []
    for row in rows:
        total = row.total_questions or 0
        correct = row.correct_count or 0
        wrong = row.wrong_count or 0
        accuracy = (correct / total * 100) if total > 0 else 0
        
        statistics.append(QuestionTypeStatistics(
            question_type=row.question_type,
            total_questions=total,
            correct_count=correct,
            wrong_count=wrong,
            accuracy_rate=round(accuracy, 2)
        ))
    
    return statistics

@router.get("/wrong-questions-summary")
async def get_wrong_questions_summary(
    subject: Optional[str] = None,
    db: AsyncSession = Depends(get_db)
):
    query = select(WrongQuestion).where(WrongQuestion.is_mastered == False)
    
    if subject:
        query = query.where(WrongQuestion.subject == subject)
    
    result = await db.execute(query)
    wrong_questions = result.scalars().all()
    
    by_subject = {}
    by_type = {}
    by_error_reason = {}
    
    for wq in wrong_questions:
        by_subject[wq.subject] = by_subject.get(wq.subject, 0) + 1
        by_type[wq.question_type] = by_type.get(wq.question_type, 0) + 1
        if wq.error_reason:
            by_error_reason[wq.error_reason] = by_error_reason.get(wq.error_reason, 0) + 1
    
    return {
        "total_unmastered": len(wrong_questions),
        "by_subject": by_subject,
        "by_question_type": by_type,
        "by_error_reason": by_error_reason
    }
