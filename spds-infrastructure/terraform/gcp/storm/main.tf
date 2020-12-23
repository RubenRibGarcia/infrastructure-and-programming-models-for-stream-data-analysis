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
  source = "../modules/computer_engine"

  name = "misc-infrastructure"

  zone = "europe-west1-b"

  instance_type = "e2-highmem-16"
  instance_count = 1
}
/*=============================
  Apache Storm
===============================*/

module "storm-nimbus" {
  source = "../modules/computer_engine"

  name = "storm-nimbus"

  zone = "europe-west1-b"

  instance_type = "e2-standard-2"
  instance_count = 0
}

module "storm-supervisor" {
  source = "../modules/computer_engine"

  name = "storm-supervisor"

  zone = "europe-west3-a"

  instance_type = "e2-standard-8"
  instance_count = 0
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


