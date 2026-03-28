# ─── Order Service ────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /build

 
COPY pom.xml .
RUN apt-get update && apt-get install -y maven && \
    mvn -q dependency:resolve

 
COPY src ./src

 
RUN mvn -q -DskipTests clean package

 
FROM eclipse-temurin:17-jre
WORKDIR /app
 
COPY --from=builder /build/target/*.jar app.jar

 
RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8083/actuator/health || exit 1

