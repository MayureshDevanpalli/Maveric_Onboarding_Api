# 🚀 Maveric ResumeConvertor API

The **Maveric ResumeConvertor API** extracts key information from resumes (PDF and DOCX) using **Google Gemini's large language models**. It returns structured JSON output adhering to a predefined schema.

---

## 📌 Features

- ✅ Extracts information from `.pdf` and `.docx` resumes
- ✅ Utilizes Google Gemini LLMs for accurate parsing
- ✅ Returns structured JSON following a strict schema
- ✅ Built with FastAPI
- ✅ Includes error handling with informative messages

---

## 🧪 Endpoint

### `POST /extract_resume_details/`

Upload a resume and receive a JSON response containing:

- 👤 Candidate Name and Position
- 🧾 Professional Summary
- 💼 Professional Experience
- 🏆 Awards
- 📜 Certifications
- 🎓 Education
- 🛠 Skills (categorized)
- 📂 Project Experience

---

## ⚙️ Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository_url>

cd mayureshdevanpalli-maveric_onboarding_api
pip install -r requirements.txt

export GEMINI_API_KEY="YOUR_API_KEY"  # Replace with your actual key

uvicorn main:app --reload --host 0.0.0.0 --port 8090

🧠 Powered By
    FastAPI
    
    Google Gemini
    
    Pydantic
