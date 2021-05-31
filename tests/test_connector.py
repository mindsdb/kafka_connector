import os
import time
import tempfile
import unittest
import json
import uuid
import threading

import requests
import kafka
import pandas as pd



MINDSDB_HOST = os.getenv("MINDSDB_HOST")
MINDSDB_URL = f"http://{MINDSDB_HOST}:47334"
HTTP_API_ROOT = f"{MINDSDB_URL}/api"


INTEGRATION_NAME = 'test_kafka'
KAFKA_PORT = 9092
KAFKA_HOST = "127.0.0.1"

CONNECTION_PARAMS = {"bootstrap_servers": [f"{KAFKA_HOST}:{KAFKA_PORT}"]}
STREAM_SUFFIX = uuid.uuid4()
STREAM_IN = f"test_stream_in_{STREAM_SUFFIX}"
STREAM_OUT = f"test_stream_out_{STREAM_SUFFIX}"
STREAM_IN_TS = f"test_stream_in_ts_{STREAM_SUFFIX}"
STREAM_OUT_TS = f"test_stream_out_ts_{STREAM_SUFFIX}"
DS_NAME = "kafka_test_ds"
PREDICTOR_NAME = "kafka_test_predictor"

CONNECTOR_NAME = "MindsDBConnector"
CONNECTORS_URL = "http://127.0.0.1:9021/api/connect/connect-default/connectors"

def read_stream(stream_name, buf, stop_event):
    consumer = kafka.KafkaConsumer(**CONNECTION_PARAMS, consumer_timeout_ms=1000)
    consumer.subscribe([stream_name])
    while not stop_event.wait(0.5):
        try:
            msg = next(consumer)
            buf.append(json.loads(msg.value))
        except StopIteration:
            pass
    consumer.close()
    print(f"STOPPING READING STREAM {stream_name} THREAD PROPERLY")

def upload_ds(name):
    df = pd.DataFrame({
            'group': ["A" for _ in range(100, 210)],
            'order': [x for x in range(100, 210)],
            'x1': [x for x in range(100,210)],
            'x2': [x*2 for x in range(100,210)],
            'y': [x*3 for x in range(100,210)]
        })
    with tempfile.NamedTemporaryFile(mode='w+', newline='', delete=False) as f:
        df.to_csv(f, index=False)
        f.flush()
        url = f'{HTTP_API_ROOT}/datasources/{name}'
        data = {"source_type": (None, 'file'),
                "file": (f.name, f, 'text/csv'),
                "source": (None, f.name.split('/')[-1]),
                "name": (None, name)}
        res = requests.put(url, files=data)
        res.raise_for_status()

def train_predictor(ds_name, predictor_name):
    params = {
        'data_source_name': ds_name,
        'to_predict': 'y',
        'kwargs': {
            'stop_training_in_x_seconds': 20,
            'join_learn_process': True
        }
    }
    url = f'{HTTP_API_ROOT}/predictors/{predictor_name}'
    res = requests.put(url, json=params)
    res.raise_for_status()

class ConnectorTest(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        upload_ds(DS_NAME)
        train_predictor(DS_NAME, PREDICTOR_NAME)

    @classmethod
    def tearDownClass(cls):
        requests.delete(f"{CONNECTORS_URL}/{CONNECTOR_NAME}")


    def test_1_create_mindsdb_stream_via_connector(self):
        print(f'\nExecuting {self._testMethodName}')

        params = {"name": CONNECTOR_NAME,
                  "config": {
                       "connector.class": "com.mindsdb.kafka.connect.MindsDBSinkConnector",
                       "topics": STREAM_IN,
                       "mindsdb.url": MINDSDB_URL,
                       "kafka.api.host": KAFKA_HOST,
                       "kafka.api.port": KAFKA_PORT,
                       "kafka.api.name": INTEGRATION_NAME,
                       "predictor.name": PREDICTOR_NAME,
                       "output.forecast.topic": STREAM_OUT,
                       # "output.anomaly.topic": "covid_out_anomaly"
                  }
                 }

        headers = {"Content-Type": "application/json"}
        res = requests.post(CONNECTORS_URL, json=params, headers=headers)
        print(res.status_code)
        print(res.text)
        self.assertTrue(res.status_code == 201, res.text)

        time.sleep(10)
        res = requests.get(f"{HTTP_API_ROOT}/config/integrations")
        self.assertTrue('integrations' in res.json(), f"Integration set is empty: {res.json()}")
        integrations = res.json()['integrations']
        self.assertTrue(INTEGRATION_NAME in integrations, f"Can't find {INTEGRATION_NAME} in existing: {integrations}")


    def test_2_making_stream_prediction(self):
        print(f'\nExecuting {self._testMethodName}')
        producer = kafka.KafkaProducer(**CONNECTION_PARAMS)

        # wait when the integration launch created stream
        time.sleep(10)
        predictions = []
        stop_event = threading.Event()
        reading_th = threading.Thread(target=read_stream, args=(STREAM_OUT, predictions, stop_event))
        reading_th.start()
        time.sleep(1)

        for x in range(1, 3):
            when_data = {'x1': x, 'x2': 2*x}
            to_send = json.dumps(when_data)
            producer.send(STREAM_IN, to_send.encode("utf-8"))
        producer.close()
        threshold = time.time() + 30
        while len(predictions) != 2 and time.time() < threshold:
            time.sleep(1)
        stop_event.set()
        self.assertTrue(len(predictions)==2, f"expected 2 predictions but got {len(predictions)}")


if __name__ == "__main__":
    try:
        unittest.main(failfast=True)
        print('Tests passed!')
    except Exception as e:
        print(f'Tests Failed!\n{e}')
