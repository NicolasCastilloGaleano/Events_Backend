from flask import Blueprint
from modules.certificate.views import CertificateView
from flask_cors import CORS


certificate_bp = Blueprint("certificate_bp", __name__)


CORS(
    certificate_bp,
    resources={
        r"/*": {
            "origins": ["http://localhost:5173"],
            "methods": ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
            "allow_headers": ["Authorization", "Content-Type"],
        }
    },
)


certificate_view = CertificateView()

# Routes
certificate_bp.route("/generate/<inscription_id>", methods=["GET"])(
    certificate_view.generate_certificate
)
