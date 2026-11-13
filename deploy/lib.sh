# Shared helpers for ApartmentCare server scripts. Sourced, not run.
set -euo pipefail
APP_DIR=/opt/apartmentcare
BACKUP_DIR=$APP_DIR/backups
# shellcheck disable=SC1091
source "$APP_DIR/deploy.env"
cd "$APP_DIR"
log() { echo "[$(date '+%F %T')] $*"; }
compose() { docker compose --env-file "$APP_DIR/.env" -f "$APP_DIR/docker-compose.prod.yml" "$@"; }

# Writes .env from SSM Parameter Store (/apartmentcare/prod/*, SecureString) plus the image to run.
# Expected parameters: DB_PASSWORD, APP_INVITE_DISPLAY_KEY, APP_PROVIDER_KEY, SUPPORT_EMAIL.
write_env() {
  local image="$1" tmp="$APP_DIR/.env.tmp" required
  umask 077
  aws ssm get-parameters-by-path --region "$AWS_REGION" --path "$SSM_PREFIX/" --with-decryption \
    --query 'Parameters[].[Name,Value]' --output text \
  | while IFS=$'\t' read -r name value; do printf '%s=%s\n' "${name##*/}" "$value"; done > "$tmp"
  for required in DB_PASSWORD APP_INVITE_DISPLAY_KEY; do
    grep -q "^${required}=" "$tmp" || { log "Missing $SSM_PREFIX/$required in Parameter Store" >&2; rm -f "$tmp"; return 1; }
  done
  {
    echo "BACKEND_IMAGE=$image"
    echo "APP_DOMAIN=$DOMAIN"
    echo "APP_LOCAL_REGISTRATION_ENABLED=${APP_LOCAL_REGISTRATION_ENABLED:-false}"
  } >> "$tmp"
  mv "$tmp" "$APP_DIR/.env"
}

current_image() { grep -s '^BACKEND_IMAGE=' "$APP_DIR/.env" | cut -d= -f2- || true; }

# pg_dump in custom format, kept locally and copied to S3. Prints the file path.
backup_db() {
  local label="$1" ts file
  mkdir -p "$BACKUP_DIR"
  if ! compose ps --status running db 2>/dev/null | grep -q db; then
    log "Database not running yet; no backup needed." >&2; return 0
  fi
  ts=$(date -u +%Y%m%dT%H%M%SZ)
  file="$BACKUP_DIR/apartmentcare-$label-$ts.dump"
  compose exec -T db pg_dump -U apartmentcare -d apartmentcare -Fc > "$file"
  [ -s "$file" ] || { log "Backup file is empty: $file" >&2; return 1; }
  aws s3 cp --only-show-errors --region "$AWS_REGION" "$file" "s3://$BACKUP_BUCKET/db/$label/$(basename "$file")"
  log "Backup saved: $file (and S3 db/$label/)" >&2
  echo "$file"
}

wait_healthy() {
  local i
  for i in $(seq 1 "${HEALTH_TRIES:-36}"); do
    if curl -fs --max-time 5 http://127.0.0.1:8080/actuator/health | grep -q '"UP"'; then return 0; fi
    sleep 5
  done
  return 1
}
