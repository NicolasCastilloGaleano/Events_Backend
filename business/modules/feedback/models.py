from bson import ObjectId
from datetime import date


class Feedback:
    def __init__(
        self,
        user_id,
        event_id,
        stars,
        message=None,
        feedback_date=date.today(),
        _id=None,
    ):
        self._id = ObjectId() if _id is None else _id
        self.user_id = ObjectId(user_id)
        self.event_id = ObjectId(event_id)
        self.feedback_date = feedback_date
        self.message = message
        self.stars = stars

    def to_dict(self):
        return {
            "_id": str(self._id),
            "user_id": str(self.user_id),
            "event_id": str(self.event_id),
            "feedback_date": str(self.feedback_date),
            "message": self.message,
            "stars": self.stars,
        }
