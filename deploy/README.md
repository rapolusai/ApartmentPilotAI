# Putting the ApartmentCare backend online

This folder holds everything needed to run the backend on one small server with HTTPS, a managed PostgreSQL
database and no secret in git. It follows the hosting plan of 11 October 2026 (AWS Mumbai, about $30–35 a month).
Nothing here runs by itself: every step below is done by a person, on purpose.

The API lives at `https://api.apartmentcare.rapolu.in`; the Android
release build is pointed at it with a Gradle property (see step 7).

## What you get
| Piece | Choice |
|---|---|
| App server | EC2 `t4g.small` (Arm, 2 vCPU, 2 GB), Ubuntu 24.04, Docker |
| HTTPS | Caddy in Docker, Let's Encrypt certificate, renews itself |
| Database | RDS PostgreSQL 16, `db.t4g.micro`, 20 GB gp3, encrypted, daily backups kept 7 days |
| Secrets | SSM Parameter Store (SecureString) under `/apartmentcare/prod/` |
| Image | `ghcr.io/rapolusai/apartmentpilotai-backend:<tag>`, built by GitHub Actions after the tests pass |
| Monitoring | CloudWatch alarms (instance status, CPU, disk, RDS storage) to your email |

Staging is the same recipe with `/apartmentcare/staging/` parameters, its own database and `api-staging.apartmentcare.rapolu.in`.

## 1. AWS account
1. Create the account, turn on MFA for the root user, and create an IAM user for yourself with MFA.
2. Billing → Budgets: a monthly budget of $50 with an email alert at 80 %.
3. Region for everything below: **Asia Pacific (Mumbai) `ap-south-1`**.

## 2. Network and database
1. Use the default VPC. Create two security groups: `apartmentcare-app` (inbound 80 and 443 from anywhere; 22 only from your IP, or none if you use Session Manager) and `apartmentcare-db` (inbound 5432 **only from `apartmentcare-app`**).
2. RDS → Create database → PostgreSQL 16, Free tier/Dev template, `db.t4g.micro`, 20 GB gp3, storage autoscaling to 50 GB, **not publicly accessible**, security group `apartmentcare-db`, automated backups 7 days, encryption on, deletion protection on. Database name `apartmentcare`. Let RDS generate the master password and store it in Secrets Manager or note it once for the next step.
3. Create the application role and database once, from the app server after step 3, using `psql`:
   ```sql
   create role apartmentcare_app login password '<generate a long random password>';
   create database apartmentcare owner apartmentcare_app;
   ```
   The app connects as `apartmentcare_app`, never as the master user. Flyway creates the `apartmentpilotai` schema and applies V1 onwards on the first start.

## 3. Secrets in Parameter Store
Create these as **SecureString** parameters (Systems Manager → Parameter Store):

| Name | Value |
|---|---|
| `/apartmentcare/prod/DB_URL` | `jdbc:postgresql://<rds-endpoint>:5432/apartmentcare` |
| `/apartmentcare/prod/DB_USER` | `apartmentcare_app` |
| `/apartmentcare/prod/DB_PASSWORD` | the password from step 2.3 |
| `/apartmentcare/prod/APP_INVITE_DISPLAY_KEY` | 32 random bytes, base64: `openssl rand -base64 32` |
| `/apartmentcare/prod/APP_PROVIDER_KEY` | at least 32 random characters: `openssl rand -hex 32` |
| `/apartmentcare/prod/SUPPORT_EMAIL` | the address shown on the privacy and deletion pages |

Back up `APP_INVITE_DISPLAY_KEY` somewhere safe (a password manager): if it is lost, invite codes already issued cannot be displayed again. Never paste any of these into chat, git, the Android project or a screenshot.

## 4. App server
1. EC2 → Launch: Ubuntu Server 24.04 (Arm), `t4g.small`, 20 GB gp3, security group `apartmentcare-app`, an IAM instance role with `AmazonSSMManagedInstanceCore` plus a policy allowing `ssm:GetParametersByPath` on `arn:aws:ssm:ap-south-1:<account>:parameter/apartmentcare/*` and `kms:Decrypt` on the default SSM key. Allocate an Elastic IP and attach it.
2. Connect (Session Manager or SSH) and install Docker:
   ```bash
   sudo apt-get update && sudo apt-get install -y docker.io docker-compose-v2 awscli postgresql-client
   sudo usermod -aG docker ubuntu
   ```
