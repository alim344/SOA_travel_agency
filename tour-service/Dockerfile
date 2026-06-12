FROM eclipse-temurin:17-jdk-alpine

# za grpc plugin
RUN apk add --no-cache gcompat

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src ./src

# brisanje kesa pre build-a
RUN rm -rf ~/.m2/repository/io/grpc/protoc-gen-grpc-java && \
    rm -rf target/protoc-plugins && \
    ./mvnw package -DskipTests

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "target/tour-service-0.0.1-SNAPSHOT.jar"]