########## BUILDER ##########
FROM eclipse-temurin:21-jdk AS builder
LABEL stage=jdkbuilder

ARG profile
RUN echo "El perfil recibido fue $profile"

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

RUN apt-get update && \
    apt-get install -y --no-install-recommends dos2unix && \
    rm -rf /var/lib/apt/lists/*

RUN dos2unix ./mvnw

COPY src ./src
RUN ./mvnw clean package "-Dspring.profiles.active=$profile"


########## RUNTIME ##########
FROM eclipse-temurin:21-jre

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        fontconfig \
        cabextract \
        xfonts-utils \
        wget && \
    mkdir -p /usr/share/fonts/truetype/msttcorefonts && \
    cd /usr/share/fonts/truetype/msttcorefonts && \
    wget -q https://downloads.sourceforge.net/corefonts/arial32.exe && \
    cabextract arial32.exe && \
    rm arial32.exe && \
    fc-cache -f -v && \
    rm -rf /var/lib/apt/lists/*

RUN addgroup usergroup && adduser --ingroup usergroup --disabled-password user
USER user

WORKDIR /app
EXPOSE 8019

COPY --from=builder /app/target/asp-pago-documents.jar /app/asp-pago-documents.jar

ENTRYPOINT ["java", "-Xms128M", "-Xmx2048M", "-jar", "/app/asp-pago-documents.jar"]