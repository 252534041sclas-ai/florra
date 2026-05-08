import pickle
import os
import sys

# Path to vector file
VECTOR_FILE = "florra/ai/tile_vectors.pkl"

if not os.path.exists(VECTOR_FILE):
    print(f"❌ {VECTOR_FILE} not found!")
    sys.exit(1)

text_out = "vectors_output.txt"

print(f"Reading {VECTOR_FILE}...")
try:
    with open(VECTOR_FILE, "rb") as f:
        data = pickle.load(f)
    
    with open(text_out, "w", encoding="utf-8") as out:
        out.write(f"Total entries: {len(data)}\n")
        out.write("-" * 40 + "\n")
        for key in data.keys():
            out.write(f"{key}\n")
            
    print(f"✅ Dumped {len(data)} keys to {text_out}")

except Exception as e:
    print(f"❌ Error reading pickle: {e}")
