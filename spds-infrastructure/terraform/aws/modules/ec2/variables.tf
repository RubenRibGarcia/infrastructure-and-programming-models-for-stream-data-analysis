variable "name" {
  description = "The isntance name"
}
variable "instance_count" {
  description = "The number of Computer Engine instances associated with this service"
}
variable "instance_type" {
  description = "The instance type to use"
}
variable "volume_size" {
  description = "Volume Size of root"
  default = 20
}
variable "zone" {
  description = "AWS Availability Zone"
}
variable "aws_security_group_id" {
  description = "Security Group id"
}
variable "key_pair_name" {
  description = "Key Pair name"
}
variable "ssh_authorized_keys" {
  type        = list(string)
  description = "SSH Authorized keys"
  default     = [""]
}