from bson import ObjectId
import json


class Notification:
    def __init__(self, event_id, notification_type, message, date, _id=None):
        self._id = ObjectId() if _id is None else _id
        self.event_id: str = event_id
        self.notification_type: str = notification_type
        self.message: str = message
        self.date = date

    def to_dict(self):
        return {
            "_id": str(self._id),
            "event_id": self.event_id,
            "notification_type": self.notification_type,
            "message": self.message,
            "date": self.date,
        }
