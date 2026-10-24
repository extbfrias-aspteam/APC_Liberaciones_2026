import logging
from pythonjsonlogger import jsonlogger

logging.getLogger().setLevel(logging.DEBUG)
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

handler = logging.StreamHandler()
logger.addHandler(handler)