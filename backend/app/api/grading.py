from fastapi import APIRouter, UploadFile, File, Form, HTTPException, Request
from typing import Optional
from app.services.ocr_service import ocr_service
from app.services.llm_service import LLMService, llm_service
from app.core.config import settings

router = APIRouter()


def _get_llm_service(request: Request) -> LLMService:
    provider = request.headers.get("X-LLM-Provider")
    api_key = request.headers.get("X-LLM-Api-Key")
    model = request.headers.get("X-LLM-Model")
    base_url = request.headers.get("X-LLM-Base-Url")
    if any([provider, api_key, model, base_url]):
        return LLMService(
            api_key=api_key,
            base_url=base_url,
            model=model,
            provider=provider,
        )
    return llm_service


@router.post("/check")
async def check_answer(
    request: Request,
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
    
    svc = _get_llm_service(request)
    grading_result = await svc.check_answer(
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
    request: Request,
    question: str = Form(...),
    student_answer: str = Form(...),
    question_type: str = Form(...),
    subject: str = Form(default="数学"),
    correct_answer: Optional[str] = Form(default=None)
):
    svc = _get_llm_service(request)
    grading_result = await svc.check_answer(
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
