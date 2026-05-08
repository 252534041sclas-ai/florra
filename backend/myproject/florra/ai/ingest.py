import os
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain.embeddings import HuggingFaceEmbeddings
from langchain.vectorstores import FAISS

def run_ingest():
    # Paths
    base_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
    data_path = os.path.join(base_dir, "data", "customer_knowledge.txt")
    vectorstore_path = os.path.join(base_dir, "florra", "ai", "vectorstore")

    print(f"Loading data from: {data_path}")
    
    # Load customer data
    if not os.path.exists(data_path):
        print("Data file not found.")
        return

    with open(data_path, "r", encoding="utf-8") as f:
        text = f.read()

    # Split text
    splitter = RecursiveCharacterTextSplitter(
        chunk_size=400,
        chunk_overlap=40
    )
    chunks = splitter.split_text(text)

    # Embeddings (NO TRAINING)
    print("Loading embeddings model...")
    embeddings = HuggingFaceEmbeddings(
        model_name="sentence-transformers/all-MiniLM-L6-v2"
    )

    # Store in vector DB
    print("Creating FAISS index...")
    db = FAISS.from_texts(chunks, embeddings)
    db.save_local(vectorstore_path)

    print(f"✅ Customer AI knowledge indexed successfully at {vectorstore_path}")

if __name__ == "__main__":
    run_ingest()
