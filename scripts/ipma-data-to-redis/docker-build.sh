#!/bin/bash

docker build --no-cache -t impads/ipma-data-to-redis scripts/ipma_data_to_redis
docker tag "impads/ipma-data-to-redis" "impads/ipma-data-to-redis:0.0.1"

