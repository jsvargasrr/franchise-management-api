# Terraform: plantilla mínima sin coste. Amplía con proveedor cloud (RDS, ElastiCache, ECS, etc.).
# Uso: cd infra/terraform && terraform init && terraform apply

terraform {  required_version = ">= 1.5"

  required_providers {
    random = {
      source  = "hashicorp/random"
      version = "~> 3.5"
    }
  }
}

resource "random_pet" "stack" {
  length    = 2
  separator = "-"
}

output "stack_name" {
  description = "Identificador de ejemplo para prefijos en la nube (S3, tags, etc.)."
  value       = random_pet.stack.id
}
