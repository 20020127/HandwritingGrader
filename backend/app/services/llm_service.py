import httpx
import json
import logging
from typing import Dict, Any, Optional
from app.core.config import settings

logger = logging.getLogger(__name__)

class LLMService:
    def __init__(
        self,
        api_key: Optional[str] = None,
        base_url: Optional[str] = None,
        model: Optional[str] = None,
        provider: Optional[str] = None,
    ):
        self.api_key = api_key or settings.LLM_API_KEY
        self.base_url = base_url or settings.LLM_BASE_URL
        self.model = model or settings.LLM_MODEL
        self.provider = provider or settings.LLM_PROVIDER
    
    async def chat_completion(self, prompt: str, system_prompt: str = "") -> str:
        try:
            if self.provider == "zhipu":
                return await self._zhipu_completion(prompt, system_prompt)
            elif self.provider == "qwen":
                return await self._qwen_completion(prompt, system_prompt)
            elif self.provider == "wenxin":
                return await self._wenxin_completion(prompt, system_prompt)
            else:
                return await self._zhipu_completion(prompt, system_prompt)
        except Exception as e:
            logger.error(f"LLM调用失败: {e}")
            raise
    
    async def _zhipu_completion(self, prompt: str, system_prompt: str) -> str:
        url = f"{self.base_url}/chat/completions"
        
        messages = []
        if system_prompt:
            messages.append({"role": "system", "content": system_prompt})
        messages.append({"role": "user", "content": prompt})
        
        payload = {
            "model": self.model,
            "messages": messages,
            "temperature": 0.7,
            "max_tokens": 2000
        }
        
        headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {self.api_key}"
        }
        
        async with httpx.AsyncClient(timeout=60.0) as client:
            response = await client.post(url, json=payload, headers=headers)
            response.raise_for_status()
            result = response.json()
            return result["choices"][0]["message"]["content"]
    
    async def _qwen_completion(self, prompt: str, system_prompt: str) -> str:
        url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"
        
        messages = []
        if system_prompt:
            messages.append({"role": "system", "content": system_prompt})
        messages.append({"role": "user", "content": prompt})
        
        payload = {
            "model": self.model or "qwen-turbo",
            "input": {"messages": messages},
            "parameters": {
                "temperature": 0.7,
                "max_tokens": 2000
            }
        }
        
        headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {self.api_key}"
        }
        
        async with httpx.AsyncClient(timeout=60.0) as client:
            response = await client.post(url, json=payload, headers=headers)
            response.raise_for_status()
            result = response.json()
            return result["output"]["choices"][0]["message"]["content"]
    
    async def _wenxin_completion(self, prompt: str, system_prompt: str) -> str:
        url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie-speed-128k"
        
        messages = []
        if system_prompt:
            messages.append({"role": "user", "content": system_prompt})
        messages.append({"role": "user", "content": prompt})
        
        payload = {
            "messages": messages,
            "temperature": 0.7,
            "max_output_tokens": 2000
        }
        
        headers = {
            "Content-Type": "application/json"
        }
        
        async with httpx.AsyncClient(timeout=60.0) as client:
            response = await client.post(
                f"{url}?access_token={self.api_key}",
                json=payload,
                headers=headers
            )
            response.raise_for_status()
            result = response.json()
            return result["result"]
    
    async def check_answer(
        self,
        question: str,
        student_answer: str,
        question_type: str,
        correct_answer: Optional[str] = None,
        subject: str = "数学"
    ) -> Dict[str, Any]:
        system_prompt = """你是一个专业的作业批改老师，擅长批改各种类型的题目。
请严格按照要求的JSON格式返回结果，不要包含其他内容。"""
        
        correct_answer_section = ""
        if correct_answer:
            correct_answer_section = f"\n参考正确答案: {correct_answer}"
        
        prompt = f"""请判断以下答案是否正确。

科目: {subject}
题目类型: {question_type}
题目: {question}
学生答案: {student_answer}
{correct_answer_section}

请严格按照以下JSON格式回复：
{{
    "is_correct": true或false,
    "score": 0-100的整数,
    "max_score": 100,
    "feedback": "详细的批改意见和解析",
    "error_type": "错误类型（如：计算错误、概念错误、粗心大意等，如果正确则为null）",
    "key_points": ["关键知识点1", "关键知识点2"]
}}

批改要求：
1. 选择题：确认选项是否正确
2. 填空题：确认填写内容是否正确（允许合理变体）
3. 计算题：检查计算过程和结果
4. 问答题：评估答案的完整性和准确性
5. 判断题：确认判断是否正确
6. 应用题：检查解题思路和最终答案
7. 几何题：检查证明过程和结论"""
        
        response = await self.chat_completion(prompt, system_prompt)
        
        try:
            json_str = response
            if "```json" in json_str:
                json_str = json_str.split("```json")[1].split("```")[0]
            elif "```" in json_str:
                json_str = json_str.split("```")[1].split("```")[0]
            
            result = json.loads(json_str.strip())
            
            return {
                "success": True,
                "is_correct": result.get("is_correct", False),
                "score": result.get("score", 0),
                "max_score": result.get("max_score", 100),
                "feedback": result.get("feedback", ""),
                "error_type": result.get("error_type"),
                "key_points": result.get("key_points", [])
            }
        except json.JSONDecodeError as e:
            logger.error(f"JSON解析失败: {e}, 原始响应: {response}")
            return {
                "success": False,
                "error": "AI响应格式错误",
                "raw_response": response
            }

llm_service = LLMService()
