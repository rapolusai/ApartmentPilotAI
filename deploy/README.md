# ApartmentCare production server

A single low-cost EC2 server, kept completely separate from ParkingPilot AI (only the AWS account is shared).

This replaces the 11 October plan (EC2 + RDS, about $30–35 a month): with no customers yet, PostgreSQL runs in Docker on the same server, about **US$14 a month**, sized for at least 10 apartments. Moving the database to RDS later is the scaling step below.

| | |
|---|---|
| Account / region | 446701321630 / ap-south-1 (Mumbai) |
| Instance | `apartmentcare-prod`, t4g.small (2 vCPU, 2 GB, ARM), Amazon Linux 2023 |
| Disk | 20 GB gp3, encrypted, kept if the instance is terminated |
| Address | `https://api.apartmentcare.rapolu.in` (Elastic IP + DNS A record) |
| Stack | Docker Compose: `app` (Spring Boot, profile `prod`), `db` (PostgreSQL 17), `caddy` (HTTPS) |
| Images | ECR `apartmentcare-prod` |
| Secrets | SSM Parameter Store SecureStrings `/apartmentcare/prod/DB_PASSWORD`, `APP_INVITE_DISPLAY_KEY`, `APP_PROVIDER_KEY`, `SUPPORT_EMAIL` |
| Backups | S3 `apartmentcare-prod-backups-446701321630` (`db/` prefix, 30 days) + weekly EBS snapshots |
| Access | SSM Session Manager only. No SSH, port 22 closed |

Memory budget on the 2 GB server: app container limit 800 MB (heap 65% of it), PostgreSQL 450 MB, Caddy 128 MB, plus a 2 GB swap file.

## Files

| File | Purpose |
|---|---|
| `../backend/Dockerfile` | Multi-stage Java 21 image, non-root; build it for `linux/arm64` for the t4g server |
| `../backend/src/main/resources/application-prod.yml` | Production profile: graceful shutdown, billing jobs on, registration off (a test-only switch exists, see First deploy) |
| `docker-compose.prod.yml`, `Caddyfile` | The stack. PostgreSQL is only on an internal Docker network; the app is published on 127.0.0.1 only; Caddy exposes only `/api/*`, `/actuator/health`, `/privacy` and `/delete-account` |
| `lib.sh` | Shared helpers (secrets from SSM, backup, health check) |
| `deploy.sh <tag>` | Pull image → pre-deploy DB backup (local + S3) → switch → health check → automatic rollback to the previous image on failure |
| `backup.sh` | Nightly backup (cron 02:30 IST), keeps 7 local dumps |
| `restore.sh <file\|s3://…> --yes` | Restore a dump (takes a safety backup first) |
| `setup-server.sh` | One-time server setup: Docker, Compose, swap, cron, CloudWatch agent |
| `cloudwatch-agent.json` | Memory, swap and disk metrics |
| `aws/*.json` | IAM role policy (least privilege), trust policy, ECR and S3 lifecycle rules |

## One-time AWS setup

Run from a machine with the AWS CLI using the `apartmentcare` profile, which assumes `OrganizationAccountAccessRole` in 446701321630 (a separate profile; the ParkingPilot profiles are never changed):

```powershell
aws configure set role_arn arn:aws:iam::446701321630:role/OrganizationAccountAccessRole --profile apartmentcare
aws configure set source_profile parkingpilot-admin --profile apartmentcare
aws configure set region ap-south-1 --profile apartmentcare
aws sts get-caller-identity --profile apartmentcare   # Account 446701321630
```
 Nothing here touches ParkingPilot resources. Every resource is tagged `Project=ApartmentCare`.

