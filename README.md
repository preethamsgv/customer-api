Customer API :

App Name: customer-api

Installations: 
1. Intellij [Any Similar Workbench]
2. JDK 21 [Java installed]
3. Maven 
4. Docker
5. Kubernetes
6. Helm

Pre-Requisites:
1. Account in Datadog
2. Generate an Datadog API Key
3. Running Datadog agent on the machine where we are running this app

Application embedded in Spring Boot framework and exposes CRUD operations on Customer Entity with its attributes. 
Data persisted using H2 In memory DB and DB interactions handled using Spring Data JPA.

Application Jar is built using "mvn -T 4 clean install -U"

**Containerization**:
Utilize the docker compose file and simply use the below command to run the app and datadog agent as separate containers
- docker-compose up --build

or

App is Containerized using Dockerfile included in the project. Images are built and run by using commands
- docker build -t customer-api .
- docker run --rm -it -p 8080:8080 --name customer-api customer-api




**Observability**: 
Datadog is used to track the metrics, behavior, logging and tracking each call.
Add datadog.properties file as well as required attributes inside Dockerfile.
Download the dd java agent as part of docker build and use docker compose file to run datadog as a sidecar container.
@Trace annotation marked on the method definitions allows datadog to pool in the stats.
Log into Datadog and check for APM. You would find "customer-api" which would provide comprehensive dashboards.




