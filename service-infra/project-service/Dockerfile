# Dockerfile – GraalVM native image multi-stage build
# Stage 1: Build the native image
FROM ghcr.io/graalvm/native-image-community:25 AS builder

WORKDIR /build
ARG MAVEN_OPTS
ARG ARTIFACT_ID

# GraalVM image does not ship Maven – install it
RUN microdnf install -y maven && microdnf clean all

# Cache dependencies
COPY pom.xml ./
RUN mvn dependency:resolve -B

# Copy source and compile native image
COPY src ./src
RUN mvn -Pnative native:compile -DskipTests -B

# Stage 2: Minimal runtime image
FROM gcr.io/distroless/base-debian12
WORKDIR /app
ARG ARTIFACT_ID
COPY --from=builder /build/target/${ARTIFACT_ID} /app/app
EXPOSE 8000
ENTRYPOINT ["/app/app"]