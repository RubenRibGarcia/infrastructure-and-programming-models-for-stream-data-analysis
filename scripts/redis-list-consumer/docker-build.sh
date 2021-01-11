#!/bin/bash

docker build --no-cache -t impads/redis-list-consumer scripts/redis-list-consumer
docker tag "impads/redis-list-consumer" "impads/redis-list-consumer:0.0.1"

