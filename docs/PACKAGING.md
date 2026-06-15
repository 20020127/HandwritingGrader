# APK打包指南

## 本地打包

### 方式1：Android Studio（推荐）

1. 打开Android Studio
2. 选择 `File` → `Open` → 选择 `android` 目录
3. 等待Gradle同步完成
4. 点击菜单 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
5. 构建完成后，APK文件在 `app/build/outputs/apk/debug/` 目录

### 方式2：命令行打包

```bash
cd android

# Debug版本（不需要签名）
./gradlew assembleDebug

# Release版本（需要签名配置）
./gradlew assembleRelease
```

## 生成签名密钥

发布到应用商店需要签名密钥。

### 步骤1：生成密钥

```bash
cd android

# Windows
generate-keystore.bat

# macOS/Linux
keytool -genkey -v -keystore release.jks -alias handwritinggrader -keyalg RSA -keysize 2048 -validity 10000
```

### 步骤2：配置签名

**方式A：环境变量（推荐）**

```bash
export KEYSTORE_PATH=/path/to/release.jks
export KEYSTORE_PASSWORD=your_password
export KEY_ALIAS=handwritinggrader
export KEY_PASSWORD=your_key_password
```

**方式B：本地配置**

1. 将 `release.jks` 复制到 `android/app/` 目录
2. 修改 `app/build.gradle.kts`：

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("release.jks")
        storePassword = "your_password"
        keyAlias = "handwritinggrader"
        keyPassword = "your_key_password"
    }
}
```

## GitHub Actions自动构建

### 配置步骤

1. **生成密钥文件**
   ```bash
   cd android
   generate-keystore.bat
   ```

2. **编码密钥文件**（用于GitHub Secrets）
   ```bash
   # Windows PowerShell
   [Convert]::ToBase64String([IO.File]::ReadAllBytes("release.jks"))
   
   # macOS/Linux
   base64 -i release.jks
   ```

3. **添加GitHub Secrets**

   进入GitHub仓库 → Settings → Secrets and variables → Actions → New repository secret

   | Secret名称 | 值 |
   |-----------|---|
   | KEYSTORE_PATH | 密钥文件路径（如 `android/app/release.jks`） |
   | KEYSTORE_PASSWORD | 密钥库密码 |
   | KEY_ALIAS | 密钥别名（如 `handwritinggrader`） |
   | KEY_PASSWORD | 密钥密码 |

4. **推送代码触发构建**
   ```bash
   git add .
   git commit -m "Add CI/CD"
   git push origin main
   ```

5. **下载APK**

   - 进入GitHub仓库 → Actions → 选择最近的构建
   - 在Artifacts部分下载 `debug-apk` 或 `release-apk`

### 手动触发构建

1. 进入GitHub仓库 → Actions
2. 选择 `Build Android APK`
3. 点击 `Run workflow`

## 发布到Google Play

### 准备工作

1. 注册Google Play开发者账号（$25一次性费用）
2. 生成签名密钥（见上方）
3. 准备应用商店素材：
   - 应用图标（512x512 PNG）
   - 截图（至少2张）
   - 描述文本
   - 隐私政策链接

### 上传APK

1. 登录 [Google Play Console](https://play.google.com/console)
2. 创建新应用
3. 填写应用信息
4. 上传签名的Release APK
5. 填写内容分级、定价等信息
6. 提交审核

## 常见问题

### Q: 构建失败怎么办？

A: 检查以下几点：
- Java版本是否为17
- Android SDK是否正确安装
- Gradle版本是否兼容

### Q: Release APK无法安装？

A: 确保：
- 设备允许安装未知来源应用
- APK已正确签名
- minSdk版本兼容

### Q: 如何更新版本号？

A: 修改 `app/build.gradle.kts` 中的：
```kotlin
defaultConfig {
    versionCode = 2  // 每次发布递增
    versionName = "1.1"
}
```

## 文件位置

```
android/app/build/outputs/apk/
├── debug/
│   └── app-debug.apk      # Debug版本
└── release/
    └── app-release.apk    # Release版本（需要签名）
```
