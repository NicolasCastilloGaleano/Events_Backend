from flask import Flask, request, jsonify
from modules.event.routes import event_bp
from config.data_base import Database
from config.config import Config
import requests

app = Flask(__name__)

app.config.from_object(Config)
db = Database(app)


def verificar_permisos(token):
    try:
        response = requests.get(
            f"{app.config['SECURITY_URI']}/permission_validation",
            headers={"Authorization": token},
            timeout=5,
        )
        return response.status_code == 200
    except requests.RequestException:
        return False


@app.before_request
def check_permissions():
    token = request.headers.get("Authorization")
    if not token or not verificar_permisos(token):
        return jsonify({"error": "No autorizado"}), 401


app.register_blueprint(event_bp, url_prefix="/events")

if __name__ == "__main__":
    app.run(debug=True)
