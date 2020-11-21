# DEFAULTS
.DEFAULT_GOAL := help

# HELP
# thanks to https://marmelab.com/blog/2016/02/29/auto-documented-makefile.html
.PHONY: help

help: ## This help.
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

# LOAD LOCAL .ENV VARIABLE FILE & DEFINE OTHER CONSTANTS
ifeq ("$(wildcard .env)","")
	@echo "\033[0;33mWARN: .env not found. Create it based on .env.example if you find it useful\033[0m"
else
include .env
endif

DIR	:= $$(pwd)

SPDS_BENCHMARK_PATH 		:= $(DIR)/spds-benchmark
SPDS_INFRASTRUCTURE_PATH 	:= $(DIR)/spds-infrastructure
DATA_ADAPTERS_PATH			:= $(DIR)/data-adapters
DATA_GENERATOR_PATH 		:= $(DIR)/data-generator

initial-setup: install-giragen-data-adapters install-spds-common

build-project:
	mvn clean compile package -f $(DIR)/pom.xml

bootstrap-run-flink: docker-run-flink-infrastructure docker-run-rabbitmq docker-run-redis docker-run-metrics-monitor
bootstrap-stop-flink: docker-stop-flink-infrastructure docker-stop-rabbitmq docker-stop-redis docker-stop-metrics-monitor

bootstrap-run-storm: docker-run-storm-infrastructure docker-run-rabbitmq docker-run-redis docker-run-metrics-monitor
bootstrap-stop-storm: docker-stop-storm-infrastructure docker-stop-rabbitmq docker-stop-redis docker-stop-metrics-monitor

bootstrap-run-kafka-stream: docker-run-kafka-infrastructure docker-run-rabbitmq docker-run-redis docker-run-kafka-connectors docker-run-metrics-monitor
bootstrap-stop-kafka-stream: docker-stop-kafka-infrastructure docker-stop-rabbitmq docker-stop-redis docker-stop-kafka-connectors docker-stop-metrics-monitor


# ------------- DATA ADAPTER ---------------------
# ------------- GIRAGEN --------------------------

GIRAGEN_DATA_ADAPTERS := $(DATA_ADAPTERS_PATH)/giragen-data-adapters

build-giragen-data-adpaters:
	mvn clean compile package -f $(GIRAGEN_DATA_ADAPTERS)/pom.xml

install-giragen-data-adapters:
	mvn clean install -U -f $(DIR)/pom.xml -pl data-adapters/giragen-data-adapters -am

# ------------- DATA GENERATOR -------------------
# ------------- GIRAGEN --------------------------

GIRAGEN_GENERATOR_PATH := $(DATA_GENERATOR_PATH)/giragen-generator

build-giragen-generator:
	mvn clean compile package -f $(GIRAGEN_GENERATOR_PATH)/pom.xml

docker-run-giragen-generator: ## Run Docker Container for Giragen Generator (Giragen Generator + RabbitMQ)
	docker-compose -f $(GIRAGEN_GENERATOR_PATH)/docker-compose.yml up -d

docker-stop-giragen-generator: ## Stops Docker Container of Giragen Generator
	docker-compose -f $(GIRAGEN_GENERATOR_PATH)/docker-compose.yml down

docker-build-giragen-generator:
	mvn clean compile package -f $(GIRAGEN_GENERATOR_PATH)/pom.xml
	sh $(GIRAGEN_GENERATOR_PATH)/docker-build.sh

docker-push-giragen-generator:
	sh $(GIRAGEN_GENERATOR_PATH)/docker-push.sh

# ------------- SPDS BENCHMARK -------------------

SPDS_INFRASTRUCTURE_BUCKET_BASE	:= ${DIR}/spds-infrastructure/ansible/files

SPDS_FLINK_PATH 	:= ${SPDS_BENCHMARK_PATH}/spds-flink
SPDS_STORM_PATH		:= ${SPDS_BENCHMARK_PATH}/spds-storm
SPDS_KAFKA_PATH		:= ${SPDS_BENCHMARK_PATH}/spds-kafka

