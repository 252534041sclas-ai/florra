import pickle
import os

VECTOR_FILE = r"c:\Users\suriy\OneDrive\Desktop\my app\myproject\florra\ai\tile_vectors.pkl"

if not os.path.exists(VECTOR_FILE):
    print(f"File not found: {VECTOR_FILE}")
else:
    try:
        with open(VECTOR_FILE, "rb") as f:
            data = pickle.load(f)
            print(f"Loaded {len(data)} vectors.")
            print("First 5 keys:")
            for k in list(data.keys())[:5]:
                print(f" - {k}")
    except Exception as e:
        print(f"Error loading: {e}")
