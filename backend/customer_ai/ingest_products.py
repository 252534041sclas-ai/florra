from sentence_transformers import SentenceTransformer
import faiss
import json
import numpy as np
import os
from PIL import Image
import pickle

# Configuration
MODEL_NAME = "sentence-transformers/clip-ViT-B-32"
DATA_FILE = "data/products.json"
INDEX_FILE = "vectorstore/products.index"
META_FILE = "vectorstore/products.pkl"

# Load Model
print(f"🔄 Loading CLIP model: {MODEL_NAME}...")
model = SentenceTransformer(MODEL_NAME)

# Load Data
print("📂 Loading product data...")
if not os.path.exists(DATA_FILE):
    print(f"❌ {DATA_FILE} not found. Run fetch_products.py first.")
    exit(1)

with open(DATA_FILE, "r", encoding="utf-8") as f:
    products = json.load(f)

vectors = []
metadata = []

print(f"🚀 Processing {len(products)} products...")

for p in products:
    # 1. Text Embedding
    text_repr = p.get("text_representation", "")
    if text_repr:
        vec_text = model.encode(text_repr)
        vectors.append(vec_text)
        metadata.append({
            "type": "text",
            "product": p
        })

    # 2. Image Embedding (if exists)
    img_path = p.get("image_path")
    if img_path and os.path.exists(img_path):
        try:
            image = Image.open(img_path).convert("RGB") # Ensure RGB
            
            # A. Original
            vectors.append(model.encode(image))
            metadata.append({"type": "image_base", "product": p})

            # B. Center Crop (Zoom in) - 80%
            width, height = image.size
            left = width * 0.1
            top = height * 0.1
            right = width * 0.9
            bottom = height * 0.9
            crop_img = image.crop((left, top, right, bottom))
            vectors.append(model.encode(crop_img))
            metadata.append({"type": "image_crop", "product": p})

            # C. Horizontal Flip
            flip_img = image.transpose(Image.FLIP_LEFT_RIGHT)
            vectors.append(model.encode(flip_img))
            metadata.append({"type": "image_flip", "product": p})
            
        except Exception as e:
            print(f"⚠️ Failed to process image for {p['name']}: {e}")

# Convert to FAISS format
if not vectors:
    print("❌ No vectors generated.")
    exit(1)

vectors_np = np.array(vectors).astype('float32')

# Normalize vectors for Cosine Similarity
faiss.normalize_L2(vectors_np)

# Create Index
print(f"🧠 Indexing {len(vectors)} vectors...")
dimension = vectors_np.shape[1]
index = faiss.IndexFlatIP(dimension) # Inner Product (Cosine after norm)
index.add(vectors_np)

# Save
os.makedirs("vectorstore", exist_ok=True)
faiss.write_index(index, INDEX_FILE)
with open(META_FILE, "wb") as f:
    pickle.dump(metadata, f)

print("✅ Ingestion Complete!")
print(f"💾 Index saved to {INDEX_FILE}")
