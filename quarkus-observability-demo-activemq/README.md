
# quarkus-observability-demo-activemq

## Introduction

Application with 2 services communicating with JMS messages:
* Legume Service
* Superhero Service

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
docker compose up -d
```

And then you can use the following command to remove all the containers:

```bash
docker compose down
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

## Performance tests with Hyperfoil

[Hyperfoil](https://hyperfoil.io/) is a performance benchmark framework for microservices.

You can find a test setup using both microservices at [hyperfoil-scripts/quarkus-activemq.hf.yaml](hyperfoil-scripts/quarkus-activemq.hf.yaml).

The benchmark starts with a **ramp-up phase** with 2 scenarios:
* Create Legumes, that will send many messages from the Legume Service to the Superhero service.
* Get a random page of Heroes from the SuperHero service

Next there is the **main phase** with 3 scenarios: 
* Create Legumes, that will send many messages from the Legume Service to the Superhero service.
* Get a random page of Legumes from the Legume service
* Get a random page of Heroes from the SuperHero service

### Local benchmark setup

Make sure you started the infrastructure and the microservices.

To install Hyperfoil, please follow instructions here: https://hyperfoil.io/quickstart/quickstart1.html

Once you are inside the Hyperfoil console you can run the following commands, step by step:
```shell
start-local
upload hyperfoil-scripts/quarkus-activemq.hf.yaml
run quarkus-activemq
stats
```
After running `stats` you will see something like this:

```shell
PHASE      METRIC                 THROUGHPUT   REQUESTS  MEAN      p50       p90       p99       p99.9      p99.99     TIMEOUTS  ERRORS  BLOCKED  2xx  3xx  4xx  5xx  CACHE
rampup     createLegumesScenario   6,00 req/s        60  23,99 ms  21,36 ms  33,55 ms  71,30 ms   71,30 ms   71,30 ms         0       0     0 ns   60    0    0    0      0
rampup     getHeroesScenario       6,00 req/s        60   3,65 ms   3,15 ms   3,83 ms  10,03 ms   10,03 ms   10,03 ms         0       0     0 ns   60    0    0    0      0
mainPhase  createLegumesScenario  98,08 req/s       491  26,16 ms  22,28 ms  46,92 ms  95,94 ms  114,29 ms  114,29 ms         0       0     0 ns  491    0    0    0      0
mainPhase  getHeroesScenario      98,08 req/s       491   3,62 ms   3,34 ms   6,13 ms  11,67 ms   29,49 ms   29,49 ms         0       0     0 ns  491    0    0    0      0
mainPhase  getLegumesScenario     98,08 req/s       491   3,62 ms   3,16 ms   6,42 ms  16,32 ms   22,68 ms   22,68 ms         0       0     0 ns  491    0    0    0      0
```

