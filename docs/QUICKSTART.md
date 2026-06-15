# 快速开始指南

## 前提条件

### 后端开发
- Python 3.8+
- pip

### Android开发
- Android Studio
- JDK 17
- Android SDK 34

## 步骤1：启动后端服务

```bash
cd backend

# Windows
setup.bat

# macOS/Linux
chmod +x setup.sh
./setup.sh
```

编辑 `.env` 文件，填入你的LLM API密钥：

```env
# 智谱AI（推荐，中文能力强）
LLM_API_KEY=your_zhipu_api_key
LLM_PROVIDER=zhipu
LLM_MODEL=glm-4

# 或者通义千问
LLM_API_KEY=your_dashscope_api_key
LLM_PROVIDER=qwen
LLM_MODEL=qwen-turbo
```

启动服务：

```bash
python main.py
```

服务将在 http://localhost:8000 启动，API文档在 http://localhost:8000/docs

## 步骤2：构建Android应用

### 方式1：Android Studio（推荐）

1. 打开Android Studio
2. 选择 `File` → `Open` → 选择 `android` 目录
3. 等待Gradle同步完成
4. 点击 `Run` 按钮运行应用

### 方式2：命令行

```bash
cd android

# Debug版本
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug
```

## 步骤3：配置API地址

如果后端服务不在 `http://10.0.2.2:8000`（模拟器默认地址），需要修改Android应用的API地址：

编辑 `android/app/src/main/java/com/handwritinggrader/data/network/NetworkModule.kt`：

```kotlin
private const val BASE_URL = "http://your-server-ip:8000/"
```

## 测试应用

### 1. 测试后端API

```bash
cd backend
python test_api.py
```

### 2. 测试Android应用

1. 启动Android模拟器或连接真机
2. 运行应用
3. 点击"开始批改作业"
4. 拍照或选择图片
5. 输入题目信息
6. 查看批改结果

## 获取LLM API密钥

### 智谱AI（推荐）

1. 访问 https://open.bigmodel.cn
2. 注册账号
3. 创建API密钥
4. 复制密钥到 `.env` 文件

### 通义千问

1. 访问 https://dashscope.aliyun.com
2. 注册账号
3. 创建API密钥
4. 复制密钥到 `.env` 文件

## 常见问题

### Q: Gradle同步失败怎么办？

A: 
1. 检查网络连接
2. 尝试使用VPN
3. 清除Gradle缓存：`~/.gradle/caches`

### Q: 应用无法连接后端？

A: 
1. 确保后端服务正在运行
2. 检查API地址是否正确
3. 如果使用真机，确保手机和电脑在同一网络

### Q: OCR识别不准确？

A: 
1. 确保拍照清晰，光线充足
2. 手写尽量工整
3. 避免图片倾斜

### Q: 如何更换LLM服务商？

A: 修改 `.env` 文件中的 `LLM_PROVIDER` 和相关配置：

```env
# 智谱AI
LLM_PROVIDER=zhipu
LLM_API_KEY=your_key
LLM_MODEL=glm-4

# 通义千问
LLM_PROVIDER=qwen
LLM_API_KEY=your_key
LLM_MODEL=qwen-turbo

# 文心一言
LLM_PROVIDER=wenxin
LLM_API_KEY=your_access_token
```

## 项目结构

```
HandwritingGrader/
├── backend/                 # 后端服务
│   ├── app/
│   │   ├── api/            # API路由
│   │   ├── core/           # 配置
│   │   ├── models/         # 数据模型
│   │   └── services/       # 业务逻辑
│   ├── main.py             # 主入口
│   └── .env                # 环境变量
├── android/                 # Android应用
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/       # Kotlin源码
│   │   │   └── res/        # 资源文件
│   │   └── build.gradle.kts
│   └── build.gradle.kts
└── docs/                    # 文档
```

## 下一步

1. 配置签名密钥（发布用）
2. 设置GitHub Actions自动构建
3. 添加更多题型支持
4. 优化OCR识别准确率
