import subprocess
import time
import sys
import os

def run_servers():
    base_path = os.path.dirname(os.path.abspath(__file__))
    
    # Paths
    django_path = os.path.join(base_path, "backend", "myproject")
    chatbot_path = os.path.join(base_path, "backend", "customer_ai")

    print("🚀 Starting Florra Unified Server...")
    
    # 1. Start Chatbot (FastAPI)
    print("🤖 Launching AI Chatbot on Port 8000...")
    chatbot_proc = subprocess.Popen(
        [sys.executable, "-m", "uvicorn", "chatbot_api:app", "--host", "0.0.0.0", "--port", "8000"],
        cwd=chatbot_path
    )

    # 2. Start Main Backend (Django)
    print("📊 Launching Main Backend on Port 8001...")
    django_proc = subprocess.Popen(
        [sys.executable, "manage.py", "runserver", "0.0.0.0:8001"],
        cwd=django_path
    )

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("\n🛑 Stopping servers...")
        chatbot_proc.terminate()
        django_proc.terminate()
        print("✅ Servers stopped.")

if __name__ == "__main__":
    run_servers()
