# Page-wise functionality and data traceability

Generated 27 September 2026 from the frozen 203-route reference and current working source. This is a source-audit inventory, **not QA approval or a completion percentage**.

For the complete **design requirements**, including late-added/overridden handlers, full validations and conditional states, use [REFERENCE_FUNCTIONAL_REQUIREMENTS.md](REFERENCE_FUNCTIONAL_REQUIREMENTS.md). The API/SQL traces here are a separate implementation audit and do not define the required behavior.

## How to read this list

- Every reference route is listed, including 8 reference-only tools.
- Actions are source definitions, including conditional branches and repeated-row templates. They are not an exact count of buttons simultaneously rendered. Filters can produce several buttons; data lists can produce any number of row buttons.
- Shared Back, theme, navigation, notifications and development-only role controls are described separately, not counted on every page.
- Reference form/actions describe design intent. They do not prove the Android implementation has every control.
- APIs and database tables below are conservative static call traces from current native destinations to controllers/services. Dynamic API expressions, polymorphic calls and branches require manual review. Read/write lists are **potential tables**, not a per-button transaction proof. Helper methods may include side effects.
- No endpoint match, no database trace, or no direct native route is explicitly marked unresolved. A merged destination is not a completed reference page.
- Each action still needs a manually confirmed native-control → API → validation → SQL → test link before acceptance.

## Inventory totals

203 routes; 505 extracted action/control definitions (not a runtime button total). 163 pages have at least one source-resolved backend call; others require tracing or are local-only.

## Shared controls

| Control | Function | Persistence / validation |
|---|---|---|
| Back | Previous native destination; Android Back must match | Navigation state; no direct business-table write |
| Theme | Light/dark appearance | Local preference; no business-table write |
| Bottom navigation | Home, Money/Dues, Issues, Community, More | Server-derived role governs accessible APIs; selection is not permission |
| Notifications | Open authenticated inbox | ap_inbox, ap_user; recipient/tenant scope enforced by backend |
| Reference role switch/reset/fake clock | Prototype tools only | Must not become production authorization or reset API |

## Page index

| # | Group | Page | Source status | Action definitions |
|---:|---|---|---|---:|
| 1 | Overview | [Home (home)](#page-1) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 20 |
| 2 | Finance | [Money (money)](#page-2) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 13 |
| 3 | Finance | [Maintenance (dues)](#page-3) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 6 |
| 4 | Finance | [Maintenance bill (bill)](#page-4) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 5 |
| 5 | Finance | [Payment details (payee)](#page-5) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 6 | Finance | [Payment details (mark-paid)](#page-6) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 7 | Finance | [Payment status (payment-status)](#page-7) | CONNECTED LOCAL EMULATOR VERIFIED | 3 |
| 8 | Finance | [Payment approvals (approvals)](#page-8) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 9 | Finance | [Review payment (payment)](#page-9) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 10 | Finance | [Payment proof (proof)](#page-10) | CONNECTED LOCAL EMULATOR VERIFIED | 3 |
| 11 | Finance | [Select payments (bulk-approval)](#page-11) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 12 | Finance | [Confirm approval (bulk-review)](#page-12) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 13 | Finance | [Reject payment (payment-reject)](#page-13) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 14 | Finance | [Payment receipt (receipt)](#page-14) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 15 | Finance | [Payment history (history)](#page-15) | CONNECTED LOCAL EMULATOR VERIFIED | 1 |
| 16 | Finance | [Unpaid flats (outstanding)](#page-16) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 17 | Finance | [Send reminders (reminder-preview)](#page-17) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 18 | Finance | [Maintenance amount (billing-settings)](#page-18) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 5 |
| 19 | Finance | [Generate monthly dues (bill-preview)](#page-19) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 20 | Finance | [Expenses (expenses)](#page-20) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 6 |
| 21 | Finance | [Expense detail (expense)](#page-21) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 22 | Finance | [Expense attachment (expense-proof)](#page-22) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 2 |
| 23 | Finance | [Expense (expense-form)](#page-23) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 24 | Finance | [Recurring expenses (recurring)](#page-24) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 25 | Finance | [Recurring expense (recurring-detail)](#page-25) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 26 | Finance | [Recurring setup (recurring-form)](#page-26) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 27 | Reports | [Monthly summary (reports)](#page-27) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 6 |
| 28 | Reports | [Expense breakdown (expense-report)](#page-28) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 29 | Reports | [Collection report (collection-report)](#page-29) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 30 | Reports | [Resident finance view (transparency)](#page-30) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 31 | Reports | [Export report (export-report)](#page-31) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 32 | Finance | [Other income (income)](#page-32) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 33 | Finance | [Income entry (income-form)](#page-33) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 34 | Reports | [Cashbook (cashbook)](#page-34) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 35 | Settings | [Financial controls (finance-settings)](#page-35) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 5 |
| 36 | Settings | [Association account (payment-settings)](#page-36) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 37 | Settings | [Opening balance (opening-balance)](#page-37) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 38 | Services | [Building issues (issues)](#page-38) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 6 |
| 39 | Services | [What needs attention? (issue-categories)](#page-39) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 0 |
| 40 | Services | [Report an issue (issue-form)](#page-40) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 41 | Services | [Similar issues (similar-issues)](#page-41) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 42 | Services | [Issue detail (issue)](#page-42) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 8 |
| 43 | Services | [Affected flats (affected)](#page-43) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 44 | Services | [Assign service (issue-assign)](#page-44) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 45 | Services | [Update issue (issue-update)](#page-45) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 46 | Services | [Issue updates (issue-comments)](#page-46) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 47 | Services | [Issue photo (issue-photo)](#page-47) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 0 |
| 48 | Services | [My requests (complaints)](#page-48) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 49 | Services | [Private request (complaint-form)](#page-49) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 50 | Services | [Request detail (complaint)](#page-50) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 6 |
| 51 | Services | [Manage request (complaint-update)](#page-51) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 52 | Services | [Service schedule (services)](#page-52) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 53 | Services | [Scheduled service (service)](#page-53) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 54 | Services | [Schedule service (service-form)](#page-54) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 55 | Services | [Complete service (service-complete)](#page-55) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 56 | Services | [Service history (service-history)](#page-56) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 57 | Community | [Community (community)](#page-57) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 13 |
| 58 | Community | [Notice board (notices)](#page-58) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 59 | Community | [Notice (notice)](#page-59) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 7 |
| 60 | Community | [Send notice (notice-form)](#page-60) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 61 | Community | [Notice attachment (notice-attachment)](#page-61) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 62 | Community | [Service vendors (vendors)](#page-62) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 63 | Community | [Vendor profile (vendor)](#page-63) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 5 |
| 64 | Community | [Vendor details (vendor-form)](#page-64) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 65 | Community | [Important contacts (contacts)](#page-65) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 66 | Community | [Contact details (contact)](#page-66) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 67 | Community | [Edit contact (contact-form)](#page-67) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 68 | Community | [Apartment files (documents)](#page-68) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 69 | Community | [Document (document)](#page-69) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 70 | Community | [Upload document (document-form)](#page-70) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 71 | Onboarding | [ApartmentCare (welcome)](#page-71) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 0 |
| 72 | Onboarding | [Choose login (login)](#page-72) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 0 |
| 73 | Onboarding | [Admin login (admin-login)](#page-73) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 6 |
| 74 | Onboarding | [Treasurer login (treasurer-login)](#page-74) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 6 |
| 75 | Onboarding | [Resident login (resident-login)](#page-75) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 6 |
| 76 | Onboarding | [Reset PIN (forgot-pin)](#page-76) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 77 | Onboarding | [Verify number (verify-reset)](#page-77) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 78 | Onboarding | [Set new PIN (reset-pin)](#page-78) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 79 | Onboarding | [Join your apartment (join)](#page-79) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 80 | Onboarding | [Invite unavailable (invite-invalid)](#page-80) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 81 | Onboarding | [Create your account (signup)](#page-81) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 82 | Onboarding | [Choose your flat (choose-flat)](#page-82) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 83 | Onboarding | [Join request (signup-pending)](#page-83) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 84 | Onboarding | [Create apartment (admin-signup)](#page-84) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 85 | Onboarding | [Apartment details (setup-apartment)](#page-85) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 86 | Onboarding | [Apartment ready (setup-done)](#page-86) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 87 | Administration | [Apartment (apartment)](#page-87) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 7 |
| 88 | Administration | [Apartment profile (apartment-edit)](#page-88) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 89 | Administration | [Blocks (blocks)](#page-89) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 90 | Administration | [Block details (block-form)](#page-90) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 91 | Administration | [Flats (flats)](#page-91) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 92 | Administration | [Flat details (flat)](#page-92) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 4 |
| 93 | Administration | [Flat setup (flat-form)](#page-93) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 94 | Administration | [Members (residents)](#page-94) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 0 |
| 95 | Administration | [Resident profile (resident)](#page-95) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 3 |
| 96 | Administration | [Join requests (join-requests)](#page-96) | CONNECTED LOCAL EMULATOR VERIFIED | 2 |
| 97 | Administration | [Review resident (join-review)](#page-97) | CONNECTED LOCAL EMULATOR VERIFIED | 2 |
| 98 | Administration | [Reject join request (join-reject)](#page-98) | CONNECTED LOCAL EMULATOR VERIFIED | 1 |
| 99 | Administration | [Invite residents (invite)](#page-99) | CONNECTED LOCAL EMULATOR VERIFIED | 5 |
| 100 | Administration | [Invite controls (invite-settings)](#page-100) | CONNECTED LOCAL EMULATOR VERIFIED | 3 |
| 101 | Administration | [Resident handover (handover)](#page-101) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 102 | Administration | [Team access (roles)](#page-102) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 103 | Administration | [Team role (role-form)](#page-103) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 104 | Settings | [Permissions (permissions)](#page-104) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 0 |
| 105 | Settings | [More (more)](#page-105) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 14 |
| 106 | Settings | [My profile (profile)](#page-106) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 107 | Settings | [Edit profile (profile-edit)](#page-107) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 108 | Settings | [Notifications (notifications)](#page-108) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 109 | Settings | [Notification (notification)](#page-109) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 110 | Settings | [Notifications (notification-settings)](#page-110) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 7 |
| 111 | Settings | [Reminder schedule (reminder-settings)](#page-111) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 2 |
| 112 | Settings | [Appearance (appearance)](#page-112) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 113 | Settings | [Account security (security)](#page-113) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 4 |
| 114 | Settings | [Change PIN (change-pin)](#page-114) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 115 | Settings | [Privacy & access (privacy)](#page-115) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 116 | Settings | [Automation (automation)](#page-116) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 6 |
| 117 | Settings | [Automation activity (automation-log)](#page-117) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 118 | Settings | [Activity log (audit)](#page-118) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 119 | Settings | [Activity detail (audit-detail)](#page-119) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 0 |
| 120 | Settings | [Subscription & billing (subscription)](#page-120) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 121 | Settings | [Help & about (help)](#page-121) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 122 | Settings | [Payment status guide (payment-help)](#page-122) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 123 | Settings | [Get help (support)](#page-123) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 124 | Settings | [About (about)](#page-124) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 125 | System states | [You’re offline (offline)](#page-125) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 126 | System states | [Session expired (session-expired)](#page-126) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 127 | System states | [Access restricted (access-denied)](#page-127) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 128 | System states | [Nothing here yet (empty-state)](#page-128) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 129 | System states | [Something went wrong (error-state)](#page-129) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 130 | System states | [Page not found (not-found)](#page-130) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 131 | Reference tools | [Screen library (screen-map)](#page-131) | REFERENCE ONLY NOT A PRODUCTION FEATURE | 0 |
| 132 | Reference tools | [Preview controls (demo-controls)](#page-132) | REFERENCE ONLY NOT A PRODUCTION FEATURE | 7 |
| 133 | Services | [Request attachment (complaint-photo)](#page-133) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 134 | System states | [Loading (loading-state)](#page-134) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 135 | Community | [Events & bookings (events)](#page-135) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 8 |
| 136 | Community | [What are you planning? (event-types)](#page-136) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 137 | Community | [Request an event (event-form)](#page-137) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 138 | Community | [Choose your parking (event-availability)](#page-138) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 3 |
| 139 | Community | [Review request (event-review)](#page-139) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 3 |
| 140 | Community | [Request sent (event-submitted)](#page-140) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 141 | Community | [Event details (event)](#page-141) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 7 |
| 142 | Community | [Booking approvals (booking-requests)](#page-142) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 2 |
| 143 | Community | [Review booking (booking-review)](#page-143) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 3 |
| 144 | Community | [Offer an alternative (booking-alternative)](#page-144) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 145 | Community | [Alternative offered (event-alternative)](#page-145) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 2 |
| 146 | Community | [Reject request (booking-reject)](#page-146) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 147 | Community | [Cancel booking (event-cancel)](#page-147) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 2 |
| 148 | Community | [Complete event (event-complete)](#page-148) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 149 | Community | [Booking calendar (event-calendar)](#page-149) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 150 | Community | [Guest parking (parking-availability)](#page-150) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 151 | Community | [Spaces & parking (resources)](#page-151) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 4 |
| 152 | Community | [Space details (resource)](#page-152) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 153 | Community | [Configure space (resource-form)](#page-153) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 154 | Community | [Unavailable dates (resource-blocks)](#page-154) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 2 |
| 155 | Community | [Block a space (resource-block-form)](#page-155) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 156 | Community | [Share my parking (parking-share)](#page-156) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 157 | Community | [Booking rules (booking-rules)](#page-157) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 158 | Community | [Booking settings (booking-rules-edit)](#page-158) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 159 | Community | [Members (members)](#page-159) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 0 |
| 160 | Community | [Member details (member)](#page-160) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 4 |
| 161 | Administration | [Add / edit member (member-form)](#page-161) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 162 | Community | [Apartment committee (committee)](#page-162) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 163 | Community | [Committee member (committee-member)](#page-163) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 2 |
| 164 | Administration | [Committee role (committee-form)](#page-164) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 165 | Overview | [Approvals (approval-inbox)](#page-165) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 166 | Community | [Choose a notice (notice-templates)](#page-166) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 167 | Community | [Preview notice (notice-preview)](#page-167) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 3 |
| 168 | Community | [Audience & responses (notice-delivery)](#page-168) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 169 | Finance | [Maintenance rate history (rate-history)](#page-169) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 170 | Finance | [Flat-specific rates (rate-overrides)](#page-170) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 171 | Finance | [Set flat rate (rate-override-form)](#page-171) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 172 | Finance | [One-time collections (contributions)](#page-172) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 173 | Finance | [One-time contribution (contribution-form)](#page-173) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 174 | Finance | [Review contribution (contribution-preview)](#page-174) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 175 | Finance | [Contribution details (contribution)](#page-175) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 0 |
| 176 | Finance | [Opening dues by flat (opening-dues)](#page-176) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 177 | Finance | [Add opening due (opening-due-form)](#page-177) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 178 | Settings | [Application charges (subscription-plans)](#page-178) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 0 |
| 179 | Settings | [Subscription invoices (subscription-invoices)](#page-179) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 180 | Settings | [Application invoice (subscription-invoice)](#page-180) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 181 | Settings | [Renewal status (subscription-renewal)](#page-181) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 182 | Settings | [Refer & unlock (referrals)](#page-182) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 5 |
| 183 | Settings | [First-month offer (referral-offer)](#page-183) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 184 | Settings | [Referral progress (referral-progress)](#page-184) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 3 |
| 185 | Settings | [New apartment referral (referral-add)](#page-185) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 186 | Settings | [Referral details (referral-detail)](#page-186) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 0 |
| 187 | Settings | [First month reward (referral-reward)](#page-187) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 4 |
| 188 | Settings | [Referral rules (referral-rules)](#page-188) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 0 |
| 189 | Settings | [Subscription overdue (subscription-overdue)](#page-189) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 190 | Reference tools | [Provider simulation (provider-simulation)](#page-190) | REFERENCE ONLY NOT A PRODUCTION FEATURE | 4 |
| 191 | Reference tools | [Verify activation · demo (activation-simulation)](#page-191) | REFERENCE ONLY NOT A PRODUCTION FEATURE | 1 |
| 192 | Reference tools | [Subscription payment · demo (subscription-payment-simulation)](#page-192) | REFERENCE ONLY NOT A PRODUCTION FEATURE | 1 |
| 193 | Community | [Community polls (polls)](#page-193) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 194 | Community | [New poll (poll-form)](#page-194) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 195 | Community | [Vote & results (poll)](#page-195) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 1 |
| 196 | Community | [Vehicles (vehicles)](#page-196) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 197 | Community | [Vehicle details (vehicle)](#page-197) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 198 | Community | [My vehicle (vehicle-form)](#page-198) | CONNECTED SOURCE NOT RUNTIME VERIFIED | 2 |
| 199 | Community | [Notify vehicle owner (vehicle-contact)](#page-199) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 200 | Administration | [Committee handover (committee-handover)](#page-200) | PARTIAL MERGED SOURCE NOT RUNTIME VERIFIED | 1 |
| 201 | Reference tools | [Demo clock (preview-clock)](#page-201) | REFERENCE ONLY NOT A PRODUCTION FEATURE | 2 |
| 202 | Reference tools | [Included in this reference (release-coverage)](#page-202) | REFERENCE ONLY NOT A PRODUCTION FEATURE | 1 |
| 203 | Reference tools | [Refund credit · demo (subscription-refund-simulation)](#page-203) | REFERENCE ONLY NOT A PRODUCTION FEATURE | 1 |

<a id="page-1"></a>

## 1. Home — home

Group: Overview. Reference roles: Admin, Treasurer, Resident. Native destination: home. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:226). Native entry: [home](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:733).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'My dues' | navigate / custom | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:232) |
| 'View receipt' | navigate / custom | 'history' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:232) |
| 'Track payment' | navigate / custom | 'payment-status?id='+S.payments.find(p=>p.bill===rb.id&&p.status==='pending').id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:232) |
| "I've paid" | navigate / custom | 'mark-paid?id='+rb.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:232) |
| 'History' | navigate | 'history' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:233) |
| 'Report issue' | navigate | 'issue-categories' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:233) |
| 'My requests' | navigate | 'complaints' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:233) |
| 'Contacts' | navigate | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:233) |
| 'View all' | navigate / section | 'issues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:234) |
| 'View all' | navigate / section | 'notices' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:235) |
| 'View all' | navigate / section | 'reports' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:236) |
| 'Dues' | navigate | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:241) |
| 'Expense' | navigate | 'expense-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:241) |
| 'Notice' | navigate | 'notice-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:241) |
| 'Invite' | navigate | 'invite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:241) |
| 'Report' | navigate | 'reports' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:241) |
| 'View all' | navigate / section | 'issues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:242) |
| 'View all' | navigate / section | 'audit' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:243) |
| 'Payment declarations' | navigate / row | 'approvals' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:243) |
| 'Lift service' | navigate / row | 'service?id=SVC-1' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:243) |

### Current native API and database trace

- GET /api/v1/bills — expression "/bills?month="+month — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:733)
- GET /api/v1/billing/rules — expression "/billing/rules" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:733)
- GET /api/v1/ops/tickets — expression "/ops/tickets?kind=ISSUE" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:830)
- GET /api/v1/notices — expression "/notices" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:830)
- GET /api/v1/reports/monthly — expression "/reports/monthly?month="+month — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:766)

Potential reads: ap_bill, ap_billing_rule, ap_contact, ap_flat, ap_notice, ap_notice_recipient, ap_payment, ap_payment_reversal, ap_settings, ap_ticket, ap_ticket_follow, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required
- throw new IllegalArgumentException("Invalid issue type.")
- throw ApiError.forbidden()

Backend source pointers: [bills](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:112), [bills](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:117), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [rules](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:93), [rules](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:36), [tickets](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:160), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:17), [notices](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:146), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:16), [report](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:143), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:238), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [sumDate](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:272), [before](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:275).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-2"></a>

## 2. Money — money

Group: Finance. Reference roles: Admin, Treasurer. Native destination: money. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247). Native entry: [money](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:418).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| Month picker | action | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Financial settings' | icon action | 'data-go="finance-settings"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Dues' | navigate | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Approvals' | navigate | 'approvals' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Expense' | navigate | 'expense-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Reports' | navigate | 'reports' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Monthly dues' | navigate / row | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Payments' | navigate / row | 'approvals' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Expenses' | navigate / row | 'expenses' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Recurring expenses' | navigate / row | 'recurring' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Other income' | navigate / row | 'income' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Cashbook' | navigate / row | 'cashbook' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Billing settings' | navigate / row | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |

Reference action/form behavior (prototype intent; never backend security proof):

- **month**: ()=>sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-3"></a>

## 3. Maintenance — dues

Group: Finance. Reference roles: Admin, Treasurer, Resident. Native destination: dues. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:248). Native entry: [dues](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:859).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| Month picker | action | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252) |
| 'Billing settings' | icon action | 'data-go="billing-settings"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252) |
| ['All','Pending','In review','Paid'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252) |
| b.flat | navigate / row | 'bill?id='+b.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:254) |
| 'Unpaid flats' | navigate / custom | 'outstanding' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252) |
| 'Generate' | navigate / custom | 'bill-preview' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252) |

Reference action/form behavior (prototype intent; never backend security proof):

- **month**: ()=>sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)

### Current native API and database trace

- GET /api/v1/bills — expression "/bills?month="+month — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:859)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [bills](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:112), [bills](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:117), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-4"></a>

## 4. Maintenance bill — bill

Group: Finance. Reference roles: Admin, Treasurer, Resident. Native destination: bill. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255). Native entry: [bill](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:880).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| isStaff()?'Record payment':"I've paid" | navigate / custom | 'mark-paid?id='+b.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255) |
| isStaff()?'Review payment':'Track payment' | navigate / custom | (isStaff()?'payment':'payment-status')+'?id='+pending.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255) |
| 'Payment details' | navigate / custom | 'payee' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255) |
| INR(p.amount) | navigate / row | 'receipt?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255) |

### Current native API and database trace

- GET /api/v1/bills/{value} — expression "/bills/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:880)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()

Backend source pointers: [bill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:115), [bill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:127), [ownedBill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:135), [balance](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:21).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-5"></a>

## 5. Payment details — payee

Group: Finance. Reference roles: Admin, Treasurer, Resident. Native destination: payee. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:256). Native entry: [payee](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1956).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Edit payment details' | navigate / custom | 'payment-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:256) |
| 'Contact treasurer' | navigate / custom | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:256) |
| 'Copy UPI ID' | action | copyUPI | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:256) |

Reference action/form behavior (prototype intent; never backend security proof):

- **copyUPI**: ()=>{if(!S.settings.upi)fail('No UPI ID has been added.');return shareText(S.settings.upi,'UPI ID');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:507)

### Current native API and database trace

- GET /api/v1/ops/settings — expression "/ops/settings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1956)

Potential reads: ap_settings.

Potential writes: ap_settings.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:94), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:18), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-6"></a>

## 6. Payment details — mark-paid

Group: Finance. Reference roles: Admin, Treasurer, Resident. Native destination: mark-paid. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257). Native entry: [markPaid](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:904).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'View bill' | navigate / custom | 'bill?id='+b.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257) |
| 'Submit payment' | submit | payment-submit | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257) |

Reference input/form definitions (attributes and allowed choices shown literally):

- amountField('amount','Amount paid',due(b),'max="${due(b)}"')
- selectField('mode','Paid using',['UPI','Cash','Bank transfer','Cheque'],'UPI','data-dynamic="payment-mode"')
- field('ref','UPI / bank reference','','text','maxlength="40" placeholder="Enter reference number"')
- field('date','Payment date',today,'date','max="${today}" required')
- fileField('Payment proof · optional')
- field('note','Note · optional','','textarea','maxlength="300" placeholder="e.g. Cash handed to treasurer"')
- checkField('paidConfirm','I have already made this payment.')

Reference action/form behavior (prototype intent; never backend security proof):

- **payment-submit**: async(D,F)=>{const b=bill(D.billId);if(!canBill(b))fail('You cannot submit for this flat.');if(billPending(b))fail('A payment is already in review for this bill.');const amount=validateAmount(D.amount);if(amount>due(b))fail('The amount exceeds the current balance.');if(!F.has('paidConfirm'))fail('Confirm that you have already made the payment.');if(!D.date\|\|D.date>today)fail('Choose a valid payment date.');const ref=safeText(D.ref,40);if(D.mode!=='Cash'&&!ref)fail('Enter the transaction / cheque reference.');if(ref&&S.payments.some(p=>p.ref===ref&&p.mode===D.mode&&p.status!=='rejected'))fail('This payment reference has already been submitted. — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)

### Current native API and database trace

- GET /api/v1/bills/{value} — expression "/bills/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:904)
- POST /api/v1/payments — expression "/payments" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:904)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal, ap_settings, ap_tenant, ap_user.

Potential writes: ap_audit, ap_inbox, ap_payment.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()
- Request DTO public record Payment(@NotNull UUID billId,@NotNull BigDecimal amount, @Pattern(regexp="UPI\|CASH\|BANK_TRANSFER\|CHEQUE") @NotNull String mode, @Size(max=80) String reference,@NotNull LocalDate paidOn,@Size(max=300) String note,@NotNull UUID requestKey)
- throw ApiError.conflict("This request key was already used with different payment details.")
- throw new IllegalArgumentException("Enter the transaction or cheque reference.")
- throw ApiError.conflict("This bill already has a payment under review.")
- throw new IllegalArgumentException("Payment date must not precede the reconciled opening balance date.")

Backend source pointers: [bill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:115), [bill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:127), [ownedBill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:135), [balance](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:21), [Payment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:18), [submit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:124), [submit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:140), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [checkOpening](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:268), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57), [notifyStaff](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:63), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-7"></a>

## 7. Payment status — payment-status

Group: Finance. Reference roles: Admin, Treasurer, Resident. Native destination: payment-status. Recorded status: CONNECTED_LOCAL_EMULATOR_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258). Native entry: [status](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/PaymentScreens.java:45).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'View receipt' | navigate / custom | 'receipt?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258) |
| 'Resubmit details' | navigate / custom | 'mark-paid?id='+p.bill | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258) |
| 'Back to maintenance' | navigate / custom | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258) |

### Current native API and database trace

- GET /api/v1/payments/{value} — expression "/payments/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/PaymentScreens.java:45)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()

Backend source pointers: [payment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:121), [payment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:169).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-8"></a>

## 8. Payment approvals — approvals

Group: Finance. Reference roles: Admin, Treasurer. Native destination: approvals. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259). Native entry: [payments](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:976).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| ['Pending','Approved','Rejected','All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259) |
| p.flat+' · '+INR(p.amount) | navigate / row | 'payment?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259) |

### Current native API and database trace

- GET /api/v1/payments — expression "/payments" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:976)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [payments](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:118), [payments](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:160), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-9"></a>

## 9. Review payment — payment

Group: Finance. Reference roles: Admin, Treasurer. Native destination: payment. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260). Native entry: [payment](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1217).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'View payment proof' | navigate / custom | 'proof?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260) |
| 'Reject' | navigate / custom | 'payment-reject?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260) |
| 'Approve' | action | approvePayment | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260) |
| 'Receipt' | navigate / custom | 'receipt?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260) |

Reference action/form behavior (prototype intent; never backend security proof):

- **approvePayment**: d=>{requireStaff();let p=pay(d.id);if(!p\|\|p.status!=='pending')fail('This payment is no longer pending.');sheet('Approve payment','<div class="card mb">${kv('Flat',p.flat)}${kv('Amount',INR(p.amount))}${kv('Mode',p.mode)}</div>'+form('approve-single','<input type="hidden" name="id" value="${p.id}">${checkField('verified',p.mode==='Cash'?'I confirm the cash was received.':'I matched this payment to the bank record.')}','Approve & issue receipt','checkCircle'));} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:502)

### Current native API and database trace

- GET /api/v1/payments/{value} — expression "/payments/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1217)
- POST /api/v1/payments/approve — expression "/payments/approve" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1217)

Potential reads: ap_bill, ap_file, ap_flat, ap_payment, ap_payment_reversal, ap_settings, ap_tenant, ap_user.

Potential writes: ap_audit, ap_inbox, ap_payment.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()
- Request DTO public record Review(@NotEmpty @Size(max=100) List<@NotNull UUID> paymentIds,boolean verified)
- throw new IllegalArgumentException("Confirm you verified every selected payment.")
- throw new IllegalArgumentException("Duplicate payment selection.")
- throw ApiError.conflict("A selected payment was rejected. Refresh the list.")
- throw ApiError.conflict("A selected payment requires proof before approval.")
- throw ApiError.conflict("The selected total exceeds a bill balance. No payments were approved.")
- Server Admin/Treasurer role required
- throw new IllegalArgumentException("Payment date must not precede the reconciled opening balance date.")

Backend source pointers: [payment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:121), [payment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:169), [Review](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:20), [approve](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:127), [approve](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:176), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [checkOpening](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:268), [balance](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:21), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-10"></a>

## 10. Payment proof — proof

Group: Finance. Reference roles: Admin, Treasurer, Resident. Native destination: proof. Recorded status: CONNECTED_LOCAL_EMULATOR_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:261). Native entry: [proof](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/PaymentScreens.java:109).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Download proof' | action | downloadProof | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:261) |
| 'Back to review' | navigate / custom | 'payment?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:261) |

Reference action/form behavior (prototype intent; never backend security proof):

- **downloadProof**: d=>{let p=pay(d.id);if(!p\|\|(!isStaff()&&p.flat!==S.residentFlat))fail('Access denied.');downloadData(p.proof);} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:508)

### Current native API and database trace

- GET /api/v1/payments/{value} — expression "/payments/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/PaymentScreens.java:109)
- GET /api/v1/ops/files — expression "/ops/files?kind=PAYMENT&parentId="+p.optString("id") — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/PaymentScreens.java:109)
- GET /api/v1/ops/files/{value} — expression "/ops/files/"+file.optString("id") — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/PaymentScreens.java:109)

Potential reads: ap_bill, ap_document, ap_expense, ap_file, ap_flat, ap_payment, ap_payment_reversal, ap_settings, ap_user.

Potential writes: ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()
- throw ApiError.conflict("Attach files before publishing.")
- throw new IllegalArgumentException("Unsupported attachment type.")
- Server Admin/Treasurer role required

Backend source pointers: [payment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:121), [payment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:169), [files](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:208), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:78), [access](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:48), [document](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:26), [bill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:127), [ownedBill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:135), [balance](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:21), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [file](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:215), [content](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:118).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-11"></a>

## 11. Select payments — bulk-approval

Group: Finance. Reference roles: Admin, Treasurer. Native destination: bulk-approval. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:262). Native entry: [bulkApproval](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:998).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Review selected' | action | reviewBulk | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:262) |
| Select all | inline button | selectAllPayments | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:262) |

Reference action/form behavior (prototype intent; never backend security proof):

- **reviewBulk**: ()=>{requireStaff();if(!selected.size)fail('Select at least one payment.');go('bulk-review');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:504)
- **selectAllPayments**: ()=>{S.payments.filter(p=>p.status==='pending').forEach(p=>selected.add(p.id));render();} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:503)

### Current native API and database trace

- GET /api/v1/payments — expression "/payments" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:998)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [payments](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:118), [payments](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:160), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-12"></a>

## 12. Confirm approval — bulk-review

Group: Finance. Reference roles: Admin, Treasurer. Native destination: bulk-review. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:263). Native entry: [bulkReview](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1049).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| p.flat | navigate / row | 'payment?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:263) |
| 'Approve '+ps.length+' payments' | submit | bulk-confirm | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:263) |

Reference input/form definitions (attributes and allowed choices shown literally):

- checkField('verified','I have verified every selected payment.')

Reference action/form behavior (prototype intent; never backend security proof):

- **bulk-confirm**: D=>{if(!D.verified)fail('Confirm verification of every selected payment.');const ids=[...selected];if(!ids.length)fail('No payment selected.');const balances={};ids.forEach(id=>{const p=pay(id);if(!p\|\|p.status!=='pending')fail('A selected payment was already reviewed. Refresh the selection.');balances[p.bill]=(balances[p.bill]\|\|0)+p.amount;if(balances[p.bill]>due(bill(p.bill)))fail('Selected total exceeds a bill balance.');});ids.forEach(approveOne);selected.clear();save();go('approvals');toast(ids.length+' payments approved. Receipts ready.');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:552)

### Current native API and database trace

- GET /api/v1/payments — expression "/payments" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1049)
- POST /api/v1/payments/approve — expression "/payments/approve" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1049)

Potential reads: ap_bill, ap_file, ap_flat, ap_payment, ap_payment_reversal, ap_settings, ap_tenant, ap_user.

Potential writes: ap_audit, ap_inbox, ap_payment.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Request DTO public record Review(@NotEmpty @Size(max=100) List<@NotNull UUID> paymentIds,boolean verified)
- throw new IllegalArgumentException("Confirm you verified every selected payment.")
- throw new IllegalArgumentException("Duplicate payment selection.")
- throw ApiError.conflict("A selected payment was rejected. Refresh the list.")
- throw ApiError.conflict("A selected payment requires proof before approval.")
- throw ApiError.conflict("The selected total exceeds a bill balance. No payments were approved.")
- Server Admin/Treasurer role required
- throw new IllegalArgumentException("Payment date must not precede the reconciled opening balance date.")

Backend source pointers: [payments](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:118), [payments](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:160), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [Review](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:20), [approve](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:127), [approve](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:176), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [checkOpening](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:268), [balance](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:21), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-13"></a>

## 13. Reject payment — payment-reject

Group: Finance. Reference roles: Admin, Treasurer. Native destination: payment-reject. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:264). Native entry: [paymentReject](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1094).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Reject payment' | submit | reject-payment | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:264) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('reasonType','Reason',['Payment not found','Amount mismatch','Duplicate reference','Proof unclear','Other'])
- field('reason','Message to resident','','textarea','required maxlength="300" placeholder="Explain what needs correcting"')

Reference action/form behavior (prototype intent; never backend security proof):

- **reject-payment**: D=>{const p=pay(D.id);if(!p\|\|p.status!=='pending')fail('This payment was already reviewed.');if(!safeText(D.reason))fail('Give the resident a reason.');p.status='rejected';p.reason=safeText(D.reasonType)+': '+safeText(D.reason,300);p.approvedAt=today;audit('Payment rejected',p.id,'In review',p.reason);notify('Payment needs correction',p.reason,'Resident','payment-status?id='+p.id,p.flat);save();go('payment?id='+p.id);toast('Rejected. Resident can resubmit.');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:553)

### Current native API and database trace

- GET /api/v1/payments — expression "/payments" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1094)
- POST /api/v1/payments/{value}/reject — expression "/payments/"+id+"/reject" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1094)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal, ap_tenant.

Potential writes: ap_audit, ap_inbox, ap_payment.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Request DTO public record Reject(@NotBlank @Size(max=300) String reason)
- Server Admin/Treasurer role required

Backend source pointers: [payments](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:118), [payments](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:160), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [Reject](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:24), [reject](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:130), [reject](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:206), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-14"></a>

## 14. Payment receipt — receipt

Group: Finance. Reference roles: Admin, Treasurer, Resident. Native destination: receipt. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:265). Native entry: [receipt](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1238).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Save receipt' | action | exportReceipt | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:265) |
| 'Share' | action | shareReceipt | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:265) |

Reference action/form behavior (prototype intent; never backend security proof):

- **exportReceipt**: d=>exportReceipt(pay(d.id)) — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:509)
- **shareReceipt**: d=>{let p=pay(d.id);if(!p\|\|p.status!=='approved'\|\|!isStaff()&&p.flat!==S.residentFlat)fail('Receipt unavailable.');return shareText(receiptText(p),'Payment receipt');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:509)

### Current native API and database trace

- GET /api/v1/receipts/{value} — expression "/receipts/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1238)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal, ap_tenant, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()

Backend source pointers: [receipt](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:134), [receipt](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:214).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-15"></a>

## 15. Payment history — history

Group: Finance. Reference roles: Admin, Treasurer, Resident. Native destination: history. Recorded status: CONNECTED_LOCAL_EMULATOR_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:266). Native entry: [history](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/PaymentScreens.java:77).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| isStaff()?p.flat+' · '+INR(p.amount):monthName(bill(p.bill)?.month) | navigate / row | (p.status==='approved'?'receipt':isStaff()?'payment':'payment-status')+'?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:266) |

