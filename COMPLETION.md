# 手写作业批改系统 - 项目完成

## 项目概述

已成功创建一个基于PaddleOCR和LLM的手写作业批改安卓应用。

## 项目结构

```
HandwritingGrader/
├── backend/                    # 后端服务
│   ├── app/
│   │   ├── api/               # API路由
│   │   │   ├── ocr.py         # OCR识别
│   │   │   ├── grading.py     # 作业批改
│   │   │   ├── wrong_questions.py  # 错题本
│   │   │   └── statistics.py  # 成绩统计
│   │   ├── core/              # 配置
│   │   │   └── config.py      # 应用配置
│   │   ├── models/            # 数据模型
│   │   │   ├── database.py    # 数据库配置
│   │   │   └── schemas.py     # 数据模型
│   │   └── services/          # 业务逻辑
│   │       ├── ocr_service.py # OCR服务
│   │       └── llm_service.py # LLM服务
│   ├── main.py                # 主入口
│   ├── requirements.txt       # Python依赖
│   ├── .env.example           # 环境变量示例
│   └── test_api.py            # API测试脚本
├── android/                   # Android应用
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/handwritinggrader/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── HandwritingGraderApp.kt
│   │   │   │   ├── data/      # 数据层
│   │   │   │   │   ├── api/   # API接口
│   │   │   │   │   ├── models/ # 数据模型
│   │   │   │   │   └── network/ # 网络配置
│   │   │   │   ├── navigation/ # 导航
│   │   │   │   └── ui/        # UI层
│   │   │   │       ├── screens/ # 屏幕
│   │   │   │       │   ├── home/ # 首页
│   │   │   │       │   ├── camera/ # 相机
│   │   │   │       │   ├── result/ # 结果
│   │   │   │       │   ├── wrongquestions/ # 错题本
│   │   │   │       │   ├── statistics/ # 统计
│   │   │   │       │   └── history/ # 历史
│   │   │   │       └── theme/ # 主题
│   │   │   ├── res/           # 资源文件
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   ├── build.gradle.kts
│   └── settings.gradle.kts
├── docs/                      # 文档
│   └── USAGE.md               # 使用说明
└── README.md                  # 项目说明
```

## 功能特性

### 核心功能
- ✅ OCR手写文字识别（基于PaddleOCR）
- ✅ AI智能批改（支持多种LLM服务商）
- ✅ 支持多种题型（选择题、填空题、计算题、问答题等）
- ✅ 支持多种科目（数学、语文、英语等）

### 辅助功能
- ✅ 错题本管理
- ✅ 成绩统计分析
- ✅ 批改历史记录

## 快速开始

### 1. 启动后端服务

```bash
cd HandwritingGrader/backend
pip install -r requirements.txt
python main.py
```

### 2. 配置LLM API

编辑 `.env` 文件：

```env
LLM_API_KEY=your_api_key_here
LLM_PROVIDER=zhipu
LLM_MODEL=glm-4
```

### 3. 构建Android应用

使用Android Studio打开 `android` 目录，构建并运行。

## 技术栈

### 后端
- **FastAPI** - Web框架
- **PaddleOCR** - OCR识别
- **SQLAlchemy** - 数据库ORM
- **SQLite** - 数据库

### Android
- **Kotlin** - 开发语言
- **Jetpack Compose** - UI框架
- **Retrofit** - 网络请求
- **Room** - 本地数据库
- **Hilt** - 依赖注入

## API接口

- `POST /api/ocr/recognize` - OCR识别
- `POST /api/grading/check` - 图片批改
- `POST /api/grading/check-text` - 文本批改
- `GET /api/wrong-questions/` - 获取错题列表
- `GET /api/statistics/overview` - 总览统计

## 支持的LLM服务商

- 智谱AI (GLM-4)
- 通义千问 (Qwen)
- 文心一言 (ERNIE)

## 下一步

1. 配置LLM API密钥
2. 安装Python依赖
3. 启动后端服务
4. 使用Android Studio构建应用
5. 测试功能
