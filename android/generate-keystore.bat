@echo off
echo ========================================
echo Generate Release Keystore
echo ========================================
echo.

set KEYSTORE_FILE=release.jks
set KEY_ALIAS=handwritinggrader
set VALIDITY=10000

echo Generating keystore: %KEYSTORE_FILE%
echo Key alias: %KEY_ALIAS%
echo Validity: %VALIDITY% days
echo.

keytool -genkey -v -keystore %KEYSTORE_FILE% -alias %KEY_ALIAS% -keyalg RSA -keysize 2048 -validity %VALIDITY%

echo.
echo ========================================
echo Keystore generated successfully!
echo ========================================
echo.
echo Next steps:
echo 1. Copy %KEYSTORE_FILE% to android/app/ directory
echo 2. Add secrets to GitHub repository:
echo    - KEYSTORE_PATH: path to %KEYSTORE_FILE%
echo    - KEYSTORE_PASSWORD: your keystore password
echo    - KEY_ALIAS: %KEY_ALIAS%
echo    - KEY_PASSWORD: your key password
echo.
pause