### Current native API and database trace

- GET /api/v1/payments — expression "/payments" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/PaymentScreens.java:77)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [payments](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:118), [payments](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:160), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-16"></a>

## 16. Unpaid flats — outstanding

Group: Finance. Reference roles: Admin, Treasurer. Native destination: outstanding. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:267). Native entry: [outstanding](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1122).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| Month picker | action | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:267) |
| 'Send reminders' | navigate / custom | 'reminder-preview' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:267) |

Reference action/form behavior (prototype intent; never backend security proof):

- **month**: ()=>sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)

### Current native API and database trace

- GET /api/v1/bills — expression "/bills?month="+month — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1122)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [bills](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:112), [bills](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:117), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-17"></a>

## 17. Send reminders — reminder-preview

Group: Finance. Reference roles: Admin, Treasurer. Native destination: reminder-preview. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:268). Native entry: [reminderPreview](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1158).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Send reminder' | action | sendReminders | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:268) |

Reference action/form behavior (prototype intent; never backend security proof):

- **sendReminders**: ()=>{requireStaff();requireOnline();if(!S.settings.reminders)fail('Enable maintenance reminders in notification settings first.');let count=addReminders();save();go('outstanding');toast(count+' demo reminders added to inbox');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:506)

### Current native API and database trace

- GET /api/v1/billing/reminders/preview — expression "/billing/reminders/preview?month="+month — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1158)
- POST /api/v1/billing/reminders/send — expression "/billing/reminders/send?month="+month — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1158)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal, ap_settings, ap_tenant, ap_user.

Potential writes: ap_audit, ap_inbox, ap_rate_limit.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required
- Request DTO public record Reminder(@NotNull UUID requestKey)
- throw ApiError.conflict("Enable maintenance reminders in notification settings first.")

Backend source pointers: [reminderPreview](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:105), [reminderPreview](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:84), [reminderRows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:114), [Reminder](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:22), [sendReminders](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:108), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/RateGate.java:14), [sendReminders](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:100), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-18"></a>

## 18. Maintenance amount — billing-settings

Group: Finance. Reference roles: Admin, Treasurer. Native destination: billing-settings. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817). Native entry: [billing](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:934).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save maintenance' | submit | billing-settings | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817) |
| 'Include vacant flats' | toggle | 'billVacant' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817) |
| 'Late fee' | toggle | 'lateEnabled' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817) |
| 'Rate history' | navigate / custom | 'rate-history' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817) |
| 'Flat overrides' | navigate / custom | 'rate-overrides' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817) |

Reference input/form definitions (attributes and allowed choices shown literally):

- amountField('amount','Monthly maintenance per flat',S.settings.amount\|\|'')
- field('effective','Effective from',effective,'month','required min="${effective}"')
- field('billingDay','Billing day',S.settings.billingDay,'number','required min="1" max="28"')
- field('dueDay','Due day',S.settings.dueDay,'number','required min="1" max="28"')
- field('lateFee','One-time late fee',S.settings.lateFee,'number','min="0" step="0.01"')
- field('grace','Grace days',S.settings.grace,'number','min="0" max="30"')

Reference action/form behavior (prototype intent; never backend security proof):

- **billing-settings**: D=>{const amount=validateAmount(D.amount),bd=Number(D.billingDay),dd=Number(D.dueDay);if(!Number.isInteger(bd)\|\|bd<1\|\|bd>28\|\|!Number.isInteger(dd)\|\|dd<bd\|\|dd>28)fail('Use billing / due days from 1–28, with due day on or after billing day.');const old=S.settings.amount;Object.assign(S.settings,{amount,billingDay:bd,dueDay:dd,lateFee:Number(D.lateFee)\|\|0,grace:Number(D.grace)\|\|0});audit('Billing settings updated','Future billing',INR(old),INR(amount)+' · due day '+dd);save();go('money');toast('Saved for future bills');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:554)

### Current native API and database trace

- GET /api/v1/billing/rules — expression "/billing/rules" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:934)
- POST /api/v1/billing/rules — expression "/billing/rules" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:934)

Potential reads: ap_bill, ap_billing_rule, ap_tenant.

Potential writes: ap_audit, ap_billing_rule.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required
- Request DTO public record Rule(@NotNull BigDecimal amount,@Pattern(regexp="[0-9]{4}-[0-9]{2}") @NotNull String effective, @Min(1) @Max(28) int billingDay,@Min(1) @Max(28) int dueDay)

Backend source pointers: [rules](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:93), [rules](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:36), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [Rule](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:16), [rule](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:96), [setRule](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:24), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [currentMonth](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:18), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-19"></a>

## 19. Generate monthly dues — bill-preview

Group: Finance. Reference roles: Admin, Treasurer. Native destination: bill-preview. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:821). Native entry: [billPreview](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1192).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| Month picker | action | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:821) |
| 'Generate missing bills' | action | generateBills | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:821) |
| 'Set maintenance' | navigate / custom | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:821) |

Reference action/form behavior (prototype intent; never backend security proof):

- **month**: ()=>sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- **generateBills**: ()=>{requireStaff();requireOnline();let count=createMissingBills(month);save();render();toast(count?count+' bills created':'No duplicates. All bills already exist.');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:505)

### Current native API and database trace

- GET /api/v1/billing/preview — expression "/billing/preview?month="+month — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1192)
- POST /api/v1/billing/generate — expression "/billing/generate?month="+month — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1192)

Potential reads: ap_bill, ap_billing_rule, ap_flat, ap_rate_override, ap_settings, ap_tenant, ap_user.

Potential writes: ap_audit, ap_bill, ap_inbox, ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required
- ApiError.conflict("Configure maintenance for this month first."))

Backend source pointers: [billingPreview](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:102), [billingPreview](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:60), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [generate](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:99), [generate](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:40), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-20"></a>

## 20. Expenses — expenses

Group: Finance. Reference roles: Admin, Treasurer, Resident. Native destination: expenses. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271). Native entry: [expenses](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1263).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| Month picker | action | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271) |
| 'Add expense' | icon action | 'data-go="expense-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271) |
| isStaff()?['All','Paid','Drafts']:['All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271) |
| e.title | navigate / row | 'expense?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271) |
| 'Recurring expenses' | navigate / custom | 'recurring' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271) |

Reference action/form behavior (prototype intent; never backend security proof):

- **month**: ()=>sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)

### Current native API and database trace

- GET /api/v1/expenses — expression "/expenses?month="+month — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1263)

Potential reads: ap_expense, ap_settings.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [expenses](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:137), [expenses](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:235).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-21"></a>

## 21. Expense detail — expense

Group: Finance. Reference roles: Admin, Treasurer, Resident. Native destination: expense. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272). Native entry: [expense](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1747).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'View bill' | navigate / custom | 'expense-proof?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272) |
| 'Edit' | navigate / custom | 'expense-form?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272) |
| e.paid?'Reverse':'Confirm paid' | action | e.paid?'reverseExpense':'confirmExpense' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272) |

### Current native API and database trace

- GET /api/v1/ops/expenses/{value} — expression "/ops/expenses/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1747)

Potential reads: ap_contact, ap_expense, ap_settings.

Potential writes: ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()

Backend source pointers: [expense](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:127), [expense](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:195), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-22"></a>

## 22. Expense attachment — expense-proof

Group: Finance. Reference roles: Admin, Treasurer, Resident. Native destination: attachments. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:273). Native entry: [render](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/AttachmentScreens.java:12).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Save attachment' | action | downloadExpenseAttachment | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:273) |

Reference action/form behavior (prototype intent; never backend security proof):

- **downloadExpenseAttachment**: d=>{const e=S.expenses.find(x=>x.id===d.id);if(!e\|\|!isStaff()&&!e.public)fail('Access denied.');downloadData(e.attachment);} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:533)

### Current native API and database trace

- GET /api/v1/ops/files — expression "/ops/files?kind=EXPENSE&parentId="+parent — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/AttachmentScreens.java:12)
- GET /api/v1/ops/files/{value} — expression "/ops/files/"+metadata.optString("id") — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/AttachmentScreens.java:33)

Potential reads: ap_bill, ap_document, ap_expense, ap_file, ap_flat, ap_payment, ap_payment_reversal, ap_settings.

Potential writes: ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()
- throw ApiError.conflict("Attach files before publishing.")
- throw new IllegalArgumentException("Unsupported attachment type.")
- Server Admin/Treasurer role required

Backend source pointers: [files](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:208), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:78), [access](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:48), [document](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:26), [bill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:127), [ownedBill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:135), [balance](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:21), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [file](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:215), [content](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:118).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-23"></a>

## 23. Expense — expense-form

Group: Finance. Reference roles: Admin, Treasurer. Native destination: expense-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274). Native entry: [newExpense](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2320).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| e.id?'Save changes':'Add expense' | submit | expense | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274) |

Reference input/form definitions (attributes and allowed choices shown literally):

- amountField('amount','Amount',e.amount\|\|'')
- field('title','Expense title',e.title\|\|'','text','required maxlength="80" placeholder="e.g. Water tanker"')
- selectField('category','Category',['Security','Housekeeping','Electricity','Water','Lift','Plumbing','Repairs','Generator','Pest control','Other'],e.category\|\|Q.category\|\|'Water')
- field('date','Date',e.date\|\|today,'date','max="${today}" required')
- selectField('mode','Paid via',['UPI','Cash','Bank transfer','Cheque'],e.mode\|\|'UPI')
- selectField('vendor','Vendor',[['','Not linked'],...S.vendors.map(v=>[v.id,v.name])],e.vendor\|\|'')
- fileField('Bill · optional')
- field('notes','Note · optional',e.notes\|\|'','textarea','maxlength="500"')
- checkField('paid','Payment completed',e.paid??true)
- checkField('public','Visible to residents',e.public??true)

Reference action/form behavior (prototype intent; never backend security proof):

- **expense**: async(D,F)=>{const amount=validateAmount(D.amount),attachment=await dataFile(F.get('attachment'));if(F.has('paid')&&D.date>today)fail('A paid expense cannot have a future payment date.');let existing=S.expenses.find(e=>e.id===D.id),old=existing?INR(existing.amount)+(existing.paid?' paid':' draft'):'None',e={...(existing\|\|{}),id:existing?.id\|\|uid('EXP'),title:safeText(D.title,80),category:D.category,amount,date:D.date,mode:D.mode,vendor:D.vendor,notes:safeText(D.notes),public:F.has('public'),paid:F.has('paid'),reversed:!!existing?.reversed&&!F.has('paid'),attachment:attachment\|\|existing?.attachment\|\|null};if(existing)Object.assign(existing,e); — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:555)

### Current native API and database trace

- POST /api/v1/ops/expenses — expression "/ops/expenses" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2320)

Potential reads: ap_command, ap_settings, ap_tenant.

Potential writes: ap_audit, ap_command, ap_expense.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.date(p,"paidOn"))
- V.bool(p,"paid",false))
- V.text(p,"title",100)
- throw new IllegalArgumentException("Expense cannot be future-dated.")
- Server Admin/Treasurer role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- throw new IllegalArgumentException("Date must not precede the opening balance.")
- V.id(body,"requestKey")

Backend source pointers: [createExpense](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:124), [createExpense](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:180), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [checkOpening](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:331), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-24"></a>

## 24. Recurring expenses — recurring

Group: Finance. Reference roles: Admin, Treasurer. Native destination: recurring. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:275). Native entry: [recurring](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1799).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Add' | navigate / section | 'recurring-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:275) |
| r.title | navigate / row | 'recurring-detail?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:275) |

### Current native API and database trace

- GET /api/v1/ops/recurring — expression "/ops/recurring" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1799)

Potential reads: ap_contact, ap_recurring.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required

Backend source pointers: [recurring](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:139), [recurring](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:254), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-25"></a>

## 25. Recurring expense — recurring-detail

Group: Finance. Reference roles: Admin, Treasurer. Native destination: recurring-detail. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:276). Native entry: [recurringDetail](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1812).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Edit' | navigate / custom | 'recurring-form?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:276) |
| r.active?'Pause':'Resume' | action | toggleRecurring | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:276) |
| 'Prepare expense' | action | prepareRecurring | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:276) |

Reference action/form behavior (prototype intent; never backend security proof):

- **toggleRecurring**: d=>{requireStaff();let r=S.recurring.find(x=>x.id===d.id);r.active=!r.active;audit('Recurring rule changed',r.title,!r.active?'Active':'Paused',r.active?'Active':'Paused');save();render();toast(r.active?'Recurring expense resumed':'Recurring expense paused');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:513)
- **prepareRecurring**: d=>{requireStaff();requireOnline();let r=S.recurring.find(x=>x.id===d.id);let e=prepareRecurring(r);save();go('expense?id='+e.id);toast('Draft ready. Confirm only when paid.');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:514)

### Current native API and database trace

- GET /api/v1/ops/recurring/{value} — expression "/ops/recurring/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1812)
- POST /api/v1/ops/recurring/{value}/active — expression "/ops/recurring/"+id+"/active" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1812)
- POST /api/v1/ops/recurring/{value}/prepare — expression "/ops/recurring/"+id+"/prepare" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1812)

Potential reads: ap_command, ap_contact, ap_expense, ap_recurring, ap_tenant.

Potential writes: ap_audit, ap_command, ap_expense, ap_recurring.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required
- V.bool(p,"active",true))
- throw new IllegalArgumentException("Confirm the current and requested active state.")
- throw ApiError.conflict("This template changed. Refresh before saving.")
- throw new IllegalArgumentException("Unsupported recurring command field.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")
- V.date(p,"cycle"))
- V.id(p,"requestKey")
- throw ApiError.conflict("Resume this recurring expense first.")
- throw ApiError.conflict("The cycle changed. Refresh before preparing.")

Backend source pointers: [recurringDetail](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:145), [recurringDetail](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:258), [recurringActive](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:148), [recurringActive](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:268), [recurringCommand](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:262), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39), [prepareRecurring](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:151).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-26"></a>

## 26. Recurring setup — recurring-form

Group: Finance. Reference roles: Admin, Treasurer. Native destination: recurring-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:277). Native entry: [recurringForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1833).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | recurring | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:277) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('title','Title',r.title\|\|'','text','required maxlength="80"')
- amountField('amount','Expected amount',r.amount\|\|'')
- selectField('category','Category',['Security','Housekeeping','Lift','Electricity','Water','Other'],r.category)
- selectField('frequency','Frequency',['Monthly','Quarterly','Every 6 months','Annual'],r.frequency\|\|'Monthly')
- field('next','Next due date',r.next\|\|'2026-10-01','date','required')
- selectField('vendor','Vendor',[['','Not linked'],...S.vendors.map(v=>[v.id,v.name])],r.vendor\|\|'')
- checkField('active','Active',r.active??true)

Reference action/form behavior (prototype intent; never backend security proof):

- **recurring**: D=>{let existing=S.recurring.find(r=>r.id===D.id);const r={id:existing?.id\|\|uid('REC'),title:safeText(D.title,80),category:D.category,amount:validateAmount(D.amount),frequency:D.frequency,next:D.next,vendor:D.vendor,active:!!D.active};if(existing)Object.assign(existing,r);else S.recurring.push(r);audit('Recurring expense saved',r.title,existing?'Edited':'New',r.frequency+' · '+INR(r.amount));save();go('recurring-detail?id='+r.id);toast('Recurring expense saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:557)

### Current native API and database trace

- GET /api/v1/ops/recurring/{value} — expression "/ops/recurring/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1833)
- GET /api/v1/ops/contacts — expression "/ops/contacts?kind=VENDOR" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1839)
- POST /api/v1/ops/recurring{value} — expression "/ops/recurring"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1839)

Potential reads: ap_contact, ap_recurring.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required
- throw new IllegalArgumentException("Invalid contact type.")

Backend source pointers: [recurringDetail](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:145), [recurringDetail](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:258), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:85), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:171).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-27"></a>

## 27. Monthly summary — reports

Group: Reports. Reference roles: Admin, Treasurer, Resident. Native destination: reports. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278). Native entry: [reports](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1879).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| Month picker | action | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278) |
| 'Export report' | icon action | 'data-go="export-report"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278) |
| 'Expense breakdown' | navigate / row | 'expense-report' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278) |
| 'Collections by flat' | navigate / row | 'collection-report' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278) |
| 'Resident view' | navigate / row | 'transparency' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278) |

Reference action/form behavior (prototype intent; never backend security proof):

- **month**: ()=>sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)

### Current native API and database trace

- GET /api/v1/reports/monthly — expression "/reports/monthly?month="+h.selectedMonth() — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1879)

Potential reads: ap_bill, ap_payment, ap_payment_reversal, ap_settings.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()

Backend source pointers: [report](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:143), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:238), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [sumDate](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:272), [before](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:275).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-28"></a>

## 28. Expense breakdown — expense-report

Group: Reports. Reference roles: Admin, Treasurer, Resident. Native destination: expense-report. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:279). Native entry: [expenses](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FinanceReportScreens.java:37).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| Month picker | action | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:279) |
| 'View expenses' | navigate / custom | 'expenses' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:279) |

Reference action/form behavior (prototype intent; never backend security proof):

- **month**: ()=>sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)

### Current native API and database trace

- GET /api/v1/reports/monthly — expression "/reports/monthly?month="+h.selectedMonth() — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FinanceReportScreens.java:37)
- GET /api/v1/expenses — expression "/expenses?month="+h.selectedMonth() — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FinanceReportScreens.java:37)

Potential reads: ap_bill, ap_expense, ap_payment, ap_payment_reversal, ap_settings.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()

Backend source pointers: [report](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:143), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:238), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [sumDate](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:272), [before](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:275), [expenses](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:137), [expenses](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:235).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-29"></a>

## 29. Collection report — collection-report

Group: Reports. Reference roles: Admin, Treasurer. Native destination: collection-report. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:280). Native entry: [collections](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FinanceReportScreens.java:71).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| Month picker | action | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:280) |
| 'CSV' | action | exportCollections | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:280) |

Reference action/form behavior (prototype intent; never backend security proof):

- **month**: ()=>sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- **exportCollections**: ()=>{requireStaff();downloadCSV('ApartmentCare_'+month+'_collections.csv',[['Flat','Resident','Billed','Approved','Outstanding','Status'],...stats().bs.map(b=>[b.flat,flat(b.flat)?.name,b.amount+(b.lateFee\|\|0),billPaid(b),due(b),billStatus(b)])]);} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:516)

### Current native API and database trace

- GET /api/v1/bills — expression "/bills?month="+h.selectedMonth() — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FinanceReportScreens.java:71)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [bills](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:112), [bills](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:117), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-30"></a>

## 30. Resident finance view — transparency

Group: Reports. Reference roles: Admin, Treasurer. Native destination: transparency. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:281). Native entry: [transparency](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1925).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Resident transparency' | toggle | 'expensesVisible' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:281) |

### Current native API and database trace

- GET /api/v1/reports/monthly — expression "/reports/monthly?month="+h.selectedMonth() — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1925)
- GET /api/v1/ops/settings — expression "/ops/settings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1925)
- POST /api/v1/ops/settings — expression "/ops/settings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1925)

Potential reads: ap_bill, ap_payment, ap_payment_reversal, ap_settings, ap_tenant.

Potential writes: ap_audit, ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()
- V.opt(p,"reminderDays",80))
- V.text(p,"quietStart",5)
- V.opt(p,"ifsc",11).toUpperCase(Locale.ROOT))
- V.opt(p,"payee",100)
- V.bool(p,"expensesVisible",true)
- throw new IllegalArgumentException("Quiet hours use HH:mm.")
- throw new IllegalArgumentException("Check the IFSC.")
- Server Admin/Treasurer role required

Backend source pointers: [report](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:143), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:238), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [sumDate](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:272), [before](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:275), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:94), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:18), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:97), [saveSettings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:46), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-31"></a>

## 31. Export report — export-report

Group: Reports. Reference roles: Admin, Treasurer, Resident. Native destination: export-report. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:282). Native entry: [exports](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FinanceReportScreens.java:96).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Download CSV' | navigate / row | '' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:282) |
| 'Print / Save PDF' | navigate / row | '' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:282) |
| 'Share summary' | navigate / row | '' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:282) |

### Current native API and database trace

- GET /api/v1/reports/monthly — expression "/reports/monthly?month="+h.selectedMonth() — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FinanceReportScreens.java:96)

Potential reads: ap_bill, ap_payment, ap_payment_reversal, ap_settings.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()

Backend source pointers: [report](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:143), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:238), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [sumDate](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:272), [before](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:275).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-32"></a>

## 32. Other income — income

Group: Finance. Reference roles: Admin, Treasurer. Native destination: income. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:283). Native entry: [income](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1720).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| Month picker | action | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:283) |
| 'Add income' | icon action | 'data-go="income-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:283) |
| e.title | navigate / row | 'income-form?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:283) |

Reference action/form behavior (prototype intent; never backend security proof):

- **month**: ()=>sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)

### Current native API and database trace

- GET /api/v1/ops/income — expression "/ops/income" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1720)

Potential reads: ap_income.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required

Backend source pointers: [income](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:118), [income](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:162), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-33"></a>

## 33. Income entry — income-form

Group: Finance. Reference roles: Admin, Treasurer. Native destination: income-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:284). Native entry: [incomeForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1735).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | income | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:284) |

Reference input/form definitions (attributes and allowed choices shown literally):

- amountField('amount','Amount received',e.amount\|\|'')
- field('title','Description',e.title\|\|'','text','required maxlength="80"')
- field('date','Received on',e.date\|\|today,'date','required max="${today}"')
- selectField('mode','Mode',['UPI','Cash','Bank transfer','Cheque'],e.mode\|\|'UPI')

