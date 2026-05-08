import torch
import torchvision.models as models
import torchvision.transforms as transforms
from PIL import Image
import numpy as np
import ssl

# Fix for SSL certificate verify failed (common in some environments)
try:
    _create_unverified_https_context = ssl._create_unverified_context
except AttributeError:
    pass
else:
    ssl._create_default_https_context = _create_unverified_https_context

# Pretrained CNN (ResNet50)
# We load it globally to avoid reloading on every request
print("Loading ResNet50 model...")
model = models.resnet50(pretrained=True)
# Remove the classification layer to get feature vectors
model = torch.nn.Sequential(*list(model.children())[:-1])
model.eval()
print("ResNet50 model loaded.")

transform = transforms.Compose([
    transforms.Resize((224,224)),
    transforms.ToTensor(),
    # Normalize with ImageNet mean and std
    transforms.Normalize(mean=[0.485, 0.456, 0.406], 
                         std=[0.229, 0.224, 0.225]),
])

def extract_features(image_path=None, image_file=None):
    """
    Extract features from either a file path or a file-like object (uploaded file).
    """
    try:
        # 1. Check if input is already a PIL Image (e.g. from augmentation)
        if isinstance(image_path, Image.Image):
             image = image_path.convert("RGB")
             
        # 2. Check if input is a path (string)
        elif image_path and isinstance(image_path, str):
            image = Image.open(image_path).convert("RGB")
            
        # 3. Check if input is a file-like object (UploadedFile)
        elif image_file:
            image = Image.open(image_file).convert("RGB")
            
        else:
            raise ValueError("No valid image provided")

        image_tensor = transform(image).unsqueeze(0)

        with torch.no_grad():
            features = model(image_tensor)

        # Flatten: (1, 2048, 1, 1) -> (2048,)
        return features.squeeze().numpy()
    except Exception as e:
        print(f"❌ Error extracting features: {e}")
        return None