3. Put this folder on the server, for example `git clone` the repository into `/opt/apartmentcare` and use `/opt/apartmentcare/deploy`.
4. Write the non-secret settings:
   ```bash
   sudo install -d -m 700 /etc/apartmentcare
   printf 'APP_DOMAIN=api.apartmentcare.rapolu.in\nACME_EMAIL=<your email>\n' | sudo tee /etc/apartmentcare/caddy.env >/dev/null
   sudo chmod 600 /etc/apartmentcare/caddy.env
   ```
5. Pull the secrets: `sudo AWS_REGION=ap-south-1 ./fetch-secrets.sh prod` (writes `/etc/apartmentcare/app.env`, mode 600).

## 5. DNS
Route 53 → hosted zone for `rapolu.in` (or your registrar's DNS): an `A` record `api.apartmentcare` → the Elastic IP. Caddy obtains the certificate on first start, so DNS must resolve before step 6.

## 6. First start
```bash
cd /opt/apartmentcare/deploy
echo "ghcr.io token for pulls: create a classic PAT with read:packages, or make the package public"
docker login ghcr.io
IMAGE_TAG=<tag from the Actions run> docker compose -f docker-compose.prod.yml up -d
docker compose -f docker-compose.prod.yml logs -f app   # wait for "Started ApartmentPilotAiApplication"
curl -s https://api.apartmentcare.rapolu.in/actuator/health   # {"status":"UP"}
curl -s https://api.apartmentcare.rapolu.in/api/v1/status
```
The database starts **empty**: Flyway applies V1 → V13 and no apartment exists yet. Registration on a public server
is disabled until phone verification is configured (`app.local-registration-enabled` stays `false`; the app says so),
which is a separate, later piece of work. Until then the first apartment is created on staging or by the operator.

## 7. Android release build
The release API address is no longer hard-coded. Build with the property set, for example in `android/gradle.properties`
on your PC (not committed) or on the command line:
```
gradlew :app:assembleRelease -PapiBaseUrl=https://api.apartmentcare.rapolu.in/api/v1
```
The release guard in `android/app/build.gradle` still blocks release builds until the Play steps are done; that is
intentional and is lifted in its own change.

## Updating to a new version
1. Merge to `main` (or push a tag `vX.Y.Z`). The "Backend image" workflow runs the tests and publishes the image.
2. On the server: `IMAGE_TAG=<new tag> docker compose -f docker-compose.prod.yml pull app && IMAGE_TAG=<new tag> docker compose -f docker-compose.prod.yml up -d app`.
3. Watch the log for the Flyway line listing the migration it applied, then check `/actuator/health`.
Migrations are additive only; an update never needs manual SQL.

## Rolling back
`IMAGE_TAG=<previous tag> docker compose -f docker-compose.prod.yml up -d app`. The previous image is still on the
server, so this takes seconds. Database migrations are not rolled back: every migration is additive, so the previous
version runs against the newer schema.

## Restoring the database
RDS → Databases → `apartmentcare` → Actions → *Restore to point in time*. Restore to a **new** instance, check it,
then either point `DB_URL` at it (edit the parameter, rerun `fetch-secrets.sh`, `up -d app`) or copy the needed rows.
Take a manual snapshot before any risky operation: Actions → *Take snapshot*.

## Monitoring
CloudWatch → Alarms, each sending to an SNS topic subscribed to your email:
- EC2 `StatusCheckFailed` ≥ 1 for 2 periods of 1 minute.
- EC2 `CPUUtilization` > 80 % for 15 minutes.
- RDS `FreeStorageSpace` < 2 GB, `CPUUtilization` > 80 % for 15 minutes, `DatabaseConnections` > 6 (the pool is 8).
- Disk: install the CloudWatch agent or a cron `df` check; the Docker logs are capped at 100 MB per container.
Also an external uptime check (for example a free UptimeRobot monitor) on `/actuator/health`.

## Security notes
- The app listens only inside the Docker network; Caddy exposes `/api/*`, `/actuator/health`, `/privacy` and `/delete-account` and nothing else.
- RDS accepts connections only from the app server's security group.
- `app.env` is root-owned, mode 600, and rewritten from Parameter Store; rotate a secret by changing the parameter and rerunning `fetch-secrets.sh` then `up -d app`.
- Keep SSH closed and use Session Manager, or allow port 22 only from your current IP.

## Moving the Nakshatra Resindency data from the PC
Not part of this setup. Production starts empty; if you decide to move that apartment, a separate export/import
is prepared that copies only that tenant, after a backup of both databases.
