# Quarkus observability demo - full

This demo consists of 2 services: legume and superhero.

The application will send OTel traces and Micrometer metrics using the standard OTLP protocol.

Logs will be sent to the console in json format, in production. On tests and devmode, classic text logs are used.

JDBC spans are enabled by default.


## How to run using Docker Compose

To build: `mvn clean install`

To start the observability stack:

```
cd docker-compose/basic`
docker-compose up
```

To run in dev mode: `mvn quarkus:dev`

To stop the observability stack: `docker-compose down`

Look at your traces here: http://localhost:16686/ and generate load here: http://localhost:8080/legumes/

Metrics will be displayed in the collector log.

## Deploy on Openshift

Install the 2 operators in Openshift cluster:
1. Red Hat OpenShift distributed tracing platform / Jaeger Operator
2. Community OpenTelemetry Operator

Go to the directory `k8s/basic` and setup the database, tracer and OpenTelemetry collector.
```
# Database
kubectl apply -f database-deployment.yaml
kubectl apply -f database-service.yaml

# Tracer (Jaeger)
kubectl apply -f openshift/jaeger-all-in-one.yaml

# OpenTelemetry collector
kubectl apply -f openshift/otel-collector-cabundle.yaml
kubectl apply -f openshift/otel-collector.yaml
kubectl apply -f openshift/otel-collector-servicemonitor.yaml

```

Build the container image:
1. adjust the `quarkus.container-image.*` settings to your image registry in `src/main/resources/application.properties` of both modules (legume and superhero).
2. build by `mvn clean install`

Apply the manifests using `kubectl` or `oc`:
```
# The generated manifests for deployment and service.
kubectl apply -f quarkus-observability-demo-full-legume/target/kubernetes/kubernetes.yml
kubectl apply -f quarkus-observability-demo-full-superhero/target/kubernetes/kubernetes.yml
# The route in Openshift (work as an ingress in vanila Kubernetes)
kubectl apply -f quarkus-observability-demo-full-legume/src/main/openshift/legume-route.yaml
```

Now we should be able to see metrics in Observe->Metrics and check traces by accessing Jaeger UI defined in the route `src/main/openshift/legume-route.yaml`.
