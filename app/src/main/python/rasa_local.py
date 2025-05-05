import os
import json
import logging
from rasa.nlu.model import Interpreter
from rasa.shared.nlu.constants import INTENT, INTENT_NAME_KEY, INTENT_RANKING_KEY
from rasa.core.agent import Agent
from rasa.core.interpreter import RasaNLUInterpreter
from rasa.shared.utils.io import json_to_string

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Get the absolute path to the model directory
MODEL_PATH = os.path.join(os.path.dirname(os.path.dirname(__file__)), "assets", "rasa_model")

def get_rasa_response(user_message):
    """Process user input and return Rasa's response."""
    try:
        # Load the Rasa model
        interpreter = Interpreter.load(MODEL_PATH)
        
        # Parse the user message
        result = interpreter.parse(user_message)
        
        # Extract intent information
        intent_data = {
            "intent": {
                "name": result.get(INTENT, {}).get(INTENT_NAME_KEY, "unknown"),
                "confidence": result.get(INTENT, {}).get("confidence", 0.0)
            },
            "entities": result.get("entities", []),
            "text": result.get("text", ""),
            "response": result.get("response", "")
        }
        
        # Log the response for debugging
        logger.info(f"Rasa response: {json_to_string(intent_data)}")
        
        return intent_data
    except Exception as e:
        logger.error(f"Error in get_rasa_response: {str(e)}")
        return {
            "intent": {
                "name": "error",
                "confidence": 0.0
            },
            "error": str(e)
        }

def initialize_rasa():
    """Initialize Rasa components."""
    try:
        if not os.path.exists(MODEL_PATH):
            logger.error(f"Model path does not exist: {MODEL_PATH}")
            return False
        return True
    except Exception as e:
        logger.error(f"Error initializing Rasa: {str(e)}")
        return False

if __name__ == "__main__":
    # Test the implementation
    if initialize_rasa():
        user_input = "Hello"
        response = get_rasa_response(user_input)
        print(json_to_string(response))
    else:
        print("Failed to initialize Rasa")
