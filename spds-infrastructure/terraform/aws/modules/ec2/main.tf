locals {
    instance_name_prefix = "${var.name}-"
    instance_dns_prefix  = "${var.name}-"
}

data "template_file" "cloud_init" {
    count = var.instance_count

    template = file("${path.module}/cloud_config.yaml")

    vars = {
        tpl_hostname            = "${local.instance_name_prefix}${count.index}"
        tpl_ssh_authorized_keys = join("\n      - ", var.ssh_authorized_keys)
    }
}

resource "aws_instance" "default" {
    count         = var.instance_count
    instance_type = var.instance_type
    ami           = "ami-093185e1a0acee74b" ## Debian Buster

    monitoring                  = false
    ebs_optimized               = true
    associate_public_ip_address = true

    availability_zone = var.zone

    vpc_security_group_ids = [
        var.aws_security_group_id
    ]

    key_name = var.key_pair_name

    user_data = element(data.template_file.cloud_init.*.rendered, count.index)

    root_block_device {
        volume_type = "gp2"
        volume_size = 20
        encrypted   = false
    }

    tags = {
        Name        = "${local.instance_name_prefix}${count.index}"
        Type        = var.name
        Provisioner = "terraform"
    }
}