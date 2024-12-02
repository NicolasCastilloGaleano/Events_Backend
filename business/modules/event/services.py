from pymongo.collection import Collection
from pymongo.database import Database
from flask import current_app, request
import requests
from modules.event.models import Event
from modules.inscription.services import InscriptionService


class EventService:
    inscription_service = InscriptionService()

    def get_collection(self):
        db: Database = current_app.db
        events: Collection = db.get_collection("events")
        return events

    def process_filters(self, filters):
        new_filters = {}

        for key, value in filters.items():
            if isinstance(value, str):
                # Para valores de texto individuales
                new_filters[key] = {"$regex": value, "$options": "i"}
            elif isinstance(value, list) and all(isinstance(v, str) for v in value):
                # Para listas de texto, usamos $in con expresiones regulares insensibles a mayúsculas
                new_filters["$or"] = [
                    {key: {"$regex": v, "$options": "i"}} for v in value
                ]
            else:
                # Dejar otros tipos de filtro sin cambios
                new_filters[key] = value
        return new_filters

    def create_event(self, event_data):
        event = Event(**event_data)
        self.get_collection().insert_one(event.to_dict())
        return event

    def get_events(self, filters):
        filters = self.process_filters(filters)
        events = list(self.get_collection().find(filters))
        for event in events:
            event["_id"] = str(event["_id"])
        return events

    def get_event_by_id(self, event_id):
        return self.get_collection().find_one({"_id": event_id})

    def get_event_users(self, event_id):
        filter = {"event_id": event_id}
        inscriptions = self.inscription_service.get_inscriptions(filter)
        token = request.headers.get("Authorization")
        users = []
        for inscription in inscriptions:
            url = f"{current_app.config['SECURITY_URI']}/users/{inscription["user_id"]}/no_role"
            user = requests.get(
                url=url,
                headers={"Authorization": token},
                timeout=5,
            ).json()["data"]
            users.append(
                {
                    "inscription_id": inscription["_id"],
                    "participated": inscription["participated"],
                    "user_id": user["id"],
                    "email": user["email"],
                    "name": (
                        user["userProfile"]["name"] if user["userProfile"] else None
                    ),
                    "image": (
                        user["userProfile"]["profilePhoto"]
                        if user["userProfile"]
                        else None
                    ),
                }
            )
        return users

    def update_event(self, event_id, update_data):
        return self.get_collection().update_one(
            {"_id": event_id}, {"$set": update_data}
        )

    def delete_event(self, event_id):
        return self.get_collection().delete_one({"_id": event_id})