APACHE_FLINK_INFRASTRUCTURE_PATH 		:= ${SPDS_INFRASTRUCTURE_PATH}/components/apache-flink
APACHE_STORM_INFRASTRUCTURE_PATH		:= ${SPDS_INFRASTRUCTURE_PATH}/components/apache-storm
APACHE_KAFKA_INFRASTRUCTURE_PATH		:= ${SPDS_INFRASTRUCTURE_PATH}/components/apache-kafka
METRICS_MONITOR_INFRASTRUCTURE_PATH		:= ${SPDS_INFRASTRUCTURE_PATH}/components/metrics-monitor
RABBIT_MQ_INFRASTRUCTURE_PATH			:= ${SPDS_INFRASTRUCTURE_PATH}/components/rabbit-mq
REDIS_INFRASTRUCTURE_PATH				:= ${SPDS_INFRASTRUCTURE_PATH}/components/redis

build-spds-benchmark-project:
	mvn clean compile package -f $(DIR)/pom.xml -pl spds-benchmark -amd

install-spds-common:
	mvn clean install -U -f $(SPDS_BENCHMARK_PATH)/pom.xml -pl spds-common -am

# ------------- SPDS FLINK -----------------

build-flink-topology: ## Maven build SPDS Flink
	mvn clean compile package -f $(SPDS_FLINK_PATH)/pom.xml
	mkdir -p $(SPDS_INFRASTRUCTURE_BUCKET_BASE)/spds-flink/jobs
	cp $(SPDS_FLINK_PATH)/spds-flink-gira-topology/target/spds-flink-gira-topology-shaded.jar \
	$(SPDS_INFRASTRUCTURE_BUCKET_BASE)/spds-flink/jobs/

docker-run-flink-infrastructure:
	docker-compose -f $(APACHE_FLINK_INFRASTRUCTURE_PATH)/docker-compose.yml up -d

docker-stop-flink-infrastructure:
	docker-compose -f $(APACHE_FLINK_INFRASTRUCTURE_PATH)/docker-compose.yml down

docker-execute-flink-topology:
	docker cp $(SPDS_FLINK_PATH)/spds-flink-gira-topology/target/spds-flink-gira-topology-shaded.jar \
	job-manager:/opt/flink/topology.jar
	docker cp $(SPDS_FLINK_PATH)/spds-flink-gira-topology/src/main/resources/application.conf \
	job-manager:/opt/flink/topology.conf
	docker exec job-manager flink run \
	-d /opt/flink/topology.jar \
	--config.file.path /opt/flink/topology.conf

# ------------- SPDS STORM -----------------

docker-build-storm:
	sh $(APACHE_STORM_INFRASTRUCTURE_PATH)/docker-build.sh

docker-push-storm:
	sh $(APACHE_STORM_INFRASTRUCTURE_PATH)/docker-push.sh

build-storm-topology: ## Maven build SPDS Storm
	mvn clean compile package -f $(SPDS_BENCHMARK_PATH)/pom.xml -pl spds-storm -amd
	mkdir -p $(SPDS_INFRASTRUCTURE_BUCKET_BASE)/spds-storm/jobs/
	cp $(SPDS_STORM_PATH)/spds-storm-gira-topology/target/spds-storm-gira-topology-shaded.jar \
 	$(SPDS_INFRASTRUCTURE_BUCKET_BASE)/spds-storm/jobs/

docker-run-storm-infrastructure:
	docker-compose -f $(APACHE_STORM_INFRASTRUCTURE_PATH)/docker-compose.yml up -d

docker-stop-storm-infrastructure:
	docker-compose -f $(APACHE_STORM_INFRASTRUCTURE_PATH)/docker-compose.yml down

docker-execute-storm-topology:
	docker cp $(SPDS_STORM_PATH)/spds-storm-gira-topology/target/spds-storm-gira-topology-shaded.jar \
	nimbus:/apache-storm-2.2.0/topology.jar
	docker cp $(SPDS_STORM_PATH)/spds-storm-gira-topology/src/main/resources/application.conf \
	nimbus:/apache-storm-2.2.0/topology.conf
	#docker exec nimbus storm jar \
