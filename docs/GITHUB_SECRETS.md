# GitHub Secrets配置指南

## 概述

本文档说明如何配置GitHub Secrets以实现自动构建和签名APK。

## 步骤1：生成签名密钥

```bash
cd android
generate-keystore.bat
```

这将生成 `release.jks` 文件。

## 步骤2：编码密钥文件

### Windows PowerShell
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release.jks"))
```

### macOS/Linux
```bash
base64 -i release.jks
```

复制输出的Base64字符串。

## 步骤3：添加GitHub Secrets

1. 进入GitHub仓库
2. 点击 `Settings` 选项卡
3. 左侧菜单选择 `Secrets and variables` → `Actions`
4. 点击 `New repository secret`
5. 添加以下Secrets：

| Secret名称 | 值 | 说明 |
|-----------|---|------|
| KEYSTORE_PATH | `android/app/release.jks` | 密钥文件路径 |
| KEYSTORE_PASSWORD | 你的密钥库密码 | 密钥库密码 |
| KEY_ALIAS | `handwritinggrader` | 密钥别名 |
| KEY_PASSWORD | 你的密钥密码 | 密钥密码 |

## 步骤4：推送代码触发构建

```bash
git add .
git commit -m "Add CI/CD"
git push origin main
```

## 步骤5：下载APK

1. 进入GitHub仓库 → `Actions` 选项卡
2. 点击最近的构建任务
3. 在 `Artifacts` 部分下载：
   - `debug-apk` - Debug版本
   - `release-apk` - Release版本（已签名）

## 手动触发构建

1. 进入GitHub仓库 → `Actions`
2. 选择 `Build Android APK`
3. 点击 `Run workflow`

## 安全注意事项

⚠️ **重要**：

1. **不要**将密钥文件提交到Git仓库
2. **不要**将密码硬编码到代码中
3. **定期**轮换密钥密码
4. **使用**强密码

## 故障排除

### 问题：构建失败，提示找不到密钥

**解决方案**：
- 检查 `KEYSTORE_PATH` 是否正确
- 确保密钥文件已正确上传（如果使用文件方式）

### 问题：签名失败

**解决方案**：
- 检查密码是否正确
- 确认密钥别名是否匹配

### 问题：APK无法安装

**解决方案**：
- 确保设备允许安装未知来源应用
- 检查minSdk版本是否兼容

## 本地测试签名

在推送之前，可以本地测试签名：

```bash
cd android

# 设置环境变量
export KEYSTORE_PATH=release.jks
export KEYSTORE_PASSWORD=your_password
export KEY_ALIAS=handwritinggrader
export KEY_PASSWORD=your_key_password

# 构建Release版本
./gradlew assembleRelease
```

检查生成的APK是否已正确签名：

```bash
apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
```
