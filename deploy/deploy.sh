#!/usr/bin/env bash
# Deploy a backend image tag: ./deploy.sh <tag>
# Backs up the database first, then switches image, health-checks, and rolls back on failure.
source /opt/apartmentcare/lib.sh
TAG="${1:?usage: deploy.sh <image-tag>}"
NEW_IMAGE="$ECR_REGISTRY/$ECR_REPO:$TAG"
PREV_IMAGE="$(current_image)"

log "Logging in to ECR"
aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$ECR_REGISTRY" >/dev/null
log "Pulling $NEW_IMAGE"
docker pull -q "$NEW_IMAGE"

log "Pre-deploy database backup"
BACKUP_FILE="$(backup_db pre-deploy)"

write_env "$NEW_IMAGE"
log "Starting stack with $NEW_IMAGE"
compose up -d --remove-orphans

if wait_healthy; then
  echo "$(date -u +%FT%TZ) $NEW_IMAGE" >> "$APP_DIR/deploy-history.log"
  log "Deploy OK: $NEW_IMAGE"
  docker image prune -af --filter "until=168h" >/dev/null || true
  exit 0
fi

log "Health check FAILED for $NEW_IMAGE"
compose logs --tail 80 app || true
if [ -n "$PREV_IMAGE" ]; then
  log "Rolling back to $PREV_IMAGE"
  write_env "$PREV_IMAGE"
  compose up -d app
  if wait_healthy; then log "Rollback healthy on $PREV_IMAGE"; else log "Rollback ALSO unhealthy, investigate now"; fi
  [ -n "$BACKUP_FILE" ] && log "If the new version ran a database migration, restore with: ./restore.sh $BACKUP_FILE --yes"
fi
exit 1
