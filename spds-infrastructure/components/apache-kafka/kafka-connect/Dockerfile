FROM impads/kafka:2.13-2.6.0

# we replace the start command creating a connector instead of starting a kafka broker.
COPY start-kafka.sh /usr/bin/

# permissions
RUN chmod a+x /usr/bin/start-kafka.sh