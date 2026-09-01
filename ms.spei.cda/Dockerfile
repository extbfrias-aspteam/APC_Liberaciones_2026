FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./
COPY local-maven-repo/asp.lib-1.0.jar /tmp/asp.lib-1.0.jar

RUN mvn -q install:install-file \
	-Dfile=/tmp/asp.lib-1.0.jar \
	-DgroupId=com.asp \
	-DartifactId=asp.lib \
	-Dversion=1.0 \
	-Dpackaging=jar

COPY src ./src

RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/ms-asp-pago-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 7071

ENTRYPOINT ["java", "-jar", "app.jar"]
