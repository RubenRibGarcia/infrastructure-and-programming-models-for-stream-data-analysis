#!/bin/bash

docker build --no-cache -t impads/kafka-connect spds-infrastructure/components/apache-kafka/kafka-connect
docker tag "impads/kafka-connect" "impads/kafka-connect:2.7.0"