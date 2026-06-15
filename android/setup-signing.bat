@echo off
echo ========================================
echo Setup Local Signing Environment
echo ========================================
echo.

set KEYSTORE_FILE=release.jks
set KEY_ALIAS=handwritinggrader

echo Checking if keystore exists...
if not exist "%KEYSTORE_FILE%" (
    echo Keystore not found. Generating new keystore...
    call generate-keystore.bat
)

echo.
echo Setting up environment variables...
echo.

set /p KEYSTORE_PASSWORD="Enter keystore password: "
set /p KEY_PASSWORD="Enter key password: "

echo.
echo Adding to user environment variables...
echo.

setx KEYSTORE_PATH "%~dp0%KEYSTORE_FILE%"
setx KEYSTORE_PASSWORD "%KEYSTORE_PASSWORD%"
setx KEY_ALIAS "%KEY_ALIAS%"
setx KEY_PASSWORD "%KEY_PASSWORD%"

echo.
echo ========================================
echo Environment variables set successfully!
echo ========================================
echo.
echo You can now build Release APK with:
echo   cd android
echo   gradlew.bat assembleRelease
echo.
echo Or restart your terminal to apply changes.
echo.
pause
