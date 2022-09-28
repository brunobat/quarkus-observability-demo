
# quarkus-observability-demo-activemq

## Introduction

Application with 2 services communicating with JMS messages.
If the demo is too simple, it will not be very realistic, therefore this uses:

- Resteasy
- OpenAPI
- Hibernate
- Postgres
- OpenTelemetry
- Artemis ActiveMQ

This demo runs under an Artemis ActiveMQ broker:
https://activemq.apache.org/components/artemis/

Docker image comes from this tutorial:
https://artemiscloud.io/docs/tutorials/deploybasicimage/

## Setup

### Build
Just use Maven to build the project with the following command:

```bash
mvn install
```
### Start infrastructure with Docker Compose

The easiest way to run the the entire system is to use `docker-compose`. This will run the apps, plus some
required infrastructure, like a postgreSQL database and the Artemis message broker.

```bash
cd ../docker-compose/basic-with-artemis
docker-compose up -d
```

And then you can use the following command to remove all the containers:

```bash
docker-compose down
```
### Start the services 

We can use the quarkus dev mode to run the services:

Open a new terminal and run for Legumes on port 8080:
```bash
cd quarkus-observability-demo-activemq-legume 
mvn quarkus:dev
```

Open a new terminal and run for Superheroes on port 8081:
```bash
cd quarkus-observability-demo-activemq-superhero 
mvn quarkus:dev
```

## Run the demo

you can execute this command to create a couple of legumes and trigger message production:
```bash
curl -v -XPOST  http://localhost:8080/legumes/init
```
List Legumes: 
```bash
curl -v -XPOST  http://localhost:8080/legumes/
```
List superheroes:
```bash
curl -v -XPOST  http://localhost:8081/heroes/
```
See traces in Jaeger: http://localhost:16686/

### Problems with containers?

Portainer to the help at: http://localhost:9000/

user: admin

pass: portainer

## Java agent alternative
Optionally with auto-instrumentation, using the java agent:
`java -javaagent:/PATH-TO/opentelemetry-javaagent.jar -jar quarkus-run.jar`

The java agent can be downloaded from: https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar


