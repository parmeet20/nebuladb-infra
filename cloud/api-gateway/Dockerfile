# --------------------------------------------------------------
# Dockerfile – GraalVM 25 native image (single file per module)
# --------------------------------------------------------------
# ---- Stage 1 : build the native executable --------------------
FROM ghcr.io/graalvm/native-image-community:25 AS builder

WORKDIR /build

# --------------------------------------------------------------
# Maven is not bundled in the GraalVM image – install it once
# --------------------------------------------------------------
RUN microdnf install -y maven && microdnf clean all

# --------------------------------------------------------------
# Make the two parent POMs visible to Maven
#   ../pom.xml          -> service‑infra/pom.xml   or cloud/pom.xml
#   ../../pom.xml       -> root pom.xml
# --------------------------------------------------------------
COPY ../pom.xml      ./parent/pom.xml
COPY ../../pom.xml   ./root/pom.xml

# --------------------------------------------------------------
# Cache all dependencies (root + this module)
# --------------------------------------------------------------
COPY pom.xml .
RUN mvn -f pom.xml -B dependency:resolve

# --------------------------------------------------------------
# Copy the rest of the source tree (needed for the native compile)
# --------------------------------------------------------------
COPY src ./src

# --------------------------------------------------------------
# Build the native executable.
#   -Pnative  activates the Spring‑Boot native profile
#   -DskipTests skips the test phase (speed)
# --------------------------------------------------------------
RUN mvn -f pom.xml -Pnative native:compile -DskipTests -B

# --------------------------------------------------------------
# ---- Stage 2 : minimal runtime image (distroless) -------------
# --------------------------------------------------------------
FROM gcr.io/distroless/base-debian12

WORKDIR /app

# The native binary name equals the module’s artifactId.
# It is produced under target/<artifactId>.
# We copy it to a fixed name /app/app for the ENTRYPOINT.
ARG ARTIFACT_ID=api-gateway
COPY --from=builder /build/target/${ARTIFACT_ID} /app/app

EXPOSE 8888
ENTRYPOINT ["/app/app"]