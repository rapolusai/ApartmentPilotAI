# Production image for the ApartmentCare backend. Build from the repository root:
#   docker build -t apartmentcare-backend:local -f backend/Dockerfile backend
# No secret is baked in: the database address, password and keys arrive as environment variables at run time
# (see deploy/docker-compose.prod.yml and deploy/README.md).

# ---- build stage: compile and package with the same Java 21 the server runs ----
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
# Download dependencies first so a source-only change reuses this layer.
RUN mvn -B -q -DskipTests dependency:go-offline || true
COPY src ./src
RUN mvn -B -q -DskipTests package && cp target/*.jar /app.jar

# ---- run stage: JRE only, non-root ----
FROM eclipse-temurin:21-jre-jammy
RUN useradd --system --uid 10001 --home /app --shell /usr/sbin/nologin apartmentcare \
 && mkdir -p /app && chown apartmentcare:apartmentcare /app
WORKDIR /app
COPY --from=build --chown=apartmentcare:apartmentcare /app.jar /app/app.jar
USER apartmentcare
# Inside the container the app must listen on all interfaces; the proxy is the only thing that can reach it.
ENV SERVER_ADDRESS=0.0.0.0 SERVER_PORT=8080 JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -fsS http://127.0.0.1:8080/actuator/health || exit 1
ENTRYPOINT ["java","-jar","/app/app.jar"]
