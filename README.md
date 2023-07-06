# quarkus-observability-demo

This is a collection of small project demonstrating Observability features on quarkus.

The projects based on the [quarkus-quickstarts](https://github.com/quarkusio/quarkus-quickstarts) with a set of REST endpoints, using hibernate, panache and a Postgres database. All use [OpenTelemetry](https://github.com/open-telemetry/opentelemetry-java/blob/main/README.md) for tracing and [Micrometer](https://github.com/micrometer-metrics/micrometer/blob/main/README.md) for metrics.

The reference project is [quarkus-observability-demo-resteasy](quarkus-observability-demo-resteasy/README.md). All others have small variations.

Each project has a specific readme with instructions.
Infraestructure required by the projects is deployed with [Docker Compose](https://docs.docker.com/compose/) and for the [quarkus-observability-demo-full](quarkus-observability-demo-full/README.md) project, with [Openshift](https://www.redhat.com/en/technologies/cloud-computing/openshift).

## Working projects:

### [quarkus-observability-demo-resteasy](quarkus-observability-demo-resteasy/README.md)
Simple Rest. The simplest project. Includes:   
* Resteasy
* OpenAPI
* Hibernate
* Postgres
* OpenTelemetry

### [quarkus-observability-demo-activemq](quarkus-observability-demo-activemq/README.md) 
2 services communication with JMS messages:
* Legume Service
* Superhero Service

The OpenTelemetry context is propagated with the messages. Includes:
* Resteasy
* OpenAPI
* Hibernate
* Postgres
* OpenTelemetry
* Artemis ActiveMQ

### [quarkus-observability-demo-micrometer](quarkus-observability-demo-micrometer/README.md) 

OTel Traces and Micrometer metrics will be sent using the OTLP protocol to the OpenTelemetry Collector. Includes:
* Resteasy
* OpenAPI
* Hibernate
* Postgres
* OpenTelemetry
* Micrometer

### [quarkus-observability-demo-opentracing-shim](quarkus-observability-demo-opentracing-shim/README.md) 
To show how to migrate from MicroProfile OpenTracing to OpenTelemetry. 

Includes a step-by-step instructions. 

### [quarkus-observability-demo-full](quarkus-observability-demo-full/README.md) 
Full observability. Traces, metrics and logs.

2 services communication with REST:
* Legume Service
* Superhero Service

OTel Traces and Micrometer metrics will be sent using the OTLP protocol to the OpenTelemetry Collector plus logs in json format. Includes:
* Resteasy
* OpenAPI
* Hibernate
* Postgres
* OpenTelemetry
* Micrometer
* Json logs

----

Main is compatible with Quarkus 3.
Branch 2.x is compatible with Quarkus 2.
