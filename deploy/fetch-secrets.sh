#!/usr/bin/env bash
# Writes /etc/apartmentcare/app.env from AWS SSM Parameter Store so that no secret is typed on the server or
# stored in git. Run as root on the app server (the instance role must allow ssm:GetParametersByPath and
# kms:Decrypt on the /apartmentcare/<stage>/ path). Usage: sudo ./fetch-secrets.sh prod
set -euo pipefail
STAGE="${1:-prod}"
PREFIX="/apartmentcare/${STAGE}"
OUT_DIR=/etc/apartmentcare
OUT="${OUT_DIR}/app.env"
REGION="${AWS_REGION:-ap-south-1}"

install -d -m 700 "${OUT_DIR}"
TMP="$(mktemp)"
trap 'rm -f "${TMP}"' EXIT

# Expected parameters (SecureString): DB_URL DB_USER DB_PASSWORD APP_INVITE_DISPLAY_KEY APP_PROVIDER_KEY SUPPORT_EMAIL
aws ssm get-parameters-by-path --region "${REGION}" --path "${PREFIX}/" --with-decryption \
  --query 'Parameters[].[Name,Value]' --output text \
| while IFS=$'\t' read -r name value; do
    key="${name##*/}"
    printf '%s=%s\n' "${key}" "${value}" >> "${TMP}"
  done

for required in DB_URL DB_USER DB_PASSWORD APP_INVITE_DISPLAY_KEY; do
  grep -q "^${required}=" "${TMP}" || { echo "Missing ${PREFIX}/${required} in Parameter Store" >&2; exit 1; }
done

install -m 600 "${TMP}" "${OUT}"
echo "Wrote ${OUT} ($(grep -c . "${OUT}") values). Restart with: docker compose -f docker-compose.prod.yml up -d app"
