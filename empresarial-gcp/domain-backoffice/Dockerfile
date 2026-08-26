# Usamos una imagen base de Java 21
FROM eclipse-temurin:21-jdk

# Creamos carpeta de la app
WORKDIR /app

# Copiamos el jar
COPY target/*.jar domain-backoffice.jar

# Exponemos puerto (ajustar si tu app usa otro)
EXPOSE 8081

# Comando para iniciar la app
CMD ["java", "-jar", "domain-backoffice.jar"]
