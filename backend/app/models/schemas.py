from sqlalchemy import Column, Integer, String, Float, Boolean, DateTime, Text, ForeignKey
from sqlalchemy.orm import relationship
from datetime import datetime
from app.models.database import Base

class Homework(Base):
    __tablename__ = "homeworks"
    
    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(200), nullable=False)
    subject = Column(String(50), nullable=False)
    grade = Column(String(20))
    create_time = Column(DateTime, default=datetime.utcnow)
    update_time = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    questions = relationship("Question", back_populates="homework", cascade="all, delete-orphan")

class Question(Base):
    __tablename__ = "questions"
    
    id = Column(Integer, primary_key=True, index=True)
    homework_id = Column(Integer, ForeignKey("homeworks.id"), nullable=False)
    question_number = Column(Integer, nullable=False)
    question_type = Column(String(20), nullable=False)
    content = Column(Text, nullable=False)
    correct_answer = Column(Text)
    max_score = Column(Float, default=10.0)
    
    homework = relationship("Homework", back_populates="questions")
    submissions = relationship("Submission", back_populates="question", cascade="all, delete-orphan")

class Submission(Base):
    __tablename__ = "submissions"
    
    id = Column(Integer, primary_key=True, index=True)
    question_id = Column(Integer, ForeignKey("questions.id"), nullable=False)
    student_answer = Column(Text, nullable=False)
    ocr_result = Column(Text)
    image_path = Column(String(500))
    is_correct = Column(Boolean)
    score = Column(Float)
    feedback = Column(Text)
    llm_response = Column(Text)
    submit_time = Column(DateTime, default=datetime.utcnow)
    
    question = relationship("Question", back_populates="submissions")

class WrongQuestion(Base):
    __tablename__ = "wrong_questions"
    
    id = Column(Integer, primary_key=True, index=True)
    question_content = Column(Text, nullable=False)
    correct_answer = Column(Text, nullable=False)
    student_answer = Column(Text, nullable=False)
    question_type = Column(String(20), nullable=False)
    subject = Column(String(50), nullable=False)
    error_reason = Column(Text)
    question_number = Column(Integer)
    homework_title = Column(String(200))
    create_time = Column(DateTime, default=datetime.utcnow)
    review_count = Column(Integer, default=0)
    is_mastered = Column(Boolean, default=False)
    last_review_time = Column(DateTime)

class Statistics(Base):
    __tablename__ = "statistics"
    
    id = Column(Integer, primary_key=True, index=True)
    subject = Column(String(50), nullable=False)
    date = Column(DateTime, default=datetime.utcnow)
    total_questions = Column(Integer, default=0)
    correct_count = Column(Integer, default=0)
    wrong_count = Column(Integer, default=0)
    accuracy_rate = Column(Float, default=0.0)
    average_score = Column(Float, default=0.0)
