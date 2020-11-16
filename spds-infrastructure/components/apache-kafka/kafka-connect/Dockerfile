FROM impads/kafka:2.13-2.6.0

# we replace the start command creating a connector instead of starting a kafka broker.
COPY start-kafka.sh /usr/bin/

COPY connectors/spds-kafka-redis-connector-shaded.jar /opt/connectors/
COPY connectors/spds-kafka-rabbitmq-connector-shaded.jar /opt/connectors/

# permissions
RUN chmod a+x /usr/bin/start-kafka.sh