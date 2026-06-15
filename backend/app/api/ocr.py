from fastapi import APIRouter, UploadFile, File, HTTPException
from typing import List
from app.services.ocr_service import ocr_service
from app.core.config import settings

router = APIRouter()

@router.post("/recognize")
async def recognize_text(file: UploadFile = File(...)):
    if file.content_type not in settings.ALLOWED_IMAGE_TYPES:
        raise HTTPException(status_code=400, detail="不支持的图片格式")
    
    contents = await file.read()
    
    if len(contents) > settings.MAX_IMAGE_SIZE:
        raise HTTPException(status_code=400, detail="图片大小超过限制")
    
    result = await ocr_service.recognize_text(contents)
    
    if not result["success"]:
        raise HTTPException(status_code=500, detail=result.get("error", "OCR识别失败"))
    
    return result

@router.post("/recognize-batch")
async def recognize_batch(files: List[UploadFile] = File(...)):
    results = []
    
    for file in files:
        if file.content_type not in settings.ALLOWED_IMAGE_TYPES:
            results.append({
                "filename": file.filename,
                "success": False,
                "error": "不支持的图片格式"
            })
            continue
        
        contents = await file.read()
        
        if len(contents) > settings.MAX_IMAGE_SIZE:
            results.append({
                "filename": file.filename,
                "success": False,
                "error": "图片大小超过限制"
            })
            continue
        
        result = await ocr_service.recognize_text(contents)
        results.append({
            "filename": file.filename,
            **result
        })
    
    return {"results": results}
