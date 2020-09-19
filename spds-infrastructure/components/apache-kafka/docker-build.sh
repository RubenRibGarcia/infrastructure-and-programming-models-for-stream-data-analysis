#!/bin/bash

docker build --no-cache -t impads/kafka spds-infrastructure/components/apache-kafka
docker tag "impads/kafka" "impads/kafka:2.13-2.6.0"

