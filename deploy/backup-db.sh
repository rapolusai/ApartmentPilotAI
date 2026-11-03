#!/usr/bin/env bash
# Daily database backup for the one-server setup (docker-compose.single.yml). Writes a compressed pg_dump to
# /var/backups/apartmentcare (root only) and keeps the last 14. Lightsail's automatic snapshots copy the whole disk
# as well, so a lost server can be restored from either.
# Install once:  sudo install -m 700 backup-db.sh /usr/local/sbin/apartmentcare-backup
#                echo '30 2 * * * root /usr/local/sbin/apartmentcare-backup' | sudo tee /etc/cron.d/apartmentcare-backup
# Restore:       gunzip -c <file>.sql.gz | docker exec -i apartmentcare-db psql -U apartmentcare_app -d apartmentcare
#                (into an empty database; stop the app first: docker stop apartmentcare-app)
set -euo pipefail
DIR=/var/backups/apartmentcare
KEEP=14
install -d -m 700 "${DIR}"
FILE="${DIR}/apartmentcare-$(date +%Y%m%d-%H%M).sql.gz"
docker exec apartmentcare-db pg_dump -U apartmentcare_app -d apartmentcare --no-owner | gzip > "${FILE}.tmp"
mv "${FILE}.tmp" "${FILE}"
chmod 600 "${FILE}"
ls -1t "${DIR}"/apartmentcare-*.sql.gz | tail -n +$((KEEP+1)) | xargs -r rm -f
echo "Backup written: ${FILE} ($(du -h "${FILE}" | cut -f1))"
