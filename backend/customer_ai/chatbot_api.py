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
MODEL_NAME = "sentence-transformers/clip-ViT-L-14"
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
MODEL_NAME = "sentence-transformers/clip-ViT-L-14"
INDEX_FILE = "vectorstore/products.index"
META_FILE = "vectorstore/products.pkl"
MIN_SIMILARITY_THRESHOLD = 0.85  # Strict threshold for 100% accuracy feel

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
            # Check if it meets the minimum similarity for "exactness"
            if idx_dist >= MIN_SIMILARITY_THRESHOLD:
                match = metadata[idx_val]
                prod = match["product"]
                prod["similarity_score"] = float(idx_dist)
                prod["match_type"] = match["type"]
                results.append(prod)
    
    # Sort by similarity
    results.sort(key=lambda x: x["similarity_score"], reverse=True)
    
    # Deduplicate results (if multiple augmentations match the same product)
    seen_ids = set()
    unique_results = []
    for r in results:
        if r["id"] not in seen_ids:
            unique_results.append(r)
            seen_ids.add(r["id"])
            
    return unique_results[:k]

def get_django_data(endpoint, params=None):
    try:
        resp = requests.get(f"{DJANGO_BASE_URL}/{endpoint}", params=params, timeout=5)
        if resp.status_code == 200:
            return resp.json()
    except Exception as e:
        print(f"❌ Django API Error: {e}")
    return None

def classify_intent(message):
    message = message.lower().strip()
    
    # 0. Greetings
    if any(k in message for k in ["hi", "hello", "hey", "vanakkam", "vanakam", "good morning", "good evening", "how are you", "yo"]):
        # But if it also contains "tile" or categories, it might be a search
        if not any(k in message for k in ["tile", "floor", "wall", "bathroom", "kitchen"]):
            return "greeting"
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
        return "I couldn't find any matching tiles right now. 😊"
    
    reply = prefix + "\n\n"
    for p in products:
        price_str = f"₹{p['price']}/sqft" if 'price' in p and p['price'] else "Price on request"
        texture_str = f"{p['finish']}" if 'finish' in p else "Natural"
        reply += f"✨ *{p['name']}* ({p['category']})\n   {texture_str} | {price_str} | Size: {p['size']}\n"
    return reply

# --- PERSONALITY & RULES (from training guidelines) ---
ASSISTANT_PERSONALITY = {
    "name": "Florra Customer AI Assistant",
    "tone": "Friendly, Professional, Conversational, Sales-focused",
    "languages": ["English", "Tamil", "Tanglish"],
    "rules": [
        "Communicate naturally like a showroom assistant",
        "Don't sound robotic",
        "Keep responses short",
        "Ask simple follow-up questions",
        "Never discuss backend or technical details"
    ]
}

