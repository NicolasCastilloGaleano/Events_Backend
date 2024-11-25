from flask import Blueprint
from modules.inscription.views import InscriptionView
from flask_cors import CORS


# Blueprint setup
inscription_bp = Blueprint("inscription_bp", __name__)

CORS(
    inscription_bp,
    resources={
        r"/*": {
            "origins": ["http://localhost:5173"],
            "methods": ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
            "allow_headers": ["Authorization", "Content-Type"],
        }
    },
)

inscription_view = InscriptionView()

# Routes
inscription_bp.route("/list", methods=["POST"])(inscription_view.get_inscriptions)
inscription_bp.route("/create", methods=["POST"])(inscription_view.create_inscription)
inscription_bp.route("/<inscription_id>", methods=["GET"])(
    inscription_view.get_inscription
)
inscription_bp.route("/<inscription_id>", methods=["PUT"])(
    inscription_view.update_inscription
)
inscription_bp.route("/<inscription_id>", methods=["DELETE"])(
    inscription_view.delete_inscription
)
