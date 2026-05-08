import pickle
import numpy as np
import os
import sys

# Adjust path to import feature_extractor
# Assuming this file sits in myproject/florra/ai/
# We need to import feature_extractor which is in the same folder
from .feature_extractor import extract_features

# Path to vector file
# We assume it's in the same directory as this script
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
VECTOR_FILE = os.path.join(BASE_DIR, "tile_vectors.pkl")

# Global cache
tile_vectors = None
last_loaded_time = 0

def load_vectors():
    global tile_vectors, last_loaded_time
    
    if not os.path.exists(VECTOR_FILE):
        print("⚠️ Vector file not found.")
        tile_vectors = {}
        return

    # Check modification time
    file_mtime = os.path.getmtime(VECTOR_FILE)
    
    # Reload if not loaded or if file changed
    if tile_vectors is None or file_mtime > last_loaded_time:
        try:
            with open(VECTOR_FILE, "rb") as f:
                tile_vectors = pickle.load(f)
            last_loaded_time = file_mtime
            print(f"✅ Loaded {len(tile_vectors)} tile vectors. (Updated)")
        except Exception as e:
            print(f"❌ Error loading vectors: {e}")
            tile_vectors = {}

def cosine_similarity(a, b):
    # a and b are numpy arrays
    norm_a = np.linalg.norm(a)
    norm_b = np.linalg.norm(b)
    if norm_a == 0 or norm_b == 0:
        return 0.0
    return np.dot(a, b) / (norm_a * norm_b)

def find_similar_tiles(image_file, top_k=6):
    """
    Finds similar tiles for an uploaded image file (InMemoryUploadedFile).
    """
    load_vectors()
    
    if not tile_vectors:
        return []

    # Extract features from the uploaded image
    query_vector = extract_features(image_file=image_file)
    
    if query_vector is None:
        return []

    scores = []
    scores = []
    for tile_name, vectors in tile_vectors.items():
        # Support both list (augmented) and single vector (legacy)
        if isinstance(vectors, list):
             # Find best match among variations
             best_score = -1.0
             for vec in vectors:
                 score = cosine_similarity(query_vector, vec)
                 if score > best_score:
                     best_score = score
             scores.append((tile_name, best_score))
        else:
             # Legacy
             score = cosine_similarity(query_vector, vectors)
             scores.append((tile_name, score))

    # Sort by score descending
    scores.sort(key=lambda x: x[1], reverse=True)
    
    # Return Top K
    return scores[:top_k]
