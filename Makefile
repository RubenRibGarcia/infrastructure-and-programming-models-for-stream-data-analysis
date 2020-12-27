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

# ------------- LOCAL BOOTSTRAP ---------------------

local-bootstrap-run-flink: docker-run-flink-infrastructure docker-run-misc-infrastructure docker-run-metrics-monitor
local-bootstrap-stop-flink: docker-stop-flink-infrastructure docker-stop-misc-infrastructure docker-stop-metrics-monitor

local-bootstrap-run-storm: docker-run-storm-infrastructure docker-run-misc-infrastructure docker-run-metrics-monitor
local-bootstrap-stop-storm: docker-stop-storm-infrastructure docker-stop-misc-infrastructure docker-stop-metrics-monitor

local-bootstrap-run-kafka-stream: docker-run-kafka-infrastructure docker-run-misc-infrastructure docker-run-kafka-connectors docker-run-metrics-monitor
local-bootstrap-stop-kafka-stream: docker-stop-kafka-infrastructure docker-stop-misc-infrastructure docker-stop-kafka-connectors docker-stop-metrics-monitor

# ------------- REMOTE BOOTSTRAP ---------------------
# ------------- Apache Flink -------------------------
# ------------- Google Cloud Platform ---------------
remote-bootstrap-gcp-run-flink:
	cd $(SPDS_INFRASTRUCTURE_PATH)/terraform/gcp/flink; \
	terraform init; \
	terraform apply -auto-approve
	sleep 3
	cd $(SPDS_INFRASTRUCTURE_PATH)/ansible/gcp; \
	ansible-playbook deploy-flink-infrastructure.yml; \
	ansible-playbook deploy-metrics-dashboard.yml; \
	ansible-playbook deploy-misc-infrastructure.yml; \
	ansible-playbook flink-job-submitter.yml;

remote-bootstrap-gcp-stop-flink:
	cd $(SPDS_INFRASTRUCTURE_PATH)/terraform/gcp/flink; \
	terraform destroy -auto-approve

# ------------- Amazon Web Services ------------------
remote-bootstrap-aws-run-flink:
	cd $(SPDS_INFRASTRUCTURE_PATH)/terraform/aws/flink; \
	terraform init; \
	terraform apply -auto-approve
	sleep 3
	cd $(SPDS_INFRASTRUCTURE_PATH)/ansible/aws; \
	ansible-playbook deploy-flink-infrastructure.yml; \
	ansible-playbook deploy-metrics-dashboard.yml; \
	ansible-playbook deploy-misc-infrastructure.yml; \
	ansible-playbook flink-job-submitter.yml;

remote-bootstrap-aws-stop-flink:
	cd $(SPDS_INFRASTRUCTURE_PATH)/terraform/aws/flink; \
	terraform destroy -auto-approve

# ------------- Apache Storm -------------------------
# ------------- Google Cloud Platform ---------------
remote-bootstrap-gcp-run-storm:
	cd $(SPDS_INFRASTRUCTURE_PATH)/terraform/gcp/storm; \
	terraform init; \
	terraform apply -auto-approve
	sleep 3
	cd $(SPDS_INFRASTRUCTURE_PATH)/ansible/gcp; \
	ansible-playbook deploy-storm-infrastructure.yml; \
	ansible-playbook deploy-metrics-dashboard.yml; \
	ansible-playbook deploy-misc-infrastructure.yml; \
	ansible-playbook storm-job-submitter.yml;

remote-bootstrap-gcp-stop-storm:
	cd $(SPDS_INFRASTRUCTURE_PATH)/terraform/gcp/storm; \
	terraform destroy -auto-approve

# ------------- Amazon Web Services ------------------
remote-bootstrap-aws-run-storm:
	cd $(SPDS_INFRASTRUCTURE_PATH)/terraform/aws/storm; \
	terraform init; \
	terraform apply -auto-approve
	sleep 3
	cd $(SPDS_INFRASTRUCTURE_PATH)/ansible/aws; \
	ansible-playbook deploy-storm-infrastructure.yml; \
	ansible-playbook deploy-metrics-dashboard.yml; \
	ansible-playbook deploy-misc-infrastructure.yml; \
	ansible-playbook storm-job-submitter.yml;

remote-bootstrap-aws-stop-storm:
	cd $(SPDS_INFRASTRUCTURE_PATH)/terraform/aws/storm; \
	terraform destroy -auto-approve

# ------------- Apache Kafka -------------------------
# ------------- Google Cloud Platform ---------------
remote-bootstrap-gcp-run-kafka:
	cd $(SPDS_INFRASTRUCTURE_PATH)/terraform/gcp/kafka; \
	terraform init; \
	terraform apply -auto-approve
	sleep 3
	cd $(SPDS_INFRASTRUCTURE_PATH)/ansible/gcp; \
	ansible-playbook deploy-kafka-infrastructure.yml; \
	ansible-playbook deploy-metrics-dashboard.yml; \
	ansible-playbook deploy-misc-infrastructure.yml; \
	ansible-playbook kafka-stream-job-submitter.yml;

