resource "google_compute_instance" "default" {
    name         = "${var.name}-${count.index}"
    machine_type = var.instance_type
    count        = var.instance_count
    zone         = var.zone
    allow_stopping_for_update = true

    boot_disk {
        initialize_params {
            size = 50
            type = "pd-ssd"
            image = "debian-cloud/debian-10"
        }
    }

    hostname = "${var.name}-${count.index}.thesis.impads.internal"
    network_interface {
        # A default network is created for all GCP projects
        network = "default"
        access_config {
            network_tier = "PREMIUM"
        }
    }

    metadata = {
        ssh-keys = "admin:ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQCyF9VzwZOn7wkvahsJ4qvCXyRfrduZt3wdop+8pQwjOkM7aQ4JvZgX9hVT6dQYwoOhF4ub6aQhCDfRUq7vS69SwTHw0RNoyQYkgx2pB0cocs5DFa1XyVbhJOENecam35BK6jqlGYbFnFbPay3x3A08iCkPGIJEmvqJaFDjb8dLIzeh2O5OWVjCUdIho0jP4GXFirzwAQdnARctLSOaF3+k6N0bR5ci3M/rb/xWOUW3nXaRTo9fp6Cz84V7LQji4flcHRTzpgVqGkJzq0VrENUx97RX/vsG9RZvV2OOunwQvtgquaWmrlKyypGn1fCsZiM56nR3j6NKexvPFDDK5ayEIDCA4RVz1qn3j1JrVBdxx9CcOvyNXjoNq8pAwKGGfy7+DQR8mvb1S4cH0MKeWTFcnf5SrU6vKgXuaqx0zXuzuL5nQ2mXYs/ys3K3rR4+QnLb4ntCEWPte2r4ILPwMlSm+84VwM+bgLW+yH2Kgm7dIuzjfHDzYPyBPldicmQxMi7qavAJlSwaCmLPCQ4cYzUOs28ASZfBqzM2pckws4BsVGBGA/8FXxbgz2vKTtSBJQDiz1Ppsmr1ffmRASl2WPtg7L3Tgq4GKtW76yU26syKIwVON/gIzvJoRDnS4Rlfi+zovAdVORTfxH4I4W4waOVrdnT1JXjaYODF0isscJmNDw== a44446@alunos.isel.pt"
    }
}