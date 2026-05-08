import os
from langchain.chains import RetrievalQA
from langchain.vectorstores import FAISS
from langchain_huggingface import HuggingFaceEmbeddings, HuggingFacePipeline
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM, pipeline

class RAGService:
    _instance = None
    qa_chain = None

    @classmethod
    def get_instance(cls):
        if cls._instance is None:
            cls._instance = cls()
        return cls._instance

    def __init__(self):
        self.initialize_chain()

    def initialize_chain(self):
        try:
            base_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
            vectorstore_path = os.path.join(base_dir, "florra", "ai", "vectorstore")

            # Load embeddings
            embeddings = HuggingFaceEmbeddings(
                model_name="sentence-transformers/all-MiniLM-L6-v2"
            )

            # Load vector DB
            if os.path.exists(vectorstore_path):
                db = FAISS.load_local(
                    vectorstore_path,
                    embeddings,
                    allow_dangerous_deserialization=True
                )
            else:
                print("Vectorstore not found. Please run ingest.py first.")
                self.qa_chain = None
                return

            # Local LLM (Google Flan-T5-Small for speed/CPU)
            model_id = "google/flan-t5-small"
            tokenizer = AutoTokenizer.from_pretrained(model_id)
            model = AutoModelForSeq2SeqLM.from_pretrained(model_id)

            pipe = pipeline(
                "text2text-generation",
                model=model,
                tokenizer=tokenizer,
                max_length=512,
                temperature=0.1,
                top_p=0.95,
                repetition_penalty=1.15
            )

            local_llm = HuggingFacePipeline(pipeline=pipe)

            self.qa_chain = RetrievalQA.from_chain_type(
                llm=local_llm,
                chain_type="stuff",
                retriever=db.as_retriever(search_kwargs={"k": 2})
            )
            
        except Exception as e:
            print(f"Error initializing RAG Service: {e}")
            self.qa_chain = None

    def get_answer(self, query):
        if self.qa_chain:
            return self.qa_chain.run(query)
        else:
            return "I am currently offline or not configured correctly. Please contact support."
