# Quarkus observability demo with the OpenTracing Shim

If the demo is too simple, it will not be very realistic, therefore this uses:
* Resteasy
* OpenAPI 
* Hibernate
* Postgres
* OpenTelemetry


To build: `mvn clean install`

To start the observability stack: 

```
cd docker-compose/simple`
docker-compose up -d
```

To run in dev mode: `mvn quarkus:dev`

To stop the observability stack: `docker-compose down`

Look at your traces here: http://localhost:16686/ and generate load here: http://localhost:8080/legumes/

## Steps to replace OpenTracing by OpenTelemetry
1. Replace  MP OpenTracing implementation by OpenTelemetry + Shim. See this branch for details: https://github.com/brunobat/quarkus-observability-demo/tree/migrate-to-otel

2. Remove the MP OpenTelemetry specific features by OpenTelemetry ones:
   1. @Traced -> @WithSpan 
   2. CDI bridge for traces

3. Replace existing manual OpenTracing instrumentation by OpenTelemetry and remove the Shim. See this branch for details: https://github.com/brunobat/quarkus-observability-demo/tree/manual-otel
