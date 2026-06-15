from fastapi import APIRouter, UploadFile, File, Form, HTTPException, Request
from typing import Optional
import base64
from app.services.llm_service import LLMService, llm_service

router = APIRouter()


def _get_llm_service(request: Request) -> LLMService:
    provider = request.headers.get("X-LLM-Provider")
    api_key = request.headers.get("X-LLM-Api-Key")
    model = request.headers.get("X-LLM-Model")
    base_url = request.headers.get("X-LLM-Base-Url")
    if any([provider, api_key, model, base_url]):
        return LLMService(api_key=api_key, base_url=base_url, model=model, provider=provider)
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
    contents = await file.read()
    if len(contents) > 10 * 1024 * 1024:
        raise HTTPException(status_code=400, detail="图片大小超过限制")

    image_b64 = base64.b64encode(contents).decode("utf-8")
    svc = _get_llm_service(request)

    grading_result = await svc.check_answer_with_image(
        question=question,
        image_base64=image_b64,
        question_type=question_type,
        correct_answer=correct_answer,
        subject=subject
    )

    if not grading_result["success"]:
        raise HTTPException(status_code=500, detail=grading_result.get("error", "AI批改失败"))

    return {
        "grading_result": grading_result,
        "student_answer": grading_result.get("student_answer", "")
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
