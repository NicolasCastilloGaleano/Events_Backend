from flask import Blueprint
from modules.event.views import EventView
from flask_cors import CORS


# Blueprint setup
event_bp = Blueprint("event_bp", __name__)


# Habilita CORS en el Blueprint de eventos
CORS(
    event_bp,
    resources={
        r"/*": {
            "origins": ["http://localhost:5173"],
            "methods": ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
            "allow_headers": ["Authorization", "Content-Type"],
        }
    },
)


event_view = EventView()

# Routes
event_bp.route("/list", methods=["POST"])(event_view.get_events)
event_bp.route("/create", methods=["POST"])(event_view.create_event)
event_bp.route("/<event_id>", methods=["GET"])(event_view.get_event)
event_bp.route("/<event_id>", methods=["PUT"])(event_view.update_event)
event_bp.route("/<event_id>", methods=["DELETE"])(event_view.delete_event)
