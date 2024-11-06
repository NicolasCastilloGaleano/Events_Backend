from flask import Flask, request, jsonify
from modules.event.routes import event_bp
from config.data_base import Database
from config.config import Config
from flask_cors import CORS
import requests

app = Flask(__name__)

app.config.from_object(Config)
db = Database(app)
CORS(app, resources={r"/*": {"origins": ["http://localhost:5173"], "methods": ["GET", "POST", "PUT", "DELETE", "OPTIONS"], "allow_headers": ["Authorization", "Content-Type"]}})

def verificar_permisos(token):
    try:
        url = f"{app.config['SECURITY_URI']}/security/permissions-validation"
        response = requests.post(
            url=url,
            headers={"Authorization": token},
            timeout=5,
            json={"route": request.path, "method": request.method},
        )
        print(response.text)
        return response.status_code == 200
    except requests.RequestException:
        return False

@app.before_request
def check_permissions():
    token = request.headers.get("Authorization")
    if (token and not verificar_permisos(token)) or (
        not token and not (request.path == "/events/list" and request.method == "POST")
    ):
        return jsonify({"error": "No autorizado"}), 401

CORS(event_bp)
app.register_blueprint(event_bp, url_prefix="/events")

if __name__ == "__main__":
    app.run(debug=True)
