from pymongo import MongoClient


class Database:
    def __init__(self, app=None):
        if app is not None:
            self.init_app(app)

    def init_app(self, app):
        # Inicializa la conexión a MongoDB usando pymongo
        client = MongoClient(app.config["MONGO_URI"])
        app.db = client.get_database(
            "business"
        )  # Crea una referencia a la base de datos
