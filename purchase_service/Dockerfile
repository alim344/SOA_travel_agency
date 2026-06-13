FROM eclipse-temurin:17-jdk-alpine
RUN apk add --no-cache gcompat
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN rm -rf ~/.m2/repository/io/grpc/protoc-gen-grpc-java && \
    rm -rf target/protoc-plugins && \
    ./mvnw package -DskipTests
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "target/purchase_service-0.0.1-SNAPSHOT.jar"]