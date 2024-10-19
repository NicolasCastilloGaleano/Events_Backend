from flask import Flask
from modules.event.routes import event_bp
from config.data_base import Database
from config.config import Config
import os

app = Flask(__name__)

app.config.from_object(Config)
print(app.config["MONGO_URI"])
print(os.getenv("MONGO_URI"))
db = Database(app)
app.register_blueprint(event_bp, url_prefix="/events")

if __name__ == "__main__":
    app.run(debug=True)
