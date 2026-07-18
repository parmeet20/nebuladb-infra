# Dockerfile – GraalVM native image multi-stage build
FROM ghcr.io/graalvm/native-image-community:25 AS builder

WORKDIR /build
ARG MAVEN_OPTS
RUN microdnf install -y maven && microdnf clean all

COPY pom.xml ./
RUN mvn dependency:resolve -B
COPY src ./src
RUN mvn -Pnative native:compile -DskipTests -B

FROM gcr.io/distroless/base-debian12
WORKDIR /app
COPY --from=builder /build/target/eureka-server /app/app
EXPOSE 8761
ENTRYPOINT ["/app/app"]