```bash
export AWS_PROFILE=apartmentcare AWS_REGION=ap-south-1
ACCT=446701321630; BUCKET=apartmentcare-prod-backups-$ACCT
TAGS='Key=Project,Value=ApartmentCare'
cd deploy/aws

# 1. Alerts topic (confirm the email AWS sends you)
TOPIC=$(aws sns create-topic --name apartmentcare-prod-alerts --tags $TAGS --query TopicArn --output text)
aws sns subscribe --topic-arn $TOPIC --protocol email --notification-endpoint <your-email>

# 2. Image repository
aws ecr create-repository --repository-name apartmentcare-prod --image-scanning-configuration scanOnPush=true --tags $TAGS
aws ecr put-lifecycle-policy --repository-name apartmentcare-prod --lifecycle-policy-text file://ecr-lifecycle.json

# 3. Backup bucket (private, encrypted, versioned, 30-day expiry)
aws s3api create-bucket --bucket $BUCKET --create-bucket-configuration LocationConstraint=$AWS_REGION
aws s3api put-public-access-block --bucket $BUCKET --public-access-block-configuration BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true
aws s3api put-bucket-encryption --bucket $BUCKET --server-side-encryption-configuration '{"Rules":[{"ApplyServerSideEncryptionByDefault":{"SSEAlgorithm":"AES256"}}]}'
aws s3api put-bucket-versioning --bucket $BUCKET --versioning-configuration Status=Enabled
aws s3api put-bucket-lifecycle-configuration --bucket $BUCKET --lifecycle-configuration file://s3-lifecycle.json
aws s3api put-bucket-tagging --bucket $BUCKET --tagging "TagSet=[{$TAGS}]"

# 4. Secrets (generated here, never written to disk or the repo)
aws ssm put-parameter --name /apartmentcare/prod/DB_PASSWORD --type SecureString --value "$(openssl rand -hex 24)" --tags $TAGS
aws ssm put-parameter --name /apartmentcare/prod/APP_INVITE_DISPLAY_KEY --type SecureString --value "$(openssl rand -base64 32)" --tags $TAGS
aws ssm put-parameter --name /apartmentcare/prod/APP_PROVIDER_KEY --type SecureString --value "$(openssl rand -hex 32)" --tags $TAGS
aws ssm put-parameter --name /apartmentcare/prod/SUPPORT_EMAIL --type SecureString --value "<support address shown on /privacy>" --tags $TAGS
# Back up APP_INVITE_DISPLAY_KEY in a password manager: if lost, issued invite codes can't be shown again.

# 5. Instance role (least privilege + SSM + CloudWatch agent)
aws iam create-role --role-name apartmentcare-prod-ec2 --assume-role-policy-document file://ec2-trust.json --tags $TAGS
aws iam put-role-policy --role-name apartmentcare-prod-ec2 --policy-name apartmentcare-prod --policy-document file://ec2-role-policy.json
aws iam attach-role-policy --role-name apartmentcare-prod-ec2 --policy-arn arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore
aws iam attach-role-policy --role-name apartmentcare-prod-ec2 --policy-arn arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy
aws iam create-instance-profile --instance-profile-name apartmentcare-prod-ec2
aws iam add-role-to-instance-profile --instance-profile-name apartmentcare-prod-ec2 --role-name apartmentcare-prod-ec2

# 6. Security group: 80/443 only
VPC=$(aws ec2 describe-vpcs --filters Name=is-default,Values=true --query 'Vpcs[0].VpcId' --output text)
SG=$(aws ec2 create-security-group --group-name apartmentcare-prod-sg --description "ApartmentCare prod: HTTP/HTTPS only" --vpc-id $VPC --query GroupId --output text)
aws ec2 authorize-security-group-ingress --group-id $SG --ip-permissions \
  'IpProtocol=tcp,FromPort=80,ToPort=80,IpRanges=[{CidrIp=0.0.0.0/0}],Ipv6Ranges=[{CidrIpv6=::/0}]' \
  'IpProtocol=tcp,FromPort=443,ToPort=443,IpRanges=[{CidrIp=0.0.0.0/0}],Ipv6Ranges=[{CidrIpv6=::/0}]'
aws ec2 create-tags --resources $SG --tags $TAGS

# 7. Instance (latest AL2023 ARM, IMDSv2 required, encrypted 20 GB gp3 kept on terminate)
AMI=$(aws ssm get-parameter --name /aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-arm64 --query Parameter.Value --output text)
IID=$(aws ec2 run-instances --image-id $AMI --instance-type t4g.small --security-group-ids $SG \
  --iam-instance-profile Name=apartmentcare-prod-ec2 \
  --metadata-options HttpTokens=required,HttpEndpoint=enabled \
  --block-device-mappings '[{"DeviceName":"/dev/xvda","Ebs":{"VolumeSize":20,"VolumeType":"gp3","Encrypted":true,"DeleteOnTermination":false}}]' \
  --tag-specifications "ResourceType=instance,Tags=[{Key=Name,Value=apartmentcare-prod},{$TAGS}]" "ResourceType=volume,Tags=[{Key=Name,Value=apartmentcare-prod},{$TAGS},{Key=Backup,Value=weekly}]" \
  --query 'Instances[0].InstanceId' --output text)
aws ec2 wait instance-running --instance-ids $IID

# 8. Fixed IP
ALLOC=$(aws ec2 allocate-address --domain vpc --tag-specifications "ResourceType=elastic-ip,Tags=[{$TAGS}]" --query AllocationId --output text)
aws ec2 associate-address --instance-id $IID --allocation-id $ALLOC
aws ec2 describe-addresses --allocation-ids $ALLOC --query 'Addresses[0].PublicIp' --output text   # → DNS A record
```

