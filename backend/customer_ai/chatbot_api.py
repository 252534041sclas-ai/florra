from fastapi import FastAPI, UploadFile, File
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
import faiss
import pickle
import numpy as np
from PIL import Image
import io

# App
app = FastAPI()

# Configuration
MODEL_NAME = "sentence-transformers/clip-ViT-B-32"
INDEX_FILE = "vectorstore/products.index"
META_FILE = "vectorstore/products.pkl"

# Global State
model = None
index = None
metadata = []

def load_resources():
    global model, index, metadata
    if model is None:
        print(f"🔄 Loading CLIP model: {MODEL_NAME}...")
        model = SentenceTransformer(MODEL_NAME)
    
    if index is None:
        print("📂 Loading index...")
        try:
            index = faiss.read_index(INDEX_FILE)
            with open(META_FILE, "rb") as f:
                metadata = pickle.load(f)
        except Exception as e:
            print(f"⚠️ Index not found: {e}. Search will fail.")

# Load on startup (or lazy load)
load_resources()

class ChatRequest(BaseModel):
    message: str

def search_index(vector, k=3):
    if index is None:
        return []
    
    # Normalize
    faiss.normalize_L2(vector)
    
    # Search
    D, I = index.search(vector, k)
    
    results = []
    for idx_dist, idx_val in zip(D[0], I[0]):
        if idx_val < len(metadata):
            match = metadata[idx_val]
            prod = match["product"]
            results.append({
                "product": prod,
                "score": float(idx_dist),
                "match_type": match["type"]
            })
    return results

@app.post("/customer/chat")
def chat(req: ChatRequest):
    if not model or not index:
        return {"reply": "System initializing or index missing."}

    # 1. Generate Text Embedding
    print(f"🔍 Searching for: {req.message}")
    vec = model.encode([req.message])
    
    # 2. Search
    results = search_index(vec, k=3)
    
    # 3. Format Response
    if not results:
        return {"reply": "I couldn't find any matching tiles."}
    
    reply = "Here are some tiles matching your description:\n\n"
    for r in results:
        p = r['product']
        reply += f"• {p['name']} ({p['category']})\n  Finish: {p['finish']}, Size: {p['size']}\n"
    
    return {"reply": reply, "products": results}

@app.post("/customer/search_image")
async def search_image(file: UploadFile = File(...)):
    if not model or not index:
        return {"reply": "System initializing or index missing."}

    # 1. Read Image
    print(f"📸 Processing uploaded image: {file.filename}")
    content = await file.read()
    image = Image.open(io.BytesIO(content))
    
    # 2. Generate Image Embedding
    vec = model.encode(image)
    vec = np.expand_dims(vec, axis=0) # batch dimension
    
    # 3. Search
    results = search_index(vec, k=3)
    
    # 4. Format Response
    if not results:
        return {"reply": "I couldn't find any visually similar tiles."}
    
    reply = "I found these tiles that look similar:\n\n"
    for r in results:
        p = r['product']
        reply += f"• {p['name']} ({p['category']})\n"
        
    return {"reply": reply, "products": results}
