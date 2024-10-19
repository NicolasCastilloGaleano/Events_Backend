from modules.event.models import Event
from pymongo.collection import Collection
from bson import ObjectId
from flask import current_app


class EventService:

    def create_event(self, event_data):
        event = Event(**event_data)
        current_app.db.events.insert_one(event.to_dict())
        return event

    def get_events(self):
        events = current_app.db.events.find()
        return events.to_list()

    def get_event_by_id(self, event_id):
        return current_app.db.events.find_one({"_id": ObjectId(event_id)})

    def update_event(self, event_id, update_data):
        return current_app.db.events.update_one(
            {"_id": ObjectId(event_id)}, {"$set": update_data}
        )

    def delete_event(self, event_id):
        return current_app.db.events.delete_one({"_id": ObjectId(event_id)})
