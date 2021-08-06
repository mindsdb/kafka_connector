import os
import json

HOST = os.getenv("MINDSDB_HOST", "172.17.0.1")
if HOST is None:
    raise Exception("Error getting MINDSDB_HOST environment variable.")
BASE_CONFIG = {"api":
               {"http":
                   {"host": HOST, "port": "47334"}},
               "config_version": "1.4",
               "debug": True,
               "integrations": {},
               "log":
               {"level":
                {"console": "DEBUG", "file": "DEBUG"}}
               }

with open("config.json", "w") as config:
    config.write(json.dumps(BASE_CONFIG))
