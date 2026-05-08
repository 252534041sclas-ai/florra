@echo off
echo 👁️ Setting up Vision AI (ResNet50)...
echo.
cd c:\Users\suriy\OneDrive\Desktop\my app\myproject

echo 📦 Building Tile Vector Index (First Run Downloads Model)...
python florra/ai/build_tile_vectors.py

echo.
echo ✅ Setup Complete! Run your Django Server now.
pause
