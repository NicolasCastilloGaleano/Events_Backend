from flask import Blueprint
from modules.feedback.views import FeedbackView
from flask_cors import CORS


# Blueprint setup
feedback_bp = Blueprint("feedback_bp", __name__)

CORS(
    feedback_bp,
    resources={
        r"/*": {
            "origins": ["http://localhost:5173"],
            "methods": ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
            "allow_headers": ["Authorization", "Content-Type"],
        }
    },
)

feedback_view = FeedbackView()

# Routes
feedback_bp.route("/list", methods=["POST"])(feedback_view.get_feedbacks)
feedback_bp.route("/create", methods=["POST"])(feedback_view.create_feedback)
feedback_bp.route("/<feedback_id>", methods=["GET"])(feedback_view.get_feedback)