#	/apache-storm-2.2.0/topology.jar \
#	org.isel.thesis.impads.storm.streams.topology.MainStormStreamsGiraTopology \
#	/apache-storm-2.2.0/topology.conf
	docker exec nimbus storm jar \
	/apache-storm-2.2.0/topology.jar \
	org.isel.thesis.impads.storm.low_level.topology.MainStormGiraTopology \
	/apache-storm-2.2.0/topology.conf

# ------------- SPDS KAFKA -----------------

build-kafka-stream-topology: ## Maven build SPDS Kafka
	mvn clean compile package -f $(SPDS_BENCHMARK_PATH)/spds-kafka/spds-kafka-stream-gira-topology/pom.xml
	mkdir -p $(SPDS_INFRASTRUCTURE_BUCKET_BASE)/spds-kafka/jobs
	cp $(SPDS_KAFKA_PATH)/spds-kafka-stream-gira-topology/target/spds-kafka-stream-gira-topology-shaded.jar \
	$(SPDS_INFRASTRUCTURE_BUCKET_BASE)/spds-kafka/jobs/
	cp $(SPDS_KAFKA_PATH)/Dockerfile-kafka-stream \
	$(SPDS_INFRASTRUCTURE_BUCKET_BASE)/spds-kafka/jobs/

docker-build-kafka:
	sh $(APACHE_KAFKA_INFRASTRUCTURE_PATH)/docker-build.sh

docker-push-kafka:
	sh $(APACHE_KAFKA_INFRASTRUCTURE_PATH)/docker-push.sh

docker-build-kafka-connect:
	sh $(APACHE_KAFKA_INFRASTRUCTURE_PATH)/kafka-connect/docker-build.sh

docker-push-kafka-connect:
	sh $(APACHE_KAFKA_INFRASTRUCTURE_PATH)/kafka-connect/docker-push.sh

docker-run-kafka-infrastructure:
	docker-compose -f $(APACHE_KAFKA_INFRASTRUCTURE_PATH)/docker-compose.yml up -d

docker-stop-kafka-infrastructure:
	docker-compose -f $(APACHE_KAFKA_INFRASTRUCTURE_PATH)/docker-compose.yml down

docker-run-kafka-connectors:
	docker-compose -f $(APACHE_KAFKA_INFRASTRUCTURE_PATH)/kafka-connect/docker-compose.yml up -d

docker-stop-kafka-connectors:
	docker-compose -f $(APACHE_KAFKA_INFRASTRUCTURE_PATH)/kafka-connect/docker-compose.yml down

docker-execute-kafka-stream-topology:
	docker-compose -f $(SPDS_KAFKA_PATH)/docker-compose.yml up -d --build kafka-stream-topology

docker-stop-kafka-stream-topology:
	docker-compose -f $(SPDS_KAFKA_PATH)/docker-compose.yml stop kafka-stream-topology

# ------------- MISC -----------------

docker-run-rabbitmq: ##Run Docker Container for RabbitMQ
	docker-compose -f $(RABBIT_MQ_INFRASTRUCTURE_PATH)/docker-compose.yml up -d

docker-stop-rabbitmq: ##Stop RabbitMQ Docker Container
	docker-compose -f $(RABBIT_MQ_INFRASTRUCTURE_PATH)/docker-compose.yml down

docker-run-redis: ##Run Docker Redis for RabbitMQ
	docker-compose -f $(REDIS_INFRASTRUCTURE_PATH)/docker-compose.yml up -d

docker-stop-redis: ##Stop Redis Docker Container
	docker-compose -f $(REDIS_INFRASTRUCTURE_PATH)/docker-compose.yml down

docker-run-metrics-monitor:
	docker-compose -f $(METRICS_MONITOR_INFRASTRUCTURE_PATH)/docker-compose.yml up -d

docker-stop-metrics-monitor:
	docker-compose -f $(METRICS_MONITOR_INFRASTRUCTURE_PATH)/docker-compose.yml down

# ------------- SERVICES NETWORK -----------------

docker-create-spds-network: ## Creates SPDS Network for Docker
	docker network create -d bridge spds-network

docker-remove-spds-network: ## Deletes SPDS Network from Docker
	docker network rm spds-network
