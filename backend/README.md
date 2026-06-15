# 手写作业批改系统 - 后端服务

基于PaddleOCR和LLM的手写作业批改API服务。

## 功能特性

- OCR手写文字识别（基于PaddleOCR）
- AI智能批改（支持多种LLM服务商）
- 错题本管理
- 成绩统计分析
- 支持多种题型（选择题、填空题、计算题、问答题等）

## 快速开始

### 1. 安装依赖

```bash
pip install -r requirements.txt
```

### 2. 配置环境变量

复制 `.env.example` 为 `.env`，填入你的API密钥：

```bash
cp .env.example .env
```

编辑 `.env` 文件：

```env
LLM_API_KEY=your_api_key_here
LLM_PROVIDER=zhipu  # 可选: zhipu, qwen, wenxin
LLM_MODEL=glm-4
```

### 3. 启动服务

```bash
python main.py
```

或者使用uvicorn：

```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

### 4. 访问API文档

启动服务后，访问以下地址查看API文档：

- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## API接口

### OCR识别

- `POST /api/ocr/recognize` - 单张图片识别
- `POST /api/ocr/recognize-batch` - 批量图片识别

### 作业批改

- `POST /api/grading/check` - 图片批改
- `POST /api/grading/check-text` - 文本批改

### 错题本

- `GET /api/wrong-questions/` - 获取错题列表
- `POST /api/wrong-questions/` - 添加错题
- `PUT /api/wrong-questions/{id}` - 更新错题
- `DELETE /api/wrong-questions/{id}` - 删除错题
- `POST /api/wrong-questions/{id}/review` - 记录复习

### 成绩统计

- `GET /api/statistics/overview` - 总览统计
- `GET /api/statistics/by-subject` - 按科目统计
- `GET /api/statistics/daily` - 每日统计
- `GET /api/statistics/by-question-type` - 按题型统计
- `GET /api/statistics/wrong-questions-summary` - 错题汇总

## LLM服务商配置

### 智谱AI（默认）

```env
LLM_PROVIDER=zhipu
LLM_API_KEY=your_zhipu_api_key
LLM_MODEL=glm-4
LLM_BASE_URL=https://open.bigmodel.cn/api/paas/v4
```

### 通义千问

```env
LLM_PROVIDER=qwen
LLM_API_KEY=your_dashscope_api_key
LLM_MODEL=qwen-turbo
```

### 文心一言

```env
LLM_PROVIDER=wenxin
LLM_API_KEY=your_access_token
```

## 技术栈

- FastAPI - Web框架
- PaddleOCR - OCR识别
- SQLAlchemy - 数据库ORM
- SQLite - 数据库
- httpx - HTTP客户端

## 目录结构

```
backend/
├── app/
│   ├── api/          # API路由
│   ├── core/         # 配置
│   ├── models/       # 数据模型
│   ├── services/     # 业务逻辑
│   └── utils/        # 工具函数
├── main.py           # 主入口
├── requirements.txt  # 依赖
└── .env              # 环境变量
```

## 许可证

MIT
