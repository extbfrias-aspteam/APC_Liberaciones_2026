import os
from flask import Flask
from app.db.database import db
from app.aval.routes import aval_bp
from dotenv import load_dotenv


def create_app():
    load_dotenv()
    app = Flask(__name__)

    print("PROCREA_DB_URI:", os.environ.get('PROCREA_DB_URI'))
    print("CERO_DB_URI:", os.environ.get('CERO_DB_URI'))
    # Configuración de BDs externas
    app.config['SQLALCHEMY_BINDS'] = {
        'procrea': os.getenv('PROCREA_DB_URI'),
        'cero': os.getenv('CERO_DB_URI')
    }

    db.init_app(app)
    app.register_blueprint(aval_bp)

    return app
