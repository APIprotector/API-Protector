# FROM alpine/java:21-jdk@sha256:366ad7df4fafeca51290ccd7d6f046cf4bf3aa312e59bb191b4be543b39f25e2
# RUN mkdir /app
# WORKDIR /app
# COPY ./api-protector-cli/target/api-protector-cli-*.jar app.jar
# ENTRYPOINT ["java", "-jar", "app.jar"]

FROM maven:3.8-openjdk-17 AS builder 
WORKDIR /app  
COPY ./api-protector-cli/pom.xml . 
COPY ./api-protector-cli/src ./src 
RUN mvn clean package -DskipTests 

FROM alpine/java:21-jdk@sha256:366ad7df4fafeca51290ccd7d6f046cf4bf3aa312e59bb191b4be543b39f25e2
WORKDIR /app
COPY --from=builder /app/target/api-protector-cli-*.jar app.jar 
ENTRYPOINT ["java", "-jar", "app.jar"]