Customer API :

App Name: customer-api

Installations: 
1. Intellij [Any Similar Workbench]
2. JDK 21 [Java installed]
3. Maven 
4. Docker Desktop, Docker Local Registry and Docker
5. Kubernetes
6. Minikube
7. Datadog
8. Helm

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
This Checks if the images are properly created and test the app for observing behavior in next step

Images built above are now pushed to local Repository for later use by Helm to pull them and build Helm charts.
```
   docker run -d -p 5000:5000 --restart always --name registry registry
```

**Observability**: 
Datadog is used to track the metrics, behavior, logging and tracking each call.
Add datadog.properties file as well as required attributes inside Dockerfile.
Download the dd java agent as part of docker build and use docker compose file to run datadog as a sidecar container.
@Trace annotation marked on the method definitions allows datadog to pool in the stats.
Log into Datadog and check for APM. You would find "customer-api" which would provide comprehensive dashboards.


**Kubernetes Deployments using Helm** :
Make sure to start the kubernetes cluster using minikube
1. Create structure using "Helm create kubernetes" command
2. Helm package 
3. Helm install {chart names from above step}
4. Check resources using kubectl get all
5. kubectl port-forward svc/kubernetes 8081:8081

**Integration Test**
Included as part of the test suite. Can be run using mvn test.This can be Automated as part of Jenkins CICD pipeline which gets triggered on every PR merge
to develop branch or main branch.


**Generating Client to Test the API**
1. Download swagger code gen plugin
2. Run this command "swagger-codegen generate -i src/main/resources/open-api/customer-api.yaml -l java -o user-api-client"
3. Client Stubs are generated from above command.

**Test Application**
1. Fetch All Customers
   ```
    curl --location 'http://localhost:8080/api/customers'
   ```
2. Fetch Customer by ID
    ```
     curl --location 'http://localhost:8080/api/customers/9a5e023f-3022-4514-84dd-b1ecf04f051a'
    ```
3. Create New Customer
    ```
      curl --location 'http://localhost:8080/api/customers' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "firstName": "Banner",
    "middleName": "earth",
    "lastName": "Hulk",
    "emailAddress": "hulk@example.com",
    "phoneNumber": "321-456-7820"
    }'
    ```
4. Update Existing Customer Attributes
    ```
      curl --location --request PUT 'http://localhost:8080/api/customers/9a5e023f-3022-4514-84dd-b1ecf04f051a' \
      --header 'Content-Type: application/json' \
      --data-raw '{
      "firstName": "Antman",
      "middleName": "research",
      "lastName": "wasp",
      "emailAddress": "antwasp@example.com",
      "phoneNumber": "123-456-7890"
      }'
    ```
5. Delete Existing Customer
    ```
      curl --location --request DELETE 'http://localhost:8080/api/customers/9a5e023f-3022-4514-84dd-b1ecf04f051a'
    ```
