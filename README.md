## MindsDB Kafka Sink Connector

Basic example of a Kafka Connect sink connector. This is a WIP. The current
implementation sends config data to a mindsdb server, in order to start a
consumer in the server pointing to a kafka data stream. The tasks of
this connector only print data to console.


### Requirements
* jdk 11
* maven
* docker-compose

### Building the project locally

With maven installed in your system, simply run:

```shell
$ mvn clean package
```

### Running the project

To start a local kafka server with the connector included, simply run
```shell
$ docker-compose up -d
```

The compose file builds the project's Dockerfile, which takes care of building the project and adding it to kafka connect.
Once all the containers are running, you can manually add the connector in [localhost:9021](http://localhost:9021/clusters).
