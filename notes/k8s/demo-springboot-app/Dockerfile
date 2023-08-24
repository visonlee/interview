FROM java:8-jdk-alpine

WORKDIR /app

ARG FILENAME

COPY ${FILENAME} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
