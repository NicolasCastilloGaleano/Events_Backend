from modules.inscription.models import Inscription
from pymongo.collection import Collection
from pymongo.database import Database
from bson import ObjectId
from flask import current_app


class InscriptionService:

    def get_collection(self):
        db: Database = current_app.db
        inscriptions: Collection = db.get_collection("inscriptions")
        return inscriptions

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

    def create_inscription(self, inscription_data):
        inscription = Inscription(**inscription_data)
        self.get_collection().insert_one(inscription.to_dict())
        return inscription

    def get_inscriptions(self, filters):
        filters = self.process_filters(filters)
        inscriptions = list(self.get_collection().find(filters))
        for inscription in inscriptions:
            inscription["_id"] = str(inscription["_id"])
        return inscriptions

    def get_inscription_by_id(self, inscription_id):
        return self.get_collection().find_one({"_id": ObjectId(inscription_id)})

    def update_inscription(self, inscription_id, update_data):
        return self.get_collection().update_one(
            {"_id": ObjectId(inscription_id)}, {"$set": update_data}
        )

    def delete_inscription(self, inscription_id):
        return self.get_collection().delete_one({"_id": ObjectId(inscription_id)})
