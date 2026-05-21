import requests
import re
import math
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

# FAQ Store (Dramatically Expanded and Showroom Trained)
FAQS = {
    "delivery": "We deliver within **3-5 business days** across Tamil Nadu. Out-of-state deliveries take 7-10 days. 🚛 Safe, padded transportation is guaranteed!",
    "return": "Unopened tiles in original packaging can be returned within **48 hours** of delivery. Any shipping breakages are replaced completely free of charge! 👍",
    "installation": "Yes, we connect you with verified partner contractors for expert tile laying/fixing. Pricing typically ranges from **₹25 to ₹45 per sq.ft** depending on the tile size and site complexity. 👷‍♂️",
    "contact": "You can visit our flagship showroom at Florra Tiles, Avinashi Road, Coimbatore, or WhatsApp our support line directly at +91 98765 43210. 📞",
    "timing": "Our showroom is open from **10:00 AM to 8:00 PM** everyday, including Sundays! 🕙",
    
    # 1. Vitrified vs Ceramic
    "vitrified_vs_ceramic": (
        "💡 *Vitrified vs. Ceramic Tiles*:\n\n"
        "• **Vitrified Tiles**: Made using a hydraulic press mixture of clay, quartz, and feldspar. They are highly dense, have an extremely low water absorption rate (<0.5%), and are scratch and stain resistant. **Best for floors** in high-traffic zones.\n"
        "• **Ceramic Tiles**: Made from baking natural clay at lower temperatures. They have a higher water absorption rate and are lighter, making them **perfect for walls** (like kitchens and bathrooms) where ease of cutting and lower weight matter most."
    ),
    
    # 2. GVT vs PGVT
    "gvt_pgvt": (
        "💡 *GVT (Glazed Vitrified) vs. PGVT (Polished Glazed Vitrified)*:\n\n"
        "• **GVT**: Features a protective glazed layer allowing matte, satin, wooden, or structured textures. Best for heavy foot traffic and outdoor/slip-prone areas.\n"
        "• **PGVT**: Has a highly polished glazed layer giving a magnificent mirror-like high-gloss finish. Perfect for living rooms and bedrooms to add spectacular grandeur, but not recommended for wet bathrooms or high-traffic commercial entryways."
    ),
    
    # 3. Cleaning & Maintenance
    "cleaning_maintenance": (
        "🧼 *Tile Cleaning & Maintenance Guidelines*:\n\n"
        "• **Matte & Structured Tiles**: Sweep regularly to clear dirt from textures. Mop using a pH-neutral cleaner. Avoid harsh floor acids, as they can permanently erode the protective finish.\n"
        "• **High-Gloss/Polished Tiles**: Use a soft microfiber mop with water and a mild glass or tile cleaner to maintain the reflective shine without streaking.\n"
        "• **Grout Lines**: Clean grout joints using a simple paste of baking soda and water or mild specialized grout cleaner. Do NOT use wire brushes."
    ),
    
    # 4. Spacers & Grout
    "spacers_grout": (
        "🧱 *Tile Spacers & Grout Recommendations*:\n\n"
        "• **Spacers**: We highly recommend using **2mm or 3mm spacers** for all floor tile installations. They allow natural building expansion/contraction, preventing tile cracking (buckling) over time, and ensure perfectly straight grout lines.\n"
        "• **Grout**: Use epoxy grout for bathrooms, kitchens, and water-prone areas because it is fully waterproof, anti-bacterial, and non-staining. For dry rooms, standard cement-based grout is perfectly suitable."
    ),
    
    # 5. Making Small Rooms Look Bigger
    "small_room_tips": (
        "🎨 *How to Make a Small Room Look Much Bigger*:\n\n"
        "• **Size**: Use larger formats like **2x4 ft** instead of 2x2 ft. Fewer grout lines visually expand the floor space.\n"
        "• **Color**: Choose light, soft tones such as ivory, beige, soft grey, or crisp white. Light reflections brighten and open up tight spaces.\n"
        "• **Finish**: High-gloss or polished tiles reflect light beautifully, creating the illusion of deep visual space."
    ),
    
    # 6. Site Measurement & Home Visits
    "site_measurement": "📐 Yes! We offer **free site measurements and design consultations** for all orders above **500 sq.ft** in the Coimbatore metropolitan area. Our engineer will visit with samples to measure and guide you on wastage estimation.",
    
    # 7. Customization
    "customization": "🎨 We support customized tile patterning and personalized digital printing (such as custom mosaic wall highlights) for bulk orders exceeding **2,000 sq.ft**. Let us know your style vision!"
}

