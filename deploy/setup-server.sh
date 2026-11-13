#!/usr/bin/env bash
# One-time setup on a fresh Amazon Linux 2023 instance (run as root via SSM).
# Expects deploy files already copied to /opt/apartmentcare.
set -euo pipefail
dnf install -y docker cronie amazon-cloudwatch-agent
systemctl enable --now docker crond

# Docker Compose v2 plugin
ARCH=$(uname -m); [ "$ARCH" = aarch64 ] || [ "$ARCH" = x86_64 ]
mkdir -p /usr/local/lib/docker/cli-plugins
curl -fsSL "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-$ARCH" \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
docker compose version

# 2 GB swap as a memory safety net on the 2 GB instance
if ! swapon --show | grep -q /swapfile; then
  fallocate -l 2G /swapfile && chmod 600 /swapfile && mkswap /swapfile && swapon /swapfile
  echo '/swapfile none swap sw 0 0' >> /etc/fstab
  sysctl -w vm.swappiness=10 && echo 'vm.swappiness=10' > /etc/sysctl.d/99-swappiness.conf
fi

cd /opt/apartmentcare
chmod 700 /opt/apartmentcare
chmod +x deploy.sh backup.sh restore.sh
mkdir -p backups && chmod 700 backups

# Nightly backup at 02:30 IST (21:00 UTC)
echo '0 21 * * * root /opt/apartmentcare/backup.sh >> /var/log/apartmentcare-backup.log 2>&1' > /etc/cron.d/apartmentcare-backup

# CloudWatch agent: memory and disk metrics for alarms
/opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s \
  -c file:/opt/apartmentcare/cloudwatch-agent.json
echo "Setup complete."
