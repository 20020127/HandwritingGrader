# 手写作业批改系统

基于PaddleOCR和LLM的手写作业批改安卓应用。

## 项目结构

```
HandwritingGrader/
├── .github/workflows/      # CI/CD配置
├── backend/                 # 后端服务
│   ├── app/
│   │   ├── api/            # API路由
│   │   ├── core/           # 配置
│   │   ├── models/         # 数据模型
│   │   ├── services/       # 业务逻辑
│   │   └── utils/          # 工具函数
│   ├── main.py             # 主入口
│   └── requirements.txt    # 依赖
├── android/                 # Android应用
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/       # Kotlin源码
│   │   │   ├── res/        # 资源文件
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── build.gradle.kts
├── docs/                    # 文档
└── README.md
```

## 功能特性

- OCR手写文字识别（基于PaddleOCR）
- AI智能批改（支持多种LLM服务商）
- 支持多种题型（选择题、填空题、计算题、问答题等）
- 错题本管理
- 成绩统计分析

## 快速开始

### 1. 启动后端服务

```bash
cd backend
pip install -r requirements.txt
python main.py
```

### 2. 配置LLM API

复制 `.env.example` 为 `.env`，填入API密钥：

```env
LLM_API_KEY=your_api_key_here
LLM_PROVIDER=zhipu
LLM_MODEL=glm-4
```

### 3. 构建Android应用

使用Android Studio打开 `android` 目录，然后构建并运行。

详细打包说明请参考：[APK打包指南](docs/PACKAGING.md)

## API接口

- `POST /api/ocr/recognize` - OCR识别
- `POST /api/grading/check` - 图片批改
- `POST /api/grading/check-text` - 文本批改
- `GET /api/wrong-questions/` - 获取错题列表
- `GET /api/statistics/overview` - 总览统计

## 技术栈

### 后端
- FastAPI
- PaddleOCR
- SQLAlchemy
- SQLite

### Android
- Kotlin
- Jetpack Compose
- Retrofit
- Room
- Hilt

## 许可证

MIT
