#
# Output for K8S

output "host" {
  value = "${google_container_cluster.cluster.endpoint}"
  sensitive = false
}