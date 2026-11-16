ARG SERVICE=trading-service
FROM maven:3.9.11-eclipse-temurin-21 AS build
ARG SERVICE
WORKDIR /build
COPY . .
RUN mvn -B -pl "${SERVICE}" -am -DskipTests package

FROM eclipse-temurin:21-jre-jammy
ARG SERVICE
WORKDIR /app
RUN groupadd --gid 10001 fourmint && useradd --uid 10001 --gid fourmint --no-create-home fourmint \
    && mkdir data && chown fourmint:fourmint data
COPY --from=build --chown=fourmint:fourmint /build/${SERVICE}/target/${SERVICE}-0.1.0-SNAPSHOT.jar /app/app.jar
USER 10001:10001
ENV PORT=8080
EXPOSE 8080
STOPSIGNAL SIGTERM
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