Reference action/form behavior (prototype intent; never backend security proof):

- **income**: D=>{let existing=S.income.find(e=>e.id===D.id),e={id:existing?.id\|\|uid('INC'),amount:validateAmount(D.amount),title:safeText(D.title,80),date:D.date,mode:D.mode};if(e.date>today)fail('Choose a date on or before today.');if(existing)Object.assign(existing,e);else S.income.push(e);audit('Other income saved',e.title,'Previous entry / new',INR(e.amount));save();go('income');toast('Income saved. Balance updated.');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:558)

### Current native API and database trace

- POST /api/v1/ops/income — expression "/ops/income" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1735)

Potential reads: ap_command, ap_settings, ap_tenant.

Potential writes: ap_audit, ap_command, ap_income.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.date(p,"receivedOn"))
- V.text(p,"title",100)
- throw new IllegalArgumentException("Income cannot be future-dated.")
- Server Admin/Treasurer role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- throw new IllegalArgumentException("Date must not precede the opening balance.")
- V.id(body,"requestKey")

Backend source pointers: [income](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:121), [income](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:166), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [checkOpening](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:331), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-34"></a>

## 34. Cashbook — cashbook

Group: Reports. Reference roles: Admin, Treasurer. Native destination: cashbook. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:285). Native entry: [cashbook](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1863).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| Month picker | action | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:285) |
| 'CSV' | action | exportCashbook | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:285) |
| e.name | navigate / row | e.to | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:285) |

Reference action/form behavior (prototype intent; never backend security proof):

- **month**: ()=>sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- **exportCashbook**: ()=>{requireStaff();downloadCSV('ApartmentCare_'+month+'_cashbook.csv',[['Date','Entry','Inflow','Outflow'],...S.payments.filter(p=>p.status==='approved'&&p.date.startsWith(month)).map(p=>[p.date,'Maintenance '+p.flat,p.amount,0]),...S.income.filter(e=>e.date.startsWith(month)).map(e=>[e.date,e.title,e.amount,0]),...S.expenses.filter(e=>e.paid&&e.date.startsWith(month)).map(e=>[e.date,e.title,0,e.amount])]);} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:517)

### Current native API and database trace

- GET /api/v1/ops/cashbook — expression "/ops/cashbook?month="+h.selectedMonth() — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1863)

Potential reads: ap_bill, ap_expense, ap_flat, ap_income, ap_payment, ap_payment_reversal.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required

Backend source pointers: [cashbook](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:157), [cashbook](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:334).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-35"></a>

## 35. Financial controls — finance-settings

Group: Settings. Reference roles: Admin, Treasurer. Native destination: finance-settings. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286). Native entry: [settings](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1538).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Billing rules' | navigate / row | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286) |
| 'Payment account' | navigate / row | 'payment-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286) |
| 'Opening balance' | navigate / row | 'opening-balance' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286) |
| 'Resident transparency' | navigate / row | 'transparency' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286) |
| 'Require payment proof' | toggle | 'proofRequired' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-36"></a>

## 36. Association account — payment-settings

Group: Settings. Reference roles: Admin, Treasurer. Native destination: payment-settings. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:287). Native entry: [settingsForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1551).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | payment-settings | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:287) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('payee','Account holder',S.settings.payee,'text','required maxlength="100"')
- field('upi','UPI ID',S.settings.upi,'text','placeholder="association@bank" maxlength="80"')
- field('bank','Bank name',S.settings.bank,'text','maxlength="80"')
- field('account','Account number',S.settings.account,'text','inputmode="numeric" maxlength="24"')
- field('ifsc','IFSC',S.settings.ifsc,'text','maxlength="11"')

Reference action/form behavior (prototype intent; never backend security proof):

- **payment-settings**: D=>{if(D.upi&&!/^[a-zA-Z0-9.\-_]{2,}@[a-zA-Z0-9]{2,}$/.test(D.upi))fail('Enter a valid UPI ID.');if(D.ifsc&&!/^[A-Z]{4}0[A-Z0-9]{6}$/i.test(D.ifsc))fail('Enter an 11-character IFSC.');['payee','upi','bank','account','ifsc'].forEach(k=>S.settings[k]=safeText(D[k],100));S.settings.ifsc=S.settings.ifsc.toUpperCase();audit('Payment account updated','Association account','Previous account','New account details saved');save();go('payee');toast('Account details saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:559)

### Current native API and database trace

- GET /api/v1/ops/settings — expression "/ops/settings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1551)
- POST /api/v1/ops/settings/{value} — expression "/ops/settings/"+section — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1551)

Potential reads: ap_command, ap_settings, ap_tenant.

Potential writes: ap_audit, ap_command, ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Choose a valid settings section.")
- throw new IllegalArgumentException("Only fields from this settings section can be changed.")
- Server Admin/Treasurer role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.opt(p,"reminderDays",80))
- V.text(p,"quietStart",5)
- V.opt(p,"ifsc",11).toUpperCase(Locale.ROOT))
- V.opt(p,"payee",100)
- V.bool(p,"expensesVisible",true)
- throw new IllegalArgumentException("Quiet hours use HH:mm.")
- throw new IllegalArgumentException("Check the IFSC.")
- V.id(body,"requestKey")

Backend source pointers: [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:94), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:18), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [settingsSection](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:100), [saveSettingsSection](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:24), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [saveSettings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:46), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-37"></a>

## 37. Opening balance — opening-balance

Group: Settings. Reference roles: Admin. Native destination: opening-balance. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:288). Native entry: [opening](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1603).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | opening-balance | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:288) |

Reference input/form definitions (attributes and allowed choices shown literally):

- amountField('opening','Bank + cash balance on 1 Sep',S.apartment.opening)

Reference action/form behavior (prototype intent; never backend security proof):

- **opening-balance**: D=>{const n=Number(D.opening);if(!Number.isFinite(n)\|\|n<0)fail('Enter a valid opening balance.');const before=S.apartment.opening;S.apartment.opening=n;audit('Opening balance changed','1 Sep 2026',INR(before),INR(n));save();go('reports');toast('Opening balance updated');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:560)

### Current native API and database trace

- GET /api/v1/ops/settings — expression "/ops/settings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1603)
- POST /api/v1/ops/opening — expression "/ops/opening" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1603)

Potential reads: ap_expense, ap_income, ap_payment, ap_settings, ap_tenant.

Potential writes: ap_audit, ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.date(p,"date"))
- throw new IllegalArgumentException("Use the first day of a current or earlier month.")
- throw ApiError.conflict("Opening cash is already set. Record a documented adjustment as income/expense instead.")
- throw new IllegalArgumentException("Opening date must not follow existing cash entries.")
- Server Admin role required

Backend source pointers: [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:94), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:18), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [opening](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:103), [opening](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:65), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-38"></a>

## 38. Building issues — issues

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: issues. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290). Native entry: [tickets](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:887).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Report' | navigate | 'issue-categories' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290) |
| 'My requests' | navigate | 'complaints' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290) |
| 'Services' | navigate | 'services' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290) |
| 'Contacts' | navigate | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290) |
| ['Active','Following','Resolved','All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290) |
| 'Report issue' | navigate / custom | 'issue-categories' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290) |

### Current native API and database trace

- GET /api/v1/ops/tickets — expression "/ops/tickets?kind="+kind — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:887)

Potential reads: ap_contact, ap_ticket, ap_ticket_follow, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Invalid issue type.")

Backend source pointers: [tickets](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:160), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:17), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-39"></a>

## 39. What needs attention? — issue-categories

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: issue-categories. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:291). Native entry: [issueCategories](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:905).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-40"></a>

## 40. Report an issue — issue-form

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: issue-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292). Native entry: [ticketForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:910).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Change' | navigate / custom | 'issue-categories' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292) |
| 'Report issue' | submit | issue-create | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('title','Problem','','text','required maxlength="80" placeholder="e.g. No water on second floor"')
- selectField('scope','Affected area',['Whole apartment',...S.apartment.blocks.map(b=>'Block '+b)],'Whole apartment')
- selectField('priority','Priority',['Normal','High','Urgent'],'Normal')
- field('description','Details','','textarea','required maxlength="1000" placeholder="Where and since when?"')
- fileField('Photo · optional')

Reference action/form behavior (prototype intent; never backend security proof):

- **issue-create**: async(D,F)=>{const i={id:uid('ISS'),category:D.category,title:safeText(D.title,80),description:safeText(D.description,1000),scope:D.scope,priority:D.priority,status:'Open',by:S.role==='Resident'?S.residentFlat:S.role,created:today,vendor:'',eta:'',affected:[S.residentFlat],comments:[],timeline:[{title:'Reported by '+(S.role==='Resident'?S.residentFlat:S.role),time:'Today · just now'}],attachment:await dataFile(F.get('attachment'))};S.issues.unshift(i);audit('Issue reported',i.id,'None',i.category+' · Open');notify('New building issue',i.title,'staff','issue?id='+i.id);save();go('issue?id='+i.id);toast('Issue reported');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:561)

### Current native API and database trace

- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:910)
- POST /api/v1/ops/tickets — expression "/ops/tickets" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:910)
- GET /api/v1/ops/tickets — expression "/ops/tickets?kind=ISSUE" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:910)

Potential reads: ap_command, ap_contact, ap_flat, ap_tenant, ap_ticket, ap_ticket_follow, ap_user.

Potential writes: ap_audit, ap_command, ap_inbox, ap_ticket, ap_ticket_event, ap_ticket_follow.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"kind","ISSUE","COMPLAINT")
- V.optionalId(p,"flatId"))
- V.text(p,"description",2000)
- throw new IllegalArgumentException("Choose a flat for a private request.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")
- throw new IllegalArgumentException("Invalid issue type.")

Backend source pointers: [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:169), [create](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:41), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [flat](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:43), [event](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:59), [notifyStaff](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:63), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-41"></a>

## 41. Similar issues — similar-issues

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: issue-form. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:293). Native entry: [ticketForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:910).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Different problem' | navigate / custom | 'issue-form?category='+encodeURIComponent(Q.category\|\|'Water') | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:293) |

### Current native API and database trace

- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:910)
- POST /api/v1/ops/tickets — expression "/ops/tickets" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:910)
- GET /api/v1/ops/tickets — expression "/ops/tickets?kind=ISSUE" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:910)

Potential reads: ap_command, ap_contact, ap_flat, ap_tenant, ap_ticket, ap_ticket_follow, ap_user.

Potential writes: ap_audit, ap_command, ap_inbox, ap_ticket, ap_ticket_event, ap_ticket_follow.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"kind","ISSUE","COMPLAINT")
- V.optionalId(p,"flatId"))
- V.text(p,"description",2000)
- throw new IllegalArgumentException("Choose a flat for a private request.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")
- throw new IllegalArgumentException("Invalid issue type.")

Backend source pointers: [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:169), [create](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:41), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [flat](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:43), [event](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:59), [notifyStaff](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:63), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-42"></a>

## 42. Issue detail — issue

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: issue. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294). Native entry: [ticket](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:948).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| v?.name\|\|'Not assigned' | navigate / row | isStaff()?'issue-assign?id='+i.id:v?'vendor?id='+v.id:'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Assign' | navigate / custom | 'issue-assign?id='+i.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Update status' | navigate / custom | 'issue-update?id='+i.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Still not fixed' | action | reopenIssue | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Close issue' | action | closeIssue | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Open' | navigate / section | 'issue-comments?id='+i.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Add an update' | navigate / custom | 'issue-comments?id='+i.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Photo' | navigate / custom | 'issue-photo?id='+i.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |

Reference action/form behavior (prototype intent; never backend security proof):

- **reopenIssue**: d=>{requireOnline();const i=S.issues.find(x=>x.id===d.id);if(i.status!=='Resolved')fail('Only resolved issues can be reopened.');i.status='Open';i.timeline.push({title:'Reopened · problem persists',time:'Today · '+S.role});audit('Issue reopened',i.id,'Resolved','Open');notify('Issue reopened',i.title,'staff','issue?id='+i.id);save();render();toast('Issue reopened');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:521)
- **closeIssue**: d=>{requireStaff();const i=S.issues.find(x=>x.id===d.id);i.status='Closed';i.timeline.push({title:'Closed by '+S.role,time:'Today'});audit('Issue closed',i.id,'Resolved','Closed');save();render();} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:522)

### Current native API and database trace

- GET /api/v1/ops/tickets/{value} — expression "/ops/tickets/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:948)
- POST /api/v1/ops/tickets/{value}/follow — expression "/ops/tickets/"+id+"/follow" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:948)
- POST /api/v1/ops/tickets/{value}/comments — expression "/ops/tickets/"+id+"/comments" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:948)

Potential reads: ap_command, ap_tenant.

Potential writes: ap_command, ap_ticket_event, ap_ticket_follow.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()
- throw ApiError.conflict("This incident has ended.")
- V.text(p,"message",2000)))
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:163), [follow](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:178), [follow](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:93), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [comment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:175), [comment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:84), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [event](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:59), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-43"></a>

## 43. Affected flats — affected

Group: Services. Reference roles: Admin, Treasurer. Native destination: affected. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:295). Native entry: [affected](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:980).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| f | navigate / row | 'flat?id='+f | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:295) |

### Current native API and database trace

- GET /api/v1/ops/tickets/{value} — expression "/ops/tickets/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:980)
- GET /api/v1/ops/tickets/{value}/affected — expression "/ops/tickets/"+id+"/affected" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:980)

Potential reads: ap_flat, ap_ticket_follow, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()
- Server Admin/Treasurer role required

Backend source pointers: [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:163), [affected](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:166), [affected](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:35).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-44"></a>

## 44. Assign service — issue-assign

Group: Services. Reference roles: Admin, Treasurer. Native destination: ticket-update. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:296). Native entry: [ticketUpdate](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:995).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Assign vendor' | submit | issue-assign | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:296) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('vendor','Vendor',[['','Choose vendor'],...S.vendors.map(v=>[v.id,v.name])],i.vendor\|\|'','required')
- field('eta','Expected restoration',i.eta\|\|'','datetime-local')
- field('note','Update to residents','','textarea','maxlength="500" placeholder="Technician will arrive soon"')
- checkField('notify','Notify affected residents',true)

Reference action/form behavior (prototype intent; never backend security proof):

- **issue-assign**: D=>{const i=S.issues.find(i=>i.id===D.id);if(!i)fail('Issue no longer exists.');let old=i.status;i.vendor=D.vendor;i.eta=D.eta;i.status='Assigned';i.timeline.push({title:'Assigned to '+S.vendors.find(v=>v.id===D.vendor)?.name,time:'Today · '+S.role});if(D.note)i.comments.push({by:S.role,text:safeText(D.note),time:'Just now'});if(D.notify)notify('Issue update',i.title+' · technician assigned','all','issue?id='+i.id);audit('Issue assigned',i.id,old,'Assigned');save();go('issue?id='+i.id);toast('Vendor assigned');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:562)

### Current native API and database trace

- GET /api/v1/ops/tickets/{value} — expression "/ops/tickets/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:995)
- GET /api/v1/ops/contacts — expression "/ops/contacts?kind=VENDOR" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:995)
- POST /api/v1/ops/tickets/{value}/status — expression "/ops/tickets/"+id+"/status" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:995)

Potential reads: ap_command, ap_contact, ap_tenant, ap_ticket_follow, ap_user.

Potential writes: ap_audit, ap_command, ap_inbox, ap_ticket, ap_ticket_event.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Invalid contact type.")
- V.choice(p,"status","OPEN","ASSIGNED","IN_PROGRESS","RESOLVED","CLOSED")
- V.optionalId(p,"vendorId"):(UUID)t.get("vendorId"))
- V.bool(p,"notifyAll",false))
- throw ApiError.forbidden()
- throw new IllegalArgumentException("Choose a vendor before assigning.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:163), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:85), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:171), [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:172), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:62), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [vendor](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:46), [event](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:59), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-45"></a>

## 45. Update issue — issue-update

Group: Services. Reference roles: Admin, Treasurer. Native destination: ticket-update. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:297). Native entry: [ticketUpdate](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:995).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save update' | submit | issue-update | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:297) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('status','Status',['Open','Assigned','In progress','Resolved','Closed'],i.status)
- field('note','Resolution / update','','textarea','required maxlength="500" placeholder="What changed?"')
- field('eta','Expected restoration',i.eta\|\|'','datetime-local')
- checkField('notify','Notify affected residents',true)

Reference action/form behavior (prototype intent; never backend security proof):

- **issue-update**: D=>{const i=S.issues.find(i=>i.id===D.id);let old=i.status;i.status=D.status;i.eta=D.eta;i.timeline.push({title:D.status+' · '+safeText(D.note),time:'Today · '+S.role});i.comments.push({by:S.role,text:safeText(D.note),time:'Just now'});if(D.notify)notify('Building issue '+D.status.toLowerCase(),i.title,'all','issue?id='+i.id);audit('Issue status updated',i.id,old,D.status);save();go('issue?id='+i.id);toast('Status updated');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:563)

### Current native API and database trace

- GET /api/v1/ops/tickets/{value} — expression "/ops/tickets/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:995)
- GET /api/v1/ops/contacts — expression "/ops/contacts?kind=VENDOR" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:995)
- POST /api/v1/ops/tickets/{value}/status — expression "/ops/tickets/"+id+"/status" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:995)

Potential reads: ap_command, ap_contact, ap_tenant, ap_ticket_follow, ap_user.

Potential writes: ap_audit, ap_command, ap_inbox, ap_ticket, ap_ticket_event.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Invalid contact type.")
- V.choice(p,"status","OPEN","ASSIGNED","IN_PROGRESS","RESOLVED","CLOSED")
- V.optionalId(p,"vendorId"):(UUID)t.get("vendorId"))
- V.bool(p,"notifyAll",false))
- throw ApiError.forbidden()
- throw new IllegalArgumentException("Choose a vendor before assigning.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:163), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:85), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:171), [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:172), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:62), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [vendor](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:46), [event](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:59), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-46"></a>

## 46. Issue updates — issue-comments

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: ticket. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:298). Native entry: [ticket](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:948).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Post update' | submit | issue-comment | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:298) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('comment','Add update','','textarea','required maxlength="500" placeholder="Write a short update"')

Reference action/form behavior (prototype intent; never backend security proof):

- **issue-comment**: D=>{const i=S.issues.find(x=>x.id===D.id);if(!i)fail('Issue no longer exists.');i.comments.push({by:S.role==='Resident'?S.residentFlat:S.role,text:safeText(D.comment),time:'Just now'});audit('Issue comment added',i.id,'',S.role);save();go('issue-comments?id='+i.id);toast('Update posted');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:564)

### Current native API and database trace

- GET /api/v1/ops/tickets/{value} — expression "/ops/tickets/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:948)
- POST /api/v1/ops/tickets/{value}/follow — expression "/ops/tickets/"+id+"/follow" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:948)
- POST /api/v1/ops/tickets/{value}/comments — expression "/ops/tickets/"+id+"/comments" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:948)

Potential reads: ap_command, ap_tenant.

Potential writes: ap_command, ap_ticket_event, ap_ticket_follow.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()
- throw ApiError.conflict("This incident has ended.")
- V.text(p,"message",2000)))
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:163), [follow](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:178), [follow](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:93), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [comment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:175), [comment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:84), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [event](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:59), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-47"></a>

## 47. Issue photo — issue-photo

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: attachments. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:299). Native entry: [attachments](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1517).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

- GET /api/v1/ops/files — expression "/ops/files?kind="+kind+"&parentId="+parent — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1517)
- GET /api/v1/ops/files/{value} — expression "/ops/files/"+x.optString("id") — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1517)

Potential reads: ap_bill, ap_document, ap_expense, ap_file, ap_flat, ap_payment, ap_payment_reversal, ap_settings.

Potential writes: ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()
- throw ApiError.conflict("Attach files before publishing.")
- throw new IllegalArgumentException("Unsupported attachment type.")
- Server Admin/Treasurer role required

Backend source pointers: [files](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:208), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:78), [access](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:48), [document](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:26), [bill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:127), [ownedBill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:135), [balance](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:21), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [file](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:215), [content](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:118).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-48"></a>

## 48. My requests — complaints

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: complaints. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300). Native entry: [tickets](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:887).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'New request' | icon action | 'data-go="complaint-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300) |
| ['Active','Closed','All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300) |
| c.title | navigate / row | 'complaint?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300) |
| 'New request' | navigate / custom | 'complaint-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300) |

### Current native API and database trace

- GET /api/v1/ops/tickets — expression "/ops/tickets?kind="+kind — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:887)

Potential reads: ap_contact, ap_ticket, ap_ticket_follow, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Invalid issue type.")

Backend source pointers: [tickets](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:160), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:17), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-49"></a>

## 49. Private request — complaint-form

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: complaint-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301). Native entry: [ticketForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:910).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Create request' | submit | complaint-create | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('flat','Flat',S.flats.map(f=>f.id),S.residentFlat)
- field('title','Request title','','text','required maxlength="80" placeholder="e.g. Bathroom pipe leakage"')
- selectField('category','Category',categories.map(c=>c[0]),'Plumbing')
- selectField('priority','Priority',['Normal','High'],'Normal')
- field('description','Details','','textarea','required maxlength="1000"')
- fileField('Photo · optional')

Reference action/form behavior (prototype intent; never backend security proof):

- **complaint-create**: async(D,F)=>{const f=isStaff()?D.flat:S.residentFlat;if(!flat(f))fail('Choose a valid flat.');const c={id:uid('CMP'),flat:f,category:D.category,title:safeText(D.title,80),description:safeText(D.description,1000),priority:D.priority,status:'Open',vendor:'',date:today,comments:[],timeline:[{title:'Request submitted',time:'Today · just now'}],attachment:await dataFile(F.get('attachment'))};S.complaints.unshift(c);notify('Private request',f+' · '+c.title,'staff','complaint?id='+c.id);audit('Private request created',c.id,'New','Open');save();go('complaint?id='+c.id);toast('Request created');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:565)

### Current native API and database trace

- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:910)
- POST /api/v1/ops/tickets — expression "/ops/tickets" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:910)
- GET /api/v1/ops/tickets — expression "/ops/tickets?kind=ISSUE" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:910)

Potential reads: ap_command, ap_contact, ap_flat, ap_tenant, ap_ticket, ap_ticket_follow, ap_user.

Potential writes: ap_audit, ap_command, ap_inbox, ap_ticket, ap_ticket_event, ap_ticket_follow.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"kind","ISSUE","COMPLAINT")
- V.optionalId(p,"flatId"))
- V.text(p,"description",2000)
- throw new IllegalArgumentException("Choose a flat for a private request.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")
- throw new IllegalArgumentException("Invalid issue type.")

Backend source pointers: [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:169), [create](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:41), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [flat](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:43), [event](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:59), [notifyStaff](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:63), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-50"></a>

## 50. Request detail — complaint

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: complaint. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302). Native entry: [ticket](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:948).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Update request' | navigate / custom | 'complaint-update?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302) |
| 'Reopen' | action | reopenComplaint | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302) |
| 'Confirm fixed' | action | closeComplaint | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302) |
| 'View attachment' | navigate / custom | 'complaint-photo?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302) |
| 'Send' | submit | complaint-comment | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('comment','Add a comment','','textarea','required maxlength="500"')

Reference action/form behavior (prototype intent; never backend security proof):

- **reopenComplaint**: d=>{const c=S.complaints.find(x=>x.id===d.id);if(!c\|\|!isStaff()&&c.flat!==S.residentFlat)fail('Access denied.');c.status='Open';c.timeline.push({title:'Reopened by resident',time:'Today'});audit('Request reopened',c.id,'Resolved','Open');save();render();toast('Request reopened');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:523)
- **closeComplaint**: d=>{const c=S.complaints.find(x=>x.id===d.id);if(!c\|\|!isStaff()&&c.flat!==S.residentFlat)fail('Access denied.');c.status='Closed';c.timeline.push({title:'Resident confirmed resolution',time:'Today'});audit('Request closed',c.id,'Resolved','Closed');save();render();toast('Marked as fixed');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:524)
- **complaint-comment**: D=>{const c=S.complaints.find(x=>x.id===D.id);if(!c\|\|!isStaff()&&c.flat!==S.residentFlat)fail('Access denied.');c.comments.push({by:S.role==='Resident'?S.residentFlat:S.role,text:safeText(D.comment),time:'Just now'});audit('Request comment added',c.id,'',S.role);save();go('complaint?id='+c.id);toast('Comment saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:566)

### Current native API and database trace

- GET /api/v1/ops/tickets/{value} — expression "/ops/tickets/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:948)
- POST /api/v1/ops/tickets/{value}/follow — expression "/ops/tickets/"+id+"/follow" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:948)
- POST /api/v1/ops/tickets/{value}/comments — expression "/ops/tickets/"+id+"/comments" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:948)

Potential reads: ap_command, ap_tenant.

Potential writes: ap_command, ap_ticket_event, ap_ticket_follow.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()
- throw ApiError.conflict("This incident has ended.")
- V.text(p,"message",2000)))
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:163), [follow](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:178), [follow](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:93), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [comment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:175), [comment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:84), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [event](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:59), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-51"></a>

## 51. Manage request — complaint-update

Group: Services. Reference roles: Admin, Treasurer. Native destination: ticket-update. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:303). Native entry: [ticketUpdate](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:995).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Update request' | submit | complaint-update | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:303) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('status','Status',['Open','Assigned','In progress','Resolved','Closed'],c.status)
- selectField('vendor','Assign vendor',[['','Not assigned'],...S.vendors.map(v=>[v.id,v.name])],c.vendor\|\|'')
- field('note','Update','','textarea','required maxlength="500"')

Reference action/form behavior (prototype intent; never backend security proof):

- **complaint-update**: D=>{const c=S.complaints.find(x=>x.id===D.id);const old=c.status;c.status=D.status;c.vendor=D.vendor;c.comments.push({by:S.role,text:safeText(D.note),time:'Just now'});c.timeline.push({title:D.status+' · '+safeText(D.note),time:'Today'});audit('Request updated',c.id,old,D.status);notify('Your request was updated',c.title+' · '+c.status,'Resident','complaint?id='+c.id,c.flat);save();go('complaint?id='+c.id);toast('Request updated');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:567)

### Current native API and database trace

- GET /api/v1/ops/tickets/{value} — expression "/ops/tickets/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:995)
- GET /api/v1/ops/contacts — expression "/ops/contacts?kind=VENDOR" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:995)
- POST /api/v1/ops/tickets/{value}/status — expression "/ops/tickets/"+id+"/status" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:995)

Potential reads: ap_command, ap_contact, ap_tenant, ap_ticket_follow, ap_user.

Potential writes: ap_audit, ap_command, ap_inbox, ap_ticket, ap_ticket_event.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Invalid contact type.")
- V.choice(p,"status","OPEN","ASSIGNED","IN_PROGRESS","RESOLVED","CLOSED")
- V.optionalId(p,"vendorId"):(UUID)t.get("vendorId"))
- V.bool(p,"notifyAll",false))
- throw ApiError.forbidden()
- throw new IllegalArgumentException("Choose a vendor before assigning.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:163), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:85), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:171), [ticket](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:172), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:62), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [vendor](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:46), [event](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:59), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-52"></a>

## 52. Service schedule — services

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: services. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:304). Native entry: [services](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1027).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| isStaff()?'Add':'' | navigate / section | isStaff()?'service-form':'' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:304) |
| s.title | navigate / row | 'service?id='+s.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:304) |

### Current native API and database trace

- GET /api/v1/ops/services — expression "/ops/services" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1027)

Potential reads: ap_contact, ap_service.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [services](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:181), [services](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:101), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-53"></a>

## 53. Scheduled service — service

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: service. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305). Native entry: [service](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1040).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Edit' | navigate / custom | 'service-form?id='+s.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305) |
| 'Complete' | navigate / custom | 'service-complete?id='+s.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305) |
| 'View' | navigate / section | 'service-history?id='+s.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305) |
| dateStr(h.date) | navigate / row | 'service-history?id='+s.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305) |

### Current native API and database trace

- GET /api/v1/ops/services/{value} — expression "/ops/services/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1040)

Potential reads: ap_contact, ap_service, ap_service_visit.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [service](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:184), [service](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:104).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-54"></a>

## 54. Schedule service — service-form

Group: Services. Reference roles: Admin, Treasurer. Native destination: service-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:306). Native entry: [serviceForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1062).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save schedule' | submit | service | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:306) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('title','Service name',s.title\|\|'','text','required maxlength="80"')
- selectField('category','Category',categories.map(c=>c[0]),s.category\|\|'Lift')
- selectField('frequency','Repeat',['Monthly','Quarterly','Every 6 months','Annual'],s.frequency\|\|'Monthly')
- field('next','Next service date',s.next\|\|today,'date','required')
- selectField('vendor','Vendor',[['','Not assigned'],...S.vendors.map(v=>[v.id,v.name])],s.vendor\|\|'')
- field('cost','Expected cost (₹)',s.cost\|\|0,'number','min="0" step="0.01"')

