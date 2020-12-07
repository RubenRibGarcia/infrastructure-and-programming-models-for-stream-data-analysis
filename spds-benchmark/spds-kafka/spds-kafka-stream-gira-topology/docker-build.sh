#!/bin/bash

docker build --no-cache -t impads/kafka-streams-gira-travels-pattern spds-benchmark/spds-kafka/spds-kafka-stream-gira-topology
docker tag "impads/kafka-streams-gira-travels-pattern" "impads/kafka-streams-gira-travels-pattern:0.0.1"

