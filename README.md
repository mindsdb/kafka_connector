# Mindsbd Kafka Connector

Based on https://github.com/riferrei/kafka-source-connector

## Building + testing

`mvn clean package`

Run kafka in a docker container:
`docker-compose up`

Configure the connector:
`curl -X POST -H "Content-Type:application/json" -d @examples/basic-example.json http://localhost:8083/connectors`

(Note, this will fail at the moment because for unkonw reason kafka "loads" the plugin but then fails to aknowledge it's pressence in the connector list)

Remove it to configure/add one again:
`curl -X DELETE  http://localhost:8083/connectors/basic-example`
