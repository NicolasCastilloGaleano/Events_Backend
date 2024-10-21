from modules.event.models import Event
from pymongo.collection import Collection
from pymongo.database import Database
from bson import ObjectId
from flask import current_app


class EventService:

    def get_collection(self):
        db: Database = current_app.db
        events: Collection = db.get_collection("events")
        return events

    def create_event(self, event_data):
        event = Event(**event_data)
        self.get_collection().insert_one(event.to_dict())
        return event

    def get_events(self):
        events = list(self.get_collection().find())
        for event in events:
            event["_id"] = str(event["_id"])
        return events

    def get_event_by_id(self, event_id):
        return self.get_collection().find_one({"_id": ObjectId(event_id)})

    def update_event(self, event_id, update_data):
        return self.get_collection().update_one(
            {"_id": ObjectId(event_id)}, {"$set": update_data}
        )

    def delete_event(self, event_id):
        return self.get_collection().delete_one({"_id": ObjectId(event_id)})
