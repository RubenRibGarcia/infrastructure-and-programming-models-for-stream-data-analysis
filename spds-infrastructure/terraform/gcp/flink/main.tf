provider "google" {
  project = var.project
  region = var.region
  zone = var.zone
}
terraform {
  backend "gcs" {
    bucket  = "tf-state-thesis"
    prefix  = "terraform/state/flink"
  }
}

/*=============================
  INFRASTRUCTURE
===============================*/

/*=============================
  Misc Infrastrutrure
===============================*/

module "misc-infrastructure" {
  source = "../modules/computer_engine"

  name = "misc-infrastructure"

  zone = "europe-west1-b"

  instance_type = "e2-highmem-16"
  instance_count = 1
}

/*=============================
  Apache Flink
===============================*/

module "flink-job-manager" {
  source = "../modules/computer_engine"

  name = "flink-job-manager"

  zone = "europe-west1-b"

  instance_type = "e2-standard-2"
  instance_count = 1
}

module "flink-task-manager" {
  source = "../modules/computer_engine"

  name = "flink-task-manager"

  zone = "europe-west3-a"

  instance_type = "e2-standard-8"
  instance_count = 1
}

/*=============================
  Metrics Dashboard
===============================*/

module "metrics-dashboard" {
  source = "../modules/computer_engine"

  name = "metrics-dashboard"

  zone = "europe-west1-b"

  instance_type = "e2-standard-2"
  instance_count = 1
}


