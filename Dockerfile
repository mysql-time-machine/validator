#
# Build stage
#
FROM maven:3.6-jdk-8-slim AS build
COPY . /validator/
WORKDIR /validator/
RUN mvn clean package -DskipTests=true

# #
# # Package stage
# #
FROM openjdk:8-jre-slim
COPY --from=build /validator/service/target/service-0.0.1-SNAPSHOT.jar /opt/validator/
COPY --from=build /validator/version /opt/validator/
COPY --from=build /validator/run.sh /opt/validator/

# # Uncomment this line for standalone dockerized deployment
# COPY --from=build /validator/config.yaml /opt/validator/

# # Use Docker-compose or Kubernetes ConfigMaps to add config config.yaml

WORKDIR /opt/validator/

# # Uncomment this line for standalone dockerized deployment
# ENTRYPOINT ["bash", "run.sh"]
