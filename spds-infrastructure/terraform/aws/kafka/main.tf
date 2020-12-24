provider "google" {
  project = var.project
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
  Misc Infrastrutrure
===============================*/

module "misc-infrastructure" {
  source = "../modules/ec2"

  name = "misc-infrastructure"

  zone = "europe-west1-b"

  instance_type = "e2-highmem-16"
  instance_count = 1
}


/*=============================
  Apache Kafka
===============================*/

module "kafka-node" {
  source = "../modules/ec2"

  name = "kafka-node"

  zone = "europe-west1-b"

  instance_type = "e2-standard-2"
  instance_count = 0
}

module "kafka-stream" {
  source = "../modules/ec2"

  name = "kafka-stream"

  zone = "europe-west3-a"

  instance_type = "e2-standard-8"
  instance_count = 0
}

/*=============================
  Metrics Dashboard
===============================*/

module "metrics-dashboard" {
  source = "../modules/ec2"

  name = "metrics-dashboard"

  zone = "europe-west1-b"

  instance_type = "e2-standard-2"
  instance_count = 1
}

