@echo off
echo ========================================
echo HandwritingGrader Backend Setup
echo ========================================
echo.

REM Check Python version
python --version 2>nul
if %errorlevel% neq 0 (
    echo Error: Python not found
    echo Please install Python 3.8+ from https://www.python.org
    pause
    exit /b 1
)

echo.
echo Installing dependencies...
pip install -r requirements.txt

echo.
echo Creating .env file...
if not exist .env (
    copy .env.example .env
    echo Created .env file. Please edit it with your API key.
) else (
    echo .env file already exists.
)

echo.
echo ========================================
echo Setup complete!
echo ========================================
echo.
echo Next steps:
echo 1. Edit .env file with your LLM API key
echo 2. Run: python main.py
echo.
pause
