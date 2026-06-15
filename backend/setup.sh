#!/bin/bash

echo "========================================"
echo "HandwritingGrader Backend Setup"
echo "========================================"
echo ""

# Check Python version
python_version=$(python3 --version 2>&1)
if [[ $python_version == *"Python 3"* ]]; then
    echo "Python found: $python_version"
else
    echo "Error: Python 3 not found"
    exit 1
fi

echo ""
echo "Installing dependencies..."
pip3 install -r requirements.txt

echo ""
echo "Creating .env file..."
if [ ! -f .env ]; then
    cp .env.example .env
    echo "Created .env file. Please edit it with your API key."
else
    echo ".env file already exists."
fi

echo ""
echo "========================================"
echo "Setup complete!"
echo "========================================"
echo ""
echo "Next steps:"
echo "1. Edit .env file with your LLM API key"
echo "2. Run: python3 main.py"
echo ""
