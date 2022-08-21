FROM openjdk:8-alpine

COPY target/uberjar/withings-client.jar /withings-client/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/withings-client/app.jar"]