### DNS (GoDaddy, where rapolu.in already lives)

rapolu.in stays exactly as it is (website, `www`, Zoho MX records, and ParkingPilot's `api` record). Add **one** record in GoDaddy → DNS: type `A`, name `api.apartmentcare`, value = the Elastic IP, TTL 600. Caddy gets the Let's Encrypt certificate on its own once `nslookup api.apartmentcare.rapolu.in` answers.

### Alarms and snapshots

```bash
# Auto-recover on hardware failure + alert
aws cloudwatch put-metric-alarm --alarm-name apartmentcare-prod-status-check --namespace AWS/EC2 --metric-name StatusCheckFailed_System \
  --dimensions Name=InstanceId,Value=$IID --statistic Maximum --period 60 --evaluation-periods 2 --threshold 1 \
  --comparison-operator GreaterThanOrEqualToThreshold --alarm-actions arn:aws:automate:$AWS_REGION:ec2:recover $TOPIC
# Burst credits running low → time to resize
aws cloudwatch put-metric-alarm --alarm-name apartmentcare-prod-cpu-credits --namespace AWS/EC2 --metric-name CPUCreditBalance \
  --dimensions Name=InstanceId,Value=$IID --statistic Average --period 300 --evaluation-periods 3 --threshold 30 \
  --comparison-operator LessThanThreshold --alarm-actions $TOPIC
# Memory and disk (from the CloudWatch agent)
aws cloudwatch put-metric-alarm --alarm-name apartmentcare-prod-memory --namespace ApartmentCare --metric-name mem_used_percent \
  --dimensions Name=InstanceId,Value=$IID --statistic Average --period 300 --evaluation-periods 3 --threshold 90 \
  --comparison-operator GreaterThanThreshold --alarm-actions $TOPIC
aws cloudwatch put-metric-alarm --alarm-name apartmentcare-prod-disk --namespace ApartmentCare --metric-name disk_used_percent \
  --dimensions Name=InstanceId,Value=$IID --statistic Average --period 300 --evaluation-periods 1 --threshold 80 \
  --comparison-operator GreaterThanThreshold --alarm-actions $TOPIC
```

- **Weekly EBS snapshot:** in the console, EC2 → Lifecycle Manager → create a policy targeting tag `Backup=weekly`, weekly, keep 4.
- **Uptime:** a free external monitor (for example UptimeRobot) on `https://api.apartmentcare.rapolu.in/actuator/health` every 5 minutes, alerting your email.
- **Budget:** Billing → Budgets → monthly cost budget US$20 filtered on tag `Project=ApartmentCare` (activate the tag as a cost allocation tag first).

## First deploy

```bash
# Build the ARM image and push (from the repo root)
TAG=$(git rev-parse --short HEAD)
REG=$ACCT.dkr.ecr.$AWS_REGION.amazonaws.com
aws ecr get-login-password | docker login --username AWS --password-stdin $REG
docker buildx build --platform linux/arm64 -t $REG/apartmentcare-prod:$TAG --push backend

# Ship the deploy files via S3 and set the server up via SSM
aws s3 sync deploy s3://$BUCKET/deploy/ --exclude 'aws/*' --exclude README.md
aws ssm send-command --instance-ids $IID --document-name AWS-RunShellScript --parameters commands="[
  \"mkdir -p /opt/apartmentcare && aws s3 sync s3://$BUCKET/deploy/ /opt/apartmentcare/\",
  \"[ -f /opt/apartmentcare/deploy.env ] || cp /opt/apartmentcare/deploy.env.example /opt/apartmentcare/deploy.env\",
  \"bash /opt/apartmentcare/setup-server.sh\"]"

# Deploy
aws ssm send-command --instance-ids $IID --document-name AWS-RunShellScript --parameters commands="[\"/opt/apartmentcare/deploy.sh $TAG\"]"

# Pre-launch testing only (empty database): allow synthetic sign-ups, run scripts/Test-*.ps1, then wipe and redeploy normally:
#   APP_LOCAL_REGISTRATION_ENABLED=true /opt/apartmentcare/deploy.sh $TAG
#   cd /opt/apartmentcare && docker compose --env-file .env -f docker-compose.prod.yml down && docker volume rm apartmentcare_pgdata
#   /opt/apartmentcare/deploy.sh $TAG
```

Check the output with `aws ssm get-command-invocation --command-id <id> --instance-id $IID`, then verify:

```bash
curl -s https://api.apartmentcare.rapolu.in/actuator/health   # {"status":"UP"}
curl -s https://api.apartmentcare.rapolu.in/api/v1/status
```

No Docker on your PC? Build on the instance instead (it is ARM): upload `backend/` to `s3://$BUCKET/deploy/src/`, then via SSM run `docker build -t $REG/apartmentcare-prod:$TAG` there and push (give the role ECR push rights on `apartmentcare-prod` only for that step). The GitHub "Backend image" workflow also publishes multi-arch images to ghcr.io if you prefer that registry.

## Android release build

The release API address is set with a Gradle property, not hard-coded (see `docs/RELEASE.md`):
```
gradlew :app:bundleRelease -PapiBaseUrl=https://api.apartmentcare.rapolu.in/api/v1
```

## Later deploys

Build and push a new tag, then run `/opt/apartmentcare/deploy.sh <tag>` via SSM. If deploy files changed, `aws s3 sync` them first and sync on the server. Migrations are additive, so the previous version still runs against a newer schema.

## Rollback and recovery

- **Bad release:** `deploy.sh` rolls back automatically if `/actuator/health` is not UP within 3 minutes. To roll back by hand: `deploy.sh <previous-tag>` (see `/opt/apartmentcare/deploy-history.log`).
- **Bad database migration:** Flyway only moves forward. Restore the pre-deploy dump, then deploy the previous tag:
  `./restore.sh /opt/apartmentcare/backups/apartmentcare-pre-deploy-<time>.dump --yes` then `./deploy.sh <previous-tag>`.
- **Server lost:** launch a new instance (steps 7–8 with the same role and security group, or from the latest snapshot), move the Elastic IP, run setup, deploy, and restore the latest `s3://…/db/nightly/` dump.
- **Remove everything:** all resources carry `Project=ApartmentCare`; none are shared with ParkingPilot.

## Scaling up

1. Resize: stop the instance, change type to `t4g.medium` (4 GB) or `t4g.large` (8 GB), start. About 5 minutes downtime; same IP and data. Raise `-Xmx` (`JAVA_OPTS`) and the compose `mem_limit`s to match.
2. Grow the disk online: modify the volume size, then `growpart` and `xfs_growfs /`.
3. Move PostgreSQL to RDS when there are paying customers; then add a second app instance behind a load balancer.
4. Once usage is steady, buy a 1-year Compute Savings Plan (about 30% off the instance).
