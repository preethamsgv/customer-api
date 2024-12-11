FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml .
RUN apt-get update && apt-get install -y maven
RUN mvn dependency:resolve
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:21-jdk
WORKDIR /app

# Download Datadog Java agent
RUN curl -L -o dd-java-agent.jar https://dtdg.co/latest-java-tracer

COPY --from=build /app/target/customer-api-*.jar app.jar

ENV DD_AGENT_HOST=datadog \
    DD_SERVICE=customer-api \
    DD_ENV=local \
    DD_VERSION=1.0.0 \
    DD_LOGS_INJECTION=true

# Expose the default Spring Boot port
EXPOSE 8080

# Start the application with the Datadog agent
ENTRYPOINT ["java", "-javaagent:/app/dd-java-agent.jar", \
    "-Ddd.profiling.enabled=true", \
    "-Ddd.logs.injection=true", \
    "-Ddd.trace.analytics.enabled=true", \
    "-jar", "/app/app.jar"]
