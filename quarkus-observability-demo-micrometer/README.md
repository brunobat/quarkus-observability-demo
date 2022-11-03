# Quarkus observability demo with Micrometer

If the demo is too simple, it will not be very realistic, therefore this uses:
* Resteasy
* OpenAPI 
* Hibernate
* Postgres
* OpenTelemetry
* Micrometer


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
