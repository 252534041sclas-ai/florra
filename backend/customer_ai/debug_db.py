import sqlite3
import os

DB_PATH = "../myproject/db.sqlite3"

if not os.path.exists(DB_PATH):
    print(f"❌ Database not found at {DB_PATH}")
    exit(1)

conn = sqlite3.connect(DB_PATH)
cursor = conn.cursor()

cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
tables = cursor.fetchall()

print("Tables found:")
for t in tables:
    print(t[0])

conn.close()
