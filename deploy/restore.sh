#!/usr/bin/env bash
# Restore a pg_dump file over the live database: ./restore.sh <file.dump|s3://...> --yes
# Takes a safety backup first. Stops the backend during the restore.
source /opt/apartmentcare/lib.sh
SRC="${1:?usage: restore.sh <file.dump|s3://bucket/key> --yes}"
[ "${2:-}" = "--yes" ] || { echo "This REPLACES the live database. Re-run with --yes to confirm."; exit 2; }
if [[ "$SRC" == s3://* ]]; then
  mkdir -p "$BACKUP_DIR"; aws s3 cp --only-show-errors --region "$AWS_REGION" "$SRC" "$BACKUP_DIR/"; SRC="$BACKUP_DIR/$(basename "$SRC")"
fi
[ -s "$SRC" ] || { echo "Missing or empty: $SRC"; exit 1; }
log "Safety backup before restore"
backup_db pre-restore >/dev/null
compose stop app
log "Restoring $SRC"
compose exec -T db pg_restore -U apartmentcare -d apartmentcare --clean --if-exists --no-owner < "$SRC"
compose start app
wait_healthy && log "Restore done, backend healthy" || { log "Backend unhealthy after restore"; exit 1; }
