provider "aws" {
  region = var.region
}

terraform {
  backend "s3" {
    bucket = "tf-state-thesis"
    key    = "terraform.tfstate"
    region = "eu-west-1"
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

  instance_type = "c5a.4xlarge"
  instance_count = 1
}

/*=============================
  Apache Flink
===============================*/

module "flink-job-manager" {
  source = "../modules/ec2"

  name = "flink-job-manager"

  instance_type = "c5a.2xlarge"
  instance_count = 1
}

module "flink-task-manager" {
  source = "../modules/ec2"

  name = "flink-task-manager"

  instance_type = "c5a.2xlarge"
  instance_count = 1
}

/*=============================
  Metrics Dashboard
===============================*/

module "metrics-dashboard" {
  source = "../modules/ec2"

  name = "metrics-dashboard"

  instance_type = "c5a.large"
  instance_count = 1
}


