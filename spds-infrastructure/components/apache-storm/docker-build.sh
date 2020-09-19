#!/bin/bash

docker build --no-cache -t impads/storm spds-infrastructure/components/apache-storm
docker tag "impads/storm" "impads/storm:2.2.0"

