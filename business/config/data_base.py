from pymongo import MongoClient, server_api


class Database:
    def __init__(self, app=None):
        if app is not None:
            self.init_app(app)

    def init_app(self, app):
        # Inicializa la conexión a MongoDB usando pymongo
        client = MongoClient(
            app.config["MONGO_URI"],
            server_api=server_api.ServerApi(
                version="1", strict=True, deprecation_errors=True
            ),
        )
        app.db = client.get_database(
            "business"
        )  # Crea una referencia a la base de datos
        app.db_security = client.get_database(
            "ms-Security"
        )  # Crea una referencia a la base de datos de seguridad
