import os
import json

HOST = os.getenv("MINDSDB_HOST")
if HOST is None:
    raise Exception("Error getting MINDSDB_HOST environment variable.")
BASE_CONFIG = {"api":
                {"http":
                    {"host": HOST, "port": "47334"}},
                 # "mongodb": {"host": "127.0.0.1", "port": "47336"},
                 # "mysql": {"certificate_path": "tests/integration_tests/flows/config/cert.pem", "host": "127.0.0.1", "password": "password", "port": "47335", "user": "mindsdb"}},
               "config_version": "1.4",
               "debug": True,
               "integrations": {},
               "log":
                {"level":
                    {"console": "DEBUG", "file": "DEBUG"}}
              }

with open("config.json", "w") as config:
    config.write(json.dumps(BASE_CONFIG))
