provider "aws" {
  region = var.region
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
  Apache Storm
===============================*/

module "storm-nimbus" {
  source = "../modules/ec2"

  name = "storm-nimbus"

  zone = "europe-west1-b"

  instance_type = "e2-standard-2"
  instance_count = 0
}

module "storm-supervisor" {
  source = "../modules/ec2"

  name = "storm-supervisor"

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


