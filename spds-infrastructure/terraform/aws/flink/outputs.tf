output "instances_names" {
    value = "${
        map(
            "${module.misc-infrastructure.service_name}", "${module.misc-infrastructure.instance_name}",
        )
    }"
}
output "instances_public_ips" {
    value = "${
        map(
            "${module.misc-infrastructure.service_name}", "${module.misc-infrastructure.public_ip}",
        )
    }"
}

output "instances_private_ips" {
    value = "${
        map(
            "${module.misc-infrastructure.service_name}", "${module.misc-infrastructure.private_ip}",
        )
    }"
}