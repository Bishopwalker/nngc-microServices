# NNGC MicroServices - AWS ECS Deployment Strategy

Complete deployment strategy for production-ready NNGC microservices using AWS ECS with Docker containers.

## 🎯 Current Status

**✅ Completed:**
- GitHub Actions CI/CD pipeline (active)
- AWS ECS task definitions prepared
- Docker configurations ready
- GitLab CI disabled (credit conservation)

**🚧 Pending (When Production Ready):**
- AWS infrastructure provisioning
- ECS cluster deployment
- Production environment configuration

## 🏗️ Architecture Overview

### Target Production Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Application   │    │      Amazon     │    │     Amazon      │
│  Load Balancer  │────│    Route 53     │────│   CloudFront    │
│      (ALB)      │    │      (DNS)      │    │      (CDN)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │
    ┌────┴────────────────────────────────────────────┐
    │                ECS Cluster                       │
    │  ┌─────────────┐  ┌─────────────┐  ┌──────────┐ │
    │  │ API Gateway │  │   Service   │  │   Task   │ │
    │  │  (2 tasks)  │  │  Registry   │  │Services  │ │
    │  └─────────────┘  │  (1 task)   │  │(1-2 each)│ │
    │                   └─────────────┘  └──────────┘ │
    └─────────────────────────────────────────────────┘
         │
    ┌────┴─────┐    ┌─────────────┐    ┌─────────────┐
    │   RDS    │    │   Systems   │    │ CloudWatch  │
    │PostgreSQL│    │  Manager    │    │ Monitoring  │
    │          │    │ (Secrets)   │    │             │
    └──────────┘    └─────────────┘    └─────────────┘
```

## 📋 Pre-Production Checklist

### 1. AWS Account Setup
- [ ] AWS account with appropriate permissions
- [ ] IAM roles created:
  - [ ] `ecsTaskExecutionRole`
  - [ ] `ecsTaskRole`
  - [ ] `ecsServiceRole`
- [ ] VPC and subnets configured
- [ ] Security groups defined

### 2. Container Registry
- [ ] ECR repositories created for each service:
  ```bash
  aws ecr create-repository --repository-name nngc-api-gateway
  aws ecr create-repository --repository-name nngc-customer-service
  aws ecr create-repository --repository-name nngc-registration-service
  aws ecr create-repository --repository-name nngc-token-service
  aws ecr create-repository --repository-name nngc-email-service
  aws ecr create-repository --repository-name nngc-google-service
  aws ecr create-repository --repository-name nngc-stripe-service
  aws ecr create-repository --repository-name nngc-service-registry
  ```

### 3. Database Setup
- [ ] RDS PostgreSQL cluster created
- [ ] Database schemas initialized
- [ ] Connection parameters stored in Systems Manager
- [ ] Database security groups configured

### 4. Secrets Management
- [ ] Database credentials in AWS Systems Manager Parameter Store
- [ ] API keys and secrets configured
- [ ] Keycloak configuration parameters set

## 🚀 Deployment Phases

### Phase 1: Infrastructure Setup (When Ready)

#### 1.1 Create ECS Cluster
```bash
aws ecs create-cluster \
  --cluster-name nngc-cluster \
  --capacity-providers FARGATE \
  --default-capacity-provider-strategy capacityProvider=FARGATE,weight=1
```

#### 1.2 Configure Load Balancer
```bash
# Create Application Load Balancer
aws elbv2 create-load-balancer \
  --name nngc-alb \
  --subnets subnet-12345678 subnet-87654321 \
  --security-groups sg-alb-12345
```

#### 1.3 Set up Service Discovery
```bash
# Create service discovery namespace
aws servicediscovery create-private-dns-namespace \
  --name nngc.local \
  --vpc vpc-12345678
```

### Phase 2: Service Deployment

#### 2.1 Deploy Core Services First
**Order of deployment:**
1. **Service Registry** (Eureka) - Other services depend on this
2. **Token Service** - Authentication dependency
3. **Customer Service** - Core business logic
4. **API Gateway** - External interface
5. **Supporting Services** - Email, Google, Stripe, Registration

#### 2.2 Service Deployment Commands
```bash
# Deploy service registry first
aws ecs create-service \
  --cluster nngc-cluster \
  --service-name nngc-service-registry \
  --task-definition nngc-service-registry:1 \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration file://aws/ecs/service-definitions/service-registry-network.json

# Wait for service registry to be healthy before deploying others
aws ecs wait services-stable \
  --cluster nngc-cluster \
  --services nngc-service-registry

# Deploy other services...
```

### Phase 3: Production Optimization

#### 3.1 Auto Scaling Configuration
```bash
# Configure auto scaling for API Gateway
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --scalable-dimension ecs:service:DesiredCount \
  --resource-id service/nngc-cluster/nngc-api-gateway \
  --min-capacity 2 \
  --max-capacity 10
