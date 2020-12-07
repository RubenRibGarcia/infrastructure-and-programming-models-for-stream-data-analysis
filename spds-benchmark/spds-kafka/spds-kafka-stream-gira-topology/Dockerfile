FROM adoptopenjdk/openjdk11:debianslim-jre

WORKDIR /
COPY target/spds-kafka-stream-gira-topology-shaded.jar kafka-stream-topology.jar
ENTRYPOINT ["java", "-jar", "kafka-stream-topology.jar" , "/opt/isel/thesis/impads/spds-kafka/topology/conf/topology.conf"]