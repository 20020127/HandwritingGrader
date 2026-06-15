# 手写作业批改系统 - 使用说明

## 快速开始

### 1. 启动后端服务

```bash
cd backend
pip install -r requirements.txt
python main.py
```

服务将在 http://localhost:8000 启动。

### 2. 配置LLM API

编辑 `.env` 文件，填入你的API密钥：

```env
# 智谱AI
LLM_API_KEY=your_zhipu_api_key
LLM_PROVIDER=zhipu
LLM_MODEL=glm-4

# 或者通义千问
LLM_API_KEY=your_dashscope_api_key
LLM_PROVIDER=qwen
LLM_MODEL=qwen-turbo
```

### 3. 使用Android应用

1. 使用Android Studio打开 `android` 目录
2. 等待Gradle同步完成
3. 连接Android设备或启动模拟器
4. 点击运行按钮

## 功能说明

### 1. 批改作业
- 点击"开始批改作业"按钮
- 选择拍照或从相册选择图片
- 输入题目信息（题目内容、题型、科目）
- 系统自动识别并批改

### 2. 错题本
- 查看所有错题
- 按科目和题型筛选
- 标记已掌握的错题
- 删除错题

### 3. 成绩统计
- 查看总览数据
- 按科目统计
- 按题型统计
- 查看正确率趋势

### 4. 批改历史
- 查看所有批改记录
- 了解学习进度

## API接口

### OCR识别
```
POST /api/ocr/recognize
Content-Type: multipart/form-data

参数:
- file: 图片文件

返回:
- success: 是否成功
- texts: 识别的文字列表
- full_text: 完整文字
```

### 作业批改
```
POST /api/grading/check
Content-Type: multipart/form-data

参数:
- file: 图片文件
- question: 题目内容
- question_type: 题型
- subject: 科目
- correct_answer: 正确答案（可选）

返回:
- ocr_result: OCR识别结果
- grading_result: 批改结果
- student_answer: 学生答案
```

### 错题本
```
GET /api/wrong-questions/
参数:
- subject: 科目（可选）
- question_type: 题型（可选）
- is_mastered: 是否掌握（可选）
```

### 成绩统计
```
GET /api/statistics/overview
参数:
- days: 统计天数（默认30）
```

## 支持的题型

- 选择题（单选、多选）
- 填空题
- 计算题
- 问答题
- 判断题
- 应用题
- 几何题

## 支持的科目

- 数学
- 语文
- 英语
- 物理
- 化学
- 生物
- 历史
- 地理
- 政治

## 常见问题

### Q: OCR识别不准确怎么办？
A: 确保拍照清晰，光线充足，手写工整。可以尝试调整图片角度。

### Q: 如何更换LLM服务商？
A: 修改 `.env` 文件中的 `LLM_PROVIDER` 和相关配置。

### Q: 数据存储在哪里？
A: 后端使用SQLite数据库，存储在 `backend/handwriting_grader.db`。

### Q: 如何备份数据？
A: 复制 `backend/handwriting_grader.db` 文件即可备份。

## 开发说明

### 项目结构
- `backend/`: 后端服务
- `android/`: Android应用

### 技术栈
- 后端: FastAPI + PaddleOCR + SQLAlchemy
- Android: Kotlin + Jetpack Compose + Retrofit

### 添加新功能
1. 后端: 在 `app/api/` 添加新路由
2. Android: 在 `ui/screens/` 添加新屏幕
3. 更新导航配置

## 许可证

MIT License