```

#### 3.2 Monitoring Setup
- CloudWatch dashboards for each service
- Custom metrics for business KPIs
- Alarms for critical failures
- Log aggregation and analysis

## 🔄 CI/CD Integration

### Current GitHub Actions Flow
1. **Code Push** → Triggers CI pipeline
2. **Build & Test** → All services built and tested in parallel
3. **Quality Gates** → Code quality checks
4. **Artifacts** → JAR files stored for deployment

### Production Deployment Flow (When Ready)
1. **Merge to Main** → Triggers deployment pipeline
2. **Build Images** → Docker images built and pushed to ECR
3. **Deploy to Staging** → Services deployed to staging ECS cluster
4. **Integration Tests** → End-to-end validation
5. **Manual Approval** → Production deployment gate
6. **Deploy to Production** → Rolling deployment with health checks

## 💰 Cost Optimization Strategy

### Current Approach (Pre-Production)
- **GitHub Actions**: Free tier (2,000 minutes/month)
- **No AWS costs**: Infrastructure prepared but not deployed
- **GitLab CI**: Disabled to conserve credits

### Production Cost Estimates
| Service | CPU | Memory | Count | Monthly Cost* |
|---------|-----|--------|-------|---------------|
| API Gateway | 0.5 vCPU | 1 GB | 2 | ~$30 |
| Customer Service | 0.25 vCPU | 0.5 GB | 2 | ~$15 |
| Token Service | 0.25 vCPU | 0.5 GB | 2 | ~$15 |
| Other Services | 0.25 vCPU | 0.5 GB | 1 each | ~$37.50 |
| **Total ECS** | | | | **~$97.50** |
| RDS (db.t3.micro) | | | | ~$15 |
| ALB | | | | ~$22 |
| **Total Monthly** | | | | **~$135** |

*Estimates based on us-east-1 pricing, actual costs may vary

## 🔒 Security Considerations

### Container Security
- [ ] Base images regularly updated
- [ ] No secrets in Docker images
- [ ] Non-root user in containers
- [ ] Security scanning in CI pipeline

### Network Security
- [ ] Private subnets for ECS tasks
- [ ] Security groups with minimal permissions
- [ ] VPC flow logs enabled
- [ ] Web Application Firewall (WAF) on ALB

### Data Security
- [ ] Encryption at rest (RDS)
- [ ] Encryption in transit (TLS)
- [ ] Secrets rotation strategy
- [ ] Audit logging enabled

## 📊 Monitoring & Observability

### Health Checks
- **Container Level**: Spring Boot Actuator `/health`
- **Load Balancer**: ALB target group health checks
- **Service Level**: ECS service health monitoring

### Metrics to Monitor
- **Application Metrics**: Request rates, response times, error rates
- **Infrastructure Metrics**: CPU, memory, network utilization
- **Business Metrics**: User registrations, payments, email delivery

### Alerting Strategy
- **Critical**: Service down, high error rates
- **Warning**: High latency, resource utilization
- **Info**: Deployment status, scaling events

## 🚨 Disaster Recovery

### Backup Strategy
- **Database**: Automated RDS backups + manual snapshots
- **Configuration**: Infrastructure as Code (Terraform/CloudFormation)
- **Application**: Container images in ECR

### Recovery Procedures
- **Service Failure**: ECS automatic restart + health checks
- **AZ Failure**: Multi-AZ deployment handles automatically
- **Region Failure**: Manual failover to secondary region

## 📅 Deployment Timeline

### Immediate (Current State)
- ✅ GitHub Actions CI active
- ✅ Code quality gates operational
- ✅ JAR artifact generation working

### When Production Ready (Your Decision)
- **Week 1**: AWS infrastructure setup
- **Week 2**: Deploy to staging environment
- **Week 3**: Load testing and optimization
- **Week 4**: Production deployment

### Recommended Approach
1. **Continue development** with GitHub Actions CI
2. **Test locally** with Docker Compose
3. **Deploy to AWS** when feature-complete and ready for users
4. **Scale gradually** based on actual usage

## 🎛️ Management Commands

### Quick Service Management
```bash
# Scale a service
aws ecs update-service --cluster nngc-cluster --service nngc-api-gateway --desired-count 4

# View service status
aws ecs describe-services --cluster nngc-cluster --services nngc-api-gateway

# View task logs
aws logs tail /ecs/nngc-api-gateway --follow

# Deploy new version
aws ecs update-service --cluster nngc-cluster --service nngc-api-gateway --force-new-deployment
```

### Emergency Procedures
```bash
# Stop all services (emergency)
for service in api-gateway customer-service registration-service token-service email-service; do
  aws ecs update-service --cluster nngc-cluster --service nngc-$service --desired-count 0
done

# Rollback to previous version
aws ecs update-service --cluster nngc-cluster --service nngc-api-gateway --task-definition nngc-api-gateway:PREVIOUS_REVISION
```

## 📝 Next Steps

### Immediate Actions
1. **Continue development** using GitHub Actions CI
2. **Test Docker builds** locally to ensure containers work
3. **Prepare production data** and configuration

### Before Going to Production
1. **Load testing** with realistic data volumes
2. **Security audit** of all configurations
3. **Documentation review** and team training
4. **Monitoring setup** and alerting configuration

### Production Go-Live Checklist
- [ ] All services tested and stable
- [ ] Database schema finalized
- [ ] SSL certificates configured
- [ ] Domain names configured
- [ ] Monitoring dashboards ready
- [ ] Team trained on operations
- [ ] Support procedures documented

---

**Current Status**: Development-ready with CI/CD pipeline active  
**Next Phase**: Deploy when feature-complete and production-ready  
**Estimated Setup Time**: 1-2 weeks when you're ready  
**Monthly Operating Cost**: ~$135 (estimated)