# Configuration
MODEL_NAME = "sentence-transformers/clip-ViT-L-14"
INDEX_FILE = "vectorstore/products.index"
META_FILE = "vectorstore/products.pkl"
MIN_SIMILARITY_THRESHOLD = 0.55  # Improved softer threshold for comprehensive AI matches

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

def parse_calculation_query(message):
    message = message.lower().strip()
    
    # 1. Look for dimensions like "10x12", "10 x 12", "10 by 12", "10*12"
    dim_match = re.findall(r'(\d+(?:\.\d+)?)\s*(?:x|by|\*)\s*(\d+(?:\.\d+)?)', message)
    if dim_match:
        length = float(dim_match[0][0])
        width = float(dim_match[0][1])
        area = length * width
        return {"area": area, "length": length, "width": width}
    
    # 2. Look for single area like "150 sqft", "150 sq ft", "150 square feet", "150sqft"
    area_match = re.findall(r'(\d+(?:\.\d+)?)\s*(?:sqft|sq\s*ft|square\s*feet|sq\s*meters|sqm)', message)
    if area_match:
        area = float(area_match[0])
        return {"area": area, "length": None, "width": None}
        
    # 3. Look for any numbers if the message contains calculate or area keywords
    if any(k in message for k in ["calculate", "calc", "area", "size", "room", "box", "dimension"]):
        nums = [float(x) for x in re.findall(r'\d+(?:\.\d+)?', message)]
        if len(nums) >= 2:
            length, width = nums[0], nums[1]
            return {"area": length * width, "length": length, "width": width}
        elif len(nums) == 1:
            return {"area": nums[0], "length": None, "width": None}
            
    return None

