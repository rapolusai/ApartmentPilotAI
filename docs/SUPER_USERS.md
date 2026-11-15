# ApartmentCare team (super user) logins

A read-only dashboard across all apartments for the ApartmentCare team. Added in migration V15.

## What it shows
- Totals: apartments, signed in within the last 30 days (anyone in the apartment), active subscriptions, on trial,
  payment due, subscription money received this month and in total, and the amount expected next month.
- One row per apartment: name, city, Admin name and mobile (tap to call), flats, members, subscription status,
  paid-until or trial end, received, expected next month, last sign-in, joined date.
- Phone: one card per apartment. Tablet (600dp and wider): a scrollable table. Search and sort. No screenshots.

Definitions:
- **Received** = verified subscription payments (`ap_provider_payment`), not maintenance collected by apartments.
- **Expected next month** = for the billing cycle that starts next calendar month: the open amount of that invoice if it
  already exists, otherwise the plan price for the apartment's flat count. The free first month counts as 0.
- **Last sign-in** = `ap_user.last_login_at` (set on each login from V15) or the newest stored session. Older
  apartments show "Not recorded" until someone signs in again.

## Security
- Team accounts live in `ap_operator`, separate from apartment logins; their sessions are in `ap_operator_session`.
  A team token works only on `/api/v1/operator/*`; apartment endpoints answer 401 to it, and apartment tokens get 401
  on `/api/v1/operator/*`.
- Accounts are created, reset and paused only with the server-side `APP_PROVIDER_KEY`. Nothing secret is in the app.
- Login: mobile + 6-digit PIN, with the same `LoginGuard` lockout as apartment logins (separate key space).

## Create or change a team login (run on the server or your PC)
Choose the PIN privately; never put it in chat, git or a document.

```bash
API=https://api.apartmentcare.rapolu.in/api/v1
# Create, or reset the PIN of, a team login
curl -sS -X POST "$API/provider/operators" -H "X-Provider-Key: $APP_PROVIDER_KEY" -H "Content-Type: application/json" \
  -d '{"name":"Rapolus","mobile":"9XXXXXXXXX","pin":"<6-digit PIN>"}'
# Pause a login (ends its sessions at once); send "active":true to restore it
curl -sS -X POST "$API/provider/operators" -H "X-Provider-Key: $APP_PROVIDER_KEY" -H "Content-Type: application/json" \
  -d '{"name":"Rapolus","mobile":"9XXXXXXXXX","active":false}'
# List team logins (no PINs are returned)
curl -sS "$API/provider/operators" -H "X-Provider-Key: $APP_PROVIDER_KEY"
```
Weak PINs (repeated digits, sequences, patterns) are refused, as for apartment logins.

## Endpoints
| Method | Path | Auth |
| --- | --- | --- |
| POST | `/api/v1/operator/login` `{mobile,pin}` | none (rate limited, lockout) |
| POST | `/api/v1/operator/logout` | team token |
| GET | `/api/v1/operator/dashboard` | team token |
| GET, POST | `/api/v1/provider/operators` | `X-Provider-Key` |