Reference action/form behavior (prototype intent; never backend security proof):

- **service**: D=>{let old=S.services.find(x=>x.id===D.id),s={id:old?.id\|\|uid('SVC'),title:safeText(D.title,80),category:D.category,frequency:D.frequency,next:D.next,vendor:D.vendor,cost:Number(D.cost)\|\|0,history:old?.history\|\|[]};if(old)Object.assign(old,s);else S.services.push(s);audit('Service schedule saved',s.title,old?'Edited':'New',s.next);save();go('service?id='+s.id);toast('Schedule saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:568)

### Current native API and database trace

- GET /api/v1/ops/contacts — expression "/ops/contacts?kind=VENDOR" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1062)
- POST /api/v1/ops/services{value} — expression "/ops/services"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1062)
- GET /api/v1/ops/services/{value} — expression "/ops/services/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1062)

Potential reads: ap_contact, ap_service, ap_service_visit.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Invalid contact type.")

Backend source pointers: [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:85), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:171), [service](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:184), [service](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:104).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-55"></a>

## 55. Complete service — service-complete

Group: Services. Reference roles: Admin, Treasurer. Native destination: service-complete. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:307). Native entry: [serviceComplete](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1082).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Complete service' | submit | service-complete | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:307) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('date','Completed on',today,'date','required max="${today}"')
- field('cost','Service cost (₹)',s.cost,'number','min="0" step="0.01" required')
- field('note','Work completed','','textarea','required maxlength="500" placeholder="Service notes"')
- fileField('Service report / bill · optional')
- checkField('expense','Create an expense entry',true)
- checkField('paid','Service cost already paid',false)

Reference action/form behavior (prototype intent; never backend security proof):

- **service-complete**: async(D,F)=>{let s=S.services.find(x=>x.id===D.id);if(!s)fail('Service not found.');if(D.date>today)fail('Completion cannot be in the future.');if(s.history.some(h=>h.date===D.date&&h.cycle===s.next))fail('This service cycle is already completed.');let cost=Number(D.cost)\|\|0;if(cost<0\|\|!Number.isFinite(cost))fail('Enter a valid cost.');let attachment=await dataFile(F.get('attachment')),entry={date:D.date,note:safeText(D.note),cost,cycle:s.next,attachment};if(F.has('expense')&&cost>0){let e={id:uid('EXP'),title:s.title,category:s.category,amount:cost,date:D.date,vendor:s.vendor,mode:'Bank transfer',notes:entry.note,paid:F.has('paid'),public:tr — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)

### Current native API and database trace

- GET /api/v1/ops/services/{value} — expression "/ops/services/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1082)
- POST /api/v1/ops/services/{value}/complete — expression "/ops/services/"+id+"/complete" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1082)

Potential reads: ap_command, ap_contact, ap_service, ap_service_visit, ap_settings, ap_tenant.

Potential writes: ap_audit, ap_command, ap_expense, ap_service, ap_service_visit.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.date(p,"scheduledOn"))
- V.date(p,"completedOn"))
- V.text(p,"note",1000))
- V.bool(p,"paid",false)&&db.count("select count(*) from ap_settings where tenant_id=? and opening_date>?",a.tenantId()
- V.bool(p,"paid",false)
- throw ApiError.conflict("Service is inactive.")
- throw ApiError.conflict("The scheduled cycle changed. Refresh before completing.")
- throw new IllegalArgumentException("Completion cannot be future-dated.")
- throw new IllegalArgumentException("Use a service category of at most 30 characters before recording an expense.")
- throw new IllegalArgumentException("Completion expense predates the opening balance.")
- Server Admin/Treasurer role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [service](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:184), [service](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:104), [complete](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:193), [completeService](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:135), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-56"></a>

## 56. Service history — service-history

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: service. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:308). Native entry: [service](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1040).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'View expense' | navigate / custom | 'expense?id='+h.expenseId | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:308) |

### Current native API and database trace

- GET /api/v1/ops/services/{value} — expression "/ops/services/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1040)

Potential reads: ap_contact, ap_service, ap_service_visit.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [service](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:184), [service](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/TicketService.java:104).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-57"></a>

## 57. Community — community

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: community. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906). Native entry: [community](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:387).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Events' | navigate | 'events' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Parking' | navigate | 'parking-availability' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Members' | navigate | 'members' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| isStaff()?'Send notice':'Notices' | navigate | isStaff()?'notice-form':'notices' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'View all' | navigate / section | 'notices' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Committee' | navigate / row | 'committee' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Polls' | navigate / row | 'polls' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Vehicles' | navigate / row | 'vehicles' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Contacts' | navigate / row | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Vendors' | navigate / row | 'vendors' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Documents' | navigate / row | 'documents' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Service schedule' | navigate / row | 'services' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Committee handover' | navigate / row | 'committee-handover' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-58"></a>

## 58. Notice board — notices

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: notices. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975). Native entry: [notices](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1347).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Create notice' | icon action | 'data-go="notice-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975) |
| isStaff()?['All','Pinned','Drafts','Scheduled']:['All','Pinned'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975) |

### Current native API and database trace

- GET /api/v1/notices — expression "/notices" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1347)

Potential reads: ap_notice, ap_notice_recipient.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [notices](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:146), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:16), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-59"></a>

## 59. Notice — notice

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: notice. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314). Native entry: [notice](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1361).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Attachment' | navigate / custom | 'notice-attachment?id='+n.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314) |
| 'Edit' | navigate / custom | 'notice-form?id='+n.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314) |
| n.pinned?'Unpin':'Pin notice' | action | pinNotice | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314) |
| 'Publish' | action | publishNotice | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314) |
| 'Share' | action | shareNotice | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314) |
| 'Mark as read' | action | readNotice | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314) |

Reference action/form behavior (prototype intent; never backend security proof):

- **pinNotice**: d=>{requireStaff();let n=S.notices.find(x=>x.id===d.id);n.pinned=!n.pinned;save();render();toast(n.pinned?'Notice pinned':'Notice unpinned');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:525)
- **publishNotice**: d=>{requireStaff();let n=S.notices.find(x=>x.id===d.id);n.status='Published';n.date=today;audit('Notice published',n.title,'Draft','Published');notify('New notice',n.title,'all','notice?id='+n.id);save();render();toast('Notice published in demo inbox');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:526)
- **shareNotice**: d=>{let n=S.notices.find(x=>x.id===d.id);if(!n\|\|!visibleNotice(n))fail('Access denied.');return shareText(n.title+'\n\n'+n.body,'Apartment notice');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:527)
- **readNotice**: d=>{let n=S.notices.find(x=>x.id===d.id);n.readFlats=n.readFlats\|\|[];if(!n.readFlats.includes(S.residentFlat)){n.readFlats.push(S.residentFlat);n.readBy++;}save();toast('Marked as read');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:528)

### Current native API and database trace

- GET /api/v1/notices/{value} — expression "/notices/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1361)
- POST /api/v1/notices/{value}/pin — expression "/notices/"+id+"/pin" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1361)
- POST /api/v1/notices/{value}/read — expression "/notices/"+id+"/read" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1361)

Potential reads: None resolved / requires manual tracing.

Potential writes: ap_notice, ap_notice_recipient.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.bool(r,"pinned",true)))
- Server Admin/Treasurer role required
- V.bool(r,"acknowledge",false)))
- throw new IllegalArgumentException("Acknowledgement was not requested.")

Backend source pointers: [notice](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:149), [noticePin](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:171), [pin](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:131), [noticeRead](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:168), [read](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:125).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-60"></a>

## 60. Send notice — notice-form

Group: Community. Reference roles: Admin, Treasurer. Native destination: notice-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792). Native entry: [noticeForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1407).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Preview notice' | submit | notice | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792) |
| 'Templates' | navigate / custom | 'notice-templates' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('title','Title',n.title\|\|t[0]\|\|'','text','required maxlength="100"')
- field('body','Message',n.body\|\|t[1]\|\|'','textarea','required maxlength="2000"')
- selectField('type','Type',['General','Maintenance','Service alert','Emergency'],n.type\|\|t[2]\|\|'General')
- selectField('category','Category',categories.map(c=>c[0]),n.category\|\|t[3]\|\|'Other')
- selectField('audience','Send to',['All flats',...S.apartment.blocks.map(b=>'Block '+b),'Unpaid flats','Selected flats'],n.audience\|\|'All flats','data-dynamic="notice-audience"')
- field('selectedFlats','Selected flat IDs',(n.selectedFlats\|\|[]).join(', '),'text','placeholder="A-101, A-203"')
- fileField('Attachment · optional')
- checkField('push','Notify on phone',n.push??true)
- checkField('ack','Ask members to acknowledge',n.ackRequired\|\|false)
- checkField('pinned','Pin notice',n.pinned\|\|false)
- selectField('mode','Delivery',['Now','Schedule'],n.status==='Scheduled'?'Schedule':'Now','data-notice-mode="true"')
- field('sendAt','Send at',n.sendAt\|\|'2026-09-14T09:00','datetime-local')

Reference action/form behavior (prototype intent; never backend security proof):

- **notice**: async(D,F)=>{let old=S.notices.find(n=>n.id===D.id),selectedFlats=(D.selectedFlats\|\|'').split(',').map(s=>s.trim()).filter(Boolean);if(D.audience==='Selected flats'&&(!selectedFlats.length\|\|selectedFlats.some(f=>!flat(f))))fail('Enter valid comma-separated flat numbers.');let attachment=await dataFile(F.get('attachment')),n={id:old?.id\|\|uid('N'),title:safeText(D.title,100),body:safeText(D.body,2000),type:D.type,category:D.category,audience:D.audience,selectedFlats,status:D.status,pinned:F.has('pinned'),date:old?.date\|\|today,readBy:old?.readBy\|\|0,attachment:attachment\|\|old?.attachment\|\|null};if(old)Object.assign(old,n);else S.notices.unshift(n — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:570)

### Current native API and database trace

- POST /api/v1/notices{value} — expression "/notices"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1407)
- GET /api/v1/notices/{value} — expression "/notices/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1407)

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [notice](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:149).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-61"></a>

## 61. Notice attachment — notice-attachment

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: attachments. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:316). Native entry: [attachments](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1517).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |

### Current native API and database trace

- GET /api/v1/ops/files — expression "/ops/files?kind="+kind+"&parentId="+parent — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1517)
- GET /api/v1/ops/files/{value} — expression "/ops/files/"+x.optString("id") — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1517)

Potential reads: ap_bill, ap_document, ap_expense, ap_file, ap_flat, ap_payment, ap_payment_reversal, ap_settings.

Potential writes: ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()
- throw ApiError.conflict("Attach files before publishing.")
- throw new IllegalArgumentException("Unsupported attachment type.")
- Server Admin/Treasurer role required

Backend source pointers: [files](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:208), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:78), [access](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:48), [document](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:26), [bill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:127), [ownedBill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:135), [balance](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:21), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [file](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:215), [content](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:118).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-62"></a>

## 62. Service vendors — vendors

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: vendors. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:317). Native entry: [contacts](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:827).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Add vendor' | icon action | 'data-go="vendor-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:317) |
| v.name | navigate / row | 'vendor?id='+v.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:317) |

### Current native API and database trace

- GET /api/v1/ops/contacts — expression "/ops/contacts?kind="+kind — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:827)

Potential reads: ap_contact.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Invalid contact type.")

Backend source pointers: [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:85), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:171).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-63"></a>

## 63. Vendor profile — vendor

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: vendor. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318). Native entry: [contact](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:841).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Call' | action | callVendor | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318) |
| 'Edit' | navigate / custom | 'vendor-form?id='+v.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318) |
| 'Copy contact' | action | copyVendor | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318) |
| s.title | navigate / row | 'service?id='+s.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318) |

Reference action/form behavior (prototype intent; never backend security proof):

- **callVendor**: d=>{const v=S.vendors.find(x=>x.id===d.id);callContact(v,'vendor-form?id='+d.id);} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:529)
- **copyVendor**: d=>{const c=S.vendors.find(x=>x.id===d.id);if(!c?.phone)fail('No phone number has been added.');return shareText(c.name+' · '+c.phone);} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:530)

### Current native API and database trace

- GET /api/v1/ops/contacts — expression "/ops/contacts?kind="+kind — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:841)

Potential reads: ap_contact.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Invalid contact type.")

Backend source pointers: [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:85), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:171).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-64"></a>

## 64. Vendor details — vendor-form

Group: Community. Reference roles: Admin, Treasurer. Native destination: vendor-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:319). Native entry: [contactForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:852).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | vendor | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:319) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('name','Vendor name',v.name\|\|'','text','required maxlength="80"')
- selectField('category','Service type',categories.map(c=>c[0]),v.category\|\|'Lift')
- field('contact','Contact person',v.contact\|\|'','text','maxlength="80"')
- field('phone','Phone number',v.phone\|\|'','tel','inputmode="tel" maxlength="16" placeholder="+91…"')
- field('amc','AMC expiry · optional',v.amc\|\|'','date')
- field('notes','Notes',v.notes\|\|'','textarea','maxlength="500"')
- checkField('public','Visible to residents',v.public??true)

Reference action/form behavior (prototype intent; never backend security proof):

- **vendor**: D=>{let v=S.vendors.find(x=>x.id===D.id),n={id:v?.id\|\|uid('V'),name:safeText(D.name,80),category:D.category,contact:safeText(D.contact,80),phone:safeText(D.phone,16),amc:D.amc,notes:safeText(D.notes),public:!!D.public};if(n.phone&&!/^\+?[\d\s-]{7,16}$/.test(n.phone))fail('Enter a valid phone number.');if(v)Object.assign(v,n);else S.vendors.push(n);audit('Vendor saved',n.name,v?'Edited':'New',n.category);save();go('vendor?id='+n.id);toast('Vendor saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:571)

### Current native API and database trace

- GET /api/v1/ops/contacts — expression "/ops/contacts?kind="+kind — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:852)
- POST /api/v1/ops/contacts{value} — expression "/ops/contacts"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:852)

Potential reads: ap_contact.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Invalid contact type.")

Backend source pointers: [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:85), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:171).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-65"></a>

## 65. Important contacts — contacts

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: contacts. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320). Native entry: [contacts](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:827).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| c.name | navigate / row | 'contact?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320) |
| 'Add contact' | navigate / custom | 'contact-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320) |
| 'View all' | navigate / section | 'vendors' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320) |
| v.name | navigate / row | 'vendor?id='+v.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320) |

### Current native API and database trace

- GET /api/v1/ops/contacts — expression "/ops/contacts?kind="+kind — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:827)

Potential reads: ap_contact.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Invalid contact type.")

Backend source pointers: [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:85), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:171).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-66"></a>

## 66. Contact details — contact

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: contact. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:321). Native entry: [contact](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:841).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Call' | action | callContact | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:321) |
| 'Edit' | navigate / custom | 'contact-form?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:321) |
| 'Copy' | action | copyContact | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:321) |

Reference action/form behavior (prototype intent; never backend security proof):

- **callContact**: d=>{const c=S.contacts.find(x=>x.id===d.id);callContact(c,'contact-form?id='+d.id);} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:529)
- **copyContact**: d=>{const c=S.contacts.find(x=>x.id===d.id);if(!c?.phone)fail('No phone number has been added.');return shareText(c.name+' · '+c.phone);} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:530)

### Current native API and database trace

- GET /api/v1/ops/contacts — expression "/ops/contacts?kind="+kind — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:841)

Potential reads: ap_contact.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Invalid contact type.")

Backend source pointers: [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:85), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:171).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-67"></a>

## 67. Edit contact — contact-form

Group: Community. Reference roles: Admin, Treasurer. Native destination: contact-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:322). Native entry: [contactForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:852).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | contact | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:322) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('name','Contact name',c.name\|\|'','text','required maxlength="80"')
- selectField('type','Type',['Admin','Treasurer','Security','Lift','Emergency','Other'],c.type\|\|'Other')
- field('phone','Verified phone number',c.phone\|\|'','tel','required maxlength="16"')
- checkField('verified','I have verified this number.')

Reference action/form behavior (prototype intent; never backend security proof):

- **contact**: D=>{if(!D.verified)fail('Confirm the phone number is verified.');if(!/^\+?[\d\s-]{3,16}$/.test(D.phone))fail('Enter a valid telephone number.');let c=S.contacts.find(x=>x.id===D.id),n={id:c?.id\|\|uid('C'),name:safeText(D.name,80),type:D.type,phone:safeText(D.phone,16),public:true};if(c)Object.assign(c,n);else S.contacts.push(n);audit('Contact saved',n.name,c?'Edited':'New','Verified phone');save();go('contact?id='+n.id);toast('Contact saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:572)

### Current native API and database trace

- GET /api/v1/ops/contacts — expression "/ops/contacts?kind="+kind — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:852)
- POST /api/v1/ops/contacts{value} — expression "/ops/contacts"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:852)

Potential reads: ap_contact.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Invalid contact type.")

Backend source pointers: [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:85), [contacts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:171).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-68"></a>

## 68. Apartment files — documents

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: documents. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323). Native entry: [documents](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1472).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Upload document' | icon action | 'data-go="document-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323) |
| isStaff()?['All','Public','Private']:['All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323) |
| d.title | navigate / row | 'document?id='+d.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323) |

### Current native API and database trace

- GET /api/v1/ops/documents — expression "/ops/documents" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1472)

Potential reads: ap_document.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [documents](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:196), [documents](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:23), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-69"></a>

## 69. Document — document

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: document. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:324). Native entry: [document](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1485).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Download' | action | downloadDocument | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:324) |
| 'Edit' | navigate / custom | 'document-form?id='+d.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:324) |
| 'Remove file' | action | removeDocument | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:324) |

Reference action/form behavior (prototype intent; never backend security proof):

- **downloadDocument**: d=>{const x=S.docs.find(x=>x.id===d.id);if(!x\|\|!isStaff()&&!x.public)fail('Access denied.');if(x.attachment)downloadData(x.attachment);else doDownload(x.name\|\|x.title+'.txt',x.body\|\|'');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:532)
- **removeDocument**: d=>{requireStaff();confirm('Remove document?','The file will be removed from this local reference.','Remove','deleteDocument','data-id="${d.id}"',true);} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:534)

### Current native API and database trace

- GET /api/v1/ops/documents/{value} — expression "/ops/documents/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1485)

Potential reads: ap_document.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()

Backend source pointers: [document](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:199), [document](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:26).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-70"></a>

## 70. Upload document — document-form

Group: Community. Reference roles: Admin, Treasurer. Native destination: document-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:325). Native entry: [documentForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1495).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save document' | submit | document | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:325) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('title','Document title',d.title\|\|'','text','required maxlength="100"')
- selectField('category','Category',['Rules','Meeting minutes','Bill','AMC','Other'],d.category\|\|'Other')
- fileField(d.id?'Replace file · optional':'File')
- checkField('public','Residents can view',d.public??true)

Reference action/form behavior (prototype intent; never backend security proof):

- **document**: async(D,F)=>{let old=S.docs.find(d=>d.id===D.id),attachment=await dataFile(F.get('attachment'));if(!attachment&&!old)fail('Choose a file to upload.');let d={...(old\|\|{}),id:old?.id\|\|uid('DOC'),title:safeText(D.title,100),category:D.category,public:F.has('public'),date:old?.date\|\|today,attachment:attachment\|\|old?.attachment};if(old)Object.assign(old,d);else S.docs.unshift(d);audit('Document saved',d.title,old?'Edited':'New',d.public?'Public':'Private');save();go('document?id='+d.id);toast('Document stored in this browser');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:573)

### Current native API and database trace

- POST /api/v1/ops/documents{value} — expression "/ops/documents"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1495)
- GET /api/v1/ops/documents/{value} — expression "/ops/documents/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1495)

Potential reads: ap_document.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()

Backend source pointers: [document](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:199), [document](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:26).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-71"></a>

## 71. ApartmentCare — welcome

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: welcome. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:345). Native entry: [welcome](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:492).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-72"></a>

## 72. Choose login — login

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: login. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:346). Native entry: [welcome](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:492).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-73"></a>

## 73. Admin login — admin-login

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: admin-login. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:347). Native entry: [login](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:551).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Fill' | action | fillLoginDemo | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:337) |
| 'Sign in' | submit | login | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:338) |
| 'Switch login' | navigate / custom | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:341) |
| 'Forgot PIN?' | navigate / custom | 'forgot-pin' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:341) |
| 'New apartment? Set up' | navigate / custom | 'admin-signup' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343) |
| 'New resident? Join' | navigate / custom | 'join' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343) |

Reference action/form behavior (prototype intent; never backend security proof):

- **fillLoginDemo**: ()=>{const f=document.querySelector('form[data-form="login"]');if(!f)return;f.elements.mobile.value='9000000000';f.elements.pin.value='123456';f.querySelector('button[type="submit"]')?.focus();} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:490)
- **login**: D=>{if(!/^[6-9]\d{9}$/.test(D.mobile)\|\|!/^\d{4,6}$/.test(D.pin))fail('Use a 10-digit mobile and a 4–6 digit demo PIN.');setRole(D.role);toast('Signed in to the demo');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)

### Current native API and database trace

- POST /api/v1/auth/login — expression "/auth/login" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:551)

Potential reads: ap_tenant, ap_user.

Potential writes: ap_rate_limit, ap_session.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Request DTO public record Login(@Pattern(regexp="[6-9][0-9]{9}") @NotNull String mobile, @Pattern(regexp="[0-9]{4,6}") @NotNull String pin,@Pattern(regexp="ADMIN\|TREASURER\|RESIDENT") @NotNull String role)
- throw new ApiError(HttpStatus.UNAUTHORIZED,"Incorrect mobile, PIN or login type.")
- throw new ApiError(HttpStatus.FORBIDDEN,"Account awaiting approval or not active. Contact the apartment admin.")

Backend source pointers: [Login](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:10), [login](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:35), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/RateGate.java:14), [login](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:37), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [issueSession](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:44), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-74"></a>

## 74. Treasurer login — treasurer-login

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: treasurer-login. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:348). Native entry: [login](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:551).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Fill' | action | fillLoginDemo | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:337) |
| 'Sign in' | submit | login | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:338) |
| 'Switch login' | navigate / custom | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:341) |
| 'Forgot PIN?' | navigate / custom | 'forgot-pin' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:341) |
| 'New apartment? Set up' | navigate / custom | 'admin-signup' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343) |
| 'New resident? Join' | navigate / custom | 'join' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343) |

Reference action/form behavior (prototype intent; never backend security proof):

- **fillLoginDemo**: ()=>{const f=document.querySelector('form[data-form="login"]');if(!f)return;f.elements.mobile.value='9000000000';f.elements.pin.value='123456';f.querySelector('button[type="submit"]')?.focus();} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:490)
- **login**: D=>{if(!/^[6-9]\d{9}$/.test(D.mobile)\|\|!/^\d{4,6}$/.test(D.pin))fail('Use a 10-digit mobile and a 4–6 digit demo PIN.');setRole(D.role);toast('Signed in to the demo');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)

### Current native API and database trace

- POST /api/v1/auth/login — expression "/auth/login" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:551)

Potential reads: ap_tenant, ap_user.

Potential writes: ap_rate_limit, ap_session.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Request DTO public record Login(@Pattern(regexp="[6-9][0-9]{9}") @NotNull String mobile, @Pattern(regexp="[0-9]{4,6}") @NotNull String pin,@Pattern(regexp="ADMIN\|TREASURER\|RESIDENT") @NotNull String role)
- throw new ApiError(HttpStatus.UNAUTHORIZED,"Incorrect mobile, PIN or login type.")
- throw new ApiError(HttpStatus.FORBIDDEN,"Account awaiting approval or not active. Contact the apartment admin.")

Backend source pointers: [Login](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:10), [login](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:35), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/RateGate.java:14), [login](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:37), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [issueSession](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:44), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-75"></a>

## 75. Resident login — resident-login

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: resident-login. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:349). Native entry: [login](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:551).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Fill' | action | fillLoginDemo | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:337) |
| 'Sign in' | submit | login | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:338) |
| 'Switch login' | navigate / custom | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:341) |
| 'Forgot PIN?' | navigate / custom | 'forgot-pin' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:341) |
| 'New apartment? Set up' | navigate / custom | 'admin-signup' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343) |
| 'New resident? Join' | navigate / custom | 'join' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343) |

Reference action/form behavior (prototype intent; never backend security proof):

- **fillLoginDemo**: ()=>{const f=document.querySelector('form[data-form="login"]');if(!f)return;f.elements.mobile.value='9000000000';f.elements.pin.value='123456';f.querySelector('button[type="submit"]')?.focus();} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:490)
- **login**: D=>{if(!/^[6-9]\d{9}$/.test(D.mobile)\|\|!/^\d{4,6}$/.test(D.pin))fail('Use a 10-digit mobile and a 4–6 digit demo PIN.');setRole(D.role);toast('Signed in to the demo');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)

### Current native API and database trace

- POST /api/v1/auth/login — expression "/auth/login" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:551)

Potential reads: ap_tenant, ap_user.

Potential writes: ap_rate_limit, ap_session.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Request DTO public record Login(@Pattern(regexp="[6-9][0-9]{9}") @NotNull String mobile, @Pattern(regexp="[0-9]{4,6}") @NotNull String pin,@Pattern(regexp="ADMIN\|TREASURER\|RESIDENT") @NotNull String role)
- throw new ApiError(HttpStatus.UNAUTHORIZED,"Incorrect mobile, PIN or login type.")
- throw new ApiError(HttpStatus.FORBIDDEN,"Account awaiting approval or not active. Contact the apartment admin.")

Backend source pointers: [Login](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:10), [login](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:35), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/RateGate.java:14), [login](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:37), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [issueSession](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:44), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-76"></a>

## 76. Reset PIN — forgot-pin

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: forgot-pin. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:350). Native entry: [recoveryUnavailable](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:573).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Continue' | submit | forgot-pin | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:350) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('mobile','Mobile number','','tel','required pattern="[6-9][0-9]{9}" inputmode="numeric" maxlength="10"')

Reference action/form behavior (prototype intent; never backend security proof):

- **forgot-pin**: D=>{draft.resetMobile=D.mobile;go('verify-reset');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-77"></a>

## 77. Verify number — verify-reset

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: verify-reset. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:351). Native entry: [recoveryUnavailable](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:573).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Verify' | submit | verify-reset | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:351) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('code','Verification code','','text','required pattern="[0-9]{6}" maxlength="6" inputmode="numeric"')

Reference action/form behavior (prototype intent; never backend security proof):

- **verify-reset**: D=>{if(D.code!=='246810')fail('Use the demo code 246810.');go('reset-pin');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-78"></a>

## 78. Set new PIN — reset-pin

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: reset-pin. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:352). Native entry: [recoveryUnavailable](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:573).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save PIN' | submit | reset-pin | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:352) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('pin','New PIN','','password','required pattern="[0-9]{4,6}" maxlength="6" inputmode="numeric"')
- field('confirm','Confirm PIN','','password','required pattern="[0-9]{4,6}" maxlength="6" inputmode="numeric"')

Reference action/form behavior (prototype intent; never backend security proof):

- **reset-pin**: D=>{if(D.pin!==D.confirm)fail('The PINs do not match.');go(loginPath(draft.signInRole));toast('Demo PIN reset flow complete');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-79"></a>

## 79. Join your apartment — join

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: join. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:353). Native entry: [join](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:649).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Find apartment' | submit | join | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:353) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('invite','Invite code','','text','required maxlength="20" placeholder="SAI-0926" autocapitalize="characters"')

Reference action/form behavior (prototype intent; never backend security proof):

- **join**: D=>{if(!S.apartment.inviteActive\|\|S.apartment.inviteExpiry<today\|\|D.invite.trim().toUpperCase()!==S.apartment.invite){go('invite-invalid');return;}draft.inviteVerified=true;go('signup');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:576)

### Current native API and database trace

- GET /api/v1/auth/invites/{value} — expression "/auth/invites/"+inviteCode — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:649)

Potential reads: ap_flat, ap_invite, ap_tenant, ap_user.

Potential writes: ap_rate_limit.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [previewInvite](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:40), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/RateGate.java:14), [previewInvite](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:145), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-80"></a>

## 80. Invite unavailable — invite-invalid

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: invite-invalid. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:354). Native entry: [systemState](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:463).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Try another code' | navigate / custom | 'join' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:354) |
| 'Back to sign in' | navigate / custom | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:354) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-81"></a>

## 81. Create your account — signup

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: signup. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:355). Native entry: [signup](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:670).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Choose flat' | submit | signup | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:355) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('name','Your name',draft.signup?.name\|\|'','text','required maxlength="80"')
- field('mobile','Mobile number',draft.signup?.mobile\|\|'','tel','required pattern="[6-9][0-9]{9}" maxlength="10" inputmode="numeric"')
- selectField('type','I am an',['Owner','Tenant'],'Owner')
- field('pin','Create PIN','','password','required pattern="[0-9]{4,6}" maxlength="6" inputmode="numeric"')
- checkField('consent','I agree to join this apartment and share my details with its admin.')

