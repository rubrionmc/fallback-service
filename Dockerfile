# syntax=docker/dockerfile:1
ARG MODE=release

FROM alpine:latest AS start-script
RUN apk add --no-cache bash
RUN printf '#!/bin/sh\nexec $JAVA_HOME/bin/java $JAVA_OPTS -jar /app/server.jar "$@"\n' > /start.sh && chmod +x /start.sh

FROM gradle:9.1-jdk25 AS builder
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
RUN gradle --version
COPY src ./src
RUN gradle clean build --no-daemon
RUN JAR_NAME=$(ls /app/build/libs/*.jar | head -n1) && \
    echo "Found JAR: $JAR_NAME" && \
    cp "$JAR_NAME" /app.jar

FROM eclipse-temurin:25-jdk AS jlink-base
RUN jlink \
    --output /opt/jdk-custom \
    --add-modules java.base,java.logging,java.management,java.instrument,java.desktop,java.net.http,jdk.unsupported \
    --compress=2 \
    --no-header-files \
    --no-man-pages \
    --strip-debug

FROM debian:bookworm-slim AS final-dev
COPY --from=jlink-base /opt/jdk-custom /opt/jdk-custom
ENV JAVA_HOME=/opt/jdk-custom
ENV PATH="$JAVA_HOME/bin:$PATH"
ENV JAVA_OPTS="-Xms512m -Xmx2g --sun-misc-unsafe-memory-access=allow"
COPY build/libs/*.jar /app/server.jar
COPY --from=start-script /start.sh /start.sh
EXPOSE 25565
ENTRYPOINT ["/start.sh"]

FROM debian:bookworm-slim AS final-release
COPY --from=jlink-base /opt/jdk-custom /opt/jdk-custom
ENV JAVA_HOME=/opt/jdk-custom
ENV PATH="$JAVA_HOME/bin:$PATH"
ENV JAVA_OPTS="-Xms512m -Xmx2g --sun-misc-unsafe-memory-access=allow"
COPY --from=builder /app.jar /app/server.jar
COPY --from=start-script /start.sh /start.sh
EXPOSE 25565
ENTRYPOINT ["/start.sh"]

FROM final-${MODE} AS final