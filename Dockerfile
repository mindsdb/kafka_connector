FROM maven:3.8-openjdk-11 AS build
COPY . /tmp
WORKDIR /tmp
RUN mvn clean package

FROM confluentinc/cp-kafka-connect-base:6.1.1
COPY --from=build /tmp/target/components/packages/mindsdb-mindsdb-kafka-connector-*.zip /tmp/mindsdb-connector.zip
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:latest && confluent-hub install --no-prompt /tmp/mindsdb-connector.zip
