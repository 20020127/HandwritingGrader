import requests
import json

BASE_URL = "http://localhost:8000"

def test_health():
    response = requests.get(f"{BASE_URL}/health")
    print(f"Health check: {response.json()}")

def test_ocr(image_path):
    with open(image_path, "rb") as f:
        files = {"file": f}
        response = requests.post(f"{BASE_URL}/api/ocr/recognize", files=files)
        print(f"OCR result: {response.json()}")

def test_grading(image_path, question, question_type, subject):
    with open(image_path, "rb") as f:
        files = {"file": f}
        data = {
            "question": question,
            "question_type": question_type,
            "subject": subject
        }
        response = requests.post(f"{BASE_URL}/api/grading/check", files=files, data=data)
        print(f"Grading result: {response.json()}")

def test_statistics():
    response = requests.get(f"{BASE_URL}/api/statistics/overview")
    print(f"Statistics: {response.json()}")

if __name__ == "__main__":
    print("Testing HandwritingGrader API...")
    print()
    
    test_health()
    print()
    
    print("To test OCR, run:")
    print("  test_ocr('path/to/image.jpg')")
    print()
    
    print("To test grading, run:")
    print("  test_grading('path/to/image.jpg', '题目内容', '选择题', '数学')")
