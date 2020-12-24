output "instances_names" {
    value = "${
        map(
            "${module.kafka-node.service_name}", "${module.kafka-node.instance_name}",
            "${module.kafka-stream.service_name}", "${module.kafka-stream.instance_name}",
            "${module.metrics-dashboard.service_name}", "${module.metrics-dashboard.instance_name}",
            "${module.misc-infrastructure.service_name}", "${module.misc-infrastructure.instance_name}",
        )
    }"
}
output "instances_public_ips" {
    value = "${
        map(
            "${module.kafka-node.service_name}", "${module.kafka-node.public_ip}",
            "${module.kafka-stream.service_name}", "${module.kafka-stream.public_ip}",
            "${module.metrics-dashboard.service_name}", "${module.metrics-dashboard.public_ip}",
            "${module.misc-infrastructure.service_name}", "${module.misc-infrastructure.public_ip}",
        )
    }"
}

output "instances_private_ips" {
    value = "${
        map(
            "${module.kafka-node.service_name}", "${module.kafka-node.private_ip}",
            "${module.kafka-stream.service_name}", "${module.kafka-stream.private_ip}",
            "${module.metrics-dashboard.service_name}", "${module.metrics-dashboard.private_ip}",
            "${module.misc-infrastructure.service_name}", "${module.misc-infrastructure.private_ip}",
        )
    }"
}