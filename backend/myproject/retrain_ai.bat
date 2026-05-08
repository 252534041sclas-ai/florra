@echo off
cd %~dp0
echo Starting AI Retraining (Indexing Products)...
echo This will scan all active products and generate AI vectors.
python manage.py shell < florra/ai/build_tile_vectors.py
echo.
echo Retraining Complete!
pause
