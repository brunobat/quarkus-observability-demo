# Quarkus observability demo - full

This demo consists of 2 services: legume and superhero.

Each time a legume is added, a rest call is performed to the Superhero service and a superhero is created.

The services are fully observable, therefore the Quarkus observability demo - full, and will send OTel traces and Micrometer metrics using the standard OTLP protocol.

Logs will be sent to the console in json format, in production. On tests and devmode, classic text logs are used.

JDBC spans are enabled by default.

## Deployment

We have two services:
* Legumes
* Superhero

One PostgreSQL database.

The Jaeager all-in-one is used as collector, and trace visualiser. Prometheus is used as metrics storage.

The deployments can be done using Docker Compose or Openshift.

### How to run using Docker Compose

We assume you have Docker installed and Docker Compose is available.

To build: `mvn clean install`

To start the observability stack:

```
cd docker-compose/basic_with_prometheus
docker-compose up
```

To run in dev mode: `mvn quarkus:dev`

To stop the observability stack: `docker-compose down`

Look at your traces here: http://localhost:16686/ and generate load here: http://localhost:8080/legumes/

Metrics will be displayed in the collector log.

### How to run using Openshift

Red Hat's [OpenShift](https://www.redhat.com/en/technologies/cloud-computing/openshift) is a container orchestration platform based on Kubernetes. 

We assume you have [kubectl installed](https://kubernetes.io/docs/tasks/tools/) to run commands against Kubernetes clusters. 

You can have a cloud instance and skip to [Deploy on Openshift](#Deploy on Openshift) or you can install a local, single node (cluster) instance.

#### Single node Openshift installation

You will need an amd64 machine with at least 9GB of RAM, 4 CPU cores and 35GB of storage space.

1. Follow the [instructions here](https://access.redhat.com/documentation/en-us/red_hat_openshift_local/2.5/html/getting_started_guide/installation_gsg).
2. TIP: On Linux, logout/login before executing `crc setup` and then  `crc start`. Your user needs access to the libvirt group.
3. Get the pull secret and store it.
4. Login into the Openshift console.
5. Switch to administrator mode.
6. Go to the operator hub. 

#### Deploy on Openshift

Install the 2 operators in Openshift cluster:
1. Red Hat OpenShift distributed tracing platform / Jaeger Operator
2. Community OpenTelemetry Operator

Go to the directory `k8s/basic` and setup the database, tracer and OpenTelemetry collector.
```
# Namespace
kubectl apply -f quarkus-demo-namespace.yaml

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

Apply the manifests using `kubectl` or `oc`, in `quarkus-observability-demo-full` directory.

The generated manifests for deployment and service:
```
kubectl apply -f quarkus-observability-demo-full-legume/target/kubernetes/kubernetes.yml
kubectl apply -f quarkus-observability-demo-full-superhero/target/kubernetes/kubernetes.yml
```
The route in Openshift (work as an ingress in vanila Kubernetes), modify the "host" of the route in the `*-route.yaml` files to the url of your service, for example: `legume.apps-crc.testing`. 

Make sure you have a wildcard DNS entry for the services domain pointing to the cluster ingress router or add entries on your local hosts file.
```
kubectl apply -f quarkus-observability-demo-full-legume/src/main/openshift/legume-route.yaml
kubectl apply -f quarkus-observability-demo-full-superhero/src/main/openshift/superhero-route.yaml
```

Now we should be able to see metrics in Observe->Metrics and check traces by accessing Jaeger UI defined in the route `src/main/openshift/legume-route.yaml`.
