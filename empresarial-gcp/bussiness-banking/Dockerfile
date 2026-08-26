FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar banking.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","banking.jar"]
