# Quarkus observability demo - full

If the demo is too simple, it will not be very realistic, therefore this uses:
* Resteasy
* OpenAPI 
* Hibernate
* Postgres
* OpenTelemetry
* Micrometer
* Json logging


The application will send OTel traces and Micrometer metrics using the standard OTLP protocol.

Logs will be sent to the console in json format, in production. On tests and devmode, classic text logs are used.

JDBC spans are enabled by default.

## How to run

To build: `mvn clean install`

To start the observability stack: 

```
cd docker-compose/simple`
docker-compose up
```

To run in dev mode: `mvn quarkus:dev`

To stop the observability stack: `docker-compose down`

Look at your traces here: http://localhost:16686/ and generate load here: http://localhost:8080/legumes/

Metrics will be displayed in the collector log.
