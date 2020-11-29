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
        ssh-keys = "rgarcia:ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDl2kqzud/5KVUW/vii0OCE2nvzPjC1PbwPRL/OL+hLBUpia8kQQWINFXcUL+/UvG+kbWWel0RWzZ2hKE21v8O6L6ayK220Hk+PdTwH/t8TZ4zdvaJ9eXd/OkQSRtrXV/eZvMDmzLADX2PsmQkiowav5EPE3r21lgjke47o76+PqUPNMrDGusiPLCtRxtqiD5CbZfa/jGylsF+2ZdxBAP4cQyopjW61Ziec8paGJ84amwEFsQ8HQpGg5DqAIA6zpD1jzUA/Zhd0e9btWWYW6GZeIudvCj+D0XfZIjQY7GGXMh8qKUTjgnVuA1oQchvxOdybIc/decHCZArD1cgpA3kVUN9B2PMEpzjCeJiLrrRp9Ita7rbOicoT7/S6g93DjC4VlefORuxoTbFWwyCQTw/jEftUd4wMAyo0FzJYbDXRbN9RbNnNIxDY0/xq2BqBkp/KkOVIEW8cXTL0htYgcywoR6CkSHMf0vYV3UBG98+24roL5hhlNGk12Q+rVdiHATKUltDvz9B4Ydo2V2YfoUIUqJ6dLLoG7EsnGWe0JcllMy+Vwf32XhYGXtApmejNyLNJ4iV5C5aIAFCd/KtPMpxO++U0wjw9aUaeg/yc34C+fCwOOGtEwNvMOaoy/2hZjI+8kIpO7GVy+9gf0rn/ZpOhXlHzO1lW3PLMOeCx15FRuQ== rubenribeirogarcia@gmail.com"
    }
}