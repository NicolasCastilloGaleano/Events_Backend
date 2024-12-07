from flask import Blueprint
from modules.notification.views import NotificationView
from flask_cors import CORS


# Blueprint setup
notification_bp = Blueprint("notification_bp", __name__)

CORS(
    notification_bp,
    resources={
        r"/*": {
            "origins": ["http://localhost:5173"],
            "methods": ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
            "allow_headers": ["Authorization", "Content-Type"],
        }
    },
)

notification_view = NotificationView()

# Routes
notification_bp.route("/list", methods=["POST"])(notification_view.get_notifications)
notification_bp.route("/create/<event_id>", methods=["POST"])(
    notification_view.create_notification
)
notification_bp.route("/<notification_id>", methods=["GET"])(
    notification_view.get_notification
)
notification_bp.route("/user-notifications/<user_id>", methods=["GET"])(
    notification_view.get_user_notification
)
notification_bp.route("/event-notifications/<event_id>", methods=["GET"])(
    notification_view.get_event_notification
)
notification_bp.route("/<notification_id>", methods=["PUT"])(
    notification_view.update_notification
)
notification_bp.route("/<notification_id>", methods=["DELETE"])(
    notification_view.delete_notification
)
