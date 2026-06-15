from fastapi import APIRouter, Request
from app.services.llm_service import LLMService

router = APIRouter()


def _get_llm_service(request: Request) -> LLMService:
    provider = request.headers.get("X-LLM-Provider")
    api_key = request.headers.get("X-LLM-Api-Key")
    model = request.headers.get("X-LLM-Model")
    base_url = request.headers.get("X-LLM-Base-Url")
    if any([provider, api_key, model, base_url]):
        return LLMService(api_key=api_key, base_url=base_url, model=model, provider=provider)
    from app.services.llm_service import llm_service
    return llm_service


@router.get("/models")
async def list_models(request: Request):
    svc = _get_llm_service(request)
    models = await svc.list_models()
    return {"models": models}
