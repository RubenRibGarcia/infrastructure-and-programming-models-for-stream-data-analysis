
provider "google" {
  project = "pragmatic-cat-271217"
  region = var.region
  zone = var.zone
}

terraform {
  backend "gcs" {
    bucket  = "tf-state-thesis"
    prefix  = "terraform/state"
  }
}

/*=============================
  INFRASTRUCTURE
===============================*/

/*=============================
  Giragen Generator
===============================*/

module "misc-infrastructure" {
  source = "./modules/computer_engine"

  name = "misc-infrastructure"

  zone = "europe-west1-b"

  instance_type = "e2-highmem-16"
  instance_count = 1
}

/*=============================
  Apache Flink
===============================*/

module "flink-job-manager" {
  source = "./modules/computer_engine"

  name = "flink-job-manager"

  zone = "europe-west1-b"

  instance_type = "e2-medium"
  instance_count = 1
}

module "flink-task-manager" {
  source = "./modules/computer_engine"

  name = "flink-task-manager"

  zone = "europe-west3-a"

  instance_type = "e2-standard-8"
  instance_count = 4
}

/*=============================
  Apache Kafka
===============================*/

module "kafka-node" {
  source = "./modules/computer_engine"

  name = "kafka-node"

  zone = "europe-west3-a"

  instance_type = "e2-standard-2"
  instance_count = 0
}

module "kafka-stream" {
  source = "./modules/computer_engine"

  name = "kafka-stream"

  zone = "europe-west3-a"

  instance_type = "e2-standard-4"
  instance_count = 0
}

/*=============================
  Apache Storm
===============================*/

module "storm-nimbus" {
  source = "./modules/computer_engine"

  name = "storm-nimbus"

  zone = "europe-west3-a"

  instance_type = "e2-standard-2"
  instance_count = 0
}

module "storm-supervisor" {
  source = "./modules/computer_engine"

  name = "storm-supervisor"

  zone = "europe-west3-a"

  instance_type = "e2-standard-8"
  instance_count = 0
}

/*=============================
  Metrics Dashboard
===============================*/

module "metrics-dashboard" {
  source = "./modules/computer_engine"

  name = "metrics-dashboard"

  zone = "europe-west1-b"

  instance_type = "e2-medium"
  instance_count = 1
}


