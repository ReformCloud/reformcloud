# This Dockerfile uses Docker Multi-Stage Builds
# See https://docs.docker.com/engine/userguide/eng-image/multistage-build/
# Requires Docker v17.05

# Use OpenJDK JDK image for intermiediate build
FROM openjdk:8-jdk-slim AS build

# Install packages required for build
RUN apt-get -y update
RUN apt-get install -y --no-install-recommends build-essential
RUN apt-get install -y --no-install-recommends git
RUN mkdir -p /usr/share/man/man1
RUN apt-get install -y --no-install-recommends maven

# Run the build
RUN git submodule update --init
RUN git fetch
RUN git pull
RUN mvn clean package

# Use OpenJDK JRE image for runtime
FROM openjdk:8-jre-slim AS run
LABEL maintainer="ReformCloud <info@reformcloud.systems>"

# Copy runner from build stage
COPY --from=build /src/reformcloud2-runner/target/runner.jar /app/runner.jar

# Ports
EXPOSE 1809
EXPOSE 2008

# Run application
ENTRYPOINT ["java"]
CMD [ "-jar", "/app/runner.jar" ]