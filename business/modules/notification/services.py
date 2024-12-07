from datetime import date
from pymongo.collection import Collection
from pymongo.database import Database
from flask import current_app, request
import requests
from modules.notification.models import Notification


class NotificationService:

    def get_collection(self):
        db: Database = current_app.db
        notifications: Collection = db.get_collection("notifications")
        return notifications

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

    def create_notification(self, event_id, data):
        data["event_id"] = event_id
        today = date.today()
        data["date"] = f"{today.day}/{today.month}/{today.year}"
        notification = Notification(**data)
        self.get_collection().insert_one(notification.to_dict())
        return notification

    def get_notifications(self, filters):
        filters = self.process_filters(filters)
        notifications = list(self.get_collection().find(filters))
        for notification in notifications:
            notification["_id"] = str(notification["_id"])
        return notifications

    def get_notification_by_id(self, notification_id):
        return self.get_collection().find_one({"_id": notification_id})

    def get_event_notifications(self, event_id):
        filter = {"event_id": event_id}
        notifications = list(self.get_collection().find(filter))
        return notifications

    def update_notification(self, notification_id, update_data):
        return self.get_collection().update_one(
            {"_id": notification_id}, {"$set": update_data}
        )

    def delete_notification(self, notification_id):
        return self.get_collection().delete_one({"_id": notification_id})
