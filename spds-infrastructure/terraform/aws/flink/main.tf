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

locals {
  ssh_authorized_keys = [
    "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQCyF9VzwZOn7wkvahsJ4qvCXyRfrduZt3wdop+8pQwjOkM7aQ4JvZgX9hVT6dQYwoOhF4ub6aQhCDfRUq7vS69SwTHw0RNoyQYkgx2pB0cocs5DFa1XyVbhJOENecam35BK6jqlGYbFnFbPay3x3A08iCkPGIJEmvqJaFDjb8dLIzeh2O5OWVjCUdIho0jP4GXFirzwAQdnARctLSOaF3+k6N0bR5ci3M/rb/xWOUW3nXaRTo9fp6Cz84V7LQji4flcHRTzpgVqGkJzq0VrENUx97RX/vsG9RZvV2OOunwQvtgquaWmrlKyypGn1fCsZiM56nR3j6NKexvPFDDK5ayEIDCA4RVz1qn3j1JrVBdxx9CcOvyNXjoNq8pAwKGGfy7+DQR8mvb1S4cH0MKeWTFcnf5SrU6vKgXuaqx0zXuzuL5nQ2mXYs/ys3K3rR4+QnLb4ntCEWPte2r4ILPwMlSm+84VwM+bgLW+yH2Kgm7dIuzjfHDzYPyBPldicmQxMi7qavAJlSwaCmLPCQ4cYzUOs28ASZfBqzM2pckws4BsVGBGA/8FXxbgz2vKTtSBJQDiz1Ppsmr1ffmRASl2WPtg7L3Tgq4GKtW76yU26syKIwVON/gIzvJoRDnS4Rlfi+zovAdVORTfxH4I4W4waOVrdnT1JXjaYODF0isscJmNDw== a44446@alunos.isel.pt"
  ]
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

  instance_type = "c5a.4xlarge"
  instance_count = 1

  ssh_authorized_keys = local.ssh_authorized_keys

  aws_security_group_id = module.networking.aws_security_group_id
  key_pair_name = module.access.key_pair_name
}

/*=============================
  Apache Flink
===============================*/

module "flink-job-manager" {
  source = "../modules/ec2"

  name = "flink-job-manager"

  instance_type = "c5a.large"
  instance_count = 1

  aws_security_group_id = module.networking.aws_security_group_id
  key_pair_name = module.access.key_pair_name
}

module "flink-task-manager" {
  source = "../modules/ec2"

  name = "flink-task-manager"

  instance_type = "c5a.2xlarge"
  instance_count = 4

  aws_security_group_id = module.networking.aws_security_group_id
  key_pair_name = module.access.key_pair_name
}

/*=============================
  Metrics Dashboard
===============================*/

module "metrics-dashboard" {
  source = "../modules/ec2"

  name = "metrics-dashboard"

  instance_type = "c5a.large"
  instance_count = 1

  aws_security_group_id = module.networking.aws_security_group_id
  key_pair_name = module.access.key_pair_name
}


