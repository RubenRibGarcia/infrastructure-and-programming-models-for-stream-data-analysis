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

module "access" {
  source = "../modules/access"
}

module "networking" {
  source = "../modules/networking"
}

/*=============================
  Misc Infrastrutrure
===============================*/

module "misc-infrastructure" {
  source = "../modules/ec2"

  name = "misc-infrastructure"

  zone = "europe-west1-b"

  instance_type = "e2-highmem-16"
  instance_count = 1

  aws_security_group_id = module.networking.aws_security_group_id
  key_pair_name = module.access.key_pair_name
}
/*=============================
  Apache Storm
===============================*/

module "storm-nimbus" {
  source = "../modules/ec2"

  name = "storm-nimbus"

  zone = "europe-west1-b"

  instance_type = "e2-standard-2"
  instance_count = 1

  aws_security_group_id = module.networking.aws_security_group_id
  key_pair_name = module.access.key_pair_name
}

module "storm-supervisor" {
  source = "../modules/ec2"

  name = "storm-supervisor"

  zone = "europe-west3-a"

  instance_type = "e2-standard-8"
  instance_count = 1

  aws_security_group_id = module.networking.aws_security_group_id
  key_pair_name = module.access.key_pair_name
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

  aws_security_group_id = module.networking.aws_security_group_id
  key_pair_name = module.access.key_pair_name
}


