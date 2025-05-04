FROM alpine/java:21-jdk@sha256:366ad7df4fafeca51290ccd7d6f046cf4bf3aa312e59bb191b4be543b39f25e2
RUN mkdir /app
WORKDIR /app
COPY ./api-protector-cli-2.0.1-SNAPSHOT-*.jar app.jar
RUN chmod 777 ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
