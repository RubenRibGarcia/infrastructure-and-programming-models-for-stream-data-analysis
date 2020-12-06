FROM bitnami/kafka:2.6.0

COPY connectors/spds-kafka-redis-connector-shaded.jar /opt/bitnami/kafka/connectors/
COPY connectors/spds-kafka-rabbitmq-connector-shaded.jar /opt/bitnami/kafka/connectors/

CMD /opt/bitnami/kafka/bin/connect-standalone.sh /opt/bitnami/kafka/config/connect-standalone.properties /opt/bitnami/kafka/config/connector.properties
