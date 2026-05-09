@echo off
echo 🚀 Starting Local AI Chatbot...
echo.
cd /d %~dp0

echo 📦 Install/Update Dependencies...
pip install -r requirements.txt

echo 🛠 Extracting Product Data...
python fetch_products.py

echo 🧠 Indexing Products (CLIP AI)...
python ingest_products.py

echo 🌐 Starting API Server...
uvicorn chatbot_api:app --host 0.0.0.0 --port 8001 --reload
pause
