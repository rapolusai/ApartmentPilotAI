#!/usr/bin/env bash
# Nightly backup (cron). Keeps 7 local dumps; S3 lifecycle keeps 30 days.
source /opt/apartmentcare/lib.sh
backup_db nightly >/dev/null
ls -1t "$BACKUP_DIR"/apartmentcare-nightly-*.dump 2>/dev/null | tail -n +8 | xargs -r rm -f
ls -1t "$BACKUP_DIR"/apartmentcare-pre-deploy-*.dump 2>/dev/null | tail -n +6 | xargs -r rm -f
