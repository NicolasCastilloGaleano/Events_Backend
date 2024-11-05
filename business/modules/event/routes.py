from flask import Blueprint
from modules.event.views import EventView


# Blueprint setup
event_bp = Blueprint("event_bp", __name__)


event_view = EventView()

# Routes
event_bp.route("/list", methods=["POST"])(event_view.get_events)
event_bp.route("/create", methods=["POST"])(event_view.create_event)
event_bp.route("/<event_id>", methods=["GET"])(event_view.get_event)
event_bp.route("/<event_id>", methods=["PUT"])(event_view.update_event)
event_bp.route("/<event_id>", methods=["DELETE"])(event_view.delete_event)