Reference action/form behavior (prototype intent; never backend security proof):

- **signup**: D=>{if(!D.consent)fail('Agree to share your details with the apartment admin.');if(!/^[6-9]\d{9}$/.test(D.mobile))fail('Enter a valid 10-digit mobile number.');draft.signup={name:safeText(D.name,80),mobile:D.mobile,type:D.type};go('choose-flat');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:577)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-82"></a>

## 82. Choose your flat — choose-flat

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: choose-flat. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:356). Native entry: [chooseFlat](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:690).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Request to join' | action | submitClaim | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:356) |

Reference action/form behavior (prototype intent; never backend security proof):

- **submitClaim**: ()=>{requireOnline();if(!draft.signup)fail('Enter your name and mobile first.');if(!draft.claimFlat)fail('Choose a flat.');const f=flat(draft.claimFlat);if(f.member)fail('That flat already has an active account.');if(S.claims.some(c=>c.flat===f.id&&c.status==='pending'))fail('A join request is already pending for this flat.');const c={...draft.signup,id:uid('CL'),flat:f.id,date:today,status:'pending'};S.claims.push(c);S.signup=c.id;notify('Resident join request',c.name+' · '+c.flat,'Admin','join-review?id='+c.id);audit('Resident requested access',c.flat,'Unassigned','Approval pending');save();go('signup-pending');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)

### Current native API and database trace

- POST /api/v1/auth/join — expression "/auth/join" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:690)

Potential reads: ap_flat, ap_invite, ap_tenant, ap_user.

Potential writes: ap_audit, ap_inbox, ap_rate_limit, ap_user.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Request DTO public record Join(@NotBlank @Size(max=60) String invite,@NotBlank @Size(max=20) String flatLabel, @NotBlank @Size(max=80) String name,@Pattern(regexp="[6-9][0-9]{9}") @NotNull String mobile, @Pattern(regexp="[0-9]{4,6}") @NotNull String pin,@Pattern(regexp="OWNER\|TENANT") @NotNull String residentType)
- throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"Account verification has not been configured. Registration is disabled outside the local profile.")

Backend source pointers: [Join](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:14), [join](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:44), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/RateGate.java:14), [join](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:151), [requireLocal](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:24), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-83"></a>

## 83. Join request — signup-pending

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: signup-pending. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357). Native entry: [go](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:173).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Open my apartment' | action | openNewResident | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357) |
| 'Try again' | navigate / custom | 'join' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357) |
| 'Check status' | action | refreshScreen | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357) |
| 'Back to sign in' | navigate / custom | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357) |

Reference action/form behavior (prototype intent; never backend security proof):

- **openNewResident**: ()=>{const c=S.claims.find(x=>x.id===S.signup);if(!c\|\|c.status!=='approved')fail('Admin approval is pending.');S.residentFlat=c.flat;S.profile.name=c.name;S.profile.mobile=c.mobile;S.profile.type=c.type;setRole('Resident');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:537)
- **refreshScreen**: ()=>render() — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:497)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-84"></a>

## 84. Create apartment — admin-signup

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: admin-signup. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:358). Native entry: [register](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:578).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Continue' | submit | admin-signup | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:358) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('name','Your name','','text','required maxlength="80"')
- field('mobile','Mobile number','','tel','required pattern="[6-9][0-9]{9}" maxlength="10"')
- field('pin','Create PIN','','password','required pattern="[0-9]{4,6}" maxlength="6"')
- checkField('confirm','I am authorised to manage this apartment.')

Reference action/form behavior (prototype intent; never backend security proof):

- **admin-signup**: D=>{if(!D.confirm)fail('Confirm you are authorised to set up the apartment.');draft.admin={name:safeText(D.name,80),mobile:D.mobile};go('setup-apartment');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:578)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-85"></a>

## 85. Apartment details — setup-apartment

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: setup-apartment. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:359). Native entry: [setup](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:601).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Create apartment' | submit | setup-apartment | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:359) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('name','Apartment name',draft.apartment?.name\|\|'','text','required maxlength="80"')
- selectField('city','City',['Hyderabad','Bengaluru','Chennai','Pune','Mumbai','Delhi NCR','Other'],'Hyderabad')
- field('address','Address','','textarea','required maxlength="300"')
- field('pincode','PIN code','','text','required pattern="[0-9]{6}" maxlength="6" inputmode="numeric"')
- field('flats','Number of flats',10,'number','min="5" max="50" required')

Reference action/form behavior (prototype intent; never backend security proof):

- **setup-apartment**: D=>{const count=Number(D.flats);if(!Number.isInteger(count)\|\|count<5\|\|count>50)fail('This release supports 5–50 flats.');const oldTheme=S.theme,oldThemeMode=S.themeMode,admin=draft.admin;S=makeSeed();S.theme=oldTheme;S.themeMode=oldThemeMode;S.role='Admin';S.staffProfiles={Admin:{name:admin?.name\|\|'Admin',mobile:admin?.mobile\|\|'',email:''}};S.apartment={...S.apartment,name:safeText(D.name,80),city:D.city,address:safeText(D.address,300),pincode:D.pincode,opening:0,blocks:['A'],invite:'APT-'+Math.random().toString(36).slice(2,6).toUpperCase()};S.flats=Array.from({length:count},(_,i)=>({id:'A-'+String(101+i),block:'A',floor:Math.floor(i/5)+1,nam — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:579)

### Current native API and database trace

- GET /api/v1/status — expression "/status" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:601)
- POST /api/v1/auth/register — expression "/auth/register" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:601)

Potential reads: ap_tenant, ap_user.

Potential writes: ap_audit, ap_block, ap_flat, ap_rate_limit, ap_session, ap_tenant, ap_user.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Request DTO public record Register(@NotBlank @Size(max=80) String name,@Pattern(regexp="[6-9][0-9]{9}") @NotNull String mobile, @Pattern(regexp="[0-9]{4,6}") @NotNull String pin,@NotBlank @Size(max=80) String apartmentName, @NotBlank @Size(max=80) String city,@NotNull @Min(5) @Max(50) Integer flats)
- throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"Account verification has not been configured. Registration is disabled outside the local profile.")

Backend source pointers: [status](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:28), [Register](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:12), [register](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:31), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/RateGate.java:14), [register](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:27), [requireLocal](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:24), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57), [issueSession](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:44), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-86"></a>

## 86. Apartment ready — setup-done

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. Native destination: setup-done. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:360). Native entry: [setupDone](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:639).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Review flats' | navigate / row | 'flats' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:360) |
| 'Set maintenance' | navigate / row | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:360) |
| 'Invite residents' | navigate / row | 'invite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:360) |
| 'Open dashboard' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:360) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-87"></a>

## 87. Apartment — apartment

Group: Administration. Reference roles: Admin. Native destination: apartment. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362). Native entry: [apartment](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:470).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Apartment profile' | navigate / row | 'apartment-edit' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |
| 'Blocks' | navigate / row | 'blocks' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |
| 'Flats' | navigate / row | 'flats' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |
| 'Residents' | navigate / row | 'residents' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |
| 'Admin & treasurer' | navigate / row | 'roles' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |
| 'Invite residents' | navigate / row | 'invite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |
| 'Resident handover' | navigate / row | 'handover' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |

### Current native API and database trace

- GET /api/v1/ops/apartment — expression "/ops/apartment" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:470)

Potential reads: ap_tenant.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [apartment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:30), [apartment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:19), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-88"></a>

## 88. Apartment profile — apartment-edit

Group: Administration. Reference roles: Admin. Native destination: apartment-edit. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:363). Native entry: [apartmentEdit](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:486).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | apartment | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:363) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('name','Apartment name',S.apartment.name,'text','required maxlength="80"')
- field('city','City',S.apartment.city,'text','required maxlength="80"')
- field('address','Address',S.apartment.address,'textarea','required maxlength="300"')
- field('pincode','PIN code',S.apartment.pincode,'text','required pattern="[0-9]{6}" maxlength="6"')

Reference action/form behavior (prototype intent; never backend security proof):

- **apartment**: D=>{const old=S.apartment.name;Object.assign(S.apartment,{name:safeText(D.name,80),city:safeText(D.city,80),address:safeText(D.address,300),pincode:D.pincode});audit('Apartment profile updated',S.apartment.name,old,S.apartment.name);save();go('apartment');toast('Apartment saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:580)

### Current native API and database trace

- GET /api/v1/ops/apartment — expression "/ops/apartment" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:486)
- POST /api/v1/ops/apartment — expression "/ops/apartment" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:486)

Potential reads: ap_tenant.

Potential writes: ap_audit, ap_tenant.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.text(p,"name",80))
- V.text(p,"city",80)
- Server Admin role required

Backend source pointers: [apartment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:30), [apartment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:19), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [apartment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:33), [saveApartment](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:22), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-89"></a>

## 89. Blocks — blocks

Group: Administration. Reference roles: Admin. Native destination: blocks. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:364). Native entry: [blocks](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:511).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Add' | navigate / section | 'block-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:364) |
| 'Block '+b | navigate / row | 'block-form?id='+b | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:364) |

### Current native API and database trace

- GET /api/v1/ops/blocks — expression "/ops/blocks" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:511)

Potential reads: ap_block, ap_flat.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [blocks](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:36), [blocks](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:30), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-90"></a>

## 90. Block details — block-form

Group: Administration. Reference roles: Admin. Native destination: block-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:365). Native entry: [blockForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:523).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | block | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:365) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('name','Block name',Q.id\|\|'','text','required maxlength="8" placeholder="A"')

Reference action/form behavior (prototype intent; never backend security proof):

- **block**: D=>{let name=D.name.trim().toUpperCase();if(S.apartment.blocks.includes(name)&&name!==D.id)fail('This block already exists.');if(D.id){S.apartment.blocks=S.apartment.blocks.map(b=>b===D.id?name:b);S.flats.filter(f=>f.block===D.id).forEach(f=>f.block=name);}else S.apartment.blocks.push(name);audit('Block saved',name,D.id\|\|'New',name);save();go('blocks');toast('Block saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:581)

### Current native API and database trace

- GET /api/v1/ops/blocks — expression "/ops/blocks" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:523)
- POST /api/v1/ops/blocks{value} — expression "/ops/blocks"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:523)

Potential reads: ap_block, ap_flat.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [blocks](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:36), [blocks](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:30), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-91"></a>

## 91. Flats — flats

Group: Administration. Reference roles: Admin. Native destination: flats. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:366). Native entry: [flats](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:499).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Add flat' | icon action | 'data-go="flat-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:366) |
| ['All',...S.apartment.blocks] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:366) |

### Current native API and database trace

- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:499)

Potential reads: ap_flat, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-92"></a>

## 92. Flat details — flat

Group: Administration. Reference roles: Admin. Native destination: flat-form. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367). Native entry: [flatForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:533).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Edit flat' | navigate / custom | 'flat-form?id='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367) |
| 'Payment history' | navigate / custom | 'history?flat='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367) |
| 'Current dues' | navigate / custom | 'bill?id='+b.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367) |
| 'Resident handover' | navigate / custom | 'handover?flat='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367) |

### Current native API and database trace

- GET /api/v1/ops/blocks — expression "/ops/blocks" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:533)
- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:533)
- POST /api/v1/ops/flats{value} — expression "/ops/flats"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:533)

Potential reads: ap_block, ap_flat, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [blocks](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:36), [blocks](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:30), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-93"></a>

## 93. Flat setup — flat-form

Group: Administration. Reference roles: Admin. Native destination: flat-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:368). Native entry: [flatForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:533).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | flat | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:368) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('id','Flat ID',f.id\|\|'','text','required maxlength="12" placeholder="A-101" ${f.id?'readonly':''}')
- selectField('block','Block',S.apartment.blocks,f.block\|\|S.apartment.blocks[0])
- field('floor','Floor',f.floor\|\|1,'number','min="0" max="99" required')
- checkField('occupied','Occupied',f.occupied??true)
- checkField('active','Active for maintenance billing',f.active??true)

Reference action/form behavior (prototype intent; never backend security proof):

- **flat**: D=>{const existing=flat(D.original),id=D.id.toUpperCase().trim();if(!existing&&flat(id))fail('That flat ID already exists.');if(!existing&&S.flats.length>=50)fail('This reference supports up to 50 flats.');const f={...(existing\|\|{name:'Unassigned',member:false,type:'Owner'}),id,block:D.block,floor:Number(D.floor),occupied:!!D.occupied,active:!!D.active,billing:S.settings.amount};if(existing)Object.assign(existing,f);else S.flats.push(f);audit('Flat saved',id,existing?'Edited':'New',f.active?'Active':'Inactive');save();go('flat?id='+id);toast('Flat saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:582)

### Current native API and database trace

- GET /api/v1/ops/blocks — expression "/ops/blocks" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:533)
- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:533)
- POST /api/v1/ops/flats{value} — expression "/ops/flats"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:533)

Potential reads: ap_block, ap_flat, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [blocks](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:36), [blocks](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:30), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-94"></a>

## 94. Members — residents

Group: Administration. Reference roles: Admin. Native destination: residents. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:769). Native entry: [directory](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:559).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

- GET /api/v1/ops/directory — expression "/ops/directory" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:559)

Potential reads: ap_flat, ap_member_record, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [directory](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:54), [directory](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:82).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-95"></a>

## 95. Resident profile — resident

Group: Administration. Reference roles: Admin. Native destination: directory-detail. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:370). Native entry: [directoryDetail](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2308).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Payments' | navigate / custom | 'history?flat='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:370) |
| 'View flat' | navigate / custom | 'flat?id='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:370) |
| 'Transfer flat account' | navigate / custom | 'handover?flat='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:370) |

### Current native API and database trace

- GET /api/v1/ops/directory — expression "/ops/directory" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2308)

Potential reads: ap_flat, ap_member_record, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [directory](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:54), [directory](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:82).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-96"></a>

## 96. Join requests — join-requests

Group: Administration. Reference roles: Admin. Native destination: join-requests. Recorded status: CONNECTED_LOCAL_EMULATOR_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:371). Native entry: [joinRequests](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:669).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| ['Pending','Approved','Rejected'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:371) |
| c.name | navigate / row | 'join-review?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:371) |

### Current native API and database trace

- GET /api/v1/members — expression "/members" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:669)

Potential reads: ap_flat, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [members](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:59), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-97"></a>

## 97. Review resident — join-review

Group: Administration. Reference roles: Admin. Native destination: join-review. Recorded status: CONNECTED_LOCAL_EMULATOR_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372). Native entry: [joinReview](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:714).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Reject' | navigate / custom | 'join-reject?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372) |
| 'Approve resident' | action | approveClaim | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372) |

Reference action/form behavior (prototype intent; never backend security proof):

- **approveClaim**: d=>{requireAdmin();const c=S.claims.find(x=>x.id===d.id);if(!c\|\|c.status!=='pending')fail('Already reviewed.');if(flat(c.flat)?.member)fail('This flat already has an account. Complete handover first.');sheet('Approve flat access',form('approve-claim','<input type="hidden" name="id" value="${c.id}"><div class="card mb">${kv('Resident',c.name)}${kv('Flat',c.flat)}</div>${checkField('verified','I verified this resident and their flat.')}','Approve resident','userPlus'));} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:536)

### Current native API and database trace

- GET /api/v1/members/{value} — expression "/members/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:714)
- POST /api/v1/members/{value}/approve — expression "/members/"+id+"/approve" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:714)

Potential reads: ap_flat, ap_tenant, ap_user.

Potential writes: ap_audit, ap_inbox, ap_user.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required
- throw ApiError.conflict("This request is not pending.")

Backend source pointers: [member](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:63), [member](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:173), [approveMember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:81), [approve](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:165), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-98"></a>

## 98. Reject join request — join-reject

Group: Administration. Reference roles: Admin. Native destination: join-reject. Recorded status: CONNECTED_LOCAL_EMULATOR_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:373). Native entry: [joinReject](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:741).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Reject request' | submit | join-reject | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:373) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('reason','Reason for resident','','textarea','required maxlength="300" placeholder="e.g. Please choose the correct flat"')

Reference action/form behavior (prototype intent; never backend security proof):

- **join-reject**: D=>{const c=S.claims.find(x=>x.id===D.id);if(!c\|\|c.status!=='pending')fail('Already reviewed.');c.status='rejected';c.reason=safeText(D.reason,300);audit('Join request rejected',c.flat,'Pending',c.reason);save();go('join-requests');toast('Request rejected');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:584)

### Current native API and database trace

- GET /api/v1/members/{value} — expression "/members/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:741)
- POST /api/v1/members/{value}/reject — expression "/members/"+id+"/reject" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:741)

Potential reads: ap_flat, ap_tenant, ap_user.

Potential writes: ap_audit, ap_user.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required
- Request DTO public record Reject(@NotBlank @Size(max=300) String reason)
- throw ApiError.conflict("This request is not pending.")

Backend source pointers: [member](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:63), [member](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:173), [Reject](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:24), [rejectMember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:85), [reject](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:177), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-99"></a>

## 99. Invite residents — invite

Group: Administration. Reference roles: Admin. Native destination: invite. Recorded status: CONNECTED_LOCAL_EMULATOR_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374). Native entry: [invite](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1368).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Copy code' | action | copyInvite | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374) |
| 'Share' | action | shareInvite | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374) |
| 'Expiry date' | navigate / row | 'invite-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374) |
| 'Approval required' | navigate / row | 'permissions' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374) |
| 'Manage invite' | navigate / custom | 'invite-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374) |

Reference action/form behavior (prototype intent; never backend security proof):

- **copyInvite**: ()=>shareText(S.apartment.invite,'Apartment invite') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:538)
- **shareInvite**: ()=>shareText('Join ${S.apartment.name}\nInvite code: ${S.apartment.invite}\nValid until ${dateStr(S.apartment.inviteExpiry)}\nYour flat needs admin approval.','Apartment invite') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:538)

### Current native API and database trace

- GET /api/v1/invites/current — expression "/invites/current" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1368)

Potential reads: ap_invite.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required
- throw new IllegalArgumentException("Invalid ciphertext length.")
- throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"The current invite cannot be displayed with this server key. Restore the configured key or issue a new code.")
- throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"Invite display encryption is NOT_CONFIGURED. Configure APP_INVITE_DISPLAY_KEY before issuing codes.")

Backend source pointers: [inviteStatus](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:66), [inviteStatus](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:92), [inviteView](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:60), [configured](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/InviteCipher.java:31), [expiryOf](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:57), [decrypt](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/InviteCipher.java:54), [requiredKey](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/InviteCipher.java:33), [latestInvite](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:54), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-100"></a>

## 100. Invite controls — invite-settings

Group: Administration. Reference roles: Admin. Native destination: invite-settings. Recorded status: CONNECTED_LOCAL_EMULATOR_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:375). Native entry: [inviteSettings](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:764).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | invite-settings | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:375) |
| 'New code' | action | regenerateInvite | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:375) |
| S.apartment.inviteActive?'Revoke':'Activate' | action | toggleInvite | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:375) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('expiry','Valid until',S.apartment.inviteExpiry,'date','min="${today}" required')

Reference action/form behavior (prototype intent; never backend security proof):

- **invite-settings**: D=>{if(D.expiry<today)fail('Choose today or a future date.');S.apartment.inviteExpiry=D.expiry;audit('Invite expiry changed',S.apartment.invite,'Old expiry',D.expiry);save();go('invite');toast('Invite expiry saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:585)
- **regenerateInvite**: ()=>{requireAdmin();confirm('Generate new code?','The current code will stop working. Approved accounts keep their access.','Generate','confirmNewInvite');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:539)
- **toggleInvite**: ()=>{requireAdmin();S.apartment.inviteActive=!S.apartment.inviteActive;audit('Invite status changed',S.apartment.invite,!S.apartment.inviteActive?'Active':'Revoked',S.apartment.inviteActive?'Active':'Revoked');save();render();toast(S.apartment.inviteActive?'Invite active':'Invite revoked');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:541)

### Current native API and database trace

- GET /api/v1/invites/current — expression "/invites/current" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:764)
- PUT /api/v1/invites/current/expiry — expression "/invites/current/expiry" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:764)
- POST /api/v1/invites — expression "/invites" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:764)

Potential reads: ap_invite, ap_tenant.

Potential writes: ap_audit, ap_invite.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required
- throw new IllegalArgumentException("Invalid ciphertext length.")
- throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"The current invite cannot be displayed with this server key. Restore the configured key or issue a new code.")
- throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"Invite display encryption is NOT_CONFIGURED. Configure APP_INVITE_DISPLAY_KEY before issuing codes.")
- Request DTO public record InviteExpiry(@NotNull LocalDate expiry)
- throw new ApiError(HttpStatus.BAD_REQUEST,"Choose today or a date within one year.")
- Request DTO public record InviteRotate(@NotNull UUID requestKey)
- throw new IllegalStateException("Could not protect the invite code.",e)

Backend source pointers: [inviteStatus](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:66), [inviteStatus](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:92), [inviteView](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:60), [configured](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/InviteCipher.java:31), [expiryOf](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:57), [decrypt](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/InviteCipher.java:54), [requiredKey](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/InviteCipher.java:33), [latestInvite](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:54), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [InviteExpiry](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/Requests.java:28), [inviteExpiry](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:72), [inviteExpiry](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:113), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-101"></a>

## 101. Resident handover — handover

Group: Administration. Reference roles: Admin. Native destination: handover. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:376). Native entry: [handover](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:657).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Start handover' | submit | handover | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:376) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('flat','Flat',S.flats.filter(f=>f.member).map(f=>[f.id,f.id+' · '+f.name]),Q.flat\|\|S.flats.find(f=>f.member)?.id)
- field('name','Incoming resident name','','text','required maxlength="80"')
- field('mobile','Incoming mobile','','tel','required pattern="[6-9][0-9]{9}" maxlength="10"')
- selectField('type','Resident type',['Owner','Tenant'],'Tenant')
- checkField('confirmed','I have verified the handover with both parties.')

Reference action/form behavior (prototype intent; never backend security proof):

- **handover**: D=>{if(!D.confirmed)fail('Confirm that the handover was verified.');const f=flat(D.flat);if(!f?.member)fail('This flat has no active account to transfer.');const previous=f.name;f.member=false;f.formerResident=previous;f.name='Awaiting handover';const c={id:uid('CL'),name:safeText(D.name,80),mobile:D.mobile,flat:f.id,type:D.type,status:'pending',date:today,handover:true};S.claims.push(c);audit('Resident handover started',f.id,previous+' · active','Previous access revoked; '+c.name+' pending');save();go('join-review?id='+c.id);toast('Old access revoked. New resident pending.');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:586)

### Current native API and database trace

- GET /api/v1/members — expression "/members" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:657)
- POST /api/v1/ops/members/{value}/end-access — expression "/ops/members/"+f.get("member")+"/end-access" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:657)

Potential reads: ap_flat, ap_session, ap_tenant, ap_user.

Potential writes: ap_audit, ap_session, ap_user.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required
- V.text(p,"reason",300))
- throw ApiError.conflict("You cannot end your own Admin access.")

Backend source pointers: [members](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:59), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [end](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:66), [endAccess](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:119), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-102"></a>

## 102. Team access — roles

Group: Administration. Reference roles: Admin. Native destination: roles. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:377). Native entry: [roles](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:635).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Add' | navigate / section | 'role-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:377) |
| r.name | navigate / row | 'role-form?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:377) |
| 'Permissions' | navigate / custom | 'permissions' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:377) |

### Current native API and database trace

- GET /api/v1/members — expression "/members" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:635)

Potential reads: ap_flat, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [members](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:59), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-103"></a>

## 103. Team role — role-form

Group: Administration. Reference roles: Admin. Native destination: role-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:378). Native entry: [roleForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:647).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | role | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:378) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('name','Name',r.name\|\|'','text','required maxlength="80"')
- selectField('flat','Flat',S.flats.filter(f=>f.member).map(f=>f.id),r.flat\|\|'A-101')
- selectField('role','Role',['Admin','Treasurer'],r.role\|\|'Treasurer')

Reference action/form behavior (prototype intent; never backend security proof):

- **role**: D=>{let old=S.roles.find(x=>x.id===D.id);if(old?.role==='Admin'&&D.role!=='Admin'&&S.roles.filter(r=>r.role==='Admin').length===1)fail('Keep at least one Admin account.');let r={id:old?.id\|\|uid('ROLE'),name:safeText(D.name,80),flat:D.flat,role:D.role};if(old)Object.assign(old,r);else S.roles.push(r);audit('Team access saved',r.name,old?'Existing':'New',r.role);save();go('roles');toast('Role saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:587)

### Current native API and database trace

- POST /api/v1/ops/members/{value}/role — expression "/ops/members/"+id+"/role" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:647)

Potential reads: ap_session, ap_tenant, ap_user.

Potential writes: ap_audit, ap_session, ap_user.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"role","ADMIN","TREASURER","RESIDENT"))
- throw ApiError.conflict("Use another Admin to change your access.")
- throw ApiError.conflict("A resident must have an assigned flat.")
- Server Admin role required

Backend source pointers: [role](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:63), [changeRole](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:107), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-104"></a>

## 104. Permissions — permissions

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: permissions. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:379). Native entry: [permissions](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2085).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-105"></a>

## 105. More — more

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: more. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381). Native entry: [more](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:401).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Apartment' | navigate / row | 'apartment' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Contacts' | navigate / row | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Documents' | navigate / row | 'documents' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Service schedule' | navigate / row | 'services' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Activity log' | navigate / row | 'audit' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Appearance' | navigate / row | 'appearance' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Notifications' | navigate / row | 'notification-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Automation' | navigate / row | 'automation' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Apartment plan' | navigate / row | 'subscription' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Security' | navigate / row | 'security' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Help & about' | navigate / row | 'help' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Screen library' | navigate / row | 'screen-map' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Preview controls' | navigate / row | 'demo-controls' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Sign out' | action | logout | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |

Reference action/form behavior (prototype intent; never backend security proof):

- **logout**: ()=>confirm('Sign out?','Saved apartment records will remain in this demo.','Sign out','confirmLogout') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:498)

### Current native API and database trace

- POST /api/v1/logout — expression "/logout" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:401)

Potential reads: ap_session.

Potential writes: ap_session.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [logout](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:48), [logout](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:195), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-106"></a>

## 106. My profile — profile

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: profile. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:382). Native entry: [profile](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:435).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Edit profile' | navigate / custom | 'profile-edit' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:382) |

### Current native API and database trace

- GET /api/v1/me — expression "/me" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:435)

Potential reads: ap_tenant, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [me](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:52), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-107"></a>

## 107. Edit profile — profile-edit

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: profile-edit. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:383). Native entry: [profileEdit](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:445).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | profile | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:383) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('name','Name',currentProfile().name,'text','required maxlength="80"')
- field('mobile','Mobile',currentProfile().mobile,'text','maxlength="16"')
- field('email','Email · optional',currentProfile().email,'email','maxlength="100"')

Reference action/form behavior (prototype intent; never backend security proof):

- **profile**: D=>{if(S.role==='Resident'){Object.assign(S.profile,{name:safeText(D.name,80),mobile:safeText(D.mobile,16),email:safeText(D.email,100)});const f=flat(S.residentFlat);if(f)f.name=S.profile.name;}else{S.staffProfiles=S.staffProfiles\|\|{};S.staffProfiles[S.role]={name:safeText(D.name,80),mobile:safeText(D.mobile,16),email:safeText(D.email,100)};}audit('Profile updated',S.role,'Existing','Saved');save();go('profile');toast('Profile saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:588)

### Current native API and database trace

- POST /api/v1/ops/profile — expression "/ops/profile" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:445)

Potential reads: None resolved / requires manual tracing.

Potential writes: ap_audit, ap_user.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.text(p,"name",80)
- throw new IllegalArgumentException("Invalid email.")

Backend source pointers: [profile](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:69), [profile](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:130), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-108"></a>

## 108. Notifications — notifications

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: notifications. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:808). Native entry: [notifications](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1495).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Mark all read' | action | readAll | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:808) |
| n.title | navigate / row | 'notification?id='+n.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:808) |
| 'Send notice' | navigate / custom | 'notice-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:808) |

Reference action/form behavior (prototype intent; never backend security proof):

- **readAll**: ()=>{S.notifications.filter(n=>(n.role==='all'\|\|n.role===S.role\|\|isStaff()&&n.role==='staff')&&visibleNotification(n)).forEach(n=>n.read=true);save();render();toast('Notifications marked as read');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:542)

### Current native API and database trace

- POST /api/v1/ops/notifications/read-all — expression "/ops/notifications/read-all" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1495)
- GET /api/v1/notifications — expression "/notifications" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1495)
- POST /api/v1/notifications/{value}/read — expression "/notifications/"+n.optString("id")+"/read" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1495)

