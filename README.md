[Uploading README.md…]()

```
# 🚀 Secure Cloud-Native File Management System

![Spring Boot]
![AWS]
![Terraform]
![Docker]
![Kubernetes]
![CI/CD]

A robust, enterprise-ready backend system focusing on secure authentication and scalable cloud infrastructure. This project demonstrates a full-cycle DevOps implementation—from Spring Boot development to Kubernetes orchestration and Automated Cloud Provisioning.

---

## 🏗 System Architecture

The application follows a modern cloud-native architecture:
* **Backend:** Spring Boot 4 REST API with JWT Security.
* **Database:** Amazon RDS (MySQL) isolated in private subnets.
* **Infrastructure:** Fully automated via Terraform.
* **Orchestration:** Managed by Kubernetes (Minikube) using Helm Charts.
* **Pipeline:** Continuous Integration and Deployment via GitHub Actions.



---

## 🛠 Tech Stack

| Category | Technologies |
| :--- | :--- |
| **Backend** | Java 21, Spring Boot 4, Spring Security, JPA/Hibernate |
| **Infrastructure** | Terraform, AWS (VPC, EC2, RDS, SG) |
| **Containerization** | Docker, Kubernetes (Minikube) |
| **Package Manager** | Helm Charts |
| **CI/CD** | GitHub Actions |
| **Security** | JWT, BCrypt Encryption |

---

## 🚀 Key Features

- **JWT Authentication:** Secure stateless login and registration flow.
- **Infrastructure as Code:** Spin up an entire AWS environment with a single command.
- **Containerized Workflow:** Fully Dockerized application for environment parity.
- **K8s Ready:** Deployment manifests packaged in Helm for easy scaling and management.
- **Automated Pipelines:** Every push triggers a build and containerization process.

---

## 🚦 Getting Started

### Prerequisites
* Java 21 & Maven
* AWS CLI & Terraform
* Docker & Minikube
* Helm

### Local Development (Docker & K8s)
1. **Clone the repo:**
   ```bash
   git clone 

```

2.  **Build and Run with Docker:**
    
    Bash
    
    ```
    docker build -t file-mgmt-app .
    docker run -p 8080:8080 file-mgmt-app
    
    ```
    
3.  **Deploy to Kubernetes using Helm:**
    
    Bash
    
    ```
    helm install file-mgmt ./charts/file-mgmt
    
    ```
    

### Infrastructure Deployment (AWS)

1.  **Initialize Terraform:**
    
    Bash
    
    ```
    cd terraform/
    terraform init
    terraform apply
    
    ```
    

----------

## 🔗 CI/CD Flow (GitHub Actions)

The project utilizes GitHub Actions to automate the SDLC:

1.  **Code Linting & Testing** on every Pull Request.
    
2.  **Docker Image Build** upon merging to `main`.
    
3.  **Push to Docker Registry** (Docker Hub/ECR).
    
4.  _(Coming Soon)_ **Auto-deploy to EKS**.
    

----------

## 📅 Future Roadmap

-   [ ] **S3 Integration:** Move file storage to Amazon S3 for 99.99% durability.
    
-   [ ] **Shared Links:** Generate pre-signed URLs for secure file sharing.
    
-   [ ] **Monitoring:** Implement Prometheus & Grafana dashboard for K8s clusters.
    
-   [ ] **EKS Migration:** Move from Minikube to production-grade Amazon EKS.
    

----------

## 👤 Author

**NamMau**

-   LinkedIn: https://www.linkedin.com/in/mau-tien-nam-fgw-hn-76b3a7296/(https://linkedin.com/in/yourprofile)
    
-   GitHub: NamMau
    

----------

_This project is part of my continuous journey in mastering Cloud-Native technologies and DevOps practices._
