from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain.embeddings import HuggingFaceEmbeddings
from langchain.vectorstores import FAISS
import os

# Ensure directories exist
os.makedirs("vectorstore", exist_ok=True)

# Load customer data
with open("data/customer_knowledge.txt", "r", encoding="utf-8") as f:
    text = f.read()

# Split text
splitter = RecursiveCharacterTextSplitter(
    chunk_size=400,
    chunk_overlap=40
)
chunks = splitter.split_text(text)

# Embeddings (NO TRAINING)
embeddings = HuggingFaceEmbeddings(
    model_name="sentence-transformers/all-MiniLM-L6-v2"
)

# Store in vector DB
db = FAISS.from_texts(chunks, embeddings)
db.save_local("vectorstore/faiss_index")

print("✅ Customer AI knowledge indexed successfully")
