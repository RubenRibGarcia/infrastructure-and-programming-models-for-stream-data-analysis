output "instances_names" {
    value = "${
        map(
            "${module.flink-job-manager.service_name}", "${module.flink-job-manager.instance_name}",
            "${module.flink-task-manager.service_name}", "${module.flink-task-manager.instance_name}",
            "${module.metrics-dashboard.service_name}", "${module.metrics-dashboard.instance_name}",
            "${module.misc-infrastructure.service_name}", "${module.misc-infrastructure.instance_name}",
        )
    }"
}
output "instances_public_ips" {
    value = "${
        map(
            "${module.flink-job-manager.service_name}", "${module.flink-job-manager.public_ip}",
            "${module.flink-task-manager.service_name}", "${module.flink-task-manager.public_ip}",
            "${module.metrics-dashboard.service_name}", "${module.metrics-dashboard.public_ip}",
            "${module.misc-infrastructure.service_name}", "${module.misc-infrastructure.public_ip}",
        )
    }"
}

output "instances_private_ips" {
    value = "${
        map(
            "${module.flink-job-manager.service_name}", "${module.flink-job-manager.private_ip}",
            "${module.flink-task-manager.service_name}", "${module.flink-task-manager.private_ip}",
            "${module.metrics-dashboard.service_name}", "${module.metrics-dashboard.private_ip}",
            "${module.misc-infrastructure.service_name}", "${module.misc-infrastructure.private_ip}",
        )
    }"
}