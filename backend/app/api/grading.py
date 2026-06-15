from fastapi import APIRouter, UploadFile, File, Form, HTTPException
from typing import Optional
from app.services.ocr_service import ocr_service
from app.services.llm_service import llm_service
from app.core.config import settings

router = APIRouter()

@router.post("/check")
async def check_answer(
    file: UploadFile = File(...),
    question: str = Form(...),
    question_type: str = Form(...),
    subject: str = Form(default="数学"),
    correct_answer: Optional[str] = Form(default=None)
):
    if file.content_type not in settings.ALLOWED_IMAGE_TYPES:
        raise HTTPException(status_code=400, detail="不支持的图片格式")
    
    contents = await file.read()
    
    if len(contents) > settings.MAX_IMAGE_SIZE:
        raise HTTPException(status_code=400, detail="图片大小超过限制")
    
    ocr_result = await ocr_service.recognize_text(contents)
    
    if not ocr_result["success"]:
        raise HTTPException(status_code=500, detail=ocr_result.get("error", "OCR识别失败"))
    
    student_answer = ocr_result["full_text"]
    
    grading_result = await llm_service.check_answer(
        question=question,
        student_answer=student_answer,
        question_type=question_type,
        correct_answer=correct_answer,
        subject=subject
    )
    
    if not grading_result["success"]:
        raise HTTPException(status_code=500, detail=grading_result.get("error", "AI批改失败"))
    
    return {
        "ocr_result": ocr_result,
        "grading_result": grading_result,
        "student_answer": student_answer
    }

@router.post("/check-text")
async def check_text_answer(
    question: str = Form(...),
    student_answer: str = Form(...),
    question_type: str = Form(...),
    subject: str = Form(default="数学"),
    correct_answer: Optional[str] = Form(default=None)
):
    grading_result = await llm_service.check_answer(
        question=question,
        student_answer=student_answer,
        question_type=question_type,
        correct_answer=correct_answer,
        subject=subject
    )
    
    if not grading_result["success"]:
        raise HTTPException(status_code=500, detail=grading_result.get("error", "AI批改失败"))
    
    return {
        "grading_result": grading_result,
        "student_answer": student_answer
    }
