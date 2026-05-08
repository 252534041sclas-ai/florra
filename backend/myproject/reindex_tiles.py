import os
import django
import pickle
import torch
import torchvision.models as models
import torchvision.transforms as transforms
from PIL import Image
import numpy as np
from django.conf import settings

# 1. Setup Django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myproject.settings')
django.setup()

from florra_admin.models import Product

# 2. Setup AI Model (ResNet50)
print("Loading ResNet50...")
model = models.resnet50(pretrained=True)
model = torch.nn.Sequential(*list(model.children())[:-1])
model.eval()

transform = transforms.Compose([
    transforms.Resize((224,224)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], 
                         std=[0.229, 0.224, 0.225]),
])

def extract_features(image_path):
    try:
        image = Image.open(image_path).convert("RGB")
        image_tensor = transform(image).unsqueeze(0)
        with torch.no_grad():
            features = model(image_tensor)
        return features.squeeze().numpy()
    except Exception as e:
        print(f"Error processing {image_path}: {e}")
        return None

# 3. Process Products
tile_vectors = {}
products = Product.objects.all()
print(f"Found {len(products)} products.")

for product in products:
    if product.image:
        image_path = product.image.path
        if os.path.exists(image_path):
            print(f"Indexing: {product.tile_name} ({product.image.name})")
            features = extract_features(image_path)
            if features is not None:
                # Store using the filename as key (as expected by AIScanView)
                filename = os.path.basename(product.image.name)
                tile_vectors[filename] = features
        else:
            print(f"Image not found for {product.tile_name}: {image_path}")

# 4. Save Vectors
vector_file = os.path.join('florra', 'ai', 'tile_vectors.pkl')
os.makedirs(os.path.dirname(vector_file), exist_ok=True)
with open(vector_file, 'wb') as f:
    pickle.dump(tile_vectors, f)

print(f"Successfully indexed {len(tile_vectors)} tiles.")
