#!/bin/bash -e

exec "$KAFKA_HOME/bin/connect-standalone.sh" "$KAFKA_HOME/config/connect-standalone.properties" "$KAFKA_HOME/config/connect-rabbit-mq.properties"
