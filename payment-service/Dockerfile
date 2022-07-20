FROM openjdk:17.0.1-slim

ADD /target/payment-*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
