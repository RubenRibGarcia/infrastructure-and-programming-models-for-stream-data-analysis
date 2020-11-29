output "instances_names" {
    value = "${
        map(
            "${module.flink-job-manager.service_name}", "${module.flink-job-manager.instance_name}",
            "${module.flink-task-manager.service_name}", "${module.flink-task-manager.instance_name}",
            "${module.kafka-node.service_name}", "${module.kafka-node.instance_name}",
            "${module.kafka-stream.service_name}", "${module.kafka-stream.instance_name}",
            "${module.storm-nimbus.service_name}", "${module.storm-nimbus.instance_name}",
            "${module.storm-supervisor.service_name}", "${module.storm-supervisor.instance_name}",
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
            "${module.kafka-node.service_name}", "${module.kafka-node.public_ip}",
            "${module.kafka-stream.service_name}", "${module.kafka-stream.public_ip}",
            "${module.storm-nimbus.service_name}", "${module.storm-nimbus.public_ip}",
            "${module.storm-supervisor.service_name}", "${module.storm-supervisor.public_ip}",
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
            "${module.kafka-node.service_name}", "${module.kafka-node.private_ip}",
            "${module.kafka-stream.service_name}", "${module.kafka-stream.private_ip}",
            "${module.storm-nimbus.service_name}", "${module.storm-nimbus.private_ip}",
            "${module.storm-supervisor.service_name}", "${module.storm-supervisor.private_ip}",
            "${module.metrics-dashboard.service_name}", "${module.metrics-dashboard.private_ip}",
            "${module.misc-infrastructure.service_name}", "${module.misc-infrastructure.private_ip}",
        )
    }"
}