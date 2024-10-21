from bson import ObjectId


class Event:
    def __init__(
        self, name, date, location, capacity, description, organizer_id, _id=None
    ):
        self._id = ObjectId() if _id is None else _id
        self.name = name
        self.date = date
        self.location = location
        self.capacity = capacity
        self.description = description
        self.organizer_id = ObjectId(organizer_id)
        self.participants = []

    def to_dict(self):
        return {
            "_id": str(self._id),
            "name": self.name,
            "date": self.date,
            "location": self.location,
            "capacity": self.capacity,
            "description": self.description,
            "organizer_id": str(self.organizer_id),
            "participants": [str(participant) for participant in self.participants],
        }
