output "service_name" {
  value = var.name
}

output "instance_name" {
  value = google_compute_instance.default.*.name
}

output "public_ip" {
  value = google_compute_instance.default.*.network_interface.0.access_config.0.nat_ip
}

output "private_ip" {
  value = google_compute_instance.default.*.network_interface.0.network_ip
}