Potential reads: ap_inbox.

Potential writes: ap_inbox.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.missing()

Backend source pointers: [readAll](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:266), [inbox](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:174), [read](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:180).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-109"></a>

## 109. Notification — notification

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: notification. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:809). Native entry: [notificationDetail](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1512).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Open details' | navigate / custom | n.target | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:809) |

### Current native API and database trace

- GET /api/v1/notifications/{value} — expression "/notifications/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1512)

Potential reads: ap_inbox.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [notification](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:177).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-110"></a>

## 110. Notifications — notification-settings

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: settings. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386). Native entry: [settingsForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1551).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Push notifications' | toggle | 'push' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |
| 'Issue updates' | toggle | 'issueNotify' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |
| 'Service reminders' | toggle | 'serviceNotify' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |
| 'New bill notifications' | toggle | 'dueNotify' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |
| 'Unpaid reminders' | toggle | 'reminders' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |
| 'Reminder schedule' | navigate / custom | 'reminder-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |
| 'Save quiet hours' | submit | quiet-hours | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('quietStart','From',preference('quietStart'),'time')
- field('quietEnd','Until',preference('quietEnd'),'time')

Reference action/form behavior (prototype intent; never backend security proof):

- **quiet-hours**: D=>{setPreference('quietStart',D.quietStart);setPreference('quietEnd',D.quietEnd);save();go('notification-settings');toast('Quiet hours saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:589)

### Current native API and database trace

- GET /api/v1/ops/settings — expression "/ops/settings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1551)
- POST /api/v1/ops/settings/{value} — expression "/ops/settings/"+section — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1551)

Potential reads: ap_command, ap_settings, ap_tenant.

Potential writes: ap_audit, ap_command, ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Choose a valid settings section.")
- throw new IllegalArgumentException("Only fields from this settings section can be changed.")
- Server Admin/Treasurer role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.opt(p,"reminderDays",80))
- V.text(p,"quietStart",5)
- V.opt(p,"ifsc",11).toUpperCase(Locale.ROOT))
- V.opt(p,"payee",100)
- V.bool(p,"expensesVisible",true)
- throw new IllegalArgumentException("Quiet hours use HH:mm.")
- throw new IllegalArgumentException("Check the IFSC.")
- V.id(body,"requestKey")

Backend source pointers: [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:94), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:18), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [settingsSection](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:100), [saveSettingsSection](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:24), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [saveSettings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:46), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-111"></a>

## 111. Reminder schedule — reminder-settings

Group: Settings. Reference roles: Admin, Treasurer. Native destination: settings. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:387). Native entry: [settingsForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1551).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | reminder-settings | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:387) |
| 'Preview recipients' | navigate / custom | 'reminder-preview' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:387) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('days','Days of month',S.settings.reminderDays,'text','required placeholder="5, 9, 11, 15"')

Reference action/form behavior (prototype intent; never backend security proof):

- **reminder-settings**: D=>{let days=D.days.split(',').map(v=>Number(v.trim()));if(!days.length\|\|days.some(v=>!Number.isInteger(v)\|\|v<1\|\|v>28))fail('Enter comma-separated days from 1 to 28.');S.settings.reminderDays=[...new Set(days)].sort((a,b)=>a-b).join(', ');audit('Reminder schedule changed','Maintenance','Previous schedule',S.settings.reminderDays);save();go('notification-settings');toast('Reminder schedule saved');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:590)

### Current native API and database trace

- GET /api/v1/ops/settings — expression "/ops/settings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1551)
- POST /api/v1/ops/settings/{value} — expression "/ops/settings/"+section — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1551)

Potential reads: ap_command, ap_settings, ap_tenant.

Potential writes: ap_audit, ap_command, ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw new IllegalArgumentException("Choose a valid settings section.")
- throw new IllegalArgumentException("Only fields from this settings section can be changed.")
- Server Admin/Treasurer role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.opt(p,"reminderDays",80))
- V.text(p,"quietStart",5)
- V.opt(p,"ifsc",11).toUpperCase(Locale.ROOT))
- V.opt(p,"payee",100)
- V.bool(p,"expensesVisible",true)
- throw new IllegalArgumentException("Quiet hours use HH:mm.")
- throw new IllegalArgumentException("Check the IFSC.")
- V.id(body,"requestKey")

Backend source pointers: [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:94), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:18), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [settingsSection](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:100), [saveSettingsSection](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:24), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [saveSettings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:46), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-112"></a>

## 112. Appearance — appearance

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: appearance. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:388). Native entry: [appearance](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2051).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Use device theme' | action | themeSystem | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:388) |
| 'Larger text' | toggle | 'largeText' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:388) |
| 'Reduce motion' | toggle | 'reduceMotion' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:388) |

Reference action/form behavior (prototype intent; never backend security proof):

- **themeSystem**: ()=>setTheme('system') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:492)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-113"></a>

## 113. Account security — security

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: profile. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:389). Native entry: [profile](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:435).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Change PIN' | navigate / row | 'change-pin' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:389) |
| 'Access permissions' | navigate / row | 'permissions' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:389) |
| 'Data & privacy' | navigate / row | 'privacy' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:389) |
| 'Sign out' | action | logout | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:389) |

Reference action/form behavior (prototype intent; never backend security proof):

- **logout**: ()=>confirm('Sign out?','Saved apartment records will remain in this demo.','Sign out','confirmLogout') — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:498)

### Current native API and database trace

- GET /api/v1/me — expression "/me" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:435)

Potential reads: ap_tenant, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [me](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:52), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-114"></a>

## 114. Change PIN — change-pin

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: change-pin. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:390). Native entry: [pin](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:457).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save' | submit | change-pin | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:390) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('current','Current PIN','','password','required pattern="[0-9]{4,6}" maxlength="6"')
- field('pin','New PIN','','password','required pattern="[0-9]{4,6}" maxlength="6"')
- field('confirm','Confirm new PIN','','password','required pattern="[0-9]{4,6}" maxlength="6"')

Reference action/form behavior (prototype intent; never backend security proof):

- **change-pin**: D=>{if(D.pin!==D.confirm)fail('The new PINs do not match.');if(D.pin===D.current)fail('Use a different new PIN.');go('security');toast('Demo PIN change flow complete');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:591)

### Current native API and database trace

- POST /api/v1/ops/change-pin — expression "/ops/change-pin" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:457)

Potential reads: ap_session, ap_user.

Potential writes: ap_audit, ap_rate_limit, ap_session, ap_user.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.text(p,"currentPin",6)
- throw new IllegalArgumentException("Use a six-digit PIN.")
- throw new ApiError(org.springframework.http.HttpStatus.BAD_REQUEST,"Current PIN is incorrect.")

Backend source pointers: [pin](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:72), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/RateGate.java:14), [changePin](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:137), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-115"></a>

## 115. Privacy & access — privacy

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: privacy. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:391). Native entry: [privacy](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2063).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Your flat only' | navigate / row | 'permissions' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:391) |
| 'Manual verification' | navigate / row | 'help' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:391) |
| 'Shared totals' | navigate / row | 'reports' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:391) |
| 'Private requests' | navigate / row | 'complaints' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:391) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-116"></a>

## 116. Automation — automation

Group: Settings. Reference roles: Admin, Treasurer. Native destination: automation. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392). Native entry: [automation](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1986).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Monthly dues' | navigate / row | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392) |
| 'Unpaid reminders' | navigate / row | 'reminder-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392) |
| 'Recurring expenses' | navigate / row | 'recurring' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392) |
| 'Service reminders' | navigate / row | 'services' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392) |
| 'Run automation' | action | runAutomation | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392) |
| 'Automation log' | navigate / custom | 'automation-log' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392) |

Reference action/form behavior (prototype intent; never backend security proof):

- **runAutomation**: ()=>{requireStaff();requireOnline();let before=S.audit.length;createMissingBills(currentMonth);if(S.settings.reminders&&S.settings.reminderDays.split(',').map(v=>Number(v.trim())).includes(Number(today.slice(-2)))){S.bills.filter(b=>b.month===currentMonth&&due(b)>0&&!billPending(b)).forEach(b=>{const key='reminder-'+b.id+'-'+today;if(!S.notifications.some(n=>n.key===key)){notify('Maintenance reminder',INR(due(b))+' pending · '+b.flat,'Resident','bill?id='+b.id,b.flat);S.notifications[0].key=key;}});}S.recurring.filter(r=>r.active&&r.next<=today).forEach(prepareRecurring);S.services.filter(s=>s.next<=nextCycle(today,'Monthly')&&S.settings.serv — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)

### Current native API and database trace

- POST /api/v1/ops/automation/run — expression "/ops/automation/run" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1986)

Potential reads: ap_bill, ap_billing_rule, ap_booking, ap_flat, ap_notice, ap_payment, ap_payment_reversal, ap_rate_override, ap_recurring, ap_service, ap_settings, ap_tenant, ap_user.

Potential writes: ap_audit, ap_bill, ap_booking, ap_booking_event, ap_expense, ap_inbox, ap_notice, ap_notice_recipient, ap_recurring, ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required
- ApiError.conflict("Configure maintenance for this month first."))
- throw ApiError.conflict("This notice is scheduled for later. Edit the draft to publish now.")
- throw new IllegalArgumentException("No active recipients in this audience.")

Backend source pointers: [run](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:263), [run](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AutomationService.java:21), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [generate](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:40), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [publish](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:117), [publishInternal](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:89), [recipientAccounts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:86), [recipient](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:75).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-117"></a>

## 117. Automation activity — automation-log

Group: Settings. Reference roles: Admin, Treasurer. Native destination: audit. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:393). Native entry: [audit](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1526).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| a.action | navigate / row | 'audit-detail?id='+a.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:393) |

### Current native API and database trace

- GET /api/v1/audit — expression "/audit" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1526)

Potential reads: ap_audit, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required

Backend source pointers: [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:184), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-118"></a>

## 118. Activity log — audit

Group: Settings. Reference roles: Admin, Treasurer. Native destination: audit. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:394). Native entry: [audit](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1526).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| a.action | navigate / row | 'audit-detail?id='+a.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:394) |

### Current native API and database trace

- GET /api/v1/audit — expression "/audit" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1526)

Potential reads: ap_audit, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required

Backend source pointers: [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:184), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-119"></a>

## 119. Activity detail — audit-detail

Group: Settings. Reference roles: Admin, Treasurer. Native destination: audit-detail. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:395). Native entry: [auditDetail](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2093).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

- GET /api/v1/audit/{value} — expression "/audit/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2093)

Potential reads: ap_audit, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required

Backend source pointers: [auditDetail](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:188).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-120"></a>

## 120. Subscription & billing — subscription

Group: Settings. Reference roles: Admin. Native destination: subscription. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:855). Native entry: [subscription](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Refer 2 apartments' | navigate / row | 'referrals' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:855) |
| 'Invoices & receipts' | navigate / row | 'subscription-invoices' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:855) |
| 'Plan charges' | navigate / row | 'subscription-plans' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:855) |
| 'Renewal status' | navigate / row | 'subscription-renewal' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:855) |

### Current native API and database trace

- GET /api/v1/ops/subscription — expression "/ops/subscription" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)
- POST /api/v1/ops/subscription/install — expression "/ops/subscription/install" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)

Potential reads: ap_flat, ap_referral, ap_subscription, ap_subscription_invoice, ap_tenant.

Potential writes: ap_subscription, ap_subscription_invoice.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [subscription](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:251), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:42), [ensure](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:27), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [price](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:24), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [install](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:254), [installed](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:59), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-121"></a>

## 121. Help & about — help

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: help. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:397). Native entry: [help](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2073).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Payment status' | navigate / row | 'payment-help' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:397) |
| 'Support' | navigate / row | 'support' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:397) |
| 'Privacy & access' | navigate / row | 'privacy' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:397) |
| 'About ApartmentCare' | navigate / row | 'about' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:397) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-122"></a>

## 122. Payment status guide — payment-help

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: payment-help. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:398). Native entry: [paymentHelp](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2102).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'In review' | navigate / row | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:398) |
| 'Paid' | navigate / row | 'history' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:398) |
| 'Rejected' | navigate / row | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:398) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-123"></a>

## 123. Get help — support

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: support. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:399). Native entry: [support](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2110).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Submit request' | submit | support | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:399) |
| 'Apartment contacts' | navigate / custom | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:399) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('topic','Topic',['Payment approval','Account access','Flat mapping','Something else'])
- field('message','Message','','textarea','required maxlength="1000"')

Reference action/form behavior (prototype intent; never backend security proof):

- **support**: D=>{S.support=S.support\|\|[];S.support.push({id:uid('SUP'),topic:D.topic,message:safeText(D.message,1000),date:today});save();sheet('Request saved','${stateHero('checkCircle','We have your message.','Demo request saved locally.')}<div class="mt">${btn('Done','help','check','full')}</div>');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:592)

### Current native API and database trace

- POST /api/v1/ops/support — expression "/ops/support" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2110)
- GET /api/v1/ops/support — expression "/ops/support" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2110)

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-124"></a>

## 124. About — about

Group: Settings. Reference roles: Admin, Treasurer, Resident. Native destination: status. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:400). Native entry: [status](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2180).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Screen library' | navigate / custom | 'screen-map' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:400) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-125"></a>

## 125. You’re offline — offline

Group: System states. Reference roles: Admin, Treasurer, Resident. Native destination: offline. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:401). Native entry: [systemState](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:463).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Retry connection' | action | goOnline | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:401) |
| 'Back to home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:401) |

Reference action/form behavior (prototype intent; never backend security proof):

- **goOnline**: ()=>{S.offline=false;persist();go('home');toast('Viewing demo data');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:497)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-126"></a>

## 126. Session expired — session-expired

Group: System states. Reference roles: Admin, Treasurer, Resident. Native destination: session-expired. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:402). Native entry: [systemState](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:463).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Sign in' | navigate / custom | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:402) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-127"></a>

## 127. Access restricted — access-denied

Group: System states. Reference roles: Admin, Treasurer, Resident. Native destination: access-denied. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:403). Native entry: [systemState](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:463).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back to home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:403) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-128"></a>

## 128. Nothing here yet — empty-state

Group: System states. Reference roles: Admin, Treasurer, Resident. Native destination: empty-state. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:404). Native entry: [systemState](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:463).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back to home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:404) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-129"></a>

## 129. Something went wrong — error-state

Group: System states. Reference roles: Admin, Treasurer, Resident. Native destination: error-state. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:405). Native entry: [systemState](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:463).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Try again' | action | retryPage | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:405) |

Reference action/form behavior (prototype intent; never backend security proof):

- **retryPage**: ()=>{go('home');toast('Demo view reloaded');} — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:497)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-130"></a>

## 130. Page not found — not-found

Group: System states. Reference roles: Admin, Treasurer, Resident. Native destination: not-found. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:406). Native entry: [systemState](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:463).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Open screen library' | navigate / custom | 'screen-map' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:406) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-131"></a>

## 131. Screen library — screen-map

Group: Reference tools. Reference roles: Admin, Treasurer, Resident. Native destination: unresolved. Recorded status: REFERENCE_ONLY_NOT_A_PRODUCTION_FEATURE.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:407). Native entry trace: unresolved / no dedicated implementation found.

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-132"></a>

## 132. Preview controls — demo-controls

Group: Reference tools. Reference roles: Admin, Treasurer, Resident. Native destination: unresolved. Recorded status: REFERENCE_ONLY_NOT_A_PRODUCTION_FEATURE.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408). Native entry trace: unresolved / no dedicated implementation found.

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| r | action | setRole | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |
| 'Offline mode' | navigate / row | 'offline' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |
| 'Session expired' | navigate / row | 'session-expired' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |
| 'Access denied' | navigate / row | 'access-denied' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |
| 'Empty state' | navigate / row | 'empty-state' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |
| 'Error & retry' | navigate / row | 'error-state' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |
| 'Reset demo data' | action | resetDemo | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |

Reference action/form behavior (prototype intent; never backend security proof):

- **setRole**: d=>setRole(d.role) — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:492)
- **resetDemo**: ()=>confirm('Reset demo data?','This restores all sample records and clears your local test changes.','Reset','confirmReset','',true) — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:499)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-133"></a>

## 133. Request attachment — complaint-photo

Group: Services. Reference roles: Admin, Treasurer, Resident. Native destination: attachments. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:410). Native entry: [attachments](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1517).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |

### Current native API and database trace

- GET /api/v1/ops/files — expression "/ops/files?kind="+kind+"&parentId="+parent — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1517)
- GET /api/v1/ops/files/{value} — expression "/ops/files/"+x.optString("id") — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1517)

Potential reads: ap_bill, ap_document, ap_expense, ap_file, ap_flat, ap_payment, ap_payment_reversal, ap_settings.

Potential writes: ap_settings.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()
- throw ApiError.conflict("Attach files before publishing.")
- throw new IllegalArgumentException("Unsupported attachment type.")
- Server Admin/Treasurer role required

Backend source pointers: [files](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:208), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:78), [access](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:48), [document](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:26), [bill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:127), [ownedBill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:135), [balance](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:21), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [file](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:215), [content](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/FileService.java:118).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-134"></a>

## 134. Loading — loading-state

Group: System states. Reference roles: Admin, Treasurer, Resident. Native destination: loading-state. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:411). Native entry: [loadingState](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:467).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Continue to loaded view' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:411) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-135"></a>

## 135. Events & bookings — events

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: events. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717). Native entry: [bookings](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1097).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'New event' | navigate | 'event-types' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| 'Calendar' | navigate | 'event-calendar' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| 'Parking' | navigate | 'parking-availability' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| 'Rules' | navigate | 'booking-rules' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| ['Upcoming','Mine','Past','All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| 'New event' | navigate / custom | 'event-types' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| 'Requests' | navigate / custom | 'booking-requests' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| 'Manage spaces' | navigate / custom | 'resources' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |

### Current native API and database trace

- GET /api/v1/ops/bookings — expression "/ops/bookings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1097)

Potential reads: ap_booking, ap_flat.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [bookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:239), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:144), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-136"></a>

## 136. What are you planning? — event-types

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: event-form. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:718). Native entry: [bookingForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Other event' | navigate / custom | 'event-form?type=Other' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:718) |

### Current native API and database trace

- GET /api/v1/ops/resources — expression "/ops/resources" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163)
- GET /api/v1/ops/settings — expression "/ops/settings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163)
- GET /api/v1/ops/bookings/{value} — expression "/ops/bookings/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163)

Potential reads: ap_resource, ap_resource_block, ap_resource_conflict, ap_settings.

Potential writes: ap_settings.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:218), [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:17), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:94), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:18), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [booking](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:242).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-137"></a>

## 137. Request an event — event-form

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: event-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719). Native entry: [bookingForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Check availability' | submit | event-draft | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719) |
| 'Booking rules' | navigate / custom | 'booking-rules' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719) |

Reference input/form definitions (attributes and allowed choices shown literally):

- checkField('rules','I agree to the booking rules.')

### Current native API and database trace

- GET /api/v1/ops/resources — expression "/ops/resources" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163)
- GET /api/v1/ops/settings — expression "/ops/settings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163)
- GET /api/v1/ops/bookings/{value} — expression "/ops/bookings/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163)

Potential reads: ap_resource, ap_resource_block, ap_resource_conflict, ap_settings.

Potential writes: ap_settings.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:218), [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:17), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:94), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:18), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [booking](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:242).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-138"></a>

## 138. Choose your parking — event-availability

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: availability. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720). Native entry: [availability](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1226).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'New event' | navigate / custom | 'event-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720) |
| 'Review request' | submit | event-allocation | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720) |
| 'Change date or space' | navigate / custom | 'event-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('cars','Car spaces',e.cars,'number','required min="0" max="'+S.bookingRules.maxCars+'"')
- field('bikes','Bike spaces',e.bikes,'number','required min="0" max="'+S.bookingRules.maxBikes+'"')

### Current native API and database trace

- GET /api/v1/ops/availability — expression "/ops/availability?start="+enc(start)+"&end="+enc(end) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1226)

Potential reads: ap_booking, ap_booking_item, ap_resource, ap_resource_block, ap_resource_conflict.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [availability](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:236), [availability](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:115), [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:17), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [free](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:101), [hasBookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:98), [usage](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:95).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-139"></a>

## 139. Review request — event-review

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: event-form. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:721). Native entry: [bookingForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Edit' | navigate / custom | 'event-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:721) |
| 'Submit' | action | submitEvent | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:721) |
| 'New event' | navigate / custom | 'event-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:721) |

### Current native API and database trace

- GET /api/v1/ops/resources — expression "/ops/resources" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163)
- GET /api/v1/ops/settings — expression "/ops/settings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163)
- GET /api/v1/ops/bookings/{value} — expression "/ops/bookings/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163)

Potential reads: ap_resource, ap_resource_block, ap_resource_conflict, ap_settings.

Potential writes: ap_settings.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:218), [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:17), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:94), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:18), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [booking](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:242).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-140"></a>

## 140. Request sent — event-submitted

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: booking. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:722). Native entry: [booking](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Track request' | navigate / custom | 'event?id='+(Q.id\|\|draft.lastEvent\|\|'EV-1') | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:722) |

### Current native API and database trace

- GET /api/v1/ops/bookings/{value} — expression "/ops/bookings/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114)
- POST /api/v1/ops/bookings/{value}/decision — expression "/ops/bookings/"+id+"/decision" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1160)

Potential reads: ap_booking, ap_booking_item, ap_command, ap_resource, ap_resource_block, ap_resource_conflict, ap_tenant.

Potential writes: ap_audit, ap_booking, ap_booking_event, ap_booking_item, ap_command, ap_inbox.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"action","APPROVE","REJECT","CANCEL","COMPLETE","OFFER","ACCEPT"))
- V.opt(p,"note",500))
- throw ApiError.conflict("Booking changed. Refresh before reviewing.")
- throw ApiError.conflict("Only pending requests can be approved.")
- throw ApiError.conflict("Request is not awaiting a decision.")
- throw new IllegalArgumentException("Explain the proposed alternative.")
- throw ApiError.forbidden()
- throw ApiError.conflict("No alternative is waiting.")
- throw ApiError.conflict("This request cannot be rejected now.")
- throw new IllegalArgumentException("Enter a rejection reason.")
- throw ApiError.conflict("This request has already ended.")
- throw new IllegalArgumentException("Enter a cancellation reason.")
- throw ApiError.conflict("Complete only after the approved booking end time.")
- Server Admin role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(item,"resourceId"))
- throw new IllegalArgumentException("Select each space once.")
- throw new IllegalArgumentException("Requested quantity exceeds configured capacity.")
- throw ApiError.conflict("A selected space is unavailable.")
- throw ApiError.conflict(r.get("name")+" is unavailable. Choose another slot.")
- throw ApiError.conflict("The event area occupies one of the selected parking spaces. Choose non-conflicting spaces.")
- V.id(i,"resourceId")
- V.id(body,"requestKey")

Backend source pointers: [booking](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:242), [decision](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:248), [decide](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:181), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [verify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:124), [free](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:101), [hasBookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:98), [usage](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:95), [asInput](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:235), [items](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:138).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-141"></a>

## 141. Event details — event

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: event. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723). Native entry: [booking](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| title | navigate / row | to | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:661) |
| 'Edit request' | navigate / custom | 'event-form?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723) |
| 'Cancel' | navigate / custom | 'event-cancel?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723) |
| 'Review request' | navigate / custom | 'booking-review?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723) |
| 'Complete event' | navigate / custom | 'event-complete?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723) |
| 'Calendar' | navigate / custom | 'event-calendar' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723) |

### Current native API and database trace

- GET /api/v1/ops/bookings/{value} — expression "/ops/bookings/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114)
- POST /api/v1/ops/bookings/{value}/decision — expression "/ops/bookings/"+id+"/decision" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1160)

Potential reads: ap_booking, ap_booking_item, ap_command, ap_resource, ap_resource_block, ap_resource_conflict, ap_tenant.

Potential writes: ap_audit, ap_booking, ap_booking_event, ap_booking_item, ap_command, ap_inbox.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"action","APPROVE","REJECT","CANCEL","COMPLETE","OFFER","ACCEPT"))
- V.opt(p,"note",500))
- throw ApiError.conflict("Booking changed. Refresh before reviewing.")
- throw ApiError.conflict("Only pending requests can be approved.")
- throw ApiError.conflict("Request is not awaiting a decision.")
- throw new IllegalArgumentException("Explain the proposed alternative.")
- throw ApiError.forbidden()
- throw ApiError.conflict("No alternative is waiting.")
- throw ApiError.conflict("This request cannot be rejected now.")
- throw new IllegalArgumentException("Enter a rejection reason.")
- throw ApiError.conflict("This request has already ended.")
- throw new IllegalArgumentException("Enter a cancellation reason.")
- throw ApiError.conflict("Complete only after the approved booking end time.")
- Server Admin role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(item,"resourceId"))
- throw new IllegalArgumentException("Select each space once.")
- throw new IllegalArgumentException("Requested quantity exceeds configured capacity.")
- throw ApiError.conflict("A selected space is unavailable.")
- throw ApiError.conflict(r.get("name")+" is unavailable. Choose another slot.")
- throw ApiError.conflict("The event area occupies one of the selected parking spaces. Choose non-conflicting spaces.")
- V.id(i,"resourceId")
- V.id(body,"requestKey")

Backend source pointers: [booking](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:242), [decision](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:248), [decide](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:181), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [verify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:124), [free](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:101), [hasBookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:98), [usage](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:95), [asInput](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:235), [items](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:138).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-142"></a>

## 142. Booking approvals — booking-requests

Group: Community. Reference roles: Admin. Native destination: bookings. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:724). Native entry: [bookings](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1097).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| ['Pending','Approved','All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:724) |
| eventFull(e)?e.title:e.type+' booking' | navigate / row | 'event?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:713) |

### Current native API and database trace

- GET /api/v1/ops/bookings — expression "/ops/bookings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1097)

Potential reads: ap_booking, ap_flat.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [bookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:239), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:144), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-143"></a>

## 143. Review booking — booking-review

Group: Community. Reference roles: Admin. Native destination: booking. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725). Native entry: [booking](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Offer alternative' | navigate / custom | 'booking-alternative?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725) |
| 'Reject' | navigate / custom | 'booking-reject?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725) |
| 'Approve booking' | submit | approve-booking | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725) |

Reference input/form definitions (attributes and allowed choices shown literally):

- checkField('verified','I checked the space, parking and apartment rules.')
- checkField('announce','Post a common-area notice',false)

### Current native API and database trace

- GET /api/v1/ops/bookings/{value} — expression "/ops/bookings/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114)
- POST /api/v1/ops/bookings/{value}/decision — expression "/ops/bookings/"+id+"/decision" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1160)

Potential reads: ap_booking, ap_booking_item, ap_command, ap_resource, ap_resource_block, ap_resource_conflict, ap_tenant.

Potential writes: ap_audit, ap_booking, ap_booking_event, ap_booking_item, ap_command, ap_inbox.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"action","APPROVE","REJECT","CANCEL","COMPLETE","OFFER","ACCEPT"))
- V.opt(p,"note",500))
- throw ApiError.conflict("Booking changed. Refresh before reviewing.")
- throw ApiError.conflict("Only pending requests can be approved.")
- throw ApiError.conflict("Request is not awaiting a decision.")
- throw new IllegalArgumentException("Explain the proposed alternative.")
- throw ApiError.forbidden()
- throw ApiError.conflict("No alternative is waiting.")
- throw ApiError.conflict("This request cannot be rejected now.")
- throw new IllegalArgumentException("Enter a rejection reason.")
- throw ApiError.conflict("This request has already ended.")
- throw new IllegalArgumentException("Enter a cancellation reason.")
- throw ApiError.conflict("Complete only after the approved booking end time.")
- Server Admin role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(item,"resourceId"))
- throw new IllegalArgumentException("Select each space once.")
- throw new IllegalArgumentException("Requested quantity exceeds configured capacity.")
- throw ApiError.conflict("A selected space is unavailable.")
- throw ApiError.conflict(r.get("name")+" is unavailable. Choose another slot.")
- throw ApiError.conflict("The event area occupies one of the selected parking spaces. Choose non-conflicting spaces.")
- V.id(i,"resourceId")
- V.id(body,"requestKey")

Backend source pointers: [booking](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:242), [decision](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:248), [decide](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:181), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [verify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:124), [free](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:101), [hasBookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:98), [usage](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:95), [asInput](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:235), [items](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:138).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-144"></a>

## 144. Offer an alternative — booking-alternative

Group: Community. Reference roles: Admin. Native destination: booking-alternative. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:726). Native entry: [bookingForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Send offer' | submit | offer-booking | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:726) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('reason','Reason','','textarea','required maxlength="300"')

### Current native API and database trace

- GET /api/v1/ops/resources — expression "/ops/resources" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163)
- GET /api/v1/ops/settings — expression "/ops/settings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163)
- GET /api/v1/ops/bookings/{value} — expression "/ops/bookings/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1163)

