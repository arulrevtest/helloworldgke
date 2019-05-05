#
# Google Cloud Platform
#
provider "google" {
  project = "${var.project}"
  region  = "${var.region}"
  #credentials = "/home/arulkr1967/terraform_sa.json"
}