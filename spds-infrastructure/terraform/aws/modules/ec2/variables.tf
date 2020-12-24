variable "name" {
  description = "The isntance name"
}
variable "instance_count" {
  description = "The number of Computer Engine instances associated with this service"
}
variable "instance_type" {
  description = "The instance type to use"
}
variable "ssh_authorized_keys" {
  type        = list(string)
  description = "SSH Authorized keys"
  default     = [""]
}