remote-bootstrap-gcp-stop-kafka:
	cd $(SPDS_INFRASTRUCTURE_PATH)/terraform/gcp/kafka; \
	terraform destroy -auto-approve

# ------------- Amazon Web Services ------------------
remote-bootstrap-aws-run-kafka:
	cd $(SPDS_INFRASTRUCTURE_PATH)/terraform/aws/kafka; \
	terraform init; \
	terraform apply -auto-approve
	sleep 3
	cd $(SPDS_INFRASTRUCTURE_PATH)/ansible/aws; \
	ansible-playbook deploy-kafka-infrastructure.yml; \
	ansible-playbook deploy-metrics-dashboard.yml; \
	ansible-playbook deploy-misc-infrastructure.yml; \
	ansible-playbook kafka-stream-job-submitter.yml;

remote-bootstrap-aws-stop-kafka:
	cd $(SPDS_INFRASTRUCTURE_PATH)/terraform/aws/kafka; \
	terraform destroy -auto-approve

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
MISC_INFRASTRUCTURE_PATH				:= ${SPDS_INFRASTRUCTURE_PATH}/components/misc-infrastructure

build-spds-benchmark-project:
	mvn clean compile package -f $(DIR)/pom.xml -pl spds-benchmark -amd

install-spds-common:
	mvn clean install -U -f $(SPDS_BENCHMARK_PATH)/pom.xml -pl spds-common -am

# ------------- SPDS FLINK -----------------

build-spds-flink: ## Maven build spds-flink module
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

build-spds-storm: ## Maven build spds-storm module
	mvn clean compile package -f $(SPDS_BENCHMARK_PATH)/pom.xml -pl spds-storm -amd
	mkdir -p $(SPDS_INFRASTRUCTURE_BUCKET_BASE)/spds-storm/jobs/
	cp $(SPDS_STORM_PATH)/spds-storm-gira-topology/target/spds-storm-gira-topology-shaded.jar \
 	$(SPDS_INFRASTRUCTURE_BUCKET_BASE)/spds-storm/jobs/

docker-build-storm:
	sh $(APACHE_STORM_INFRASTRUCTURE_PATH)/docker-build.sh

docker-push-storm:
	sh $(APACHE_STORM_INFRASTRUCTURE_PATH)/docker-push.sh

docker-run-storm-infrastructure:
	docker-compose -f $(APACHE_STORM_INFRASTRUCTURE_PATH)/docker-compose.yml up -d

docker-stop-storm-infrastructure:
	docker-compose -f $(APACHE_STORM_INFRASTRUCTURE_PATH)/docker-compose.yml down

docker-execute-storm-topology:
	docker cp $(SPDS_STORM_PATH)/spds-storm-gira-topology/target/spds-storm-gira-topology-shaded.jar \
	nimbus:/apache-storm-2.2.0/topology.jar
	docker cp $(SPDS_STORM_PATH)/spds-storm-gira-topology/src/main/resources/application.conf \
	nimbus:/apache-storm-2.2.0/topology.conf
	docker exec nimbus storm jar \
	/apache-storm-2.2.0/topology.jar \
	org.isel.thesis.impads.storm.topology.MainStormGiraTopology \
	/apache-storm-2.2.0/topology.conf

# ------------- SPDS KAFKA -----------------

build-spds-kafka: ## Maven build spds-kafka module
	mvn clean compile package -f $(SPDS_BENCHMARK_PATH)/pom.xml -pl spds-kafka -amd

docker-build-kafka-stream-gira-travels-pattern:
	sh $(SPDS_KAFKA_PATH)/spds-kafka-stream-gira-topology/docker-build.sh

docker-push-kafka-stream-gira-travels-pattern:
	sh $(SPDS_KAFKA_PATH)/spds-kafka-stream-gira-topology/docker-push.sh

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

docker-run-misc-infrastructure: ## Runs RabbitMQ and Redis Container
	docker-compose -f $(MISC_INFRASTRUCTURE_PATH)/docker-compose.yml up -d

docker-stop-misc-infrastructure: ## Stops RabbitMQ and Redis container
	docker-compose -f $(MISC_INFRASTRUCTURE_PATH)/docker-compose.yml down

docker-run-metrics-monitor: ## Runs Metrics Dashboard (Grafana+InfluxDB) and Metrics Agent (Telegraf)
	docker-compose -f $(METRICS_MONITOR_INFRASTRUCTURE_PATH)/docker-compose.yml up -d

docker-stop-metrics-monitor: ## Stops Metrics Dashboard and Metrics Agent
	docker-compose -f $(METRICS_MONITOR_INFRASTRUCTURE_PATH)/docker-compose.yml down

# ------------- SERVICES NETWORK -----------------

docker-create-spds-network: ## Creates SPDS Network for Docker
	docker network create -d bridge spds-network

docker-remove-spds-network: ## Removes SPDS Network from Docker
	docker network rm spds-network
