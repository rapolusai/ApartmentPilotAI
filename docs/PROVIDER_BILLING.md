# Software subscriptions and first-month referrals

This module stores operator-reviewed records. It does NOT process a Google Play/Razorpay/UPI purchase, automatically refund a payment or independently prove an installation.

## Apartment Admin flow
More → Subscription & billing shows plan, first-period end, referral code, referred apartments, invoice amounts and status. Reporting an installation records only the Admin's signal; it never qualifies a reward by itself. Referee enters the referrer's code while still in its first period.

## Backend operator interfaces
- `POST /api/v1/provider/apartments/{tenantId}/verify`
  Body: `{"verifiedBy":"authorised operator reference","identityAndApartmentVerified":true}`
- `POST /api/v1/provider/invoices/{invoiceId}/settle`
  Body: `{"reference":"unique externally verified payment reference","amount":99,"verified":true}`

Both require `X-Provider-Key`, supplied from a separate server-side `APP_PROVIDER_KEY` secret of at least32 characters. They fail closed when it is absent/unconfigured. The key must never be embedded in the Android APK, committed to source or given to an apartment Admin. These are administrative service endpoints, not authenticated evidence from a payment gateway.

For verification, the apartment must have reported its installation and have at least one approved Resident. The operator must separately verify the authorised Admin and independent new apartment before invoking the endpoint. Real provider integration is still required before claiming automated external verification.

## Chosen local-model reward semantics
- First period starts at the apartment's creation/activation date and ends at its next calendar-month anniversary, exclusive.
- Referrer and two independently verified referred apartments must qualify before each respective first period ends.
- One parent per referee; no self/circular referral. Global transaction-level promotion locking coordinates cross-apartment awards.
- Exactly one award batch per referrer. Each apartment's first invoice can be waived only once, including when it participates in a later valid chain.
- Already-paid first invoice produces a refund-review record instead of pretending money has been refunded or granting another free month. No refund-provider API is connected.
- Invoices after the first period use the ordinary category price. Registration/install signals do not reset the period.
- Category uses configured physical flat count, including vacant/inactive units, not repeated logins.
- Amounts in this local ledger are not final tax invoices; tax presentation and live billing configuration require commercial review.
- Access is enforced (corrected 2026-10-09, gap G-21; this line previously said overdue status did not restrict access). V9 gives each apartment a three-day trial (`ap_tenant.trial_ends_at`; apartments that existed at rollout got at least three days from rollout). On every authenticated request `SecurityConfig` asks `SubscriptionService.entitlement`, which uses `SubscriptionAccessPolicy`: `ACTIVE` when the subscription invoice for today is fully covered (`credit+paid=amount`), otherwise `TRIAL` before `trial_ends_at` and `PAYMENT_DUE` after it. In `PAYMENT_DUE` every authenticated API call for every role in that apartment returns **HTTP 402** `{"code":"SUBSCRIPTION_REQUIRED","message":"Subscription required. Your apartment trial has ended. …"}`. The only exceptions are `POST /api/v1/logout` for anyone and, for the Admin only, `GET /api/v1/ops/subscription`, `POST /api/v1/ops/subscription/offer`, `POST /api/v1/ops/subscription/install` and `POST /api/v1/ops/referrals/claim`. Public routes (status, register, join, login) are not affected. There is no grace period and no read-only mode. Checkout is not configured (`checkoutConfigured=false`), so an invoice is covered only by a provider-recorded payment or a referral credit. A production grace/read-only policy and real purchase verification are still to be designed with the actual purchase provider.

Do not enable or call these endpoints on real customer data until the full commercial, billing, security and store-policy decisions are finalised.
