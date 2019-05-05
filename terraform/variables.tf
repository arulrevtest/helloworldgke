variable "project" {
  default = "arulgkedemo"
}

variable "region" {
  default = "us-east1"
}

variable "cluster_name" {
  default = "cd-jenkins"
}

variable "cluster_zone" {
  default = "us-east1-d"
}

variable "cluster_k8s_version" {
  default = "1.12.7-gke.10"
}

variable "initial_node_count" {
  default = 2
}

variable "autoscaling_min_node_count" {
  default = 1
}

variable "autoscaling_max_node_count" {
  default = 2
}

variable "disk_size_gb" {
  default = 50
}

variable "disk_type" {
  default = "pd-standard"
}

variable "machine_type" {
  default = "n1-standard-2"
}