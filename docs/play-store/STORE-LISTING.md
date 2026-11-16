# ApartmentCare: Play Console store listing (version 1.0.0, 15 Nov 2026)

Paste into Play Console > Grow users > Store presence > Main store listing. Updated 16 Nov: launch without SMS (the Admin approves residents and resets forgotten PINs), Play subscriptions, one support email, one privacy URL.

## App name (30 max)
ApartmentCare

## Short description (80 max)
Maintenance dues, payments, expenses, complaints and notices for your apartment.

## Full description
ApartmentCare helps apartment associations run their building in one place: maintenance dues, payment verification, expenses, complaints and notices, with separate access for the Admin, the Treasurer and residents.

FOR ADMINS
- Register your apartment and its flats in minutes
- Invite residents with a code and approve each flat claim yourself
- Set the monthly maintenance amount and generate bills for every flat
- Publish notices to every resident's in-app inbox
- Assign a Treasurer to share the financial work
- Reset a resident's forgotten PIN from the Members page

FOR TREASURERS
- Review payment declarations and approve them with a digital receipt
- Record expenses and see them in the cashbook
- Monthly reports of collections, expenses and balance
- Two-person rule: the person who records a payment cannot approve it

FOR RESIDENTS AND FLAT OWNERS
- See your flat's dues and payment history
- Tell the association you have paid (UPI, bank transfer or cash) and get a receipt once it is verified
- Report building issues and follow their progress
- Read notices and community updates

PRIVACY FIRST
- No advertisements, no analytics, no tracking
- Each apartment's data is visible only to its own members
- Forgot your PIN? Your apartment Admin can set a new one
- Delete your account from inside the app at any time

SUBSCRIPTION
Free for 5 days for a new apartment. After that, the apartment's Admin subscribes monthly through Google Play; one subscription covers the Admin, the Treasurer and every resident. Cancel any time in Google Play.

ApartmentCare records and verifies maintenance payments made directly to your association; it does not move that money or take card details. Not an emergency service.

## Release notes (1.0.0)
First release of ApartmentCare: apartment registration, resident invites with Admin approval, monthly maintenance bills, payment declarations with verified digital receipts, expenses and cashbook, monthly reports, complaints and building issues, notices, PIN reset by the Admin, and the apartment subscription through Google Play.

## Store settings
| Field | Value |
|---|---|
| Category | House & Home |
| Tags | Apartment management, Home services |
| Contact email | support@rapolu.in |
| Website | https://rapolu.in/apartmentcare |
| Privacy policy URL | https://api.apartmentcare.rapolu.in/privacy (live, shows support@rapolu.in). Use this one until the rapolu.in pages fix the "gamil" typo. |
| Account deletion URL | https://api.apartmentcare.rapolu.in/delete-account |
| Promo video (optional) | YouTube URL of the English demo, if it is uploaded (public or unlisted, ads off) |

## Graphics (all in playstore-upload/)
| Slot | File | Status |
|---|---|---|
| App icon 512x512 | app-icon-512.png | Ready |
| Feature graphic 1024x500 | feature-graphic-1024x500.png | Ready |
| Phone screenshots (2-8) | modern-screenshots/phone-screenshots/01..07 (1080x1920, captions) | Ready; recapture planned from the release build |
| 10-inch tablet screenshots | modern-screenshots/tablet-10in-screenshots/01..07 (1620x2880) | Ready |
| 7-inch tablet screenshots | reuse the 10-inch set | Optional |

## Data safety answers (confirm each on the Console page)
| Data type | Collected | Shared | Why | Notes |
|---|---|---|---|---|
| Name | Yes | No | App functionality, account management | Required |
| Phone number | Yes | No | App functionality, account management | Required (used as the sign-in ID) |
| Purchase history | Yes (subscription status from Google Play) | No | App functionality | Admin only |
| Other financial info (maintenance payments, receipts) | Yes | No | App functionality | Entered by users |
| Photos / files (payment proofs, expense attachments) | Yes, if the user attaches one | No | App functionality | Optional |
| Other user-generated content (complaints, notices) | Yes | No | App functionality | Optional |
| App activity / diagnostics / location / contacts | No | | | |

Encrypted in transit: Yes. Users can request deletion: Yes (in app and the deletion URL). Data sold: No.
