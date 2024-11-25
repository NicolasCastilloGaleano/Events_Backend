from bson import ObjectId
from datetime import date


class Inscription:
    def __init__(
        self,
        user_id,
        event_id,
        inscription_date=date.today(),
        participated=False,
        _id=None,
    ):
        self._id = ObjectId() if _id is None else _id
        self.user_id = ObjectId(user_id)
        self.event_id = ObjectId(event_id)
        self.inscription_date = inscription_date
        self.participated = participated

    def to_dict(self):
        return {
            "_id": str(self._id),
            "user_id": str(self.user_id),
            "event_id": str(self.event_id),
            "inscription_date": str(self.inscription_date),
            "participated": self.participated,
        }
