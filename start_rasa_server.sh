#!/bin/bash

# Navigate to Rasa project directory
cd /c/Users/ryanc/StudioProjects/11.56\ 25.2.2025/Assignme

# Activate virtual environment
source venv/Scripts/activate

# Start Rasa server
rasa run --enable-api --cors "*" --port 5005