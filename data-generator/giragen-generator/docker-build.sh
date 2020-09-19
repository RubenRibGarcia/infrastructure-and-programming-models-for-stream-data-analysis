#!/bin/bash

docker build --no-cache -t impads/giragen-generator giragen-generator
docker tag "impads/giragen-generator" "impads/giragen-generator:0.0.1-SNAPSHOT"

