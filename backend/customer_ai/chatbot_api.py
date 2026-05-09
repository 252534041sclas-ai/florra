import requests
import re
from fastapi import FastAPI, UploadFile, File, Query
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
DJANGO_BASE_URL = "http://127.0.0.1:8000/api"

# FAQ Store (Basic)
FAQS = {
    "delivery": "We usually deliver within 3-5 business days across Tamil Nadu. Out-of-state delivery takes 7-10 days.",
    "return": "Tiles can be returned within 48 hours of delivery if they are unused and in original packaging. Broken tiles are replaced if reported immediately.",
    "installation": "Yes, we provide installation support through our verified partner contractors. Pricing depends on the area (sqft).",
    "contact": "You can visit our showroom at Florra Tiles, Coimbatore or WhatsApp us at +91 98765 43210."
}

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

def search_index(vector, k=5):
    if index is None:
        return []
    
    # Normalize
    faiss.normalize_L2(vector)
    
    # Search
    D, I = index.search(vector, k)
    
    results = []
    for idx_dist, idx_val in zip(D[0], I[0]):
        if idx_val < len(metadata) and idx_val != -1:
            match = metadata[idx_val]
            prod = match["product"]
            # Add similarity metadata to product object for the UI
            prod["similarity_score"] = float(idx_dist)
            prod["match_type"] = match["type"]
            results.append(prod)
    return results

def get_django_data(endpoint, params=None):
    try:
        resp = requests.get(f"{DJANGO_BASE_URL}/{endpoint}", params=params, timeout=5)
        if resp.status_code == 200:
            return resp.json()
    except Exception as e:
        print(f"❌ Django API Error: {e}")
    return None

def classify_intent(message):
    message = message.lower()
    
    # Visual Search request (Tanglish/Tamil)
    if any(k in message for k in ["match", "similar", "iddhu mari", "idhupola", "photo", "image", "upload"]):
        return "visual_search_hint"

    # 1. Order Tracking (Eng + Tamil + Tanglish)
    if any(k in message for k in ["order", "track", "delivery", "bill", "invoice", "yenga", "enga", "status", "varum"]):
        return "track_order"
    
    # 2. Quotation Status (Eng + Tamil + Tanglish)
    if any(k in message for k in ["quote", "quotation", "enquiry", "status", "update", "kodu"]):
        return "check_quote"
    
    # 3. Price Estimation / Area Calc (Eng + Tamil + Tanglish)
    if any(k in message for k in ["cost", "how much", "price", "sqft", "sq ft", "room", "calculate", "vilai", "rate", "evlo", "evvalavu"]):
        if re.search(r'\d+', message):
            return "calculate_price"
        return "search_products"
    
    # 4. FAQs (Eng + Tamil + Tanglish)
    if any(k in message for k in ["return", "policy", "contact", "address", "phone", "install", "panna", "idham", "edam", "ponnu"]):
        return "faq"
    
    # 5. Recommendations (Eng + Tamil + Tanglish)
    if any(k in message for k in ["suggest", "recommend", "best", "bathroom", "kitchen", "hall", "living", "bedroom", "pannu", "nalla"]):
        return "recommendations"
        
    return "search_products"

def format_product_list(products, prefix=""):
    if not products:
        return "I couldn't find any matching tiles."
    
    reply = prefix + "\n\n"
    for p in products:
        price_str = f"₹{p['price']}/sqft" if 'price' in p and p['price'] else ""
        reply += f"• *{p['name']}* ({p['category']})\n  Finish: {p['finish']}, Size: {p['size']} {price_str}\n"
    return reply

@app.post("/customer/chat")
def chat(req: ChatRequest):
    if not model or not index:
        return {"reply": "System initializing or index missing."}

    intent = classify_intent(req.message)
    print(f"🤖 Intent Detected: {intent} for message: {req.message}")

    # --- HANDLE INTENTS ---

    if intent == "visual_search_hint":
        return {"reply": "I can definitely help you find similar tiles! Please click the 📎 attachment icon to upload a photo of the tile you're looking for."}

    # 1. TRACK ORDER
    if intent == "track_order":
        # Extract Bill No if present
        bill_no_match = re.search(r'([A-Z0-9]+)', req.message.upper())
        if bill_no_match:
            bill_no = bill_no_match.group(1)
            bills = get_django_data("bills/list/", {"search": bill_no})
            if bills:
                b = bills[0]
                return {"reply": f"📦 Order Status for {b['bill_no']}:\nStatus: {b['status']}\nDate: {b['bill_date'][:10]}\nTotal: ₹{b['grand_total']}"}
        return {"reply": "Please provide your Bill Number (e.g., BILL001) so I can track it for you."}

    # 2. CHECK QUOTATION
    if intent == "check_quote":
        enquiries = get_django_data("enquiries/")
        if enquiries:
            e = enquiries[0]
            reply = f"📝 Quotation Status:\nStatus: {e['status'].title()}\n"
            if e['quotation_price']:
                reply += f"Estimated Price: ₹{e['quotation_price']}\nNotes: {e['quotation_notes']}"
            else:
                reply += "Our team is still reviewing your request. We'll notify you soon."
            return {"reply": reply}
        return {"reply": "You don't have any active quotation requests."}

    # 3. CALCULATE PRICE
    if intent == "calculate_price":
        nums = re.findall(r'\d+', req.message)
        if len(nums) >= 2:
            w, h = float(nums[0]), float(nums[1])
            area = w * h
            price = area * 60 
            return {"reply": f"📐 Calculation for {w}x{h} room:\nTotal Area: {area} sqft\nApproximate Tiles Needed: {round(area/16)} boxes (for 2x2)\nEstimated Cost: ₹{price} (@₹60/sqft)"}
        return {"reply": "Please provide the room dimensions (e.g., 10x12) for estimation."}

    # 4. FAQ
    if intent == "faq":
        for key in FAQS:
            if key in req.message.lower():
                return {"reply": f"ℹ️ {FAQS[key]}"}
        return {"reply": "I can help with delivery, returns, and showroom info. What would you like to know?"}

    # 5. RECOMMENDATIONS & SEARCH (CLIP)
    vec = model.encode([req.message])
    products = search_index(vec, k=5)
    
    if intent == "recommendations":
        prefix = "🌟 Based on your preference, I recommend these premium tiles:"
    else:
        prefix = "🔍 I found these tiles matching your description:"

    reply = format_product_list(products, prefix)
    return {"reply": reply, "products": products}

@app.post("/customer/search_image")
async def search_image(file: UploadFile = File(...)):
    if not model or not index:
        return {"reply": "System initializing or index missing."}

    # 1. Read Image
    print(f"📸 Processing uploaded image for Visual Search: {file.filename}")
    content = await file.read()
    image = Image.open(io.BytesIO(content))
    
    # 2. Generate Image Embedding
    vec = model.encode(image)
    vec = np.expand_dims(vec, axis=0) # batch dimension
    
    # 3. Search
    products = search_index(vec, k=5)
    
    # 4. Format Response
    if not products:
        return {"reply": "I analyzed your photo but couldn't find any visually similar tiles in our current collection."}
    
    reply = "I've analyzed your photo! Here are the tiles that look most similar to what you're looking for:"
    return {"reply": reply, "products": products}
