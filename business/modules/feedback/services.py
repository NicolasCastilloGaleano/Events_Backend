from modules.feedback.models import Feedback
from pymongo.collection import Collection
from pymongo.database import Database
from flask import current_app


class FeedbackService:

    def get_collection(self):
        db: Database = current_app.db
        feedbacks: Collection = db.get_collection("feedbacks")
        return feedbacks

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

    def create_feedback(self, feedback_data):
        feedback = Feedback(**feedback_data)
        self.get_collection().insert_one(feedback.to_dict())
        return feedback

    def get_feedbacks(self, filters):
        filters = self.process_filters(filters)
        feedbacks = list(self.get_collection().find(filters))
        for feedback in feedbacks:
            feedback["_id"] = str(feedback["_id"])
        return feedbacks

    def get_feedback_by_id(self, feedback_id):
        return self.get_collection().find_one({"_id": feedback_id})
