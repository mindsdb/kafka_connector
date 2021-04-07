# Mindsbd Kafka Connector

Based on https://github.com/riferrei/kafka-source-connector

## Building + testing

`mvn clean package; cp target/components/packages/you-mindsdb-connect-mindsdb-0.1.0/you-mindsdb-connect-mindsdb-0.1.0/lib/mindsdb-connect-mindsdb-0.1.0.jar .`

Run kafka in a docker container:
`docker-compose up`

Configure the connector:
`curl -X POST -H "Content-Type:application/json" -d @examples/basic-example.json http://localhost:8083/connectors`

(Note, this will fail at the moment because for unkonw reason kafka "loads" the plugin but then fails to aknowledge it's pressence in the connector list)

Remove it to configure/add one again:
`curl -X DELETE  http://localhost:8083/connectors/basic-example`