Potential reads: ap_resource, ap_resource_block, ap_resource_conflict, ap_settings.

Potential writes: ap_settings.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:218), [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:17), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:94), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:18), [settings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:49), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [booking](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:242).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-145"></a>

## 145. Alternative offered — event-alternative

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: booking. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:727). Native entry: [booking](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Change request' | navigate / custom | 'event-form?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:727) |
| 'Accept' | action | acceptEventOffer | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:727) |

### Current native API and database trace

- GET /api/v1/ops/bookings/{value} — expression "/ops/bookings/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114)
- POST /api/v1/ops/bookings/{value}/decision — expression "/ops/bookings/"+id+"/decision" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1160)

Potential reads: ap_booking, ap_booking_item, ap_command, ap_resource, ap_resource_block, ap_resource_conflict, ap_tenant.

Potential writes: ap_audit, ap_booking, ap_booking_event, ap_booking_item, ap_command, ap_inbox.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"action","APPROVE","REJECT","CANCEL","COMPLETE","OFFER","ACCEPT"))
- V.opt(p,"note",500))
- throw ApiError.conflict("Booking changed. Refresh before reviewing.")
- throw ApiError.conflict("Only pending requests can be approved.")
- throw ApiError.conflict("Request is not awaiting a decision.")
- throw new IllegalArgumentException("Explain the proposed alternative.")
- throw ApiError.forbidden()
- throw ApiError.conflict("No alternative is waiting.")
- throw ApiError.conflict("This request cannot be rejected now.")
- throw new IllegalArgumentException("Enter a rejection reason.")
- throw ApiError.conflict("This request has already ended.")
- throw new IllegalArgumentException("Enter a cancellation reason.")
- throw ApiError.conflict("Complete only after the approved booking end time.")
- Server Admin role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(item,"resourceId"))
- throw new IllegalArgumentException("Select each space once.")
- throw new IllegalArgumentException("Requested quantity exceeds configured capacity.")
- throw ApiError.conflict("A selected space is unavailable.")
- throw ApiError.conflict(r.get("name")+" is unavailable. Choose another slot.")
- throw ApiError.conflict("The event area occupies one of the selected parking spaces. Choose non-conflicting spaces.")
- V.id(i,"resourceId")
- V.id(body,"requestKey")

Backend source pointers: [booking](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:242), [decision](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:248), [decide](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:181), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [verify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:124), [free](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:101), [hasBookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:98), [usage](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:95), [asInput](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:235), [items](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:138).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-146"></a>

## 146. Reject request — booking-reject

Group: Community. Reference roles: Admin. Native destination: booking. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:728). Native entry: [booking](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Reject request' | submit | reject-booking | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:728) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('reason','Reason','','textarea','required maxlength="300"')

### Current native API and database trace

- GET /api/v1/ops/bookings/{value} — expression "/ops/bookings/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114)
- POST /api/v1/ops/bookings/{value}/decision — expression "/ops/bookings/"+id+"/decision" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1160)

Potential reads: ap_booking, ap_booking_item, ap_command, ap_resource, ap_resource_block, ap_resource_conflict, ap_tenant.

Potential writes: ap_audit, ap_booking, ap_booking_event, ap_booking_item, ap_command, ap_inbox.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"action","APPROVE","REJECT","CANCEL","COMPLETE","OFFER","ACCEPT"))
- V.opt(p,"note",500))
- throw ApiError.conflict("Booking changed. Refresh before reviewing.")
- throw ApiError.conflict("Only pending requests can be approved.")
- throw ApiError.conflict("Request is not awaiting a decision.")
- throw new IllegalArgumentException("Explain the proposed alternative.")
- throw ApiError.forbidden()
- throw ApiError.conflict("No alternative is waiting.")
- throw ApiError.conflict("This request cannot be rejected now.")
- throw new IllegalArgumentException("Enter a rejection reason.")
- throw ApiError.conflict("This request has already ended.")
- throw new IllegalArgumentException("Enter a cancellation reason.")
- throw ApiError.conflict("Complete only after the approved booking end time.")
- Server Admin role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(item,"resourceId"))
- throw new IllegalArgumentException("Select each space once.")
- throw new IllegalArgumentException("Requested quantity exceeds configured capacity.")
- throw ApiError.conflict("A selected space is unavailable.")
- throw ApiError.conflict(r.get("name")+" is unavailable. Choose another slot.")
- throw ApiError.conflict("The event area occupies one of the selected parking spaces. Choose non-conflicting spaces.")
- V.id(i,"resourceId")
- V.id(body,"requestKey")

Backend source pointers: [booking](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:242), [decision](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:248), [decide](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:181), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [verify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:124), [free](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:101), [hasBookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:98), [usage](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:95), [asInput](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:235), [items](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:138).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-147"></a>

## 147. Cancel booking — event-cancel

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: booking. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:729). Native entry: [booking](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Cancel booking' | submit | cancel-booking | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:729) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('reason','Reason','','textarea','required maxlength="300"')
- checkField('confirmed','Cancel this booking and release the spaces.')

### Current native API and database trace

- GET /api/v1/ops/bookings/{value} — expression "/ops/bookings/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114)
- POST /api/v1/ops/bookings/{value}/decision — expression "/ops/bookings/"+id+"/decision" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1160)

Potential reads: ap_booking, ap_booking_item, ap_command, ap_resource, ap_resource_block, ap_resource_conflict, ap_tenant.

Potential writes: ap_audit, ap_booking, ap_booking_event, ap_booking_item, ap_command, ap_inbox.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"action","APPROVE","REJECT","CANCEL","COMPLETE","OFFER","ACCEPT"))
- V.opt(p,"note",500))
- throw ApiError.conflict("Booking changed. Refresh before reviewing.")
- throw ApiError.conflict("Only pending requests can be approved.")
- throw ApiError.conflict("Request is not awaiting a decision.")
- throw new IllegalArgumentException("Explain the proposed alternative.")
- throw ApiError.forbidden()
- throw ApiError.conflict("No alternative is waiting.")
- throw ApiError.conflict("This request cannot be rejected now.")
- throw new IllegalArgumentException("Enter a rejection reason.")
- throw ApiError.conflict("This request has already ended.")
- throw new IllegalArgumentException("Enter a cancellation reason.")
- throw ApiError.conflict("Complete only after the approved booking end time.")
- Server Admin role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(item,"resourceId"))
- throw new IllegalArgumentException("Select each space once.")
- throw new IllegalArgumentException("Requested quantity exceeds configured capacity.")
- throw ApiError.conflict("A selected space is unavailable.")
- throw ApiError.conflict(r.get("name")+" is unavailable. Choose another slot.")
- throw ApiError.conflict("The event area occupies one of the selected parking spaces. Choose non-conflicting spaces.")
- V.id(i,"resourceId")
- V.id(body,"requestKey")

Backend source pointers: [booking](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:242), [decision](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:248), [decide](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:181), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [verify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:124), [free](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:101), [hasBookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:98), [usage](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:95), [asInput](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:235), [items](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:138).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-148"></a>

## 148. Complete event — event-complete

Group: Community. Reference roles: Admin. Native destination: booking. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:730). Native entry: [booking](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Complete' | submit | complete-booking | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:730) |

Reference input/form definitions (attributes and allowed choices shown literally):

- checkField('checked','The event has ended and spaces are clear.')
- field('note','Completion note','','textarea','maxlength="300"')

### Current native API and database trace

- GET /api/v1/ops/bookings/{value} — expression "/ops/bookings/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1114)
- POST /api/v1/ops/bookings/{value}/decision — expression "/ops/bookings/"+id+"/decision" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1160)

Potential reads: ap_booking, ap_booking_item, ap_command, ap_resource, ap_resource_block, ap_resource_conflict, ap_tenant.

Potential writes: ap_audit, ap_booking, ap_booking_event, ap_booking_item, ap_command, ap_inbox.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"action","APPROVE","REJECT","CANCEL","COMPLETE","OFFER","ACCEPT"))
- V.opt(p,"note",500))
- throw ApiError.conflict("Booking changed. Refresh before reviewing.")
- throw ApiError.conflict("Only pending requests can be approved.")
- throw ApiError.conflict("Request is not awaiting a decision.")
- throw new IllegalArgumentException("Explain the proposed alternative.")
- throw ApiError.forbidden()
- throw ApiError.conflict("No alternative is waiting.")
- throw ApiError.conflict("This request cannot be rejected now.")
- throw new IllegalArgumentException("Enter a rejection reason.")
- throw ApiError.conflict("This request has already ended.")
- throw new IllegalArgumentException("Enter a cancellation reason.")
- throw ApiError.conflict("Complete only after the approved booking end time.")
- Server Admin role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(item,"resourceId"))
- throw new IllegalArgumentException("Select each space once.")
- throw new IllegalArgumentException("Requested quantity exceeds configured capacity.")
- throw ApiError.conflict("A selected space is unavailable.")
- throw ApiError.conflict(r.get("name")+" is unavailable. Choose another slot.")
- throw ApiError.conflict("The event area occupies one of the selected parking spaces. Choose non-conflicting spaces.")
- V.id(i,"resourceId")
- V.id(body,"requestKey")

Backend source pointers: [booking](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:242), [decision](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:248), [decide](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:181), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [verify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:124), [free](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:101), [hasBookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:98), [usage](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:95), [asInput](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:235), [items](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:138).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-149"></a>

## 149. Booking calendar — event-calendar

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: event-calendar. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:731). Native entry: [eventCalendar](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2280).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Show date' | submit | calendar-date | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:731) |
| 'Request a space' | navigate / custom | 'event-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:731) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('date','View date',d,'date','required')

### Current native API and database trace

- GET /api/v1/ops/bookings — expression "/ops/bookings" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2280)

Potential reads: ap_booking, ap_flat.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [bookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:239), [list](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:144), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-150"></a>

## 150. Guest parking — parking-availability

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: parking-availability. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732). Native entry: [availability](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1226).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Check spaces' | submit | parking-search | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732) |
| 'Request parking' | navigate / custom | 'event-form?type=Guest%20parking' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732) |
| 'Share my parking' | navigate / custom | 'parking-share' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('start','From',c.start,'datetime-local','required')
- field('end','Until',c.end,'datetime-local','required')

### Current native API and database trace

- GET /api/v1/ops/availability — expression "/ops/availability?start="+enc(start)+"&end="+enc(end) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1226)

Potential reads: ap_booking, ap_booking_item, ap_resource, ap_resource_block, ap_resource_conflict.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [availability](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:236), [availability](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:115), [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:17), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [free](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:101), [hasBookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:98), [usage](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:95).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-151"></a>

## 151. Spaces & parking — resources

Group: Community. Reference roles: Admin. Native destination: resources. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733). Native entry: [resources](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1250).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Add' | navigate / section | 'resource-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733) |
| r.name | navigate / row | 'resource?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733) |
| 'Block dates' | navigate / custom | 'resource-blocks' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733) |
| 'Rules' | navigate / custom | 'booking-rules' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733) |

### Current native API and database trace

- GET /api/v1/ops/resources — expression "/ops/resources" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1250)

Potential reads: ap_resource, ap_resource_block, ap_resource_conflict.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:218), [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:17), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-152"></a>

## 152. Space details — resource

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: resource. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734). Native entry: [resource](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1263).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Edit resource' | navigate / custom | 'resource-form?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734) |

### Current native API and database trace

- GET /api/v1/ops/resources — expression "/ops/resources" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1263)
- DELETE /api/v1/ops/resource-blocks/{value} — expression "/ops/resource-blocks/"+v.optString("id") — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1263)

Potential reads: ap_resource, ap_resource_block, ap_resource_conflict, ap_tenant.

Potential writes: ap_audit, ap_resource_block.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.missing()
- Server Admin role required

Backend source pointers: [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:218), [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:17), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [unblock](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:233), [removeBlock](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:88), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-153"></a>

## 153. Configure space — resource-form

Group: Community. Reference roles: Admin. Native destination: resource-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735). Native entry: [resourceForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1285).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save space' | submit | resource | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('name','Name',r.name\|\|'','text','required maxlength="60"')
- selectField('type','Type',['Common space','Guest car','Guest bike','Private car'],r.type\|\|'Common space')
- field('capacity','Maximum guests / capacity',r.capacity\|\|20,'number','required min="1" max="200"')
- selectField('owner','Private bay holder',[['','Not applicable'],...S.flats.filter(f=>f.member).map(f=>[f.id,f.id+' · '+f.name])],r.owner\|\|'')
- field('blocks','Also occupies these bay IDs',(r.blocks\|\|[]).join(', '),'text','placeholder="CAR-1, CAR-2"')
- checkField('enabled','Available for booking',r.enabled??true)

### Current native API and database trace

- GET /api/v1/ops/resources — expression "/ops/resources" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1285)
- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1285)
- POST /api/v1/ops/resources{value} — expression "/ops/resources"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1285)

Potential reads: ap_flat, ap_resource, ap_resource_block, ap_resource_conflict, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:218), [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:17), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-154"></a>

## 154. Unavailable dates — resource-blocks

Group: Community. Reference roles: Admin. Native destination: resource. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:736). Native entry: [resource](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1263).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Add' | navigate / section | 'resource-block-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:736) |
| resource(b.resource)?.name | navigate / row | 'resource-block-form?id='+b.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:736) |

### Current native API and database trace

- GET /api/v1/ops/resources — expression "/ops/resources" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1263)
- DELETE /api/v1/ops/resource-blocks/{value} — expression "/ops/resource-blocks/"+v.optString("id") — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1263)

Potential reads: ap_resource, ap_resource_block, ap_resource_conflict, ap_tenant.

Potential writes: ap_audit, ap_resource_block.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.missing()
- Server Admin role required

Backend source pointers: [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:218), [resources](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:17), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [unblock](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:233), [removeBlock](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:88), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-155"></a>

## 155. Block a space — resource-block-form

Group: Community. Reference roles: Admin. Native destination: resource-block-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:737). Native entry: [resourceBlock](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1331).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save blocked time' | submit | resource-block | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:737) |
| 'Remove block' | action | removeResourceBlock | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:737) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('resource','Space',S.resources.filter(r=>r.type!=='Protected').map(r=>[r.id,r.name]),b.resource\|\|'SPACE-1')
- field('start','From',b.start\|\|'2026-09-24T08:00','datetime-local','required')
- field('end','Until',b.end\|\|'2026-09-24T17:00','datetime-local','required')
- field('reason','Reason',b.reason\|\|'','text','required maxlength="100"')

### Current native API and database trace

- POST /api/v1/ops/resources/{value}/blocks — expression "/ops/resources/"+id+"/blocks" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1331)

Potential reads: ap_booking, ap_booking_item, ap_command, ap_resource, ap_resource_conflict, ap_tenant.

Potential writes: ap_audit, ap_command, ap_resource_block.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.text(p,"reason",300)))
- throw ApiError.conflict("A linked physical space has an approved reservation.")
- throw ApiError.conflict("Approved reservations overlap. Resolve those bookings first.")
- Server Admin role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [block](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:230), [block](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:72), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [hasBookings](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:98), [usage](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:95), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-156"></a>

## 156. Share my parking — parking-share

Group: Community. Reference roles: Resident. Native destination: parking-share. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:738). Native entry: [parkingShare](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1339).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| title | navigate / row | to | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:661) |
| 'Save availability' | submit | share-parking | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:738) |
| 'Stop sharing' | action | unshareParking | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:738) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('start','Share from',r.shareStart\|\|'2026-09-20T16:00','datetime-local','required')
- field('end','Share until',r.shareEnd\|\|'2026-09-20T22:00','datetime-local','required')
- checkField('consent','I authorise guest use during these hours.',r.consent)

### Current native API and database trace

- POST /api/v1/ops/resources/{value}/release — expression "/ops/resources/"+id+"/release" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1339)

Potential reads: ap_booking, ap_booking_item, ap_resource, ap_tenant.

Potential writes: ap_audit, ap_resource.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.bool(p,"consent",false))
- throw ApiError.forbidden()
- throw ApiError.conflict("Contact the Admin to cancel approved reservations before withdrawing this release.")
- throw ApiError.conflict("The release must cover existing approved reservations.")

Backend source pointers: [release](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:227), [release](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/BookingService.java:59), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-157"></a>

## 157. Booking rules — booking-rules

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: settings. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:739). Native entry: [settings](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1538).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Edit rules' | navigate / custom | 'booking-rules-edit' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:739) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-158"></a>

## 158. Booking settings — booking-rules-edit

Group: Community. Reference roles: Admin. Native destination: settings. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740). Native entry: [settings](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1538).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save rules' | submit | booking-rules | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('maxGuests','Maximum guests',r.maxGuests,'number','required min="1" max="200"')
- field('maxHours','Maximum hours',r.maxHours,'number','required min="1" max="24"')
- field('advanceDays','Book ahead · days',r.advanceDays,'number','required min="1" max="365"')
- field('setupMinutes','Setup · minutes',r.setupMinutes,'number','required min="0" max="180"')
- field('cleanupMinutes','Cleanup · minutes',r.cleanupMinutes,'number','required min="0" max="180"')
- field('quietStart','Quiet hours from',r.quietStart,'time','required')
- field('quietEnd','Until',r.quietEnd,'time','required')
- field('maxCars','Max cars',r.maxCars,'number','min="0" max="30" required')
- field('maxBikes','Max bikes',r.maxBikes,'number','min="0" max="50" required')

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-159"></a>

## 159. Members — members

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: directory. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:768). Native entry: [members](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1327).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

- GET /api/v1/members — expression "/members" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1327)

Potential reads: ap_flat, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [members](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:59), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-160"></a>

## 160. Member details — member

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: directory-detail. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770). Native entry: [member](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1345).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Edit member' | navigate / custom | 'member-form?id='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770) |
| 'Payments' | navigate / custom | 'history?flat='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770) |
| f.member?'Transfer flat account':'Share invite' | navigate / custom | f.member?'handover?flat='+f.id:'invite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770) |

### Current native API and database trace

- GET /api/v1/members — expression "/members" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1345)
- POST /api/v1/members/{value}/approve — expression "/members/"+id+"/approve" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1345)
- POST /api/v1/members/{value}/treasurer — expression "/members/"+id+"/treasurer" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:1345)

Potential reads: ap_flat, ap_session, ap_tenant, ap_user.

Potential writes: ap_audit, ap_inbox, ap_session, ap_user.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required
- throw ApiError.conflict("This request is not pending.")
- throw ApiError.conflict("Keep an active Admin account.")
- throw ApiError.conflict("Choose an active resident.")

Backend source pointers: [members](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:59), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [approveMember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:81), [approve](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:165), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [treasurer](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:89), [grantTreasurer](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/AuthService.java:186), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-161"></a>

## 161. Add / edit member — member-form

Group: Administration. Reference roles: Admin. Native destination: member-record-form. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:771). Native entry: [memberRecord](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:577).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save member' | submit | member-record | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:771) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('flat','Flat',S.flats.filter(f=>!f.member\|\|f.id===Q.id).map(f=>f.id),Q.id\|\|'B-205')
- field('name','Member name',x.name\|\|'','text','required maxlength="80"')
- field('mobile','Mobile number',x.mobile\|\|'','tel',f?.member?'maxlength="16" readonly':'required pattern="[6-9][0-9]{9}" maxlength="10"')
- selectField('type','Resident type',['Owner','Tenant'],x.type\|\|'Owner')
- checkField('publicPhone','Member consent to show phone',f?.publicPhone\|\|false)

### Current native API and database trace

- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:577)
- GET /api/v1/ops/directory — expression "/ops/directory" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:577)
- POST /api/v1/ops/directory{value} — expression "/ops/directory"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:577)

Potential reads: ap_flat, ap_member_record, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [directory](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:54), [directory](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:82).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-162"></a>

## 162. Apartment committee — committee

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: committee. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:772). Native entry: [committee](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:599).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| isAdmin()?'Add':'' | navigate / section | isAdmin()?'committee-form':'' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:772) |
| flat(c.flat)?.name\|\|c.flat | navigate / row | 'committee-member?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:772) |
| 'App permissions' | navigate / custom | 'roles' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:772) |

### Current native API and database trace

- GET /api/v1/ops/committee — expression "/ops/committee" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:599)

Potential reads: ap_committee, ap_flat.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [committee](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:76), [committee](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:147), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-163"></a>

## 163. Committee member — committee-member

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: committee-form. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:773). Native entry: [committeeForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:614).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Edit' | navigate / custom | 'committee-form?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:773) |
| 'Archive' | action | archiveCommittee | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:773) |

### Current native API and database trace

- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:614)
- GET /api/v1/ops/committee — expression "/ops/committee" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:614)
- POST /api/v1/ops/committee{value} — expression "/ops/committee"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:614)

Potential reads: ap_committee, ap_flat, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [committee](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:76), [committee](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:147).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-164"></a>

## 164. Committee role — committee-form

Group: Administration. Reference roles: Admin. Native destination: committee-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:774). Native entry: [committeeForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:614).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save committee role' | submit | committee | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:774) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('flat','Member',S.flats.filter(f=>f.member).map(f=>[f.id,f.name+' · '+f.id]),c.flat\|\|'A-203')
- selectField('title','Committee title',['President','Vice President','Secretary','Joint Secretary','Treasurer','Committee Member'],c.title\|\|'Secretary')
- field('start','Term starts',c.start\|\|today,'date','required')
- field('end','Term ends',c.end\|\|'2027-03-31','date','required')

### Current native API and database trace

- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:614)
- GET /api/v1/ops/committee — expression "/ops/committee" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:614)
- POST /api/v1/ops/committee{value} — expression "/ops/committee"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:614)

Potential reads: ap_committee, ap_flat, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [committee](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:76), [committee](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:147).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-165"></a>

## 165. Approvals — approval-inbox

Group: Overview. Reference roles: Admin, Treasurer. Native destination: approval-inbox. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:775). Native entry: [tasks](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1971).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Maintenance payments' | navigate / row | 'approvals' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:775) |
| 'New members' | navigate / row | 'join-requests' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:775) |
| 'Event bookings' | navigate / row | 'booking-requests' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:775) |

### Current native API and database trace

- GET /api/v1/ops/tasks — expression "/ops/tasks" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1971)

Potential reads: ap_booking, ap_expense, ap_payment, ap_ticket, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required

Backend source pointers: [tasks](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:270), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-166"></a>

## 166. Choose a notice — notice-templates

Group: Community. Reference roles: Admin, Treasurer. Native destination: notice-templates. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:791). Native entry: [noticeTemplates](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1392).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Write a notice' | navigate / custom | 'notice-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:791) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-167"></a>

## 167. Preview notice — notice-preview

Group: Community. Reference roles: Admin, Treasurer. Native destination: notice-preview. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793). Native entry: [noticePreview](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1451).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Edit' | navigate / custom | 'notice-form?id='+n.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793) |
| n.sendAt?'Schedule':'Publish' | action | publishAnnouncement | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793) |
| 'Keep draft' | action | keepNoticeDraft | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793) |

### Current native API and database trace

- GET /api/v1/notices/{value}/preview — expression "/notices/"+id+"/preview" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1451)
- POST /api/v1/notices/{value}/publish — expression "/notices/"+id+"/publish" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1451)
- POST /api/v1/notices/{value}/schedule — expression "/notices/"+id+"/schedule" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1451)

Potential reads: ap_bill, ap_command, ap_flat, ap_payment, ap_payment_reversal, ap_tenant, ap_user.

Potential writes: ap_audit, ap_command, ap_inbox, ap_notice, ap_notice_recipient.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.conflict("Only an unpublished draft can be previewed.")
- Server Admin/Treasurer role required
- throw ApiError.conflict("This notice is scheduled for later. Edit the draft to publish now.")
- throw new IllegalArgumentException("No active recipients in this audience.")
- throw ApiError.conflict("Only an unpublished draft can be scheduled.")
- throw new IllegalArgumentException("Choose a future publishing time.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [noticePreview](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:152), [preview](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:35), [recipientAccounts](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:86), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [recipient](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:75), [publish](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:162), [publish](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:117), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [publishInternal](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:89), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57), [schedule](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:165), [schedule](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:101).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-168"></a>

## 168. Audience & responses — notice-delivery

Group: Community. Reference roles: Admin, Treasurer. Native destination: notice. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:796). Native entry: [notice](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1361).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| flat(id)?.name\|\|id | navigate / row | 'member?id='+id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:796) |

### Current native API and database trace

- GET /api/v1/notices/{value} — expression "/notices/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1361)
- POST /api/v1/notices/{value}/pin — expression "/notices/"+id+"/pin" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1361)
- POST /api/v1/notices/{value}/read — expression "/notices/"+id+"/read" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1361)

Potential reads: None resolved / requires manual tracing.

Potential writes: ap_notice, ap_notice_recipient.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.bool(r,"pinned",true)))
- Server Admin/Treasurer role required
- V.bool(r,"acknowledge",false)))
- throw new IllegalArgumentException("Acknowledgement was not requested.")

Backend source pointers: [notice](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:149), [noticePin](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:171), [pin](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:131), [noticeRead](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:168), [read](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/NoticeService.java:125).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-169"></a>

## 169. Maintenance rate history — rate-history

Group: Finance. Reference roles: Admin, Treasurer. Native destination: rate-history. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:818). Native entry: [rateHistory](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:961).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| r.amount==null?'Not configured':INR(r.amount)+' / flat' | navigate / row | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:818) |

### Current native API and database trace

- GET /api/v1/billing/rules — expression "/billing/rules" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:961)

Potential reads: ap_billing_rule.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required

Backend source pointers: [rules](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:93), [rules](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:36), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-170"></a>

## 170. Flat-specific rates — rate-overrides

Group: Finance. Reference roles: Admin, Treasurer. Native destination: rate-overrides. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:819). Native entry: [overrides](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1620).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Add' | navigate / section | 'rate-override-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:819) |
| r.flat+' · '+INR(r.amount) | navigate / row | 'rate-override-form?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:819) |

### Current native API and database trace

- GET /api/v1/ops/rate-overrides — expression "/ops/rate-overrides" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1620)

Potential reads: ap_flat, ap_rate_override.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin/Treasurer role required

Backend source pointers: [overrides](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:106), [overrides](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:79), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-171"></a>

## 171. Set flat rate — rate-override-form

Group: Finance. Reference roles: Admin, Treasurer. Native destination: rate-override-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:820). Native entry: [overrideForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1635).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save flat rate' | submit | rate-override | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:820) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('flat','Flat',S.flats.filter(f=>f.active).map(f=>f.id),r.flat\|\|'A-203')
- amountField('amount','Monthly amount',r.amount\|\|S.settings.amount\|\|'')
- field('effective','Effective from',r.effective\|\|'2026-10','month','required min="2026-10"')
- field('reason','Reason',r.reason\|\|'','textarea','required maxlength="300"')

### Current native API and database trace

- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1635)
- POST /api/v1/ops/rate-overrides — expression "/ops/rate-overrides" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1635)

Potential reads: ap_bill, ap_command, ap_flat, ap_rate_override, ap_tenant, ap_user.

Potential writes: ap_audit, ap_command, ap_rate_override.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.id(p,"flatId"))
- V.text(p,"effective",7)))
- V.money(p,"amount")
- Server Admin/Treasurer role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [override](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:109), [override](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:83), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [flat](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:43), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-172"></a>

## 172. One-time collections — contributions

Group: Finance. Reference roles: Admin, Treasurer. Native destination: contributions. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:825). Native entry: [contributions](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1648).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Add' | navigate / section | 'contribution-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:825) |
| g.title | navigate / row | 'contribution?id='+g.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:825) |

### Current native API and database trace

- GET /api/v1/bills — expression "/bills?month="+h.selectedMonth() — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1648)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [bills](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:112), [bills](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:117), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-173"></a>

## 173. One-time contribution — contribution-form

Group: Finance. Reference roles: Admin, Treasurer. Native destination: contribution-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:826). Native entry: [charge](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1665).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Review collection' | submit | contribution-draft | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:826) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('title','Collection name','','text','required maxlength="80" placeholder="e.g. Lift repair fund"')
- amountField('amount','Amount per flat','500')
- selectField('scope','Charge to',['All active flats','Selected flats'],'All active flats')
- field('flats','Selected flats · optional','','text','placeholder="A-101, A-203"')
- field('due','Due date','2026-09-25','date','required min="2026-09-13"')

### Current native API and database trace

- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1665)
- POST /api/v1/ops/charges/preview — expression "/ops/charges/preview" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1665)
- POST /api/v1/ops/charges — expression "/ops/charges" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1665)

Potential reads: ap_bill, ap_command, ap_flat, ap_tenant, ap_user.

