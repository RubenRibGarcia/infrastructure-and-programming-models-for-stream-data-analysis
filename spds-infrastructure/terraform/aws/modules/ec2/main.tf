locals {
    instance_name_prefix = "${var.name}-"
    instance_dns_prefix  = "${var.name}-"
}

resource "aws_security_group" "default" {
    name        = "impads-security-group"
    description = "Allow HTTP, HTTPS and SSH traffic"

    ingress {
        description = "SSH"
        from_port   = 22
        to_port     = 22
        protocol    = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }

    ingress {
        description = "HTTPS"
        from_port   = 443
        to_port     = 443
        protocol    = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }

    ingress {
        description = "HTTP"
        from_port   = 80
        to_port     = 80
        protocol    = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }

    egress {
        from_port   = 0
        to_port     = 0
        protocol    = "-1"
        cidr_blocks = ["0.0.0.0/0"]
    }

    tags = {
        Name = "terraform"
    }
}

resource "aws_key_pair" "default" {
    key_name   = "impads"
    public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQCyF9VzwZOn7wkvahsJ4qvCXyRfrduZt3wdop+8pQwjOkM7aQ4JvZgX9hVT6dQYwoOhF4ub6aQhCDfRUq7vS69SwTHw0RNoyQYkgx2pB0cocs5DFa1XyVbhJOENecam35BK6jqlGYbFnFbPay3x3A08iCkPGIJEmvqJaFDjb8dLIzeh2O5OWVjCUdIho0jP4GXFirzwAQdnARctLSOaF3+k6N0bR5ci3M/rb/xWOUW3nXaRTo9fp6Cz84V7LQji4flcHRTzpgVqGkJzq0VrENUx97RX/vsG9RZvV2OOunwQvtgquaWmrlKyypGn1fCsZiM56nR3j6NKexvPFDDK5ayEIDCA4RVz1qn3j1JrVBdxx9CcOvyNXjoNq8pAwKGGfy7+DQR8mvb1S4cH0MKeWTFcnf5SrU6vKgXuaqx0zXuzuL5nQ2mXYs/ys3K3rR4+QnLb4ntCEWPte2r4ILPwMlSm+84VwM+bgLW+yH2Kgm7dIuzjfHDzYPyBPldicmQxMi7qavAJlSwaCmLPCQ4cYzUOs28ASZfBqzM2pckws4BsVGBGA/8FXxbgz2vKTtSBJQDiz1Ppsmr1ffmRASl2WPtg7L3Tgq4GKtW76yU26syKIwVON/gIzvJoRDnS4Rlfi+zovAdVORTfxH4I4W4waOVrdnT1JXjaYODF0isscJmNDw== a44446@alunos.isel.pt"
}

resource "aws_instance" "default" {
    count         = var.instance_count
    instance_type = var.instance_type
    ami           = "ami-093185e1a0acee74b" ## Debian Buster

    monitoring                  = true
    ebs_optimized               = true
    associate_public_ip_address = true

    vpc_security_group_ids = [
        aws_security_group.default.id
    ]

    key_name = aws_key_pair.default.key_name

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