def classify_intent(message):
    message = message.lower().strip()
    
    # 0. Greetings
    if any(k in message for k in ["hi", "hello", "hey", "vanakkam", "vanakam", "good morning", "good evening", "how are you", "yo"]):
        if not any(k in message for k in ["tile", "floor", "wall", "bathroom", "kitchen"]):
            return "greeting"
            
    if any(k in message for k in ["match", "similar", "iddhu mari", "idhupola", "photo", "image", "upload"]):
        return "visual_search_hint"

    # 1. Order Tracking
    if any(k in message for k in ["order", "track", "delivery", "bill", "invoice", "yenga", "enga", "status", "varum"]):
        return "track_order"
    
    # 2. Quotation Status
    if any(k in message for k in ["quote", "quotation", "enquiry", "status", "update", "kodu"]):
        return "check_quote"
    
    # 3. Price Estimation / Area Calc
    if any(k in message for k in ["cost", "how much", "price", "sqft", "sq ft", "room", "calculate", "calc", "vilai", "rate", "evlo", "evvalavu", "box", "dimension"]):
        if re.search(r'\d+', message) or parse_calculation_query(message) is not None:
            return "calculate_price"
        return "search_products"
    
    # 4. FAQs
    if any(k in message for k in ["return", "policy", "contact", "address", "phone", "install", "panna", "idham", "edam", "ponnu"]):
        return "faq"
    
    # 5. Recommendations / Tile Design / Room Specific Advice
    if any(k in message for k in ["suggest", "recommend", "best", "bathroom", "kitchen", "hall", "living", "bedroom", "pannu", "nalla", "design", "pattern", "color", "style"]):
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
            return "Hello! 👋 Welcome to Florra Tiles. Are you designing your dream home? I am here to help you!"
        if any(k in query for k in ["vanakkam", "vanakam", "namaste", "vanakam"]):
            return "Welcome! 🙏 Florra Tiles warmly welcomes you. Which room are you selecting tiles for today?"
        
        # Proactive suggestion on simple Hi
        trending_reply = "Hi there! 😊 I'm your Florra AI Assistant. Here to help you with tile selections, layouts, and calculations.\n\nCheck out our currently trending top-rated designs: \n\n"
        if products:
            trending_reply += format_product_list(products)
        else:
            trending_reply += "✨ *Carrara White Marble* (Floor)\n✨ *Rustic Oak Plank* (Living)\n✨ *Satin Grey Matte* (Floor)"
        trending_reply += "\n\nWhich area are you designing? Bathroom, Kitchen, Living Room, or Bedroom?"
        return trending_reply

    # 2. TILE DESIGN & ROOM SPECIFIC RECOMMENDATIONS (In premium English)
    if any(k in query for k in ["bathroom", "restroom", "toilet"]):
        return (
            "For bathrooms, safety, slip-resistance, and moisture management are essential. 🚿\n\n"
            "💡 *Design Recommendations*:\n"
            "• *Flooring*: Always select **Anti-skid Matte finish tiles** (like 12x12 inch or 2x2 ft) to guarantee safety when wet.\n"
            "• *Walls*: Use high-gloss Vitrified tiles (like 2x4 ft or 12x18 inch) to reflect light, making the bathroom appear clean, bright, and spacious.\n"
            "• *Style Tip*: A dark textured charcoal or mocha floor paired with crisp light-grey glossy walls creates a spectacular premium modern contrast!\n\n"
            "Would you like me to show you anti-skid bathroom floor tiles?"
        )
    
    if any(k in query for k in ["hall", "living", "veranda"]):
        return (
            "For your living room, you want to create a grand, warm, and highly inviting entrance. ✨\n\n"
            "💡 *Design Recommendations*:\n"
            "• *Size*: Use large-format tiles like **2x4 ft or 4x8 ft** to minimize grout joints, achieving a seamless luxury appearance.\n"
            "• *Finish*: **High-Gloss Glazed Vitrified Tiles (GVT)** offer a gorgeous polished mirror sheen that brightens the room.\n"
            "• *Style Tip*: Classic Italian marble texture (like Carrara or Calacatta) or cozy light beige creates a stunning showroom look.\n\n"
            "Would you like to browse our collection of premium glossy living room tiles?"
        )
    
    if any(k in query for k in ["kitchen", "samayal"]):
        return (
            "Kitchens require durable materials that are fully stain-resistant and easy to clean. 🍳\n\n"
            "💡 *Design Recommendations*:\n"
            "• *Backsplash*: Go with glazed Ceramic tiles in **Subway, Mosaic, or colorful Moroccan patterns** to add a beautiful design statement.\n"
            "• *Flooring*: Choose heavy-duty Matte-finish Vitrified tiles (2x2 ft) as they are highly stain and scratch resistant.\n\n"
            "Shall I recommend our top-selling kitchen wall highlights for you?"
        )
    
    if any(k in query for k in ["bedroom", "padukai"]):
        return (
            "Bedrooms are personal spaces designed for relaxation, quiet, and comfort. 🛌\n\n"
            "💡 *Design Recommendations*:\n"
            "• *Flooring*: **Wood-plank texture tiles** are extremely popular, offering the warm, rich look of hardwood with zero maintenance.\n"
            "• *Finish*: Soft Satin or Matte finishes are preferred here as they reduce glare from lighting and feel highly comforting.\n\n"
            "Would you like to view our wooden-finish bedroom collection?"
        )

    if any(k in query for k in ["parking", "outdoor", "balcony"]):
        return (
            "Balconies, pathways, and parking zones require extreme weather resistance and load durability. 🚗\n\n"
            "💡 *Design Recommendations*:\n"
            "• *Body*: Choose heavy-duty **12mm to 16mm thick vitrified parking tiles** engineered to withstand high vehicle weight.\n"
            "• *Safety*: Structured anti-skid rough finishes are mandatory for absolute safety during rains.\n\n"
            "Would you like to see our highly durable parking tile options?"
        )

    # 3. MATERIAL & TEXTURE RECOMMENDATIONS
    if "marble" in query:
        return (
            "Marble-look vitrified tiles give you a timeless classic luxury aesthetic! 😍\n\n"
            "With modern high-definition printing, they replicate genuine Italian marble perfectly, with absolutely zero sealing or maintenance needed."
        )
    
    if "wood" in query or "maram" in query:
        return (
            "Wood-plank ceramic and vitrified tiles are exceptional! 🌳\n\n"
            "They capture the natural grain, knots, and textures of premium oak or walnut, giving your space a warm, organic design with highly durable properties."
        )

    # 4. BUDGET & PRICE
    if any(k in query for k in ["budget", "cheap", "low price", "kammi", "koraivana"]):
        return "Of course! 😊 We offer high-quality budget-friendly tiles starting from just **₹45/sq.ft**. Shall I display our highly popular and affordable glossy and matte designs?"
    
    if any(k in query for k in ["premium", "luxury", "best quality", "costly"]):
        return "Are you looking for absolute luxury? ✨ Our full-body vitrified collections, Italian marble series, and designer large-format tiles represent our ultimate premium standard. They look spectacular!"

    # 5. DYNAMIC CALCULATIONS & PRICE ESTIMATIONS
    if intent == "calculate_price":
        calc_data = parse_calculation_query(query)
        if calc_data:
            area = calc_data["area"]
            # Standard tile calculations (including 10% wastage)
            wastage_area = area * 1.10
            
            # 1. Standard 2x2 ft tiles (4 sqft per tile)
            tiles_2x2 = math.ceil(wastage_area / 4.0)
            boxes_2x2 = math.ceil(tiles_2x2 / 4.0)  # 4 tiles per box (16 sqft)
            
            # 2. Standard 2x4 ft tiles (8 sqft per tile)
            tiles_2x4 = math.ceil(wastage_area / 8.0)
            boxes_2x4 = math.ceil(tiles_2x4 / 2.0)  # 2 tiles per box (16 sqft)
            
            # 3. Standard 12x12 inch tiles (1 sqft per tile)
            tiles_12x12 = math.ceil(wastage_area / 1.0)
            boxes_12x12 = math.ceil(tiles_12x12 / 10.0) # 10 tiles per box (10 sqft)
            
            dims_str = f"📐 *Dimensions Provided*: {calc_data['length']} ft × {calc_data['width']} ft\n" if calc_data['length'] else ""
            
            return (
                f"Here is your customized tile estimate! 📐\n\n"
                f"{dims_str}"
                f"📊 *Net Floor Area*: **{area:.1f} sq.ft**\n"
                f"⚠️ *With 10% Wastage Buffer*: **{wastage_area:.1f} sq.ft** (Highly recommended for corner cuts and future replacements)\n\n"
                f"💡 *Select Your Preferred Size Option*:\n\n"
                f"🧱 **Option 1: 2x2 ft Tiles (Standard Floor)**\n"
                f"   • Total Tiles Required: *{tiles_2x2} tiles*\n"
                f"   • Total Boxes to Order: **{boxes_2x2} boxes** (16 sqft/box)\n\n"
                f"🧱 **Option 2: 2x4 ft Tiles (Premium Seamless)**\n"
                f"   • Total Tiles Required: *{tiles_2x4} tiles*\n"
                f"   • Total Boxes to Order: **{boxes_2x4} boxes** (16 sqft/box)\n\n"
                f"🧱 **Option 3: 12x12 inch Tiles (Bathroom Floor/Small Room)**\n"
                f"   • Total Tiles Required: *{tiles_12x12} tiles*\n"
                f"   • Total Boxes to Order: **{boxes_12x12} boxes** (10 sqft/box)\n\n"
                f"Would you like me to recommend trending designs or calculate the exact cost for these options?"
            )
        return "I can calculate the exact tiles and boxes you need! 📐 Please provide your room dimensions (e.g. *10x12*) or total square footage (e.g. *250 sqft*)."

    # 6. FAQ & SERVICES
    if intent == "faq":
        query_clean = query.lower()
        if any(k in query_clean for k in ["delivery", "time", "eppo"]):
            return FAQS["delivery"]
        if any(k in query_clean for k in ["return", "replace", "broken", "damage"]):
            return FAQS["return"]
        if any(k in query_clean for k in ["install", "partner", "fixing", "laying", "mason"]):
            return FAQS["installation"]
        if any(k in query_clean for k in ["timing", "open", "close", "hour"]):
            return FAQS["timing"]
        if any(k in query_clean for k in ["difference", "vitrified", "ceramic", "versus", "vs"]):
            return FAQS["vitrified_vs_ceramic"]
        if any(k in query_clean for k in ["gvt", "pgvt"]):
            return FAQS["gvt_pgvt"]
        if any(k in query_clean for k in ["clean", "wash", "maintenance", "stain", "acid"]):
            return FAQS["cleaning_maintenance"]
        if any(k in query_clean for k in ["spacer", "grout", "joint", "gap"]):
            return FAQS["spacers_grout"]
        if any(k in query_clean for k in ["small room", "make bigger", "enlarge", "expand"]):
            return FAQS["small_room_tips"]
        if any(k in query_clean for k in ["visit", "measurement", "site", "home visit"]):
            return FAQS["site_measurement"]
        if any(k in query_clean for k in ["custom", "print"]):
            return FAQS["customization"]
            
        return (
            "I can answer all your technical and design doubts! 📚 Here is what I am trained on:\n\n"
            "• *Tile Care*: 'How do I clean matte tiles?'\n"
            "• *Technology*: 'What is the difference between vitrified and ceramic?'\n"
            "• *Installation*: 'Should I use spacers or grout?'\n"
            "• *Aesthetics*: 'How do I make a small room look bigger?'\n"
            "• *Services*: 'Do you offer site measurements?'\n\n"
            "What would you like to ask? 😊"
        )

    if intent == "track_order":
        return "Absolutely! To track your order status, please enter your **Bill Number** or Invoice ID. 📦"

    # 7. RECOMMENDATIONS & SEARCH SHOWCASE (FALLBACKS)
    if intent == "recommendations":
        reply = "🌟 Based on your style preferences, here are our trending premium tile selections: \n\n"
        reply += format_product_list(products) if products else "• Carrara Marble\n• Satin Grey\n• Oak Wood"
        reply += "\nCould you tell me your target budget or size preferences to refine this?"
        return reply

    if intent == "search_products":
        if not products:
            return "I couldn't find any exact matches for that in our current inventory. 😅 Would you like to try a different color, style, or finish?"
        
        reply = "Certainly! Here are the matches from our current inventory: \n\n"
        reply += format_product_list(products)
        reply += "\nWould you like to compare these or request a quote for any of them?"
        return reply

    return "I am here to help you! Please ask me anything about tile designs, sizes, dynamic calculators, quotations, or order statuses. 😊"

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
        k_val = 12 if intent != "greeting" else 4
        products = search_index(vec, k=k_val)

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
    products = search_index(final_vec, k=12)
    
    if not products:
        return {"reply": "I've analyzed your photo, but I couldn't find an exact match in our current inventory. 😅 Would you like to view some similar trending patterns?"}
    
    reply = "I've analyzed your photo! 😍 Here are the tiles that match your design perfectly. Please check them out:"
    return {"reply": reply, "products": products}