Potential writes: ap_audit, ap_bill, ap_command, ap_inbox.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"kind","CONTRIBUTION","OPENING_DUE")
- V.date(p,"dueDate"))
- V.text(p,"month",7)))
- V.money(p,"amount"))
- throw new IllegalArgumentException("Contribution due date cannot be in the past.")
- Server Admin/Treasurer role required
- V.optionalId(p,"flatId"))
- V.opt(p,"flatLabels",500))
- V.opt(p,"scope",20)))flats.addAll(db.rows("select id,label from ap_flat where tenant_id=? and active=true order by label",a.tenantId())))
- throw new IllegalArgumentException("Choose either one flat or a flat list, not both.")
- throw new IllegalArgumentException("Enter valid comma-separated flat labels.")
- throw new IllegalArgumentException("Select between 1 and 50 active flats.")
- throw new IllegalArgumentException("No active flats selected.")
- throw new IllegalArgumentException("Opening dues must identify one flat.")
- throw ApiError.conflict("An opening due already exists for this flat.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previewCharge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:115), [previewCharge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:100), [chargeFlats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:142), [charge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:112), [charge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:119), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-174"></a>

## 174. Review contribution — contribution-preview

Group: Finance. Reference roles: Admin, Treasurer. Native destination: contribution-preview. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:827). Native entry: [contributionPreview](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1689).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Create dues' | action | createContribution | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:827) |
| 'New collection' | navigate / custom | 'contribution-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:827) |

### Current native API and database trace

- POST /api/v1/ops/charges — expression "/ops/charges" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1689)

Potential reads: ap_bill, ap_command, ap_flat, ap_tenant, ap_user.

Potential writes: ap_audit, ap_bill, ap_command, ap_inbox.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"kind","CONTRIBUTION","OPENING_DUE")
- V.date(p,"dueDate"))
- V.text(p,"month",7)))
- V.money(p,"amount"))
- throw new IllegalArgumentException("Contribution due date cannot be in the past.")
- throw ApiError.conflict("An opening due already exists for this flat.")
- Server Admin/Treasurer role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.optionalId(p,"flatId"))
- V.opt(p,"flatLabels",500))
- V.opt(p,"scope",20)))flats.addAll(db.rows("select id,label from ap_flat where tenant_id=? and active=true order by label",a.tenantId())))
- throw new IllegalArgumentException("Choose either one flat or a flat list, not both.")
- throw new IllegalArgumentException("Enter valid comma-separated flat labels.")
- throw new IllegalArgumentException("Select between 1 and 50 active flats.")
- throw new IllegalArgumentException("No active flats selected.")
- throw new IllegalArgumentException("Opening dues must identify one flat.")
- V.id(body,"requestKey")

Backend source pointers: [charge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:112), [charge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:119), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [chargeFlats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:142), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-175"></a>

## 175. Contribution details — contribution

Group: Finance. Reference roles: Admin, Treasurer. Native destination: bill. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:828). Native entry: [bill](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:880).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

- GET /api/v1/bills/{value} — expression "/bills/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/MainActivity.java:880)

Potential reads: ap_bill, ap_flat, ap_payment, ap_payment_reversal.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- throw ApiError.forbidden()

Backend source pointers: [bill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:115), [bill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:127), [ownedBill](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:135), [balance](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/finance/FinanceService.java:21).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-176"></a>

## 176. Opening dues by flat — opening-dues

Group: Finance. Reference roles: Admin, Treasurer. Native destination: opening-dues. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:829). Native entry: [charge](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1665).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Add' | navigate / section | 'opening-due-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:829) |

### Current native API and database trace

- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1665)
- POST /api/v1/ops/charges/preview — expression "/ops/charges/preview" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1665)
- POST /api/v1/ops/charges — expression "/ops/charges" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1665)

Potential reads: ap_bill, ap_command, ap_flat, ap_tenant, ap_user.

Potential writes: ap_audit, ap_bill, ap_command, ap_inbox.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"kind","CONTRIBUTION","OPENING_DUE")
- V.date(p,"dueDate"))
- V.text(p,"month",7)))
- V.money(p,"amount"))
- throw new IllegalArgumentException("Contribution due date cannot be in the past.")
- Server Admin/Treasurer role required
- V.optionalId(p,"flatId"))
- V.opt(p,"flatLabels",500))
- V.opt(p,"scope",20)))flats.addAll(db.rows("select id,label from ap_flat where tenant_id=? and active=true order by label",a.tenantId())))
- throw new IllegalArgumentException("Choose either one flat or a flat list, not both.")
- throw new IllegalArgumentException("Enter valid comma-separated flat labels.")
- throw new IllegalArgumentException("Select between 1 and 50 active flats.")
- throw new IllegalArgumentException("No active flats selected.")
- throw new IllegalArgumentException("Opening dues must identify one flat.")
- throw ApiError.conflict("An opening due already exists for this flat.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previewCharge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:115), [previewCharge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:100), [chargeFlats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:142), [charge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:112), [charge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:119), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-177"></a>

## 177. Add opening due — opening-due-form

Group: Finance. Reference roles: Admin, Treasurer. Native destination: opening-dues. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:830). Native entry: [charge](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1665).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Add opening due' | submit | opening-due | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:830) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('flat','Flat',S.flats.filter(f=>f.active).map(f=>f.id),'A-203')
- amountField('amount','Unpaid amount','')
- field('asOf','Balance as of',today,'date','required max="2026-09-13"')
- field('reason','Description','','textarea','required maxlength="300" placeholder="Unpaid balance before joining the app"')
- checkField('verified','I checked this opening balance.')

### Current native API and database trace

- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1665)
- POST /api/v1/ops/charges/preview — expression "/ops/charges/preview" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1665)
- POST /api/v1/ops/charges — expression "/ops/charges" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:1665)

Potential reads: ap_bill, ap_command, ap_flat, ap_tenant, ap_user.

Potential writes: ap_audit, ap_bill, ap_command, ap_inbox.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.choice(p,"kind","CONTRIBUTION","OPENING_DUE")
- V.date(p,"dueDate"))
- V.text(p,"month",7)))
- V.money(p,"amount"))
- throw new IllegalArgumentException("Contribution due date cannot be in the past.")
- Server Admin/Treasurer role required
- V.optionalId(p,"flatId"))
- V.opt(p,"flatLabels",500))
- V.opt(p,"scope",20)))flats.addAll(db.rows("select id,label from ap_flat where tenant_id=? and active=true order by label",a.tenantId())))
- throw new IllegalArgumentException("Choose either one flat or a flat list, not both.")
- throw new IllegalArgumentException("Enter valid comma-separated flat labels.")
- throw new IllegalArgumentException("Select between 1 and 50 active flats.")
- throw new IllegalArgumentException("No active flats selected.")
- throw new IllegalArgumentException("Opening dues must identify one flat.")
- throw ApiError.conflict("An opening due already exists for this flat.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previewCharge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:115), [previewCharge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:100), [chargeFlats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:142), [charge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:112), [charge](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/AccountingService.java:119), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-178"></a>

## 178. Application charges — subscription-plans

Group: Settings. Reference roles: Admin. Native destination: subscription-plans. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:856). Native entry: [subscriptionPlans](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2134).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

- GET /api/v1/ops/subscription — expression "/ops/subscription" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2134)

Potential reads: ap_flat, ap_referral, ap_subscription, ap_subscription_invoice, ap_tenant.

Potential writes: ap_subscription, ap_subscription_invoice.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [subscription](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:251), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:42), [ensure](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:27), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [price](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:24), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-179"></a>

## 179. Subscription invoices — subscription-invoices

Group: Settings. Reference roles: Admin. Native destination: subscription. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:857). Native entry: [subscription](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| i.id | navigate / row | 'subscription-invoice?id='+i.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:857) |

### Current native API and database trace

- GET /api/v1/ops/subscription — expression "/ops/subscription" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)
- POST /api/v1/ops/subscription/install — expression "/ops/subscription/install" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)

Potential reads: ap_flat, ap_referral, ap_subscription, ap_subscription_invoice, ap_tenant.

Potential writes: ap_subscription, ap_subscription_invoice.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [subscription](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:251), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:42), [ensure](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:27), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [price](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:24), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [install](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:254), [installed](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:59), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-180"></a>

## 180. Application invoice — subscription-invoice

Group: Settings. Reference roles: Admin. Native destination: subscription. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:858). Native entry: [subscription](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save record' | action | downloadSubInvoice | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:858) |

### Current native API and database trace

- GET /api/v1/ops/subscription — expression "/ops/subscription" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)
- POST /api/v1/ops/subscription/install — expression "/ops/subscription/install" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)

Potential reads: ap_flat, ap_referral, ap_subscription, ap_subscription_invoice, ap_tenant.

Potential writes: ap_subscription, ap_subscription_invoice.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [subscription](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:251), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:42), [ensure](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:27), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [price](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:24), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [install](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:254), [installed](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:59), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-181"></a>

## 181. Renewal status — subscription-renewal

Group: Settings. Reference roles: Admin. Native destination: subscription. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:859). Native entry: [subscription](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Invoices' | navigate / custom | 'subscription-invoices' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:859) |

### Current native API and database trace

- GET /api/v1/ops/subscription — expression "/ops/subscription" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)
- POST /api/v1/ops/subscription/install — expression "/ops/subscription/install" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)

Potential reads: ap_flat, ap_referral, ap_subscription, ap_subscription_invoice, ap_tenant.

Potential writes: ap_subscription, ap_subscription_invoice.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [subscription](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:251), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:42), [ensure](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:27), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [price](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:24), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [install](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:254), [installed](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:59), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-182"></a>

## 182. Refer & unlock — referrals

Group: Settings. Reference roles: Admin. Native destination: subscription. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860). Native entry: [subscription](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'View reward' | navigate / custom | 'referral-reward' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860) |
| 'Share referral code' | action | shareApartmentReferral | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860) |
| 'Activate offer' | navigate / custom | 'referral-offer' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860) |
| 'Track referrals' | navigate / custom | 'referral-progress' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860) |
| 'Offer rules' | navigate / custom | 'referral-rules' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860) |

### Current native API and database trace

- GET /api/v1/ops/subscription — expression "/ops/subscription" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)
- POST /api/v1/ops/subscription/install — expression "/ops/subscription/install" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)

Potential reads: ap_flat, ap_referral, ap_subscription, ap_subscription_invoice, ap_tenant.

Potential writes: ap_subscription, ap_subscription_invoice.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [subscription](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:251), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:42), [ensure](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:27), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [price](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:24), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [install](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:254), [installed](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:59), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-183"></a>

## 183. First-month offer — referral-offer

Group: Settings. Reference roles: Admin. Native destination: referral-offer. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:861). Native entry: [referralOffer](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2149).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Activate offer' | submit | referral-opt-in | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:861) |

Reference input/form definitions (attributes and allowed choices shown literally):

- checkField('terms','I accept the referral offer rules.')

### Current native API and database trace

- GET /api/v1/ops/subscription — expression "/ops/subscription" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2149)
- POST /api/v1/ops/subscription/offer — expression "/ops/subscription/offer" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2149)

Potential reads: ap_flat, ap_referral, ap_subscription, ap_subscription_invoice, ap_tenant.

Potential writes: ap_audit, ap_subscription, ap_subscription_invoice.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required
- throw new IllegalArgumentException("Accept the referral offer rules.")
- throw ApiError.conflict("The first-month offer window has ended.")

Backend source pointers: [subscription](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:251), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:42), [ensure](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:27), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [price](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:24), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [offer](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:257), [acceptOffer](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:65), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-184"></a>

## 184. Referral progress — referral-progress

Group: Settings. Reference roles: Admin. Native destination: subscription. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:862). Native entry: [subscription](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Add' | navigate / section | 'referral-add' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:862) |
| r.name | navigate / row | 'referral-detail?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:862) |
| 'Share code' | action | shareApartmentReferral | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:862) |

### Current native API and database trace

- GET /api/v1/ops/subscription — expression "/ops/subscription" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)
- POST /api/v1/ops/subscription/install — expression "/ops/subscription/install" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)

Potential reads: ap_flat, ap_referral, ap_subscription, ap_subscription_invoice, ap_tenant.

Potential writes: ap_subscription, ap_subscription_invoice.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [subscription](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:251), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:42), [ensure](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:27), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [price](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:24), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [install](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:254), [installed](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:59), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-185"></a>

## 185. New apartment referral — referral-add

Group: Settings. Reference roles: Admin. Native destination: referral-claim. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:863). Native entry: [referralClaim](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2044).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Save referral' | submit | referral-add | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:863) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('name','Apartment name','','text','required maxlength="80"')
- field('city','City','','text','required maxlength="80"')
- field('code','Apartment identifier','','text','required maxlength="30" placeholder="e.g. APT-GREEN-02"')
- field('flats','Number of flats',20,'number','min="5" max="50" required')

### Current native API and database trace

- POST /api/v1/ops/referrals/claim — expression "/ops/referrals/claim" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2044)

Potential reads: ap_flat, ap_referral, ap_referral_award, ap_subscription, ap_subscription_invoice, ap_tenant.

Potential writes: ap_audit, ap_referral, ap_referral_award, ap_subscription, ap_subscription_invoice, ap_subscription_refund_review.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.text(p,"code",24).toUpperCase(Locale.ROOT))
- throw ApiError.conflict("You cannot refer your own apartment.")
- throw ApiError.conflict("The first-month referral window has ended.")
- throw ApiError.conflict("This apartment already has a referrer.")
- throw ApiError.conflict("Circular referrals are not permitted.")
- Server Admin role required

Backend source pointers: [claim](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:260), [claim](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:75), [promotionLock](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:115), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [lockPair](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:106), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [ensure](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:27), [price](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:24), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57), [award](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:139), [eligible](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:135), [waive](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:156), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-186"></a>

## 186. Referral details — referral-detail

Group: Settings. Reference roles: Admin. Native destination: subscription. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:864). Native entry: [subscription](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

- GET /api/v1/ops/subscription — expression "/ops/subscription" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)
- POST /api/v1/ops/subscription/install — expression "/ops/subscription/install" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)

Potential reads: ap_flat, ap_referral, ap_subscription, ap_subscription_invoice, ap_tenant.

Potential writes: ap_subscription, ap_subscription_invoice.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [subscription](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:251), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:42), [ensure](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:27), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [price](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:24), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [install](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:254), [installed](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:59), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-187"></a>

## 187. First month reward — referral-reward

Group: Settings. Reference roles: Admin. Native destination: subscription. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865). Native entry: [subscription](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| S.apartment.name | navigate / row | 'subscription-invoice?id='+firstInvoice().id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865) |
| r.name | navigate / row | 'referral-detail?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865) |
| 'Next payment date' | navigate / custom | 'subscription-renewal' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865) |
| 'Track referrals' | navigate / custom | 'referral-progress' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865) |

### Current native API and database trace

- GET /api/v1/ops/subscription — expression "/ops/subscription" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)
- POST /api/v1/ops/subscription/install — expression "/ops/subscription/install" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)

Potential reads: ap_flat, ap_referral, ap_subscription, ap_subscription_invoice, ap_tenant.

Potential writes: ap_subscription, ap_subscription_invoice.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [subscription](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:251), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:42), [ensure](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:27), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [price](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:24), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [install](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:254), [installed](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:59), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-188"></a>

## 188. Referral rules — referral-rules

Group: Settings. Reference roles: Admin. Native destination: referral-rules. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:866). Native entry: [referralRules](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2169).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| No direct definition extracted | May use shared/dynamic controls | Manual inspection required | Page source above |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-189"></a>

## 189. Subscription overdue — subscription-overdue

Group: Settings. Reference roles: Admin. Native destination: subscription. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:868). Native entry: [subscription](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'View invoices' | navigate / custom | 'subscription-invoices' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:868) |

### Current native API and database trace

- GET /api/v1/ops/subscription — expression "/ops/subscription" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)
- POST /api/v1/ops/subscription/install — expression "/ops/subscription/install" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2001)

Potential reads: ap_flat, ap_referral, ap_subscription, ap_subscription_invoice, ap_tenant.

Potential writes: ap_subscription, ap_subscription_invoice.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [subscription](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:251), [summary](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:42), [ensure](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:27), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [price](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:24), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:50), [install](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:254), [installed](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/SubscriptionService.java:59), [update](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:13).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-190"></a>

## 190. Provider simulation — provider-simulation

Group: Reference tools. Reference roles: Admin. Native destination: unresolved. Recorded status: REFERENCE_ONLY_NOT_A_PRODUCTION_FEATURE.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:870). Native entry trace: unresolved / no dedicated implementation found.

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| r.name | navigate / row | 'activation-simulation?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:870) |
| 'Move to month 2' | action | simulateRenewal | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:870) |
| 'Record subscription payment' | navigate / custom | 'subscription-payment-simulation' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:870) |
| 'Demo clock & automation' | navigate / custom | 'preview-clock' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:870) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-191"></a>

## 191. Verify activation · demo — activation-simulation

Group: Reference tools. Reference roles: Admin. Native destination: unresolved. Recorded status: REFERENCE_ONLY_NOT_A_PRODUCTION_FEATURE.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:871). Native entry trace: unresolved / no dedicated implementation found.

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Apply demo event' | submit | activation-simulation | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:871) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('stage','Next state',['Referred','Installed','Setup complete','Activated'],r.stage)
- checkField('phoneVerified','Admin identity verified',r.phoneVerified)
- checkField('residentVerified','Setup complete + one approved resident',r.residentVerified)
- checkField('distinct','Distinct new apartment; not a duplicate',r.distinct)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-192"></a>

## 192. Subscription payment · demo — subscription-payment-simulation

Group: Reference tools. Reference roles: Admin. Native destination: unresolved. Recorded status: REFERENCE_ONLY_NOT_A_PRODUCTION_FEATURE.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:872). Native entry trace: unresolved / no dedicated implementation found.

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Record demo payment' | submit | subscription-payment-simulation | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:872) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('invoice','Invoice',S.subscription.invoices.filter(i=>subBalance(i)>0).map(i=>[i.id,i.id+' · '+INR(subBalance(i))]))
- field('reference','Reference','','text','required maxlength="40" placeholder="DEMO-SUB-001"')
- checkField('verified','Provider verified the subscription payment.')

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-193"></a>

## 193. Community polls — polls

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: polls. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:885). Native entry: [polls](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2186).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| isAdmin()?'New':'' | navigate / section | isAdmin()?'poll-form':'' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:885) |
| p.question | navigate / row | 'poll?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:885) |

### Current native API and database trace

- GET /api/v1/ops/polls — expression "/ops/polls" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2186)

Potential reads: ap_poll.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [polls](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ExtrasController.java:14), [polls](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/ExtrasService.java:16), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-194"></a>

## 194. New poll — poll-form

Group: Community. Reference roles: Admin. Native destination: poll-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:886). Native entry: [pollForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2199).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Publish poll' | submit | poll | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:886) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('question','Question','','text','required maxlength="150"')
- field('options','Choices · one per line','Yes\nNo','textarea','required maxlength="500"')
- field('end','Voting closes','2026-09-25','date','required min="2026-09-13"')

### Current native API and database trace

- POST /api/v1/ops/polls — expression "/ops/polls" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2199)

Potential reads: ap_command, ap_tenant, ap_user.

Potential writes: ap_audit, ap_command, ap_inbox, ap_poll, ap_poll_option.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.text(body,"title",160))
- V.opt(body,"description",1000)
- throw new IllegalArgumentException("Choose a closing time within 90 days.")
- throw new IllegalArgumentException("Provide 2–6 options.")
- throw new IllegalArgumentException("Invalid poll option.")
- throw new IllegalArgumentException("Duplicate poll option.")
- Server Admin role required
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [poll](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ExtrasController.java:20), [createPoll](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/ExtrasService.java:26), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-195"></a>

## 195. Vote & results — poll

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: poll. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887). Native entry: [poll](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2214).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Close poll' | action | closePoll | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887) |

### Current native API and database trace

- GET /api/v1/ops/polls/{value} — expression "/ops/polls/"+id — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2214)
- POST /api/v1/ops/polls/{value}/vote — expression "/ops/polls/"+id+"/vote" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2214)
- POST /api/v1/ops/polls/{value}/close — expression "/ops/polls/"+id+"/close" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2214)

Potential reads: ap_poll, ap_poll_option, ap_poll_vote, ap_tenant.

Potential writes: ap_audit, ap_poll, ap_poll_vote.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.id(body,"optionId"))
- throw new IllegalArgumentException("Voting needs an active flat association.")
- throw ApiError.conflict("Voting has closed.")
- Server Admin role required

Backend source pointers: [poll](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ExtrasController.java:17), [poll](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/ExtrasService.java:19), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [vote](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ExtrasController.java:23), [vote](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/ExtrasService.java:50), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [close](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ExtrasController.java:26), [close](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/ExtrasService.java:60), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-196"></a>

## 196. Vehicles — vehicles

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: vehicles. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:888). Native entry: [vehicles](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2234).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| S.role==='Resident'?'Add':'' | navigate / section | S.role==='Resident'?'vehicle-form':'' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:888) |
| v.number | navigate / row | 'vehicle?id='+v.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:888) |

### Current native API and database trace

- GET /api/v1/ops/vehicles — expression "/ops/vehicles" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2234)

Potential reads: ap_flat, ap_vehicle.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [vehicles](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ExtrasController.java:29), [vehicles](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/ExtrasService.java:68).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-197"></a>

## 197. Vehicle details — vehicle

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: vehicle. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:889). Native entry: [vehicle](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2247).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Vehicle blocking? Notify owner' | navigate / custom | 'vehicle-contact?id='+v.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:889) |
| 'Edit vehicle' | navigate / custom | 'vehicle-form?id='+v.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:889) |

### Current native API and database trace

- GET /api/v1/ops/vehicles — expression "/ops/vehicles" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2247)
- POST /api/v1/ops/vehicles/{value}/contact — expression "/ops/vehicles/"+id+"/contact" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2247)

Potential reads: ap_command, ap_flat, ap_tenant, ap_user, ap_vehicle.

Potential writes: ap_audit, ap_command, ap_inbox, ap_rate_limit.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.text(body,"message",300))
- throw ApiError.conflict("No active flat account is available. Contact the Admin.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [vehicles](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ExtrasController.java:29), [vehicles](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/ExtrasService.java:68), [contact](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ExtrasController.java:38), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/RateGate.java:14), [contactOwner](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/ExtrasService.java:94), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-198"></a>

## 198. My vehicle — vehicle-form

Group: Community. Reference roles: Resident. Native destination: vehicle-form. Recorded status: CONNECTED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:890). Native entry: [vehicleForm](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2260).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Back home' | navigate / custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Save vehicle' | submit | vehicle | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:890) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('number','Registration number',v.number\|\|'','text','required maxlength="20"')
- selectField('type','Vehicle',['Car','Two-wheeler'],v.type\|\|'Car')
- field('color','Colour',v.color\|\|'','text','required maxlength="30"')

### Current native API and database trace

- GET /api/v1/ops/vehicles — expression "/ops/vehicles" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2260)
- GET /api/v1/ops/flats — expression "/ops/flats" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2260)
- POST /api/v1/ops/vehicles{value} — expression "/ops/vehicles"+(id.isEmpty()?"":"/"+id) — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2260)

Potential reads: ap_flat, ap_user, ap_vehicle.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Backend source pointers: [vehicles](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ExtrasController.java:29), [vehicles](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/ExtrasService.java:68), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/OperationsController.java:45), [flats](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/CommunityService.java:55), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-199"></a>

## 199. Notify vehicle owner — vehicle-contact

Group: Community. Reference roles: Admin, Treasurer, Resident. Native destination: vehicle. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:891). Native entry: [vehicle](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2247).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Notify owner' | submit | vehicle-contact | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:891) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('reason','Issue',['Blocking my vehicle','Blocking entry / exit','Lights left on','Other'])
- field('note','Note · optional','','textarea','maxlength="250"')

### Current native API and database trace

- GET /api/v1/ops/vehicles — expression "/ops/vehicles" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2247)
- POST /api/v1/ops/vehicles/{value}/contact — expression "/ops/vehicles/"+id+"/contact" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:2247)

Potential reads: ap_command, ap_flat, ap_tenant, ap_user, ap_vehicle.

Potential writes: ap_audit, ap_command, ap_inbox, ap_rate_limit.

Backend validation/guard expressions found in the call trace (includes helper branches):

- V.text(body,"message",300))
- throw ApiError.conflict("No active flat account is available. Contact the Admin.")
- V.id(body,"requestKey"))
- throw ApiError.conflict("Request key was already used for different details.")
- throw new IllegalArgumentException("Invalid request.")
- V.id(body,"requestKey")

Backend source pointers: [vehicles](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ExtrasController.java:29), [vehicles](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/ExtrasService.java:68), [contact](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ExtrasController.java:38), [count](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/security/RateGate.java:14), [contactOwner](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/ExtrasService.java:94), [lockTenant](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:54), [one](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:44), [find](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:41), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16), [previous](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:32), [hash](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:24), [canonical](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:15), [notify](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:60), [remember](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:39), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/operations/Commands.java:53), [audit](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:57).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-200"></a>

## 200. Committee handover — committee-handover

Group: Administration. Reference roles: Admin. Native destination: roles. Recorded status: PARTIAL_MERGED_SOURCE_NOT_RUNTIME_VERIFIED.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:892). Native entry: [roles](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:635).

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Export handover summary' | action | exportHandover | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:892) |

### Current native API and database trace

- GET /api/v1/members — expression "/members" — [native method](/D:/ApartmentPilotAI/android/app/src/main/java/com/rapolus/apartmentpilotai/ui/FeatureScreens.java:635)

Potential reads: ap_flat, ap_user.

Potential writes: None resolved / requires manual tracing.

Backend validation/guard expressions found in the call trace (includes helper branches):

- Server Admin role required

Backend source pointers: [members](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/api/ApiController.java:59), [rows](/D:/ApartmentPilotAI/backend/src/main/java/com/rapolus/apartmentpilotai/store/Db.java:16).

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-201"></a>

## 201. Demo clock — preview-clock

Group: Reference tools. Reference roles: Admin, Treasurer. Native destination: unresolved. Recorded status: REFERENCE_ONLY_NOT_A_PRODUCTION_FEATURE.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:945). Native entry trace: unresolved / no dedicated implementation found.

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Set clock' | submit | preview-clock | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:945) |
| 'Run scheduled tasks' | action | runAutomation | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:945) |

Reference input/form definitions (attributes and allowed choices shown literally):

- field('now','Simulation date and time',S.referenceNow,'datetime-local','required')

Reference action/form behavior (prototype intent; never backend security proof):

- **runAutomation**: ()=>{requireStaff();requireOnline();let before=S.audit.length;createMissingBills(currentMonth);if(S.settings.reminders&&S.settings.reminderDays.split(',').map(v=>Number(v.trim())).includes(Number(today.slice(-2)))){S.bills.filter(b=>b.month===currentMonth&&due(b)>0&&!billPending(b)).forEach(b=>{const key='reminder-'+b.id+'-'+today;if(!S.notifications.some(n=>n.key===key)){notify('Maintenance reminder',INR(due(b))+' pending · '+b.flat,'Resident','bill?id='+b.id,b.flat);S.notifications[0].key=key;}});}S.recurring.filter(r=>r.active&&r.next<=today).forEach(prepareRecurring);S.services.filter(s=>s.next<=nextCycle(today,'Monthly')&&S.settings.serv — [handler](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-202"></a>

## 202. Included in this reference — release-coverage

Group: Reference tools. Reference roles: Admin, Treasurer, Resident. Native destination: unresolved. Recorded status: REFERENCE_ONLY_NOT_A_PRODUCTION_FEATURE.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:966). Native entry trace: unresolved / no dedicated implementation found.

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| t | navigate / row | to | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:967) |

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.

<a id="page-203"></a>

## 203. Refund credit · demo — subscription-refund-simulation

Group: Reference tools. Reference roles: Admin. Native destination: unresolved. Recorded status: REFERENCE_ONLY_NOT_A_PRODUCTION_FEATURE.

Reference: [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:976). Native entry trace: unresolved / no dedicated implementation found.

### Buttons and actions in the reference

| Label / definition | Type | Function target | Source |
|---|---|---|---|
| 'Record demo refund' | submit | subscription-refund-simulation | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:976) |

Reference input/form definitions (attributes and allowed choices shown literally):

- selectField('invoice','First-month invoice',S.subscription.invoices.filter(i=>i.status==='Refund due').map(i=>[i.id,i.id+' · '+INR(i.paidAmount\|\|i.amount)]))
- field('reference','Refund reference','','text','required maxlength="40"')
- checkField('verified','Provider confirms the first-month payment was refunded.')

### Current native API and database trace

No literal native API path resolved. Local-only page or tracing gap; **unverified**, not assumed implemented.

Potential reads: None resolved / requires manual tracing.

Potential writes: None resolved / requires manual tracing.

Backend validation trace: unresolved or no server mutation. Do not infer that validation is unnecessary.

Acceptance still required: role/tenant/record scope, input negatives, duplicate/retry/race behavior where applicable, server/offline/session errors, persistence/revisit, Android Back, phone/tablet orientations, reference comparison and independent QA. Reference-only tools are excluded from production acceptance.