def get_personality_response(intent, products=None, query=""):
    query = query.lower()
    
    # 1. GREETINGS & INTRO
    if intent == "greeting":
        if "hello" in query:
            return "Hello! 👋 Welcome back to Florra. Designing ungada dream home? I'm here to help!"
        if any(k in query for k in ["vanakkam", "vanakam", "namaste"]):
            return "Vanakkam! 🙏 Florra Tiles ungali anbudan varaverkirathu. Enna tiles choose panna help venum?"
        
        # Proactive suggestion on simple Hi
        trending_reply = "Hi there! 😊 I'm your Florra Assistant.\n\nIppo trend-ah irukura top 3 designs check pannunga: \n\n"
        if products:
            trending_reply += format_product_list(products)
        else:
            trending_reply += "✨ *Carrara White* (Floor)\n✨ *Rustic Oak* (Living)\n✨ *Black Marquina* (Floor)"
        trending_reply += "\n\nEdhavadhu particular room-ku designs venuma? Bathroom or Kitchen?"
        return trending_reply

    # 2. ROOM SPECIFIC ADVICE (Tanglish)
    if any(k in query for k in ["bathroom", "restroom", "toilet"]):
        return "Bathroom ku anti-skid matte tiles thaan best safety. 🚿 Dark colors like Grey or Brown choice pannunga, maintain panna easy ah irukum. Matching wall tiles show pannata?"
    
    if any(k in query for k in ["hall", "living", "veranda"]):
        return "Living room ku glossy large size tiles (2x4 or 4x8) premium look kudukum. ✨ Light colors like Beige or Off-White try pannunga, room perusa theriyum. Options kaatata?"
    
    if any(k in query for k in ["kitchen", "samayal"]):
        return "Kitchen wall tiles stain-resistant-ah irukanum. 🍳 Pattern designs or subway tiles ippo trend. Floor-ku anti-skid matte finish suggest panren."
    
    if any(k in query for k in ["bedroom", "padukai"]):
        return "Bedroom ku wooden finish tiles or light marble designs romba cozy-ah irukum. 🛌 Nalla sleep and comfort kulla look idhu!"

    if any(k in query for k in ["parking", "outdoor", "balcony"]):
        return "Parking and Outdoor-ku heavy-duty 12mm or 16mm thickness tiles thaan correct. 🚗 Rough finish anti-skid tiles select pannunga, rain time la safety-ah irukum."

    # 3. MATERIAL & TEXTURE (Tanglish)
    if "marble" in query:
        return "Marble finish tiles eppovume classic look! 😍 Real marble range la premium vitrified tiles available. Maintenance-free luxury idhu!"
    
    if "wood" in query or "maram" in query:
        return "Wooden finish tiles natural look kudukum. 🌳 Hall or Bedroom ku idhu best choice. Real wood mariye textures available!"

    # 4. BUDGET & PRICE (Tanglish)
    if any(k in query for k in ["budget", "cheap", "low price", "kammi", "koraivana"]):
        return "Kandippa! 😊 Budget-friendly options starting from ₹45/sqft la iruku. Economy range matte and glossy designs kaatava?"
    
    if any(k in query for k in ["premium", "luxury", "best quality", "costly"]):
        return "Luxury search panringala? ✨ Full-body vitrified and Italian marble finish tiles thaan premium choice. Uncompromised quality!"

    # 5. FAQ & SERVICES
    if intent == "faq":
        if any(k in query for k in ["delivery", "time", "eppo"]):
            return "Delivery usually 3-5 days la aayidum across Tamil Nadu. 🚛 Fast delivery panna try panvom!"
        if any(k in query for k in ["return", "replace", "broken"]):
            return "48 hours kulla return pannalam if tiles unused. Broken tiles direct-ah replace aayidum. 👍"
        if any(k in query for k in ["install", "partner", "fixing"]):
            return "Yes, fixing-ku nalla partner contractors irukanga. Area details sonna correct cost solren. 👷‍♂️"
        if any(k in query for k in ["timing", "open", "close"]):
            return "Showroom Morning 10 AM to Night 8 PM open-ah irukum. All days active! 🕙"
        return "Showroom timing, delivery, return policy pathi na solluven. Enna help venum? 😊"

    if intent == "track_order":
        return "Sure! Order track panna ungada Bill Number sollunga. Delivery status check panni solren. 📦"

    if intent == "calculate_price":
        return "Okay! Room dimensions (length x width) sollunga, approximate cost and box count calculate panni solren. 📐"

    # 6. PRODUCT SHOWCASE (FALLBACK)
    if intent == "recommendations":
        reply = "🌟 Based on your choice, check out these premium tiles. Designs trending-ah irukum! \n\n"
        reply += format_product_list(products) if products else "• Carrara Marble\n• Satin Grey\n• Oak Wood"
        reply += "\nUngal budget range sollunga, specific options suggest panren. 😊"
        return reply

    if intent == "search_products":
        if not products:
            return "Sorry, andha design ippo stock la illa. 😅 Vera design or different color try pannalama?"
        
        reply = "Yes, kandippa! 😊 Indha designs unga description ku matching-ah irukum: \n\n"
        reply += format_product_list(products)
        reply += "\nLarge size tiles venuma or standard size?"
        return reply

    return "I'm here to help! Tiles choice, quotations, or tracking orders pathi edhu venum nallum kelunga. 😊"

@app.post("/customer/chat")
def chat(req: ChatRequest):
    if not model or not index:
        return {"reply": "Initializing... Please wait a moment. 😊"}

    intent = classify_intent(req.message)
    print(f"🤖 Personality Intent: {intent} for: {req.message}")

    # For search/recommendations or generic Hi, get some products
    products = []
    if intent in ["search_products", "recommendations", "greeting"]:
        # For greeting, search for "trending"
        search_query = req.message if intent != "greeting" else "trending top quality tiles"
        vec = model.encode([search_query])
        products = search_index(vec, k=3)

    reply = get_personality_response(intent, products, req.message)
    return {"reply": reply, "products": products}

@app.post("/customer/search_image")
async def search_image(file: UploadFile = File(...), message: str = Query(None)):
    if not model or not index:
        return {"reply": "System initializing... Just a second! 😊"}

    content = await file.read()
    image = Image.open(io.BytesIO(content))
    img_vec = model.encode(image)
    
    if message and len(message.strip()) > 1:
        text_vec = model.encode([message])[0]
        final_vec = (img_vec * 0.7) + (text_vec * 0.3)
    else:
        final_vec = img_vec
        
    final_vec = np.expand_dims(final_vec, axis=0)
    products = search_index(final_vec, k=3)
    
    if not products:
        return {"reply": "Photo analyze pannen, but exactly match aagura designs ippo illa. 😅 Similar pattern vera designs show pannalama?"}
    
    reply = "I've analyzed your photo! 😍 Indha tiles unga photo ku romba match aaguthu. Check pannunga:"
    return {"reply": reply, "products": products}
