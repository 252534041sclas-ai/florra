import pickle
import os

vector_file = 'florra/ai/tile_vectors.pkl'
if os.path.exists(vector_file):
    with open(vector_file, 'rb') as f:
        data = pickle.load(f)
        print(f"Total vectors: {len(data)}")
        print("Keys:")
        for k in list(data.keys())[:10]:
            print(f"  - {k}")
else:
    print("Vector file not found")
