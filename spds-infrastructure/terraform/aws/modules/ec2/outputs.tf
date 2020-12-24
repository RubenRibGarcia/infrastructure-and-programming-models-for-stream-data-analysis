output "service_name" {
  value = var.name
}

output "instance_name" {
  value = aws_instance.default.*.tags.Name
}

output "public_ip" {
  value = aws_instance.default.*.public_ip
}

output "private_ip" {
  value = aws_instance.default.*.private_ip
}