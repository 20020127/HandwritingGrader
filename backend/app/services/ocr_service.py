from paddleocr import PaddleOCR
from PIL import Image
import io
import logging
from typing import List, Dict, Any
from app.core.config import settings

logger = logging.getLogger(__name__)

class OCRService:
    def __init__(self):
        self.ocr = None
        self._initialize_ocr()
    
    def _initialize_ocr(self):
        try:
            self.ocr = PaddleOCR(
                use_angle_cls=settings.PADDLEOCR_USE_ANGLE_CLS,
                lang=settings.PADDLEOCR_LANG,
                show_log=False
            )
            logger.info("PaddleOCR初始化成功")
        except Exception as e:
            logger.error(f"PaddleOCR初始化失败: {e}")
            raise
    
    async def recognize_text(self, image_bytes: bytes) -> Dict[str, Any]:
        try:
            image = Image.open(io.BytesIO(image_bytes))
            
            import numpy as np
            image_array = np.array(image)
            
            result = self.ocr.ocr(image_array, cls=True)
            
            texts = []
            full_text = ""
            
            if result and result[0]:
                for line in result[0]:
                    text = line[1][0]
                    confidence = line[1][1]
                    box = line[0]
                    
                    texts.append({
                        "text": text,
                        "confidence": confidence,
                        "box": box
                    })
                    full_text += text + "\n"
            
            return {
                "success": True,
                "texts": texts,
                "full_text": full_text.strip(),
                "text_count": len(texts)
            }
            
        except Exception as e:
            logger.error(f"OCR识别失败: {e}")
            return {
                "success": False,
                "error": str(e),
                "texts": [],
                "full_text": "",
                "text_count": 0
            }
    
    async def recognize_handwriting(self, image_bytes: bytes) -> Dict[str, Any]:
        return await self.recognize_text(image_bytes)

ocr_service = OCRService()
