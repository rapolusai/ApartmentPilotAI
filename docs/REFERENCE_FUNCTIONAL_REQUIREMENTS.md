# Functional requirements collected from the approved reference UI

This document describes **what the approved design requires**, independently of whether native/backend code already implements it. All 203 page render definitions and every action/form handler in the reference are preserved in the companion JSON. The reference was parsed, not executed or modified.

Prototype data, demo sign-in, browser storage, fake clock, reset/role tools and simulated provider success are **not production implementations**. Production authorization, persistence, secrets, payment verification and integrations must follow the project rules. Requirements implied by UI are separated from test acceptance and existing-source coverage.

Reference SHA256: FF5554324E630F3B63235EC723E094BE6CDFF02DBF434898BED5D6F290F3AE28. Pages: 203. Action handlers: 92. Form handlers: 72. These are handler-definition counts, not simultaneous button counts.

## Shared requirements on applicable pages

- Back and Android Back return through the correct navigation flow.
- Theme supports light/dark; page content and controls remain usable on phones/tablets in both orientations.
- Role-sensitive navigation never grants permissions; backend must independently enforce actor, tenant and parent-record access.
- Preserve the shared footer Powered By @Rapolu`s, including dialogs and error states.
- Loading, empty, forbidden, missing-record, offline, session-expired and validation states must not fabricate successful saves.
- Filters/search/month selection affect visible records, not recorded financial history.
- Dialog actions include Cancel/Close and a distinct confirmation; confirmation does not bypass validation.

Reference-wide form/online decorators and role guard registrations (apply in addition to per-handler checks):

- ADMIN_FORMS=new Set(['apartment','block','flat','join-reject','approve-claim','invite-settings','handover','role','opening-balance']) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:547)
- STAFF_FORMS=new Set(['approve-single','bulk-confirm','reject-payment','billing-settings','expense','reverse-expense','recurring','income','payment-settings','issue-assign','issue-update','complaint-update','service','service-complete','notice','vendor','contact','document','reminder-settings']) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:548)
- for(const name of ['submitEvent','acceptEventOffer','removeResourceBlock','confirmRemoveResourceBlock','unshareParking','archiveCommittee','confirmArchiveCommittee','publishAnnouncement','publishNotice','keepNoticeDraft','ackNotice','createContribution','simulateRenewal','castVote','closePoll','confirmClosePoll']){  const fn=ACTIONS[name];ACTIONS[name]=(...args)=>{requireOnline();return fn(...args);}; } — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:969)
## Page index

| # | Group | Page | Roles | Linked handlers |
|---:|---|---|---|---:|
| 1 | Overview | [Home (home)](#requirement-1) | Admin/Treasurer/Resident | 1 |
| 2 | Finance | [Money (money)](#requirement-2) | Admin/Treasurer | 2 |
| 3 | Finance | [Maintenance (dues)](#requirement-3) | Admin/Treasurer/Resident | 2 |
| 4 | Finance | [Maintenance bill (bill)](#requirement-4) | Admin/Treasurer/Resident | 0 |
| 5 | Finance | [Payment details (payee)](#requirement-5) | Admin/Treasurer/Resident | 1 |
| 6 | Finance | [Payment details (mark-paid)](#requirement-6) | Admin/Treasurer/Resident | 1 |
| 7 | Finance | [Payment status (payment-status)](#requirement-7) | Admin/Treasurer/Resident | 0 |
| 8 | Finance | [Payment approvals (approvals)](#requirement-8) | Admin/Treasurer | 0 |
| 9 | Finance | [Review payment (payment)](#requirement-9) | Admin/Treasurer | 2 |
| 10 | Finance | [Payment proof (proof)](#requirement-10) | Admin/Treasurer/Resident | 1 |
| 11 | Finance | [Select payments (bulk-approval)](#requirement-11) | Admin/Treasurer | 2 |
| 12 | Finance | [Confirm approval (bulk-review)](#requirement-12) | Admin/Treasurer | 1 |
| 13 | Finance | [Reject payment (payment-reject)](#requirement-13) | Admin/Treasurer | 1 |
| 14 | Finance | [Payment receipt (receipt)](#requirement-14) | Admin/Treasurer/Resident | 2 |
| 15 | Finance | [Payment history (history)](#requirement-15) | Admin/Treasurer/Resident | 0 |
| 16 | Finance | [Unpaid flats (outstanding)](#requirement-16) | Admin/Treasurer | 2 |
| 17 | Finance | [Send reminders (reminder-preview)](#requirement-17) | Admin/Treasurer | 1 |
| 18 | Finance | [Maintenance amount (billing-settings)](#requirement-18) | Admin/Treasurer | 1 |
| 19 | Finance | [Generate monthly dues (bill-preview)](#requirement-19) | Admin/Treasurer | 3 |
| 20 | Finance | [Expenses (expenses)](#requirement-20) | Admin/Treasurer/Resident | 2 |
| 21 | Finance | [Expense detail (expense)](#requirement-21) | Admin/Treasurer/Resident | 0 |
| 22 | Finance | [Expense attachment (expense-proof)](#requirement-22) | Admin/Treasurer/Resident | 1 |
| 23 | Finance | [Expense (expense-form)](#requirement-23) | Admin/Treasurer | 1 |
| 24 | Finance | [Recurring expenses (recurring)](#requirement-24) | Admin/Treasurer | 0 |
| 25 | Finance | [Recurring expense (recurring-detail)](#requirement-25) | Admin/Treasurer | 2 |
| 26 | Finance | [Recurring setup (recurring-form)](#requirement-26) | Admin/Treasurer | 1 |
| 27 | Reports | [Monthly summary (reports)](#requirement-27) | Admin/Treasurer/Resident | 2 |
| 28 | Reports | [Expense breakdown (expense-report)](#requirement-28) | Admin/Treasurer/Resident | 2 |
| 29 | Reports | [Collection report (collection-report)](#requirement-29) | Admin/Treasurer | 3 |
| 30 | Reports | [Resident finance view (transparency)](#requirement-30) | Admin/Treasurer | 0 |
| 31 | Reports | [Export report (export-report)](#requirement-31) | Admin/Treasurer/Resident | 0 |
| 32 | Finance | [Other income (income)](#requirement-32) | Admin/Treasurer | 2 |
| 33 | Finance | [Income entry (income-form)](#requirement-33) | Admin/Treasurer | 1 |
| 34 | Reports | [Cashbook (cashbook)](#requirement-34) | Admin/Treasurer | 3 |
| 35 | Settings | [Financial controls (finance-settings)](#requirement-35) | Admin/Treasurer | 0 |
| 36 | Settings | [Association account (payment-settings)](#requirement-36) | Admin/Treasurer | 1 |
| 37 | Settings | [Opening balance (opening-balance)](#requirement-37) | Admin | 1 |
| 38 | Services | [Building issues (issues)](#requirement-38) | Admin/Treasurer/Resident | 1 |
| 39 | Services | [What needs attention? (issue-categories)](#requirement-39) | Admin/Treasurer/Resident | 0 |
| 40 | Services | [Report an issue (issue-form)](#requirement-40) | Admin/Treasurer/Resident | 1 |
| 41 | Services | [Similar issues (similar-issues)](#requirement-41) | Admin/Treasurer/Resident | 1 |
| 42 | Services | [Issue detail (issue)](#requirement-42) | Admin/Treasurer/Resident | 3 |
| 43 | Services | [Affected flats (affected)](#requirement-43) | Admin/Treasurer | 0 |
| 44 | Services | [Assign service (issue-assign)](#requirement-44) | Admin/Treasurer | 1 |
| 45 | Services | [Update issue (issue-update)](#requirement-45) | Admin/Treasurer | 1 |
| 46 | Services | [Issue updates (issue-comments)](#requirement-46) | Admin/Treasurer/Resident | 1 |
| 47 | Services | [Issue photo (issue-photo)](#requirement-47) | Admin/Treasurer/Resident | 0 |
| 48 | Services | [My requests (complaints)](#requirement-48) | Admin/Treasurer/Resident | 0 |
| 49 | Services | [Private request (complaint-form)](#requirement-49) | Admin/Treasurer/Resident | 1 |
| 50 | Services | [Request detail (complaint)](#requirement-50) | Admin/Treasurer/Resident | 3 |
| 51 | Services | [Manage request (complaint-update)](#requirement-51) | Admin/Treasurer | 1 |
| 52 | Services | [Service schedule (services)](#requirement-52) | Admin/Treasurer/Resident | 0 |
| 53 | Services | [Scheduled service (service)](#requirement-53) | Admin/Treasurer/Resident | 0 |
| 54 | Services | [Schedule service (service-form)](#requirement-54) | Admin/Treasurer | 1 |
| 55 | Services | [Complete service (service-complete)](#requirement-55) | Admin/Treasurer | 1 |
| 56 | Services | [Service history (service-history)](#requirement-56) | Admin/Treasurer/Resident | 0 |
| 57 | Community | [Community (community)](#requirement-57) | Admin/Treasurer/Resident | 0 |
| 58 | Community | [Notice board (notices)](#requirement-58) | Admin/Treasurer/Resident | 0 |
| 59 | Community | [Notice (notice)](#requirement-59) | Admin/Treasurer/Resident | 4 |
| 60 | Community | [Send notice (notice-form)](#requirement-60) | Admin/Treasurer | 1 |
| 61 | Community | [Notice attachment (notice-attachment)](#requirement-61) | Admin/Treasurer/Resident | 0 |
| 62 | Community | [Service vendors (vendors)](#requirement-62) | Admin/Treasurer/Resident | 0 |
| 63 | Community | [Vendor profile (vendor)](#requirement-63) | Admin/Treasurer/Resident | 4 |
| 64 | Community | [Vendor details (vendor-form)](#requirement-64) | Admin/Treasurer | 1 |
| 65 | Community | [Important contacts (contacts)](#requirement-65) | Admin/Treasurer/Resident | 0 |
| 66 | Community | [Contact details (contact)](#requirement-66) | Admin/Treasurer/Resident | 4 |
| 67 | Community | [Edit contact (contact-form)](#requirement-67) | Admin/Treasurer | 1 |
| 68 | Community | [Apartment files (documents)](#requirement-68) | Admin/Treasurer/Resident | 0 |
| 69 | Community | [Document (document)](#requirement-69) | Admin/Treasurer/Resident | 4 |
| 70 | Community | [Upload document (document-form)](#requirement-70) | Admin/Treasurer | 1 |
| 71 | Onboarding | [ApartmentCare (welcome)](#requirement-71) | Admin/Treasurer/Resident | 0 |
| 72 | Onboarding | [Choose login (login)](#requirement-72) | Admin/Treasurer/Resident | 0 |
| 73 | Onboarding | [Admin login (admin-login)](#requirement-73) | Admin/Treasurer/Resident | 2 |
| 74 | Onboarding | [Treasurer login (treasurer-login)](#requirement-74) | Admin/Treasurer/Resident | 2 |
| 75 | Onboarding | [Resident login (resident-login)](#requirement-75) | Admin/Treasurer/Resident | 2 |
| 76 | Onboarding | [Reset PIN (forgot-pin)](#requirement-76) | Admin/Treasurer/Resident | 1 |
| 77 | Onboarding | [Verify number (verify-reset)](#requirement-77) | Admin/Treasurer/Resident | 1 |
| 78 | Onboarding | [Set new PIN (reset-pin)](#requirement-78) | Admin/Treasurer/Resident | 1 |
| 79 | Onboarding | [Join your apartment (join)](#requirement-79) | Admin/Treasurer/Resident | 1 |
| 80 | Onboarding | [Invite unavailable (invite-invalid)](#requirement-80) | Admin/Treasurer/Resident | 0 |
| 81 | Onboarding | [Create your account (signup)](#requirement-81) | Admin/Treasurer/Resident | 1 |
| 82 | Onboarding | [Choose your flat (choose-flat)](#requirement-82) | Admin/Treasurer/Resident | 2 |
| 83 | Onboarding | [Join request (signup-pending)](#requirement-83) | Admin/Treasurer/Resident | 2 |
| 84 | Onboarding | [Create apartment (admin-signup)](#requirement-84) | Admin/Treasurer/Resident | 1 |
| 85 | Onboarding | [Apartment details (setup-apartment)](#requirement-85) | Admin/Treasurer/Resident | 1 |
| 86 | Onboarding | [Apartment ready (setup-done)](#requirement-86) | Admin/Treasurer/Resident | 0 |
| 87 | Administration | [Apartment (apartment)](#requirement-87) | Admin | 0 |
| 88 | Administration | [Apartment profile (apartment-edit)](#requirement-88) | Admin | 1 |
| 89 | Administration | [Blocks (blocks)](#requirement-89) | Admin | 0 |
| 90 | Administration | [Block details (block-form)](#requirement-90) | Admin | 1 |
| 91 | Administration | [Flats (flats)](#requirement-91) | Admin | 0 |
| 92 | Administration | [Flat details (flat)](#requirement-92) | Admin | 0 |
| 93 | Administration | [Flat setup (flat-form)](#requirement-93) | Admin | 1 |
| 94 | Administration | [Members (residents)](#requirement-94) | Admin | 0 |
| 95 | Administration | [Resident profile (resident)](#requirement-95) | Admin | 0 |
| 96 | Administration | [Join requests (join-requests)](#requirement-96) | Admin | 0 |
| 97 | Administration | [Review resident (join-review)](#requirement-97) | Admin | 2 |
| 98 | Administration | [Reject join request (join-reject)](#requirement-98) | Admin | 1 |
| 99 | Administration | [Invite residents (invite)](#requirement-99) | Admin | 2 |
| 100 | Administration | [Invite controls (invite-settings)](#requirement-100) | Admin | 5 |
| 101 | Administration | [Resident handover (handover)](#requirement-101) | Admin | 1 |
| 102 | Administration | [Team access (roles)](#requirement-102) | Admin | 0 |
| 103 | Administration | [Team role (role-form)](#requirement-103) | Admin | 1 |
| 104 | Settings | [Permissions (permissions)](#requirement-104) | Admin/Treasurer/Resident | 0 |
| 105 | Settings | [More (more)](#requirement-105) | Admin/Treasurer/Resident | 3 |
| 106 | Settings | [My profile (profile)](#requirement-106) | Admin/Treasurer/Resident | 0 |
| 107 | Settings | [Edit profile (profile-edit)](#requirement-107) | Admin/Treasurer/Resident | 1 |
| 108 | Settings | [Notifications (notifications)](#requirement-108) | Admin/Treasurer/Resident | 1 |
| 109 | Settings | [Notification (notification)](#requirement-109) | Admin/Treasurer/Resident | 0 |
| 110 | Settings | [Notifications (notification-settings)](#requirement-110) | Admin/Treasurer/Resident | 1 |
| 111 | Settings | [Reminder schedule (reminder-settings)](#requirement-111) | Admin/Treasurer | 1 |
| 112 | Settings | [Appearance (appearance)](#requirement-112) | Admin/Treasurer/Resident | 3 |
| 113 | Settings | [Account security (security)](#requirement-113) | Admin/Treasurer/Resident | 3 |
| 114 | Settings | [Change PIN (change-pin)](#requirement-114) | Admin/Treasurer/Resident | 1 |
| 115 | Settings | [Privacy & access (privacy)](#requirement-115) | Admin/Treasurer/Resident | 0 |
| 116 | Settings | [Automation (automation)](#requirement-116) | Admin/Treasurer | 1 |
| 117 | Settings | [Automation activity (automation-log)](#requirement-117) | Admin/Treasurer | 0 |
| 118 | Settings | [Activity log (audit)](#requirement-118) | Admin/Treasurer | 0 |
| 119 | Settings | [Activity detail (audit-detail)](#requirement-119) | Admin/Treasurer | 0 |
| 120 | Settings | [Subscription & billing (subscription)](#requirement-120) | Admin | 0 |
| 121 | Settings | [Help & about (help)](#requirement-121) | Admin/Treasurer/Resident | 0 |
| 122 | Settings | [Payment status guide (payment-help)](#requirement-122) | Admin/Treasurer/Resident | 0 |
| 123 | Settings | [Get help (support)](#requirement-123) | Admin/Treasurer/Resident | 1 |
| 124 | Settings | [About (about)](#requirement-124) | Admin/Treasurer/Resident | 0 |
| 125 | System states | [You’re offline (offline)](#requirement-125) | Admin/Treasurer/Resident | 1 |
| 126 | System states | [Session expired (session-expired)](#requirement-126) | Admin/Treasurer/Resident | 0 |
| 127 | System states | [Access restricted (access-denied)](#requirement-127) | Admin/Treasurer/Resident | 0 |
| 128 | System states | [Nothing here yet (empty-state)](#requirement-128) | Admin/Treasurer/Resident | 0 |
| 129 | System states | [Something went wrong (error-state)](#requirement-129) | Admin/Treasurer/Resident | 1 |
| 130 | System states | [Page not found (not-found)](#requirement-130) | Admin/Treasurer/Resident | 0 |
| 131 | Reference tools | [Screen library (screen-map)](#requirement-131) | Admin/Treasurer/Resident | 1 |
| 132 | Reference tools | [Preview controls (demo-controls)](#requirement-132) | Admin/Treasurer/Resident | 4 |
| 133 | Services | [Request attachment (complaint-photo)](#requirement-133) | Admin/Treasurer/Resident | 0 |
| 134 | System states | [Loading (loading-state)](#requirement-134) | Admin/Treasurer/Resident | 0 |
| 135 | Community | [Events & bookings (events)](#requirement-135) | Admin/Treasurer/Resident | 0 |
| 136 | Community | [What are you planning? (event-types)](#requirement-136) | Admin/Treasurer/Resident | 0 |
| 137 | Community | [Request an event (event-form)](#requirement-137) | Admin/Treasurer/Resident | 1 |
| 138 | Community | [Choose your parking (event-availability)](#requirement-138) | Admin/Treasurer/Resident | 1 |
| 139 | Community | [Review request (event-review)](#requirement-139) | Admin/Treasurer/Resident | 1 |
| 140 | Community | [Request sent (event-submitted)](#requirement-140) | Admin/Treasurer/Resident | 0 |
| 141 | Community | [Event details (event)](#requirement-141) | Admin/Treasurer/Resident | 0 |
| 142 | Community | [Booking approvals (booking-requests)](#requirement-142) | Admin | 0 |
| 143 | Community | [Review booking (booking-review)](#requirement-143) | Admin | 1 |
| 144 | Community | [Offer an alternative (booking-alternative)](#requirement-144) | Admin | 1 |
| 145 | Community | [Alternative offered (event-alternative)](#requirement-145) | Admin/Treasurer/Resident | 1 |
| 146 | Community | [Reject request (booking-reject)](#requirement-146) | Admin | 1 |
| 147 | Community | [Cancel booking (event-cancel)](#requirement-147) | Admin/Treasurer/Resident | 1 |
| 148 | Community | [Complete event (event-complete)](#requirement-148) | Admin | 1 |
| 149 | Community | [Booking calendar (event-calendar)](#requirement-149) | Admin/Treasurer/Resident | 1 |
| 150 | Community | [Guest parking (parking-availability)](#requirement-150) | Admin/Treasurer/Resident | 1 |
| 151 | Community | [Spaces & parking (resources)](#requirement-151) | Admin | 0 |
| 152 | Community | [Space details (resource)](#requirement-152) | Admin/Treasurer/Resident | 0 |
| 153 | Community | [Configure space (resource-form)](#requirement-153) | Admin | 1 |
| 154 | Community | [Unavailable dates (resource-blocks)](#requirement-154) | Admin | 0 |
| 155 | Community | [Block a space (resource-block-form)](#requirement-155) | Admin | 4 |
| 156 | Community | [Share my parking (parking-share)](#requirement-156) | Resident | 2 |
| 157 | Community | [Booking rules (booking-rules)](#requirement-157) | Admin/Treasurer/Resident | 0 |
| 158 | Community | [Booking settings (booking-rules-edit)](#requirement-158) | Admin | 1 |
| 159 | Community | [Members (members)](#requirement-159) | Admin/Treasurer/Resident | 0 |
| 160 | Community | [Member details (member)](#requirement-160) | Admin/Treasurer/Resident | 0 |
| 161 | Administration | [Add / edit member (member-form)](#requirement-161) | Admin | 1 |
| 162 | Community | [Apartment committee (committee)](#requirement-162) | Admin/Treasurer/Resident | 0 |
| 163 | Community | [Committee member (committee-member)](#requirement-163) | Admin/Treasurer/Resident | 3 |
| 164 | Administration | [Committee role (committee-form)](#requirement-164) | Admin | 1 |
| 165 | Overview | [Approvals (approval-inbox)](#requirement-165) | Admin/Treasurer | 0 |
| 166 | Community | [Choose a notice (notice-templates)](#requirement-166) | Admin/Treasurer | 0 |
| 167 | Community | [Preview notice (notice-preview)](#requirement-167) | Admin/Treasurer | 2 |
| 168 | Community | [Audience & responses (notice-delivery)](#requirement-168) | Admin/Treasurer | 0 |
| 169 | Finance | [Maintenance rate history (rate-history)](#requirement-169) | Admin/Treasurer | 0 |
| 170 | Finance | [Flat-specific rates (rate-overrides)](#requirement-170) | Admin/Treasurer | 0 |
| 171 | Finance | [Set flat rate (rate-override-form)](#requirement-171) | Admin/Treasurer | 1 |
| 172 | Finance | [One-time collections (contributions)](#requirement-172) | Admin/Treasurer | 0 |
| 173 | Finance | [One-time contribution (contribution-form)](#requirement-173) | Admin/Treasurer | 1 |
| 174 | Finance | [Review contribution (contribution-preview)](#requirement-174) | Admin/Treasurer | 1 |
| 175 | Finance | [Contribution details (contribution)](#requirement-175) | Admin/Treasurer | 0 |
| 176 | Finance | [Opening dues by flat (opening-dues)](#requirement-176) | Admin/Treasurer | 0 |
| 177 | Finance | [Add opening due (opening-due-form)](#requirement-177) | Admin/Treasurer | 1 |
| 178 | Settings | [Application charges (subscription-plans)](#requirement-178) | Admin | 0 |
| 179 | Settings | [Subscription invoices (subscription-invoices)](#requirement-179) | Admin | 0 |
| 180 | Settings | [Application invoice (subscription-invoice)](#requirement-180) | Admin | 1 |
| 181 | Settings | [Renewal status (subscription-renewal)](#requirement-181) | Admin | 0 |
| 182 | Settings | [Refer & unlock (referrals)](#requirement-182) | Admin | 1 |
| 183 | Settings | [First-month offer (referral-offer)](#requirement-183) | Admin | 1 |
| 184 | Settings | [Referral progress (referral-progress)](#requirement-184) | Admin | 1 |
| 185 | Settings | [New apartment referral (referral-add)](#requirement-185) | Admin | 1 |
| 186 | Settings | [Referral details (referral-detail)](#requirement-186) | Admin | 0 |
| 187 | Settings | [First month reward (referral-reward)](#requirement-187) | Admin | 0 |
| 188 | Settings | [Referral rules (referral-rules)](#requirement-188) | Admin | 0 |
| 189 | Settings | [Subscription overdue (subscription-overdue)](#requirement-189) | Admin | 0 |
| 190 | Reference tools | [Provider simulation (provider-simulation)](#requirement-190) | Admin | 1 |
| 191 | Reference tools | [Verify activation · demo (activation-simulation)](#requirement-191) | Admin | 1 |
| 192 | Reference tools | [Subscription payment · demo (subscription-payment-simulation)](#requirement-192) | Admin | 1 |
| 193 | Community | [Community polls (polls)](#requirement-193) | Admin/Treasurer/Resident | 0 |
| 194 | Community | [New poll (poll-form)](#requirement-194) | Admin | 1 |
| 195 | Community | [Vote & results (poll)](#requirement-195) | Admin/Treasurer/Resident | 4 |
| 196 | Community | [Vehicles (vehicles)](#requirement-196) | Admin/Treasurer/Resident | 0 |
| 197 | Community | [Vehicle details (vehicle)](#requirement-197) | Admin/Treasurer/Resident | 0 |
| 198 | Community | [My vehicle (vehicle-form)](#requirement-198) | Resident | 1 |
| 199 | Community | [Notify vehicle owner (vehicle-contact)](#requirement-199) | Admin/Treasurer/Resident | 1 |
| 200 | Administration | [Committee handover (committee-handover)](#requirement-200) | Admin | 1 |
| 201 | Reference tools | [Demo clock (preview-clock)](#requirement-201) | Admin/Treasurer | 2 |
| 202 | Reference tools | [Included in this reference (release-coverage)](#requirement-202) | Admin/Treasurer/Resident | 0 |
| 203 | Reference tools | [Refund credit · demo (subscription-refund-simulation)](#requirement-203) | Admin | 1 |

<a id="requirement-1"></a>

## 1. Home — home

Group: Overview. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:226). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'My dues' | navigation/custom | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:232) |
| 'View receipt' | navigation/custom | 'history' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:232) |
| 'Track payment' | navigation/custom | 'payment-status?id='+S.payments.find(p=>p.bill===rb.id&&p.status==='pending').id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:232) |
| "I've paid" | navigation/custom | 'mark-paid?id='+rb.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:232) |
| 'History' | navigation | 'history' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:233) |
| 'Report issue' | navigation | 'issue-categories' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:233) |
| 'My requests' | navigation | 'complaints' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:233) |
| 'Contacts' | navigation | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:233) |
| 'View all' | section navigation | 'issues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:234) |
| ${esc(i.title)} ${esc(i.scope)} · ${esc(i.priority)} priority | inline button | issue?id=${i.id} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245) |
| ${i.affected.includes(S.residentFlat)?'Following ✓':"I'm affected"} | inline button | affected | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245) |
| View update ${I('arrowRight')} | inline button | issue?id=${i.id} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245) |
| 'View all' | section navigation | 'notices' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:235) |
| ${iconBox(catIcon(n.category),catColor(n.category))} ${esc(n.title)} ${dateStr(n.date)} · ${esc(n.audience)} ${I('chevron','chevron')} | inline button | notice?id=${n.id} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:246) |
| 'View all' | section navigation | 'reports' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:236) |
| Outstanding ${iconBox('clock','amber')} ${INR(a.outstanding)} ${(a.flatCount??a.bs.length)-a.paidCount} flats | inline button | outstanding | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:239) |
| Available balance ${iconBox('bank')} ${INR(a.balance)} Bank + cash · demo | inline button | reports | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:239) |
| ${I('checkCircle')} ${pending} payments need your review Verify once. Everything updates. ${I('chevron','chevron')} | inline button | approvals | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:240) |
| 'Dues' | navigation | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:241) |
| 'Expense' | navigation | 'expense-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:241) |
| 'Notice' | navigation | 'notice-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:241) |
| 'Invite' | navigation | 'invite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:241) |
| 'Report' | navigation | 'reports' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:241) |
| 'View all' | section navigation | 'issues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:242) |
| 'View all' | section navigation | 'audit' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:243) |
| 'Payment declarations' | record/navigation row | 'approvals' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:243) |
| 'Lift service' | record/navigation row | 'service?id=SVC-1' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:243) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:184)
- S.role==='Admin' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:184)
- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:230)
- rb — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:231)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- state==='Paid' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:232)
- !rb — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:232)
- state==='In review' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:232)
- issue — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:234)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !i — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- !i.affected.includes(S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- resident — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245)
- i.affected.includes(S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- S.notices.find(n=>visibleNotice(n)&&n.status==='Published') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:235)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.status!=='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='All flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience.startsWith('Block ') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='Unpaid flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='Selected flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- a.billed — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:238)
- pending — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:240)
- isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:241)
- issue — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:242)

### Required validation and role guards

- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Issue not found.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)

### Empty states, confirmations, feedback and dialogs

- toast('You’ll see updates for this issue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- toast('You are already following this issue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- empty('All services running','','checkCircle') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:234)
- empty('No notices yet','','megaphone') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:235)
- empty('All caught up','','checkCircle') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:242)

### Navigation and data effects

Navigation targets/expressions: dues; history; issue?id=${i.id}; notice?id=${n.id}; outstanding; reports; approvals.

Reference state collections accessed: issues, notices, offline, payments, profile, residentFlat, role, staffProfiles. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:affected ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-2"></a>

## 2. Money — money

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| ${iconBox('calendar')} ${t} ${month===m?I('check'):I('chevron')} | inline button | selectMonth | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493) |
| Select month | month selection | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Financial settings' | icon control | 'data-go="finance-settings"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Dues' | navigation | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Approvals' | navigation | 'approvals' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Expense' | navigation | 'expense-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Reports' | navigation | 'reports' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Monthly dues' | record/navigation row | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Payments' | record/navigation row | 'approvals' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Expenses' | record/navigation row | 'expenses' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Recurring expenses' | record/navigation row | 'recurring' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Other income' | record/navigation row | 'income' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Cashbook' | record/navigation row | 'cashbook' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |
| 'Billing settings' | record/navigation row | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:247) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- month===m — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: payments, recurring. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:month ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)), action:selectMonth ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:494)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-3"></a>

## 3. Maintenance — dues

Group: Finance. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:248). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| ${iconBox('calendar')} ${t} ${month===m?I('check'):I('chevron')} | inline button | selectMonth | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493) |
| Select month | month selection | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252) |
| 'Billing settings' | icon control | 'data-go="billing-settings"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252) |
| ['All','Pending','In review','Paid'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252) |
| b.flat | record/navigation row | 'bill?id='+b.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:254) |
| 'Unpaid flats' | navigation/custom | 'outstanding' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252) |
| 'Generate' | navigation/custom | 'bill-preview' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252) |

### Inputs and selectable options

- search('Search flat or resident') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252)

### Conditional states / visibility

- f==='Paid' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:250)
- f==='Pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:250)
- f==='In review' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:250)
- month===m — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- bs.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:254)
- due(b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:254)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- empty('No dues here','Try another month or filter.','receipt') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:252)

### Navigation and data effects

Navigation targets/expressions: outstanding; bill-preview.

Reference state collections accessed: bills, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:month ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)), action:selectMonth ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:494)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-4"></a>

## 4. Maintenance bill — bill

Group: Finance. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| isStaff()?'Record payment':"I've paid" | navigation/custom | 'mark-paid?id='+b.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255) |
| isStaff()?'Review payment':'Track payment' | navigation/custom | (isStaff()?'payment':'payment-status')+'?id='+pending.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255) |
| 'Payment details' | navigation/custom | 'payee' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255) |
| INR(p.amount) | record/navigation row | 'receipt?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !canBill(b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- pending — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255)
- due(b)>0&&!pending — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255)
- approved.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- note('Payment submitted. Receipt follows verification.','', 'clock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:255)

### Navigation and data effects

Navigation targets/expressions: home; payee.

Reference state collections accessed: payments. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-5"></a>

## 5. Payment details — payee

Group: Finance. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:256). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Edit payment details' | navigation/custom | 'payment-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:256) |
| 'Contact treasurer' | navigation/custom | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:256) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Copy UPI ID' | action | 'copyUPI' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:256) |

### Inputs and selectable options

- <textarea id="sharetext" readonly> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Conditional states / visibility

- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:256)
- S.settings.upi — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:256)
- !S.settings.upi — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:507)
- navigator.share — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- e.name==='AbortError' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Required validation and role guards

- fail('No UPI ID has been added.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:507)

### Empty states, confirmations, feedback and dialogs

- note('Confirm the account with your treasurer before paying. No payment is processed by this prototype.','warn') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:256)
- toast('Copied to clipboard') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- sheet('Copy / share','<div class="field"><label for="sharetext">Text</label><textarea id="sharetext" readonly>${esc(text)}</textarea></div><p>Select and copy the text.</p>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Navigation and data effects

Navigation targets/expressions: payment-settings; contacts.

Reference state collections accessed: settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:copyUPI ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:507)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-6"></a>

## 6. Payment details — mark-paid

Group: Finance. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'View bill' | navigation/custom | 'bill?id='+b.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257) |
| 'Submit payment' | submit | 'payment-submit' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257) |

### Inputs and selectable options

- <input type="hidden" name="billId" value="${b.id}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)
- amountField('amount','Amount paid',due(b),'max="${due(b)}"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)
- selectField('mode','Paid using',['UPI','Cash','Bank transfer','Cheque'],'UPI','data-dynamic="payment-mode"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)
- field('ref','UPI / bank reference','','text','maxlength="40" placeholder="Enter reference number"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)
- field('date','Payment date',today,'date','max="${today}" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)
- fileField('Payment proof · optional') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)
- field('note','Note · optional','','textarea','maxlength="300" placeholder="e.g. Cash handed to treasurer"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)
- checkField('paidConfirm','I have already made this payment.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)

### Conditional states / visibility

- !canBill(b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !due(b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)
- billPending(b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- !canBill(b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- billPending(b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- !Number.isFinite(num)\|\|num<=0\|\|num>10000000 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- amount>due(b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- !F.has('paidConfirm') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- !D.date\|\|D.date>today — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- D.mode!=='Cash'&&!ref — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- ref&&S.payments.some(p=>p.ref===ref&&p.mode===D.mode&&p.status!=='rejected') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- !file\|\|!file.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- file.size>2*1024*1024 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- !/^(image\/(png\|jpeg\|gif\|webp)\|application\/pdf\|text\/plain)$/.test(file.type) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- S.settings.proofRequired&&D.mode!=='Cash'&&!proof — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)

### Required validation and role guards

- fail('You cannot submit for this flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- fail('A payment is already in review for this bill.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- fail('Enter an amount between ₹0.01 and ₹1,00,00,000.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- fail('The amount exceeds the current balance.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- fail('Confirm that you have already made the payment.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- fail('Choose a valid payment date.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- fail('Enter the transaction / cheque reference.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- fail('This payment reference has already been submitted.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- fail('Your apartment requires a payment proof file.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- stateHero('checkCircle','Already paid') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)
- stateHero('clock','Already in review','Wait for the existing payment to be reviewed.','amber') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)
- toast('Submitted for verification') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)
- note('Submitting does not mark the bill paid. The treasurer must verify it.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:257)

### Navigation and data effects

Navigation targets/expressions: home; 'payment-status?id='+p.id.

Reference state collections accessed: payments, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:payment-submit ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-7"></a>

## 7. Payment status — payment-status

Group: Finance. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'View receipt' | navigation/custom | 'receipt?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258) |
| 'Resubmit details' | navigation/custom | 'mark-paid?id='+p.bill | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258) |
| 'Back to maintenance' | navigation/custom | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !p\|\|(!isStaff()&&p.flat!==S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- p.status==='approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- p.status==='rejected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No payment to track','','wallet') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258)
- stateHero('checkCircle','Payment approved',INR(p.amount)+' · '+p.flat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258)
- stateHero('x','Payment not approved',p.reason\|\|'Please check your payment details.','red') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258)
- stateHero('clock','Sent for review',INR(p.amount)+' · '+p.flat,'amber') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:258)

### Navigation and data effects

Navigation targets/expressions: dues.

Reference state collections accessed: payments, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-8"></a>

## 8. Payment approvals — approvals

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ${I('checkCircle')}Select multiple | inline button | bulk-approval | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259) |
| ['Pending','Approved','Rejected','All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259) |
| p.flat+' · '+INR(p.amount) | record/navigation row | 'payment?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259) |

### Inputs and selectable options

- search('Search flat or reference') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259)

### Conditional states / visibility

- ps.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259)
- p.mode==='Cash' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- p.status==='pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259)
- p.status==='approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('All caught up','New payment declarations will appear here.','checkCircle') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:259)

### Navigation and data effects

Navigation targets/expressions: bulk-approval.

Reference state collections accessed: payments. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-9"></a>

## 9. Review payment — payment

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'View payment proof' | navigation/custom | 'proof?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260) |
| 'Reject' | navigation/custom | 'payment-reject?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Approve & issue receipt' | submit | 'approve-single' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:502) |
| 'Approve' | action | 'approvePayment' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260) |
| 'Receipt' | navigation/custom | 'receipt?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${p.id}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:502)
- checkField('verified',p.mode==='Cash'?'I confirm the cash was received.':'I matched this payment to the bank record.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:502)

### Conditional states / visibility

- !p — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- p.status==='pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260)
- p.status==='approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260)
- p.note — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260)
- p.proof — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- !p\|\|p.status!=='pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:502)
- !D.verified — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:551)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !p\|\|p.status!=='pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:455)
- !b\|\|p.amount>due(b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:455)
- p.mode==='Cash' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:502)
- p.status==='rejected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:502)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- fail('This payment is no longer pending.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:502)
- fail('Confirm that you verified the payment.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:551)
- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:455)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:455)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('This payment has already been reviewed.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:455)
- fail('This payment exceeds the current outstanding balance.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:455)

### Empty states, confirmations, feedback and dialogs

- empty('No payment selected') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260)
- note('Check your bank record or confirm cash received. A screenshot alone is not verification.','warn','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260)
- sheet('Approve payment','<div class="card mb">${kv('Flat',p.flat)}${kv('Amount',INR(p.amount))}${kv('Mode',p.mode)}</div>'+form('approve-single','<input type="hidden" name="id" value="${p.id}">${checkField('verified',p.mode==='Cash'?'I confirm the cash was received.':'I matched this payment to the bank record.')}','Approve & issue receipt','checkCircle')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:502)
- toast('Payment approved. Receipt created.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:551)
- note(p.reason\|\|'Payment was rejected.','danger') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:260)

### Navigation and data effects

Navigation targets/expressions: 'receipt?id='+D.id.

Reference state collections accessed: offline, payments, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:approvePayment ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:502)), form:approve-single ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:551)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-10"></a>

## 10. Payment proof — proof

Group: Finance. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:261). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Download proof' | action | 'downloadProof' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:261) |
| 'Back to review' | navigation/custom | 'payment?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:261) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !p\|\|(!isStaff()&&p.flat!==S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:261)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- p.proof?.data?.startsWith('data:image') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:261)
- p.proof?.data — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:261)
- !p\|\|(!isStaff()&&p.flat!==S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:508)
- !a?.data — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:461)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:261)

### Required validation and role guards

- fail('Access denied.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:508)

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- note('Private to this flat and the finance team.','','lock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:261)
- toast('No file was attached.','info') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:461)

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:downloadProof ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:508)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-11"></a>

## 11. Select payments — bulk-approval

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:262). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Select all | inline button | selectAllPayments | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:262) |
| 'Review selected' | action | 'reviewBulk' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:262) |

### Inputs and selectable options

- <input type="checkbox" data-select-payment="${p.id}" ${selected.has(p.id)?'checked':''}> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:262)

### Conditional states / visibility

- ps.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:262)
- selected.has(p.id) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:262)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- !selected.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:504)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:504)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- fail('Select at least one payment.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:504)

### Empty states, confirmations, feedback and dialogs

- empty('No pending payments','','checkCircle') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:262)

### Navigation and data effects

Navigation targets/expressions: 'bulk-review'.

Reference state collections accessed: payments. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:selectAllPayments ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:503)), action:reviewBulk ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:504)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-12"></a>

## 12. Confirm approval — bulk-review

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:263). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| p.flat | record/navigation row | 'payment?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:263) |
| 'Approve '+ps.length+' payments' | submit | 'bulk-confirm' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:263) |

### Inputs and selectable options

- checkField('verified','I have verified every selected payment.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:263)

### Conditional states / visibility

- !D.verified — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:552)
- !ids.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:552)
- !p\|\|p.status!=='pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:552)
- balances[p.bill]>due(bill(p.bill)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:552)

### Required validation and role guards

- fail('Confirm verification of every selected payment.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:552)
- fail('No payment selected.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:552)
- fail('A selected payment was already reviewed. Refresh the selection.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:552)
- fail('Selected total exceeds a bill balance.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:552)

### Empty states, confirmations, feedback and dialogs

- toast(ids.length+' payments approved. Receipts ready.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:552)
- note('Only approve payments you have matched to the bank or received in cash.','warn','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:263)

### Navigation and data effects

Navigation targets/expressions: 'approvals'.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:bulk-confirm ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:552)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-13"></a>

## 13. Reject payment — payment-reject

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:264). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Reject payment' | submit | 'reject-payment' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:264) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${p.id}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:264)
- selectField('reasonType','Reason',['Payment not found','Amount mismatch','Duplicate reference','Proof unclear','Other']) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:264)
- field('reason','Message to resident','','textarea','required maxlength="300" placeholder="Explain what needs correcting"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:264)

### Conditional states / visibility

- !p — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:264)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- !p\|\|p.status!=='pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:553)
- !safeText(D.reason) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:553)

### Required validation and role guards

- fail('This payment was already reviewed.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:553)
- fail('Give the resident a reason.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:553)

### Empty states, confirmations, feedback and dialogs

- empty('Payment unavailable') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:264)
- toast('Rejected. Resident can resubmit.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:553)

### Navigation and data effects

Navigation targets/expressions: 'payment?id='+p.id.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:reject-payment ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:553)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-14"></a>

## 14. Payment receipt — receipt

Group: Finance. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:265). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| Print / Save PDF | inline button | dynamic target | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:467) |
| 'Save receipt' | action | 'exportReceipt' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:265) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Share' | action | 'shareReceipt' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:265) |

### Inputs and selectable options

- <textarea id="sharetext" readonly> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Conditional states / visibility

- !p\|\|(!isStaff()&&p.flat!==S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:265)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- p.status!=='approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:265)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- !p\|\|p.status!=='approved'\|\|(!isStaff()&&p.flat!==S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:467)
- content instanceof Blob — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:460)
- !p\|\|p.status!=='approved'\|\|!isStaff()&&p.flat!==S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:509)
- navigator.share — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- e.name==='AbortError' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Required validation and role guards

- fail('Receipt is unavailable.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:467)
- fail('Receipt unavailable.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:509)

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- empty('Receipt not available','The payment must be approved first.','clock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:265)
- toast('Copied to clipboard') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- sheet('Copy / share','<div class="field"><label for="sharetext">Text</label><textarea id="sharetext" readonly>${esc(text)}</textarea></div><p>Select and copy the text.</p>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- note('Issued after manual verification. Not a payment gateway receipt.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:265)

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: apartment, payments, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:exportReceipt ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:509)), action:shareReceipt ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:509)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-15"></a>

## 15. Payment history — history

Group: Finance. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:266). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| isStaff()?p.flat+' · '+INR(p.amount):monthName(bill(p.bill)?.month) | record/navigation row | (p.status==='approved'?'receipt':isStaff()?'payment':'payment-status')+'?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:266) |

### Inputs and selectable options

- search('Search payments') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:266)

### Conditional states / visibility

- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:266)
- Q.flat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:266)
- ps.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:266)
- p.status==='approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:266)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- p.status==='pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:266)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No payment history','','receipt') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:266)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: payments, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-16"></a>

## 16. Unpaid flats — outstanding

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:267). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| ${iconBox('calendar')} ${t} ${month===m?I('check'):I('chevron')} | inline button | selectMonth | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493) |
| Select month | month selection | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:267) |
| 'Send reminders' | navigation/custom | 'reminder-preview' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:267) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- month===m — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- bs.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:267)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- empty('Everyone is up to date','','checkCircle') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:267)

### Navigation and data effects

Navigation targets/expressions: reminder-preview.

Reference state collections accessed: bills. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:month ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)), action:selectMonth ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:494)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-17"></a>

## 17. Send reminders — reminder-preview

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:268). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Send reminder' | action | 'sendReminders' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:268) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !S.settings.reminders — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:506)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:506)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:506)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Enable maintenance reminders in notification settings first.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:506)

### Empty states, confirmations, feedback and dialogs

- toast(count+' demo reminders added to inbox') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:506)
- note('Prototype: saved in the notification inbox. No external push or SMS is sent.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:268)

### Navigation and data effects

Navigation targets/expressions: 'outstanding'.

Reference state collections accessed: bills, offline, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:sendReminders ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:506)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-18"></a>

## 18. Maintenance amount — billing-settings

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save maintenance' | submit | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817) |
| 'Include vacant flats' | preference toggle | 'billVacant' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817) |
| 'Late fee' | preference toggle | 'lateEnabled' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817) |
| 'Rate history' | navigation/custom | 'rate-history' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817) |
| 'Flat overrides' | navigation/custom | 'rate-overrides' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817) |

### Inputs and selectable options

- amountField('amount','Monthly maintenance per flat',S.settings.amount\|\|'') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817)
- field('effective','Effective from',effective,'month','required min="${effective}"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817)
- field('billingDay','Billing day',S.settings.billingDay,'number','required min="1" max="28"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817)
- field('dueDay','Due day',S.settings.dueDay,'number','required min="1" max="28"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817)
- field('lateFee','One-time late fee',S.settings.lateFee,'number','min="0" step="0.01"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817)
- field('grace','Grace days',S.settings.grace,'number','min="0" max="30"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817)

### Conditional states / visibility

- S.bills.some(b=>b.month===currentMonth&&!b.kind\|\|b.kind==='maintenance') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817)
- S.settings.amount — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- !Number.isFinite(num)\|\|num<=0\|\|num>10000000 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- !Number.isInteger(n)\|\|n<min\|\|n>max — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- !/^\d{4}-\d{2}$/.test(D.effective)\|\|D.effective<currentMonth — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:823)
- S.bills.some(b=>b.month>=D.effective&&(!b.kind\|\|b.kind==='maintenance')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:823)
- !Number.isFinite(fee)\|\|fee<0\|\|!Number.isInteger(grace)\|\|grace<0\|\|grace>30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:823)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:823)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- fail('Enter an amount between ₹0.01 and ₹1,00,00,000.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- fail(label+' must be between '+min+' and '+max+'.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- fail('Choose the current or a future month.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:823)
- fail('Bills already exist for that period. Choose a later effective month.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:823)
- fail('Check late fee and grace days.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:823)

### Empty states, confirmations, feedback and dialogs

- toast('Rate saved for future bills') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:823)
- note('Existing bills and receipts stay unchanged.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:817)

### Navigation and data effects

Navigation targets/expressions: 'rate-history'; rate-history; rate-overrides.

Reference state collections accessed: apartment, billingRules, bills, role, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:billing-settings ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:823)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-19"></a>

## 19. Generate monthly dues — bill-preview

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:821). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| ${iconBox('calendar')} ${t} ${month===m?I('check'):I('chevron')} | inline button | selectMonth | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493) |
| Select month | month selection | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:821) |
| 'Generate missing bills' | action | 'generateBills' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:821) |
| 'Set maintenance' | navigation/custom | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:821) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- month===m — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- r — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:821)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !S.bills.some(b=>b.flat===f.id&&b.month===m) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:456)
- S.settings.dueNotify — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:456)
- count — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:456)
- count — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:505)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:505)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:505)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)

### Empty states, confirmations, feedback and dialogs

- sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- note('Existing flat / month bills are skipped. Other contributions do not replace monthly maintenance.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:821)
- toast(count?count+' bills created':'No duplicates. All bills already exist.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:505)
- empty('Maintenance amount needed','Set the amount and due day first.','receipt',btn('Set maintenance','billing-settings','sliders')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:821)

### Navigation and data effects

Navigation targets/expressions: billing-settings.

Reference state collections accessed: billingRules, bills, flats, offline, rateOverrides, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:month ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)), action:selectMonth ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:494)), action:generateBills ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:505)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-20"></a>

## 20. Expenses — expenses

Group: Finance. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| ${iconBox('calendar')} ${t} ${month===m?I('check'):I('chevron')} | inline button | selectMonth | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493) |
| Select month | month selection | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271) |
| 'Add expense' | icon control | 'data-go="expense-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271) |
| isStaff()?['All','Paid','Drafts']:['All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271) |
| e.title | record/navigation row | 'expense?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271) |
| 'Recurring expenses' | navigation/custom | 'recurring' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271) |

### Inputs and selectable options

- search('Search spending') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271)

### Conditional states / visibility

- !isStaff()&&!S.settings.expensesVisible — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- f==='Paid' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271)
- f==='Drafts' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271)
- month===m — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271)
- es.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271)
- !e.paid — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- empty('No expenses here','','wallet') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:271)

### Navigation and data effects

Navigation targets/expressions: home; recurring.

Reference state collections accessed: expenses, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:month ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)), action:selectMonth ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:494)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-21"></a>

## 21. Expense detail — expense

Group: Finance. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'View bill' | navigation/custom | 'expense-proof?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272) |
| 'Edit' | navigation/custom | 'expense-form?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272) |
| e.paid?'Reverse':'Confirm paid' | action | e.paid?'reverseExpense':'confirmExpense' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !e\|\|(!isStaff()&&(!e.public\|\|!S.settings.expensesVisible)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- e.reversed — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272)
- e.paid — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272)
- e.notes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272)
- e.attachment — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:272)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: expenses, settings, vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-22"></a>

## 22. Expense attachment — expense-proof

Group: Finance. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:273). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Save attachment' | action | 'downloadExpenseAttachment' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:273) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !e\|\|(!isStaff()&&!e.public) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:273)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !a — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- a.data?.startsWith('data:image') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- a.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- !e\|\|!isStaff()&&!e.public — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:533)
- !a?.data — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:461)

### Required validation and role guards

- fail('Access denied.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:533)

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- empty('No attachment','A file has not been added.','file') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- toast('No file was attached.','info') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:461)

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: expenses. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:downloadExpenseAttachment ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:533)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-23"></a>

## 23. Expense — expense-form

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| e.id?'Save changes':'Add expense' | submit | 'expense' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${e.id\|\|''}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)
- amountField('amount','Amount',e.amount\|\|'') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)
- field('title','Expense title',e.title\|\|'','text','required maxlength="80" placeholder="e.g. Water tanker"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)
- selectField('category','Category',['Security','Housekeeping','Electricity','Water','Lift','Plumbing','Repairs','Generator','Pest control','Other'],e.category\|\|Q.category\|\|'Water') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)
- field('date','Date',e.date\|\|today,'date','max="${today}" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)
- selectField('mode','Paid via',['UPI','Cash','Bank transfer','Cheque'],e.mode\|\|'UPI') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)
- selectField('vendor','Vendor',[['','Not linked'],...S.vendors.map(v=>[v.id,v.name])],e.vendor\|\|'') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)
- fileField('Bill · optional') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)
- field('notes','Note · optional',e.notes\|\|'','textarea','maxlength="500"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)
- checkField('paid','Payment completed',e.paid??true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)
- checkField('public','Visible to residents',e.public??true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)

### Conditional states / visibility

- !Number.isFinite(num)\|\|num<=0\|\|num>10000000 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- !file\|\|!file.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- file.size>2*1024*1024 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- !/^(image\/(png\|jpeg\|gif\|webp)\|application\/pdf\|text\/plain)$/.test(file.type) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- F.has('paid')&&D.date>today — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:555)
- existing — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:555)
- existing.paid — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:555)
- e.paid — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:555)
- e.recurring — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:459)
- r&&r.next===e.cycle — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:459)
- e.id — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)

### Required validation and role guards

- fail('Enter an amount between ₹0.01 and ₹1,00,00,000.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- fail('A paid expense cannot have a future payment date.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:555)

### Empty states, confirmations, feedback and dialogs

- toast(e.paid?'Expense saved. Balances updated.':'Draft saved. Balance unchanged.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:555)
- note('A paid expense updates balances immediately. Draft expenses do not.','','report') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:274)

### Navigation and data effects

Navigation targets/expressions: 'expense?id='+e.id.

Reference state collections accessed: expenses, recurring, vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:expense ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:555)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-24"></a>

## 24. Recurring expenses — recurring

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:275). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Add' | section navigation | 'recurring-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:275) |
| r.title | record/navigation row | 'recurring-detail?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:275) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- S.recurring.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:275)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- r.active — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:275)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No recurring expenses','','repeat') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:275)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: recurring. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-25"></a>

## 25. Recurring expense — recurring-detail

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:276). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Edit' | navigation/custom | 'recurring-form?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:276) |
| r.active?'Pause':'Resume' | action | 'toggleRecurring' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:276) |
| 'Prepare expense' | action | 'prepareRecurring' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:276) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !r — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:276)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- r.active — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:276)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- !r.active — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:513)
- r.active — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:513)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- existing — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:458)
- !r.active — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:458)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:513)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:514)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:514)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Resume this recurring expense first.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:458)

### Empty states, confirmations, feedback and dialogs

- empty('No recurring expense') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:276)
- toast(r.active?'Recurring expense resumed':'Recurring expense paused') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:513)
- toast('Draft ready. Confirm only when paid.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:514)
- note('Creates one draft for this cycle. Confirm the payment to post the expense.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:276)

### Navigation and data effects

Navigation targets/expressions: 'expense?id='+e.id.

Reference state collections accessed: expenses, offline, recurring, vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:toggleRecurring ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:513)), action:prepareRecurring ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:514)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-26"></a>

## 26. Recurring setup — recurring-form

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:277). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'recurring' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:277) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${r.id\|\|''}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:277)
- field('title','Title',r.title\|\|'','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:277)
- amountField('amount','Expected amount',r.amount\|\|'') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:277)
- selectField('category','Category',['Security','Housekeeping','Lift','Electricity','Water','Other'],r.category) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:277)
- selectField('frequency','Frequency',['Monthly','Quarterly','Every 6 months','Annual'],r.frequency\|\|'Monthly') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:277)
- field('next','Next due date',r.next\|\|'2026-10-01','date','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:277)
- selectField('vendor','Vendor',[['','Not linked'],...S.vendors.map(v=>[v.id,v.name])],r.vendor\|\|'') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:277)
- checkField('active','Active',r.active??true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:277)

### Conditional states / visibility

- !Number.isFinite(num)\|\|num<=0\|\|num>10000000 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- existing — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:557)

### Required validation and role guards

- fail('Enter an amount between ₹0.01 and ₹1,00,00,000.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)

### Empty states, confirmations, feedback and dialogs

- toast('Recurring expense saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:557)

### Navigation and data effects

Navigation targets/expressions: 'recurring-detail?id='+r.id.

Reference state collections accessed: recurring, vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:recurring ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:557)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-27"></a>

## 27. Monthly summary — reports

Group: Reports. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| Outstanding dues ${INR(a.outstanding)} | inline button | ${isStaff()? | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278) |
| Total spent ${INR(a.spend)} | inline button | expense-report | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| ${iconBox('calendar')} ${t} ${month===m?I('check'):I('chevron')} | inline button | selectMonth | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493) |
| Select month | month selection | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278) |
| 'Export report' | icon control | 'data-go="export-report"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278) |
| 'Expense breakdown' | record/navigation row | 'expense-report' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278) |
| 'Collections by flat' | record/navigation row | 'collection-report' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278) |
| 'Resident view' | record/navigation row | 'transparency' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !isStaff()&&!S.settings.expensesVisible — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- month===m — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- a.billed — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278)
- month!==currentMonth — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- note('Historical sample contains this demo resident’s payments only.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:278)

### Navigation and data effects

Navigation targets/expressions: home; ${isStaff()?; expense-report.

Reference state collections accessed: settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:month ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)), action:selectMonth ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:494)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-28"></a>

## 28. Expense breakdown — expense-report

Group: Reports. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:279). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| ${iconBox('calendar')} ${t} ${month===m?I('check'):I('chevron')} | inline button | selectMonth | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493) |
| Select month | month selection | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:279) |
| 'View expenses' | navigation/custom | 'expenses' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:279) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !isStaff()&&!S.settings.expensesVisible — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:279)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- month===m — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- total — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:279)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- empty('No spending this month') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:279)

### Navigation and data effects

Navigation targets/expressions: home; expenses.

Reference state collections accessed: expenses, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:month ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)), action:selectMonth ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:494)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-29"></a>

## 29. Collection report — collection-report

Group: Reports. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:280). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| ${iconBox('calendar')} ${t} ${month===m?I('check'):I('chevron')} | inline button | selectMonth | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493) |
| Select month | month selection | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:280) |
| 'CSV' | action | 'exportCollections' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:280) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- month===m — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- content instanceof Blob — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:460)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:516)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)

### Empty states, confirmations, feedback and dialogs

- sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- empty('No bills this month') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:280)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:month ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)), action:selectMonth ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:494)), action:exportCollections ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:516)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-30"></a>

## 30. Resident finance view — transparency

Group: Reports. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:281). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Resident transparency' | preference toggle | 'expensesVisible' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:281) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- note('Preview: residents see totals, not other flats’ payment proofs.','','eye') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:281)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-31"></a>

## 31. Export report — export-report

Group: Reports. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:282). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Download CSV' | record/navigation row | '' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:282) |
| 'Print / Save PDF' | record/navigation row | '' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:282) |
| 'Share summary' | record/navigation row | '' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:282) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !isStaff()&&!S.settings.expensesVisible — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:282)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: apartment, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-32"></a>

## 32. Other income — income

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:283). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| ${iconBox('calendar')} ${t} ${month===m?I('check'):I('chevron')} | inline button | selectMonth | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493) |
| Select month | month selection | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:283) |
| 'Add income' | icon control | 'data-go="income-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:283) |
| e.title | record/navigation row | 'income-form?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:283) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- month===m — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- empty('No other income','','plus') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:283)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: income. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:month ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)), action:selectMonth ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:494)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-33"></a>

## 33. Income entry — income-form

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:284). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'income' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:284) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${e.id\|\|''}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:284)
- amountField('amount','Amount received',e.amount\|\|'') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:284)
- field('title','Description',e.title\|\|'','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:284)
- field('date','Received on',e.date\|\|today,'date','required max="${today}"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:284)
- selectField('mode','Mode',['UPI','Cash','Bank transfer','Cheque'],e.mode\|\|'UPI') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:284)

### Conditional states / visibility

- !Number.isFinite(num)\|\|num<=0\|\|num>10000000 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- e.date>today — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:558)
- existing — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:558)

### Required validation and role guards

- fail('Enter an amount between ₹0.01 and ₹1,00,00,000.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- fail('Choose a date on or before today.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:558)

### Empty states, confirmations, feedback and dialogs

- toast('Income saved. Balance updated.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:558)

### Navigation and data effects

Navigation targets/expressions: 'income'.

Reference state collections accessed: income. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:income ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:558)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-34"></a>

## 34. Cashbook — cashbook

Group: Reports. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:285). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| ${iconBox('calendar')} ${t} ${month===m?I('check'):I('chevron')} | inline button | selectMonth | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493) |
| Select month | month selection | month | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:285) |
| 'CSV' | action | 'exportCashbook' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:285) |
| e.name | record/navigation row | e.to | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:285) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- month===m — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- content instanceof Blob — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:460)
- e.type==='in' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:285)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:517)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)

### Empty states, confirmations, feedback and dialogs

- sheet('Select month','<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>'<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>').join('')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)
- empty('No transactions','','clipboard') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:285)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: expenses, income, payments. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:month ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)), action:selectMonth ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:494)), action:exportCashbook ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:517)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-35"></a>

## 35. Financial controls — finance-settings

Group: Settings. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Billing rules' | record/navigation row | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286) |
| 'Payment account' | record/navigation row | 'payment-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286) |
| 'Opening balance' | record/navigation row | 'opening-balance' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286) |
| 'Resident transparency' | record/navigation row | 'transparency' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286) |
| 'Require payment proof' | preference toggle | 'proofRequired' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:286)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-36"></a>

## 36. Association account — payment-settings

Group: Settings. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:287). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'payment-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:287) |

### Inputs and selectable options

- field('payee','Account holder',S.settings.payee,'text','required maxlength="100"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:287)
- field('upi','UPI ID',S.settings.upi,'text','placeholder="association@bank" maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:287)
- field('bank','Bank name',S.settings.bank,'text','maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:287)
- field('account','Account number',S.settings.account,'text','inputmode="numeric" maxlength="24"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:287)
- field('ifsc','IFSC',S.settings.ifsc,'text','maxlength="11"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:287)

### Conditional states / visibility

- D.upi&&!/^[a-zA-Z0-9.\-_]{2,}@[a-zA-Z0-9]{2,}$/.test(D.upi) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:559)
- D.ifsc&&!/^[A-Z]{4}0[A-Z0-9]{6}$/i.test(D.ifsc) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:559)

### Required validation and role guards

- fail('Enter a valid UPI ID.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:559)
- fail('Enter an 11-character IFSC.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:559)

### Empty states, confirmations, feedback and dialogs

- toast('Account details saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:559)
- note('Visible to residents. Double-check before sharing.','warn','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:287)

### Navigation and data effects

Navigation targets/expressions: 'payee'.

Reference state collections accessed: settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:payment-settings ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:559)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-37"></a>

## 37. Opening balance — opening-balance

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:288). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'opening-balance' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:288) |

### Inputs and selectable options

- amountField('opening','Bank + cash balance on 1 Sep',S.apartment.opening) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:288)

### Conditional states / visibility

- !Number.isFinite(n)\|\|n<0 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:560)

### Required validation and role guards

- fail('Enter a valid opening balance.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:560)

### Empty states, confirmations, feedback and dialogs

- toast('Opening balance updated') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:560)
- note('Opening balance is separate from maintenance collections. Changes are audited.','warn') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:288)

### Navigation and data effects

Navigation targets/expressions: 'reports'.

Reference state collections accessed: apartment. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:opening-balance ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:560)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-38"></a>

## 38. Building issues — issues

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Report' | navigation | 'issue-categories' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290) |
| 'My requests' | navigation | 'complaints' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290) |
| 'Services' | navigation | 'services' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290) |
| 'Contacts' | navigation | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290) |
| ['Active','Following','Resolved','All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290) |
| ${esc(i.title)} ${esc(i.scope)} · ${esc(i.priority)} priority | inline button | issue?id=${i.id} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245) |
| ${i.affected.includes(S.residentFlat)?'Following ✓':"I'm affected"} | inline button | affected | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245) |
| View update ${I('arrowRight')} | inline button | issue?id=${i.id} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245) |
| 'Report issue' | navigation/custom | 'issue-categories' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290) |

### Inputs and selectable options

- search('Search building issues') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290)

### Conditional states / visibility

- f==='Active' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290)
- f==='Resolved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290)
- f==='Following' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290)
- items.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !i — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- !i.affected.includes(S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- resident — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245)
- i.affected.includes(S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Issue not found.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)

### Empty states, confirmations, feedback and dialogs

- toast('You’ll see updates for this issue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- toast('You are already following this issue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- empty('No issues here','Report a problem or try another filter.','checkCircle',btn('Report issue','issue-categories','plus','secondary full')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:290)

### Navigation and data effects

Navigation targets/expressions: issue?id=${i.id}; issue-categories.

Reference state collections accessed: issues, offline, residentFlat, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:affected ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-39"></a>

## 39. What needs attention? — issue-categories

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:291). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ${iconBox(i,col)} ${esc(c)} | inline button | issue-form?category=${encodeURIComponent(c)} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:291) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- note('For immediate danger, contact local emergency services. This app is not an emergency response service.','warn','alert') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:291)

### Navigation and data effects

Navigation targets/expressions: issue-form?category=${encodeURIComponent(c)}.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-40"></a>

## 40. Report an issue — issue-form

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ${I('link')} ${similar.length} similar issue already open Join it instead of reporting twice ${I('chevron')} | inline button | similar-issues?category=${encodeURIComponent(cat)} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292) |
| 'Change' | navigation/custom | 'issue-categories' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292) |
| 'Report issue' | submit | 'issue-create' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292) |

### Inputs and selectable options

- <input type="hidden" name="category" value="${esc(cat)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292)
- field('title','Problem','','text','required maxlength="80" placeholder="e.g. No water on second floor"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292)
- selectField('scope','Affected area',['Whole apartment',...S.apartment.blocks.map(b=>'Block '+b)],'Whole apartment') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292)
- selectField('priority','Priority',['Normal','High','Urgent'],'Normal') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292)
- field('description','Details','','textarea','required maxlength="1000" placeholder="Where and since when?"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292)
- fileField('Photo · optional') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292)

### Conditional states / visibility

- similar.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292)
- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:561)
- !file\|\|!file.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- file.size>2*1024*1024 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- !/^(image\/(png\|jpeg\|gif\|webp)\|application\/pdf\|text\/plain)$/.test(file.type) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- toast('Issue reported') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:561)
- note('Only report shared-area issues here. Use My requests for private flat repairs.','','users') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:292)

### Navigation and data effects

Navigation targets/expressions: similar-issues?category=${encodeURIComponent(cat)}; issue-categories; 'issue?id='+i.id.

Reference state collections accessed: apartment, issues, residentFlat, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:issue-create ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:561)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-41"></a>

## 41. Similar issues — similar-issues

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:293). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ${esc(i.title)} ${esc(i.scope)} · ${esc(i.priority)} priority | inline button | issue?id=${i.id} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245) |
| ${i.affected.includes(S.residentFlat)?'Following ✓':"I'm affected"} | inline button | affected | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245) |
| View update ${I('arrowRight')} | inline button | issue?id=${i.id} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245) |
| 'Different problem' | navigation/custom | 'issue-form?category='+encodeURIComponent(Q.category\|\|'Water') | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:293) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !i — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- !i.affected.includes(S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- resident — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245)
- i.affected.includes(S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:245)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Issue not found.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)

### Empty states, confirmations, feedback and dialogs

- toast('You’ll see updates for this issue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- toast('You are already following this issue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- empty('No similar issue') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:293)

### Navigation and data effects

Navigation targets/expressions: issue?id=${i.id}.

Reference state collections accessed: issues, offline, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:affected ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-42"></a>

## 42. Issue detail — issue

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| View ${I('chevron')} | inline button | affected?id=${i.id} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| ${i.affected.includes(S.residentFlat)?'Following ✓':"I'm affected"} | inline button | affected | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| v?.name\|\|'Not assigned' | record/navigation row | isStaff()?'issue-assign?id='+i.id:v?'vendor?id='+v.id:'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Assign' | navigation/custom | 'issue-assign?id='+i.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Update status' | navigation/custom | 'issue-update?id='+i.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Still not fixed' | action | 'reopenIssue' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Close issue' | action | 'closeIssue' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Open' | section navigation | 'issue-comments?id='+i.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Add an update' | navigation/custom | 'issue-comments?id='+i.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |
| 'Photo' | navigation/custom | 'issue-photo?id='+i.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !i — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !i — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- !i.affected.includes(S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294)
- i.affected.includes(S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294)
- i.priority==='Urgent' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294)
- i.eta — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294)
- v — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294)
- i.status==='Resolved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294)
- i.status!=='Resolved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:521)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- i.comments.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294)
- i.attachment — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294)

### Required validation and role guards

- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Issue not found.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:521)
- fail('Only resolved issues can be reopened.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:521)
- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:522)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)

### Empty states, confirmations, feedback and dialogs

- empty('Issue not found') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294)
- toast('You’ll see updates for this issue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- toast('You are already following this issue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)
- note('Urgent reports need a phone call as well. Do not wait for an app reply.','danger','phone') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:294)
- toast('Issue reopened') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:521)

### Navigation and data effects

Navigation targets/expressions: affected?id=${i.id}.

Reference state collections accessed: issues, offline, residentFlat, role, vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:affected ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)), action:reopenIssue ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:521)), action:closeIssue ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:522)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-43"></a>

## 43. Affected flats — affected

Group: Services. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:295). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| f | record/navigation row | 'flat?id='+f | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:295) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: issues. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-44"></a>

## 44. Assign service — issue-assign

Group: Services. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:296). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Assign vendor' | submit | 'issue-assign' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:296) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${i.id}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:296)
- selectField('vendor','Vendor',[['','Choose vendor'],...S.vendors.map(v=>[v.id,v.name])],i.vendor\|\|'','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:296)
- field('eta','Expected restoration',i.eta\|\|'','datetime-local') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:296)
- field('note','Update to residents','','textarea','maxlength="500" placeholder="Technician will arrive soon"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:296)
- checkField('notify','Notify affected residents',true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:296)

### Conditional states / visibility

- !i — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:562)
- D.note — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:562)
- D.notify — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:562)

### Required validation and role guards

- fail('Issue no longer exists.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:562)

### Empty states, confirmations, feedback and dialogs

- toast('Vendor assigned') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:562)

### Navigation and data effects

Navigation targets/expressions: 'issue?id='+i.id.

Reference state collections accessed: issues, role, vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:issue-assign ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:562)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-45"></a>

## 45. Update issue — issue-update

Group: Services. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:297). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save update' | submit | 'issue-update' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:297) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${i.id}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:297)
- selectField('status','Status',['Open','Assigned','In progress','Resolved','Closed'],i.status) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:297)
- field('note','Resolution / update','','textarea','required maxlength="500" placeholder="What changed?"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:297)
- field('eta','Expected restoration',i.eta\|\|'','datetime-local') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:297)
- checkField('notify','Notify affected residents',true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:297)

### Conditional states / visibility

- D.notify — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:563)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- toast('Status updated') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:563)

### Navigation and data effects

Navigation targets/expressions: 'issue?id='+i.id.

Reference state collections accessed: issues, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:issue-update ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:563)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-46"></a>

## 46. Issue updates — issue-comments

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:298). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Post update' | submit | 'issue-comment' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:298) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${i.id}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:298)
- field('comment','Add update','','textarea','required maxlength="500" placeholder="Write a short update"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:298)

### Conditional states / visibility

- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- !i — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:564)
- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:564)

### Required validation and role guards

- fail('Issue no longer exists.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:564)

### Empty states, confirmations, feedback and dialogs

- empty('No updates yet','','message') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:298)
- toast('Update posted') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:564)

### Navigation and data effects

Navigation targets/expressions: 'issue-comments?id='+i.id.

Reference state collections accessed: issues, residentFlat, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:issue-comment ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:564)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-47"></a>

## 47. Issue photo — issue-photo

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:299). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Shared or dynamic controls only | Inspect page definition | No direct control definition | Page link above |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !a — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- a.data?.startsWith('data:image') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- a.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No attachment','A file has not been added.','file') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: issues. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-48"></a>

## 48. My requests — complaints

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'New request' | icon control | 'data-go="complaint-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300) |
| ['Active','Closed','All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300) |
| c.title | record/navigation row | 'complaint?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300) |
| 'New request' | navigation/custom | 'complaint-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300) |

### Inputs and selectable options

- search('Search requests') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300)

### Conditional states / visibility

- f==='Active' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300)
- f==='Closed' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No requests here','','ticket',btn('New request','complaint-form','plus','secondary full')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:300)

### Navigation and data effects

Navigation targets/expressions: complaint-form.

Reference state collections accessed: complaints, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-49"></a>

## 49. Private request — complaint-form

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Create request' | submit | 'complaint-create' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301) |

### Inputs and selectable options

- <input type="hidden" name="flat" value="${S.residentFlat}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301)
- selectField('flat','Flat',S.flats.map(f=>f.id),S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301)
- field('title','Request title','','text','required maxlength="80" placeholder="e.g. Bathroom pipe leakage"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301)
- selectField('category','Category',categories.map(c=>c[0]),'Plumbing') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301)
- selectField('priority','Priority',['Normal','High'],'Normal') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301)
- field('description','Details','','textarea','required maxlength="1000"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301)
- fileField('Photo · optional') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301)

### Conditional states / visibility

- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:565)
- !flat(f) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:565)
- !file\|\|!file.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- file.size>2*1024*1024 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- !/^(image\/(png\|jpeg\|gif\|webp)\|application\/pdf\|text\/plain)$/.test(file.type) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301)

### Required validation and role guards

- fail('Choose a valid flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:565)

### Empty states, confirmations, feedback and dialogs

- toast('Request created') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:565)
- note('Visible only to your flat and the apartment team.','','lock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:301)

### Navigation and data effects

Navigation targets/expressions: 'complaint?id='+c.id.

Reference state collections accessed: complaints, flats, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:complaint-create ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:565)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-50"></a>

## 50. Request detail — complaint

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Update request' | navigation/custom | 'complaint-update?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302) |
| 'Reopen' | action | 'reopenComplaint' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302) |
| 'Confirm fixed' | action | 'closeComplaint' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302) |
| 'View attachment' | navigation/custom | 'complaint-photo?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302) |
| 'Send' | submit | 'complaint-comment' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${c.id}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302)
- field('comment','Add a comment','','textarea','required maxlength="500"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302)

### Conditional states / visibility

- !c\|\|(!isStaff()&&c.flat!==S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302)
- c.status==='Resolved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302)
- !c\|\|!isStaff()&&c.flat!==S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:523)
- !c\|\|!isStaff()&&c.flat!==S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:524)
- c.attachment — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:302)
- !c\|\|!isStaff()&&c.flat!==S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:566)
- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:566)

### Required validation and role guards

- fail('Access denied.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:523)
- fail('Access denied.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:524)
- fail('Access denied.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:566)

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- toast('Request reopened') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:523)
- toast('Marked as fixed') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:524)
- toast('Comment saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:566)

### Navigation and data effects

Navigation targets/expressions: home; 'complaint?id='+c.id.

Reference state collections accessed: complaints, residentFlat, role, vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:reopenComplaint ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:523)), action:closeComplaint ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:524)), form:complaint-comment ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:566)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-51"></a>

## 51. Manage request — complaint-update

Group: Services. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:303). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Update request' | submit | 'complaint-update' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:303) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${c.id}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:303)
- selectField('status','Status',['Open','Assigned','In progress','Resolved','Closed'],c.status) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:303)
- selectField('vendor','Assign vendor',[['','Not assigned'],...S.vendors.map(v=>[v.id,v.name])],c.vendor\|\|'') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:303)
- field('note','Update','','textarea','required maxlength="500"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:303)

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- toast('Request updated') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:567)

### Navigation and data effects

Navigation targets/expressions: 'complaint?id='+c.id.

Reference state collections accessed: complaints, role, vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:complaint-update ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:567)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-52"></a>

## 52. Service schedule — services

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:304). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| isStaff()?'Add':'' | section navigation | isStaff()?'service-form':'' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:304) |
| s.title | record/navigation row | 'service?id='+s.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:304) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:304)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- new Date(s.next)-new Date(today)<7*86400000 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:304)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No services scheduled','','calendar') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:304)
- note('Intervals are configurable examples. Follow your equipment provider’s actual service schedule.','','calendar') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:304)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: services. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-53"></a>

## 53. Scheduled service — service

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Edit' | navigation/custom | 'service-form?id='+s.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305) |
| 'Complete' | navigation/custom | 'service-complete?id='+s.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305) |
| 'View' | section navigation | 'service-history?id='+s.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305) |
| dateStr(h.date) | record/navigation row | 'service-history?id='+s.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305)
- s.history.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('Service not found') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305)
- empty('No completed service yet','','history') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:305)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: services, vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-54"></a>

## 54. Schedule service — service-form

Group: Services. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:306). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save schedule' | submit | 'service' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:306) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${s.id\|\|''}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:306)
- field('title','Service name',s.title\|\|'','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:306)
- selectField('category','Category',categories.map(c=>c[0]),s.category\|\|'Lift') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:306)
- selectField('frequency','Repeat',['Monthly','Quarterly','Every 6 months','Annual'],s.frequency\|\|'Monthly') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:306)
- field('next','Next service date',s.next\|\|today,'date','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:306)
- selectField('vendor','Vendor',[['','Not assigned'],...S.vendors.map(v=>[v.id,v.name])],s.vendor\|\|'') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:306)
- field('cost','Expected cost (₹)',s.cost\|\|0,'number','min="0" step="0.01"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:306)

### Conditional states / visibility

- old — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:568)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- toast('Schedule saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:568)

### Navigation and data effects

Navigation targets/expressions: 'service?id='+s.id.

Reference state collections accessed: services, vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:service ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:568)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-55"></a>

## 55. Complete service — service-complete

Group: Services. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:307). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Complete service' | submit | 'service-complete' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:307) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${s.id}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:307)
- field('date','Completed on',today,'date','required max="${today}"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:307)
- field('cost','Service cost (₹)',s.cost,'number','min="0" step="0.01" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:307)
- field('note','Work completed','','textarea','required maxlength="500" placeholder="Service notes"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:307)
- fileField('Service report / bill · optional') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:307)
- checkField('expense','Create an expense entry',true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:307)
- checkField('paid','Service cost already paid',false) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:307)

### Conditional states / visibility

- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)
- D.date>today — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)
- s.history.some(h=>h.date===D.date&&h.cycle===s.next) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)
- cost<0\|\|!Number.isFinite(cost) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)
- !file\|\|!file.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- file.size>2*1024*1024 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- !/^(image\/(png\|jpeg\|gif\|webp)\|application\/pdf\|text\/plain)$/.test(file.type) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- F.has('expense')&&cost>0 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)
- e.paid — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)

### Required validation and role guards

- fail('Service not found.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)
- fail('Completion cannot be in the future.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)
- fail('This service cycle is already completed.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)
- fail('Enter a valid cost.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)

### Empty states, confirmations, feedback and dialogs

- toast('Completed. Next service scheduled.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)
- note('Next service is calculated from the completion date. Unpaid costs remain draft expenses.','','repeat') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:307)

### Navigation and data effects

Navigation targets/expressions: 'service?id='+s.id.

Reference state collections accessed: expenses, services. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:service-complete ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-56"></a>

## 56. Service history — service-history

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:308). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'View expense' | navigation/custom | 'expense?id='+h.expenseId | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:308) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:308)
- isStaff()&&h.expenseId — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:308)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No completed services','','history') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:308)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: services. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-57"></a>

## 57. Community — community

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Events' | navigation | 'events' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Parking' | navigation | 'parking-availability' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Members' | navigation | 'members' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| isStaff()?'Send notice':'Notices' | navigation | isStaff()?'notice-form':'notices' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'View all' | section navigation | 'notices' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Committee' | record/navigation row | 'committee' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Polls' | record/navigation row | 'polls' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Vehicles' | record/navigation row | 'vehicles' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Contacts' | record/navigation row | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Vendors' | record/navigation row | 'vendors' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Documents' | record/navigation row | 'documents' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Service schedule' | record/navigation row | 'services' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |
| 'Committee handover' | record/navigation row | 'committee-handover' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No notices yet','','megaphone') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:906)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: notices. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-58"></a>

## 58. Notice board — notices

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Create notice' | icon control | 'data-go="notice-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975) |
| isStaff()?['All','Pinned','Drafts','Scheduled']:['All','Pinned'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975) |
| ${esc(n.title)} ${esc(n.body.slice(0,88))}${n.body.length>88?'…':''} | inline button | notice?id=${n.id} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975) |
| ${n.status==='Draft'?'Preview':'Read'} ${I('arrowRight')} | inline button | ${n.status=== | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975) |

### Inputs and selectable options

- search('Search notices') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975)

### Conditional states / visibility

- f==='Pinned' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975)
- f==='Drafts' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975)
- f==='Scheduled' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- n.status==='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975)
- n.body.length>88 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975)
- n.status==='Draft' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No notices here','','megaphone') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:975)

### Navigation and data effects

Navigation targets/expressions: notice?id=${n.id}; ${n.status===.

Reference state collections accessed: notices. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-59"></a>

## 59. Notice — notice

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Attachment' | navigation/custom | 'notice-attachment?id='+n.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314) |
| 'Edit' | navigation/custom | 'notice-form?id='+n.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314) |
| n.pinned?'Unpin':'Pin notice' | action | 'pinNotice' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314) |
| 'Publish' | action | 'publishNotice' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Share' | action | 'shareNotice' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314) |
| 'Mark as read' | action | 'readNotice' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314) |

### Inputs and selectable options

- <textarea id="sharetext" readonly> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Conditional states / visibility

- !n\|\|!visibleNotice(n) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.status!=='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='All flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience.startsWith('Block ') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='Unpaid flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='Selected flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- n.category==='Water' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314)
- n.type==='Maintenance' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- n.status==='Draft' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314)
- n.attachment — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- n.pinned — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:525)
- n.pinned — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:314)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !n — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:800)
- !n\|\|!visibleNotice(n) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:527)
- navigator.share — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- e.name==='AbortError' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- !n\|\|!visibleNotice(n) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:802)
- !n.reads.includes(S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:802)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:525)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:800)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:800)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Notice unavailable.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:800)
- fail('Access denied.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:527)
- fail('Notice unavailable.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:802)

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- toast(n.pinned?'Notice pinned':'Notice unpinned') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:525)
- toast('Copied to clipboard') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- sheet('Copy / share','<div class="field"><label for="sharetext">Text</label><textarea id="sharetext" readonly>${esc(text)}</textarea></div><p>Select and copy the text.</p>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- toast('Marked as read') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:802)

### Navigation and data effects

Navigation targets/expressions: home; 'notice-preview?id='+n.id.

Reference state collections accessed: notices, offline, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:pinNotice ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:525)), action:publishNotice ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:800)), action:shareNotice ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:527)), action:readNotice ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:802)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-60"></a>

## 60. Send notice — notice-form

Group: Community. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Preview notice' | submit | 'notice' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792) |
| 'Templates' | navigation/custom | 'notice-templates' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- field('title','Title',n.title\|\|t[0]\|\|'','text','required maxlength="100"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- field('body','Message',n.body\|\|t[1]\|\|'','textarea','required maxlength="2000"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- selectField('type','Type',['General','Maintenance','Service alert','Emergency'],n.type\|\|t[2]\|\|'General') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- selectField('category','Category',categories.map(c=>c[0]),n.category\|\|t[3]\|\|'Other') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- selectField('audience','Send to',['All flats',...S.apartment.blocks.map(b=>'Block '+b),'Unpaid flats','Selected flats'],n.audience\|\|'All flats','data-dynamic="notice-audience"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- field('selectedFlats','Selected flat IDs',(n.selectedFlats\|\|[]).join(', '),'text','placeholder="A-101, A-203"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- fileField('Attachment · optional') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- checkField('push','Notify on phone',n.push??true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- checkField('ack','Ask members to acknowledge',n.ackRequired\|\|false) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- checkField('pinned','Pin notice',n.pinned\|\|false) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- selectField('mode','Delivery',['Now','Schedule'],n.status==='Scheduled'?'Schedule':'Now','data-notice-mode="true"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- field('sendAt','Send at',n.sendAt\|\|'2026-09-14T09:00','datetime-local') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)

### Conditional states / visibility

- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- D.audience==='Selected flats'&&(!selected.length\|\|selected.some(id=>!flat(id))) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:797)
- D.mode==='Schedule'&&(!D.sendAt\|\|D.sendAt<=S.referenceNow) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:797)
- editDraft — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:797)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- D.mode==='Schedule' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:797)
- !file\|\|!file.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- file.size>2*1024*1024 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- !/^(image\/(png\|jpeg\|gif\|webp)\|application\/pdf\|text\/plain)$/.test(file.type) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- old — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:797)
- n.audience==='Selected flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- n.status==='Scheduled' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)
- n.status==='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:797)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- fail('Enter valid flat IDs.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:797)
- fail('Choose a future publishing time.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:797)
- requiredText(D.title,'Title',100) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:797)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- requiredText(D.body,'Message',2000) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:797)

### Empty states, confirmations, feedback and dialogs

- note('Editing saves a new draft. Existing notices are not silently replaced.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:792)

### Navigation and data effects

Navigation targets/expressions: 'notice-preview?id='+n.id; notice-templates.

Reference state collections accessed: apartment, notices, referenceNow. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:notice ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:797)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-61"></a>

## 61. Notice attachment — notice-attachment

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:316). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !n\|\|!visibleNotice(n) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:316)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.status!=='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='All flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience.startsWith('Block ') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='Unpaid flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='Selected flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !a — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- a.data?.startsWith('data:image') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- a.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- empty('No attachment','A file has not been added.','file') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: notices, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-62"></a>

## 62. Service vendors — vendors

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:317). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Add vendor' | icon control | 'data-go="vendor-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:317) |
| v.name | record/navigation row | 'vendor?id='+v.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:317) |

### Inputs and selectable options

- search('Search vendors') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:317)

### Conditional states / visibility

- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:317)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No vendors found','','tools') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:317)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-63"></a>

## 63. Vendor profile — vendor

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Edit contact' | navigation/custom | 'vendor-form?id='+d.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545) |
| 'Apartment contacts' | navigation/custom | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545) |
| 'Cancel' | action | 'closeSheet' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545) |
| 'Open dialler' | action | 'dial' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545) |
| 'Call' | action | 'callVendor' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318) |
| 'Edit' | navigation/custom | 'vendor-form?id='+v.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318) |
| 'Copy contact' | action | 'copyVendor' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318) |
| s.title | record/navigation row | 'service?id='+s.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318) |

### Inputs and selectable options

- <textarea id="sharetext" readonly> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Conditional states / visibility

- !v\|\|(!isStaff()&&!v.public) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !c?.phone — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318)
- !c?.phone — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:530)
- navigator.share — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- e.name==='AbortError' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- v.amc — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318)
- v.public — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

- fail('No phone number has been added.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:530)

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- sheet('Number not added','<p>Add a verified number before using this contact.</p>${isStaff()?btn('Edit contact',editRoute,'edit','full'):btn('Apartment contacts','contacts','phone','full')}') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545)
- sheet('Call '+c.name,'<p>${esc(c.phone)}</p><div class="actions">${actBtn('Cancel','closeSheet','','secondary')}${actBtn('Open dialler','dial','phone','','data-phone="${esc(c.phone)}"')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545)
- toast('Copied to clipboard') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- sheet('Copy / share','<div class="field"><label for="sharetext">Text</label><textarea id="sharetext" readonly>${esc(text)}</textarea></div><p>Select and copy the text.</p>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- empty('No linked service','','calendar') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:318)

### Navigation and data effects

Navigation targets/expressions: home; contacts.

Reference state collections accessed: services, vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:callVendor ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:529)), action:closeSheet ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)), action:dial ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:531)), action:copyVendor ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:530)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-64"></a>

## 64. Vendor details — vendor-form

Group: Community. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:319). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'vendor' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:319) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${v.id\|\|''}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:319)
- field('name','Vendor name',v.name\|\|'','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:319)
- selectField('category','Service type',categories.map(c=>c[0]),v.category\|\|'Lift') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:319)
- field('contact','Contact person',v.contact\|\|'','text','maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:319)
- field('phone','Phone number',v.phone\|\|'','tel','inputmode="tel" maxlength="16" placeholder="+91…"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:319)
- field('amc','AMC expiry · optional',v.amc\|\|'','date') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:319)
- field('notes','Notes',v.notes\|\|'','textarea','maxlength="500"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:319)
- checkField('public','Visible to residents',v.public??true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:319)

### Conditional states / visibility

- n.phone&&!/^\+?[\d\s-]{7,16}$/.test(n.phone) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:571)
- v — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:571)

### Required validation and role guards

- fail('Enter a valid phone number.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:571)

### Empty states, confirmations, feedback and dialogs

- toast('Vendor saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:571)

### Navigation and data effects

Navigation targets/expressions: 'vendor?id='+n.id.

Reference state collections accessed: vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:vendor ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:571)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-65"></a>

## 65. Important contacts — contacts

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| c.name | record/navigation row | 'contact?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320) |
| 'Add contact' | navigation/custom | 'contact-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320) |
| 'View all' | section navigation | 'vendors' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320) |
| v.name | record/navigation row | 'vendor?id='+v.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- c.type==='Emergency' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- note('Emergency? Contact local emergency services directly. Do not wait for an in-app reply.','warn','alert') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:320)

### Navigation and data effects

Navigation targets/expressions: contact-form.

Reference state collections accessed: contacts, vendors. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-66"></a>

## 66. Contact details — contact

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:321). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Edit contact' | navigation/custom | 'contact-form?id='+d.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545) |
| 'Apartment contacts' | navigation/custom | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545) |
| 'Cancel' | action | 'closeSheet' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545) |
| 'Open dialler' | action | 'dial' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545) |
| 'Call' | action | 'callContact' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:321) |
| 'Edit' | navigation/custom | 'contact-form?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:321) |
| 'Copy' | action | 'copyContact' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:321) |

### Inputs and selectable options

- <textarea id="sharetext" readonly> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !c?.phone — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:321)
- !c?.phone — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:530)
- navigator.share — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- e.name==='AbortError' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- !c.phone — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:321)

### Required validation and role guards

- fail('No phone number has been added.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:530)

### Empty states, confirmations, feedback and dialogs

- stateHero('phone',c.name,c.phone\|\|'Verified number not added') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:321)
- sheet('Number not added','<p>Add a verified number before using this contact.</p>${isStaff()?btn('Edit contact',editRoute,'edit','full'):btn('Apartment contacts','contacts','phone','full')}') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545)
- sheet('Call '+c.name,'<p>${esc(c.phone)}</p><div class="actions">${actBtn('Cancel','closeSheet','','secondary')}${actBtn('Open dialler','dial','phone','','data-phone="${esc(c.phone)}"')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:545)
- toast('Copied to clipboard') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- sheet('Copy / share','<div class="field"><label for="sharetext">Text</label><textarea id="sharetext" readonly>${esc(text)}</textarea></div><p>Select and copy the text.</p>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- note('Demo contacts intentionally contain no real telephone numbers.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:321)

### Navigation and data effects

Navigation targets/expressions: contacts.

Reference state collections accessed: contacts. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:callContact ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:529)), action:closeSheet ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)), action:dial ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:531)), action:copyContact ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:530)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-67"></a>

## 67. Edit contact — contact-form

Group: Community. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:322). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'contact' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:322) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${c.id\|\|''}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:322)
- field('name','Contact name',c.name\|\|'','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:322)
- selectField('type','Type',['Admin','Treasurer','Security','Lift','Emergency','Other'],c.type\|\|'Other') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:322)
- field('phone','Verified phone number',c.phone\|\|'','tel','required maxlength="16"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:322)
- checkField('verified','I have verified this number.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:322)

### Conditional states / visibility

- !D.verified — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:572)
- !/^\+?[\d\s-]{3,16}$/.test(D.phone) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:572)
- c — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:572)

### Required validation and role guards

- fail('Confirm the phone number is verified.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:572)
- fail('Enter a valid telephone number.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:572)

### Empty states, confirmations, feedback and dialogs

- toast('Contact saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:572)

### Navigation and data effects

Navigation targets/expressions: 'contact?id='+n.id.

Reference state collections accessed: contacts. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:contact ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:572)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-68"></a>

## 68. Apartment files — documents

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Upload document' | icon control | 'data-go="document-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323) |
| isStaff()?['All','Public','Private']:['All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323) |
| d.title | record/navigation row | 'document?id='+d.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323) |

### Inputs and selectable options

- search('Search documents') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323)

### Conditional states / visibility

- f==='Public' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323)
- f==='Private' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- d.public — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No documents','','folder') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:323)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: docs. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-69"></a>

## 69. Document — document

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:324). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Download' | action | 'downloadDocument' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:324) |
| 'Edit' | navigation/custom | 'document-form?id='+d.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:324) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Cancel' | action | 'closeSheet' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Remove' | action | 'deleteDocument' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Remove file' | action | 'removeDocument' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:324) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !d\|\|(!isStaff()&&!d.public) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:324)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- d.public — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:324)
- d.attachment — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:324)
- !a — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- a.data?.startsWith('data:image') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- a.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- !x\|\|!isStaff()&&!x.public — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:532)
- x.attachment — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:532)
- !a?.data — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:461)
- content instanceof Blob — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:460)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:324)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- danger — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)

### Required validation and role guards

- fail('Access denied.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:532)
- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:534)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- empty('No attachment','A file has not been added.','file') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- toast('No file was attached.','info') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:461)
- confirm('Remove document?','The file will be removed from this local reference.','Remove','deleteDocument','data-id="${d.id}"',true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:534)
- toast('Document removed') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:534)
- sheet(title,'<p>${esc(text)}</p><div class="actions">${actBtn('Cancel','closeSheet','','secondary')}${actBtn(label,action,danger?'trash':'check',danger?'danger':'',attrs)}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)

### Navigation and data effects

Navigation targets/expressions: home; 'documents'.

Reference state collections accessed: docs. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:downloadDocument ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:532)), action:removeDocument ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:534)), action:deleteDocument ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:534)), action:closeSheet ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-70"></a>

## 70. Upload document — document-form

Group: Community. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:325). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save document' | submit | 'document' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:325) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${d.id\|\|''}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:325)
- field('title','Document title',d.title\|\|'','text','required maxlength="100"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:325)
- selectField('category','Category',['Rules','Meeting minutes','Bill','AMC','Other'],d.category\|\|'Other') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:325)
- fileField(d.id?'Replace file · optional':'File') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:325)
- checkField('public','Residents can view',d.public??true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:325)

### Conditional states / visibility

- !file\|\|!file.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- file.size>2*1024*1024 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- !/^(image\/(png\|jpeg\|gif\|webp)\|application\/pdf\|text\/plain)$/.test(file.type) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:454)
- !attachment&&!old — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:573)
- old — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:573)
- d.public — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:573)
- d.id — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:325)

### Required validation and role guards

- fail('Choose a file to upload.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:573)

### Empty states, confirmations, feedback and dialogs

- toast('Document stored in this browser') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:573)
- note('Local preview files: images, PDF or text, up to 2 MB.','','file') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:325)

### Navigation and data effects

Navigation targets/expressions: 'document?id='+d.id.

Reference state collections accessed: docs. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:document ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:573)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-71"></a>

## 71. ApartmentCare — welcome

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:345). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Shared or dynamic controls only | Inspect page definition | No direct control definition | Page link above |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-72"></a>

## 72. Choose login — login

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:346). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Shared or dynamic controls only | Inspect page definition | No direct control definition | Page link above |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-73"></a>

## 73. Admin login — admin-login

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:347). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Fill' | action | 'fillLoginDemo' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:337) |
| 'Sign in' | submit | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:338) |
| 'Switch login' | navigation/custom | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:341) |
| 'Forgot PIN?' | navigation/custom | 'forgot-pin' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:341) |
| 'New apartment? Set up' | navigation/custom | 'admin-signup' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343) |
| 'New resident? Join' | navigation/custom | 'join' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343) |

### Inputs and selectable options

- <input type="hidden" name="role" value="${role}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:338)
- field('mobile','Mobile number','','tel','required pattern="[6-9][0-9]{9}" inputmode="numeric" maxlength="10" autocomplete="username" placeholder="10-digit mobile number"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:339)
- field('pin','PIN','','password','required pattern="[0-9]{4,6}" minlength="4" maxlength="6" inputmode="numeric" autocomplete="current-password" placeholder="4–6 digit PIN"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:340)

### Conditional states / visibility

- role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:336)
- !f — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:490)
- !/^[6-9]\d{9}$/.test(D.mobile)\|\|!/^\d{4,6}$/.test(D.pin) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)
- !['Admin','Treasurer','Resident'].includes(role) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:446)
- !S.settings.configured&&isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:933)
- role==='Admin' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343)
- role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343)

### Required validation and role guards

- fail('Use a 10-digit mobile and a 4–6 digit demo PIN.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)

### Empty states, confirmations, feedback and dialogs

- toast('Signed in to the demo') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)
- note('Demo only. Do not enter your real credentials.','','lock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:342)
- note('Treasurer access is assigned by your apartment admin.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343)

### Navigation and data effects

Navigation targets/expressions: 'home'; 'setup-done'; login; forgot-pin; admin-signup; join.

Reference state collections accessed: role, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:fillLoginDemo ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:490)), form:login ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:933)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-74"></a>

## 74. Treasurer login — treasurer-login

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:348). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Fill' | action | 'fillLoginDemo' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:337) |
| 'Sign in' | submit | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:338) |
| 'Switch login' | navigation/custom | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:341) |
| 'Forgot PIN?' | navigation/custom | 'forgot-pin' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:341) |
| 'New apartment? Set up' | navigation/custom | 'admin-signup' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343) |
| 'New resident? Join' | navigation/custom | 'join' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343) |

### Inputs and selectable options

- <input type="hidden" name="role" value="${role}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:338)
- field('mobile','Mobile number','','tel','required pattern="[6-9][0-9]{9}" inputmode="numeric" maxlength="10" autocomplete="username" placeholder="10-digit mobile number"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:339)
- field('pin','PIN','','password','required pattern="[0-9]{4,6}" minlength="4" maxlength="6" inputmode="numeric" autocomplete="current-password" placeholder="4–6 digit PIN"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:340)

### Conditional states / visibility

- role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:336)
- !f — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:490)
- !/^[6-9]\d{9}$/.test(D.mobile)\|\|!/^\d{4,6}$/.test(D.pin) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)
- !['Admin','Treasurer','Resident'].includes(role) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:446)
- !S.settings.configured&&isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:933)
- role==='Admin' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343)
- role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343)

### Required validation and role guards

- fail('Use a 10-digit mobile and a 4–6 digit demo PIN.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)

### Empty states, confirmations, feedback and dialogs

- toast('Signed in to the demo') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)
- note('Demo only. Do not enter your real credentials.','','lock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:342)
- note('Treasurer access is assigned by your apartment admin.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343)

### Navigation and data effects

Navigation targets/expressions: 'home'; 'setup-done'; login; forgot-pin; admin-signup; join.

Reference state collections accessed: role, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:fillLoginDemo ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:490)), form:login ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:933)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-75"></a>

## 75. Resident login — resident-login

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:349). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Fill' | action | 'fillLoginDemo' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:337) |
| 'Sign in' | submit | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:338) |
| 'Switch login' | navigation/custom | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:341) |
| 'Forgot PIN?' | navigation/custom | 'forgot-pin' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:341) |
| 'New apartment? Set up' | navigation/custom | 'admin-signup' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343) |
| 'New resident? Join' | navigation/custom | 'join' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343) |

### Inputs and selectable options

- <input type="hidden" name="role" value="${role}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:338)
- field('mobile','Mobile number','','tel','required pattern="[6-9][0-9]{9}" inputmode="numeric" maxlength="10" autocomplete="username" placeholder="10-digit mobile number"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:339)
- field('pin','PIN','','password','required pattern="[0-9]{4,6}" minlength="4" maxlength="6" inputmode="numeric" autocomplete="current-password" placeholder="4–6 digit PIN"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:340)

### Conditional states / visibility

- role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:336)
- !f — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:490)
- !/^[6-9]\d{9}$/.test(D.mobile)\|\|!/^\d{4,6}$/.test(D.pin) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)
- !['Admin','Treasurer','Resident'].includes(role) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:446)
- !S.settings.configured&&isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:933)
- role==='Admin' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343)
- role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343)

### Required validation and role guards

- fail('Use a 10-digit mobile and a 4–6 digit demo PIN.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)

### Empty states, confirmations, feedback and dialogs

- toast('Signed in to the demo') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)
- note('Demo only. Do not enter your real credentials.','','lock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:342)
- note('Treasurer access is assigned by your apartment admin.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:343)

### Navigation and data effects

Navigation targets/expressions: 'home'; 'setup-done'; login; forgot-pin; admin-signup; join.

Reference state collections accessed: role, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:fillLoginDemo ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:490)), form:login ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:933)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-76"></a>

## 76. Reset PIN — forgot-pin

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:350). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Continue' | submit | 'forgot-pin' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:350) |

### Inputs and selectable options

- field('mobile','Mobile number','','tel','required pattern="[6-9][0-9]{9}" inputmode="numeric" maxlength="10"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:350)

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('key','Forgot your PIN?','Use your registered mobile number.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:350)

### Navigation and data effects

Navigation targets/expressions: 'verify-reset'.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:forgot-pin ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-77"></a>

## 77. Verify number — verify-reset

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:351). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Verify' | submit | 'verify-reset' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:351) |

### Inputs and selectable options

- field('code','Verification code','','text','required pattern="[0-9]{6}" maxlength="6" inputmode="numeric"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:351)

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- D.code!=='246810' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)

### Required validation and role guards

- fail('Use the demo code 246810.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)

### Empty states, confirmations, feedback and dialogs

- stateHero('shield','Verify it’s you','Enter the six-digit code.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:351)
- note('Demo code: 246810. No SMS is sent.','','info') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:351)

### Navigation and data effects

Navigation targets/expressions: 'reset-pin'.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:verify-reset ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-78"></a>

## 78. Set new PIN — reset-pin

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:352). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save PIN' | submit | 'reset-pin' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:352) |

### Inputs and selectable options

- field('pin','New PIN','','password','required pattern="[0-9]{4,6}" maxlength="6" inputmode="numeric"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:352)
- field('confirm','Confirm PIN','','password','required pattern="[0-9]{4,6}" maxlength="6" inputmode="numeric"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:352)

### Conditional states / visibility

- D.pin!==D.confirm — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)

### Required validation and role guards

- fail('The PINs do not match.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)

### Empty states, confirmations, feedback and dialogs

- toast('Demo PIN reset flow complete') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)
- note('Prototype only. Do not enter a PIN you use elsewhere.','','lock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:352)

### Navigation and data effects

Navigation targets/expressions: loginPath(draft.signInRole).

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:reset-pin ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-79"></a>

## 79. Join your apartment — join

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:353). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Find apartment' | submit | 'join' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:353) |

### Inputs and selectable options

- field('invite','Invite code','','text','required maxlength="20" placeholder="SAI-0926" autocapitalize="characters"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:353)

### Conditional states / visibility

- !S.apartment.inviteActive\|\|S.apartment.inviteExpiry<today\|\|D.invite.trim().toUpperCase()!==S.apartment.invite — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:576)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- note('Demo invite: '+S.apartment.invite) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:353)

### Navigation and data effects

Navigation targets/expressions: 'invite-invalid'; 'signup'.

Reference state collections accessed: apartment. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:join ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:576)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-80"></a>

## 80. Invite unavailable — invite-invalid

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:354). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Try another code' | navigation/custom | 'join' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:354) |
| 'Back to sign in' | navigation/custom | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:354) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('link','Check your invite','The code is invalid, expired or revoked.','amber') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:354)

### Navigation and data effects

Navigation targets/expressions: join; login.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-81"></a>

## 81. Create your account — signup

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:355). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Choose flat' | submit | 'signup' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:355) |

### Inputs and selectable options

- field('name','Your name',draft.signup?.name\|\|'','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:355)
- field('mobile','Mobile number',draft.signup?.mobile\|\|'','tel','required pattern="[6-9][0-9]{9}" maxlength="10" inputmode="numeric"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:355)
- selectField('type','I am an',['Owner','Tenant'],'Owner') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:355)
- field('pin','Create PIN','','password','required pattern="[0-9]{4,6}" maxlength="6" inputmode="numeric"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:355)
- checkField('consent','I agree to join this apartment and share my details with its admin.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:355)

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- !D.consent — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:577)
- !/^[6-9]\d{9}$/.test(D.mobile) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:577)

### Required validation and role guards

- fail('Agree to share your details with the apartment admin.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:577)
- fail('Enter a valid 10-digit mobile number.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:577)

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: 'choose-flat'.

Reference state collections accessed: apartment. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:signup ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:577)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-82"></a>

## 82. Choose your flat — choose-flat

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:356). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ${f.id} Available | inline button | selectClaimFlat | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:356) |
| 'Request to join' | action | 'submitClaim' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:356) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- draft.claimFlat===f.id — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:356)
- !fs.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:356)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !draft.signup — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)
- !draft.claimFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)
- f.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)
- S.claims.some(c=>c.flat===f.id&&c.status==='pending') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)

### Required validation and role guards

- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Enter your name and mobile first.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)
- fail('Choose a flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)
- fail('That flat already has an active account.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)
- fail('A join request is already pending for this flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)

### Empty states, confirmations, feedback and dialogs

- empty('No unassigned flats','Ask the admin to complete a resident handover.','home') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:356)
- note('Access begins after the admin approves your flat claim.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:356)

### Navigation and data effects

Navigation targets/expressions: 'signup-pending'.

Reference state collections accessed: claims, flats, offline, signup. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:selectClaimFlat ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)), action:submitClaim ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-83"></a>

## 83. Join request — signup-pending

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Open my apartment' | action | 'openNewResident' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357) |
| 'Try again' | navigation/custom | 'join' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357) |
| 'Check status' | action | 'refreshScreen' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357) |
| 'Back to sign in' | navigation/custom | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- c?.status==='approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !c\|\|c.status!=='approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:537)
- !['Admin','Treasurer','Resident'].includes(role) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:446)
- c?.status==='rejected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)

### Required validation and role guards

- fail('Admin approval is pending.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:537)

### Empty states, confirmations, feedback and dialogs

- stateHero('checkCircle','Welcome home!',c.flat+' · '+S.apartment.name) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357)
- stateHero('x','Request not approved',c.reason\|\|'Please contact your apartment admin.','red') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357)
- stateHero('clock','Almost home.',(c?.flat\|\|draft.claimFlat\|\|'Your flat')+' · Awaiting admin approval','amber') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:357)

### Navigation and data effects

Navigation targets/expressions: 'home'; join; login.

Reference state collections accessed: apartment, claims, profile, residentFlat, role, signup. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:openNewResident ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:537)), action:refreshScreen ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:497)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-84"></a>

## 84. Create apartment — admin-signup

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:358). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Continue' | submit | 'admin-signup' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:358) |

### Inputs and selectable options

- field('name','Your name','','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:358)
- field('mobile','Mobile number','','tel','required pattern="[6-9][0-9]{9}" maxlength="10"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:358)
- field('pin','Create PIN','','password','required pattern="[0-9]{4,6}" maxlength="6"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:358)
- checkField('confirm','I am authorised to manage this apartment.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:358)

### Conditional states / visibility

- !D.confirm — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:578)

### Required validation and role guards

- fail('Confirm you are authorised to set up the apartment.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:578)

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: 'setup-apartment'.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:admin-signup ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:578)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-85"></a>

## 85. Apartment details — setup-apartment

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:359). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Create apartment' | submit | 'setup-apartment' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:359) |

### Inputs and selectable options

- field('name','Apartment name',draft.apartment?.name\|\|'','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:359)
- selectField('city','City',['Hyderabad','Bengaluru','Chennai','Pune','Mumbai','Delhi NCR','Other'],'Hyderabad') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:359)
- field('address','Address','','textarea','required maxlength="300"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:359)
- field('pincode','PIN code','','text','required pattern="[0-9]{6}" maxlength="6" inputmode="numeric"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:359)
- field('flats','Number of flats',10,'number','min="5" max="50" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:359)

### Conditional states / visibility

- !Number.isInteger(count)\|\|count<5\|\|count>50 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:579)
- i%3===0 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:154)
- i===1 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:155)
- empty — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:624)
- empty — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:625)
- empty — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:629)
- empty — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:639)
- empty — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:643)
- empty — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:646)
- empty — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:647)
- empty — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:649)
- n<=10 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)
- n<=30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)

### Required validation and role guards

- fail('This release supports 5–50 flats.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:579)

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: 'setup-done'.

Reference state collections accessed: apartment, flats, k, role, roles, staffProfiles, subscription, theme, themeMode. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:setup-apartment ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:929)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-86"></a>

## 86. Apartment ready — setup-done

Group: Onboarding. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:360). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Review flats' | record/navigation row | 'flats' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:360) |
| 'Set maintenance' | record/navigation row | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:360) |
| 'Invite residents' | record/navigation row | 'invite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:360) |
| 'Open dashboard' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:360) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('checkCircle','Your community is ready.',S.apartment.name) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:360)

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: apartment, flats. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-87"></a>

## 87. Apartment — apartment

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Apartment profile' | record/navigation row | 'apartment-edit' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |
| 'Blocks' | record/navigation row | 'blocks' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |
| 'Flats' | record/navigation row | 'flats' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |
| 'Residents' | record/navigation row | 'residents' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |
| 'Admin & treasurer' | record/navigation row | 'roles' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |
| 'Invite residents' | record/navigation row | 'invite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |
| 'Resident handover' | record/navigation row | 'handover' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:362) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: apartment, flats. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-88"></a>

## 88. Apartment profile — apartment-edit

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:363). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'apartment' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:363) |

### Inputs and selectable options

- field('name','Apartment name',S.apartment.name,'text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:363)
- field('city','City',S.apartment.city,'text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:363)
- field('address','Address',S.apartment.address,'textarea','required maxlength="300"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:363)
- field('pincode','PIN code',S.apartment.pincode,'text','required pattern="[0-9]{6}" maxlength="6"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:363)

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- toast('Apartment saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:580)

### Navigation and data effects

Navigation targets/expressions: 'apartment'.

Reference state collections accessed: apartment. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:apartment ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:580)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-89"></a>

## 89. Blocks — blocks

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:364). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Add' | section navigation | 'block-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:364) |
| 'Block '+b | record/navigation row | 'block-form?id='+b | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:364) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: apartment, flats. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-90"></a>

## 90. Block details — block-form

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:365). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'block' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:365) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${esc(Q.id\|\|'')}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:365)
- field('name','Block name',Q.id\|\|'','text','required maxlength="8" placeholder="A"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:365)

### Conditional states / visibility

- S.apartment.blocks.includes(name)&&name!==D.id — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:581)
- D.id — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:581)
- b===D.id — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:581)

### Required validation and role guards

- fail('This block already exists.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:581)

### Empty states, confirmations, feedback and dialogs

- toast('Block saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:581)
- note('Renaming a block does not rename existing flat IDs.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:365)

### Navigation and data effects

Navigation targets/expressions: 'blocks'.

Reference state collections accessed: apartment, flats. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:block ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:581)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-91"></a>

## 91. Flats — flats

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:366). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Add flat' | icon control | 'data-go="flat-form"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:366) |
| ['All',...S.apartment.blocks] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:366) |
| ${esc(f.id)} ${!f.member?'Unassigned':b?billStatus(b):'Active'} | inline button | flat?id=${f.id} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:366) |

### Inputs and selectable options

- search('Search flat or resident') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:366)

### Conditional states / visibility

- b&&billPending(b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:366)
- b&&due(b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:366)
- !f.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:366)
- b — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:366)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: flat?id=${f.id}.

Reference state collections accessed: apartment, bills, flats. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-92"></a>

## 92. Flat details — flat

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Edit flat' | navigation/custom | 'flat-form?id='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367) |
| 'Payment history' | navigation/custom | 'history?flat='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367) |
| 'Current dues' | navigation/custom | 'bill?id='+b.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367) |
| 'Resident handover' | navigation/custom | 'handover?flat='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- f.active — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367)
- f.occupied — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367)
- f.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367)
- b — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:367)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: bills, flats. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-93"></a>

## 93. Flat setup — flat-form

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:368). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'flat' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:368) |

### Inputs and selectable options

- <input type="hidden" name="original" value="${f.id\|\|''}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:368)
- field('id','Flat ID',f.id\|\|'','text','required maxlength="12" placeholder="A-101" ${f.id?'readonly':''}') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:368)
- selectField('block','Block',S.apartment.blocks,f.block\|\|S.apartment.blocks[0]) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:368)
- field('floor','Floor',f.floor\|\|1,'number','min="0" max="99" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:368)
- checkField('occupied','Occupied',f.occupied??true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:368)
- checkField('active','Active for maintenance billing',f.active??true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:368)

### Conditional states / visibility

- !existing&&flat(id) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:582)
- !existing&&S.flats.length>=50 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:582)
- existing — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:582)
- f.active — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:582)
- f.id — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:368)

### Required validation and role guards

- fail('That flat ID already exists.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:582)
- fail('This reference supports up to 50 flats.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:582)

### Empty states, confirmations, feedback and dialogs

- toast('Flat saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:582)

### Navigation and data effects

Navigation targets/expressions: 'flat?id='+id.

Reference state collections accessed: apartment, flats, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:flat ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:582)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-94"></a>

## 94. Members — residents

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:769). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Shared or dynamic controls only | Inspect page definition | No direct control definition | Page link above |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-95"></a>

## 95. Resident profile — resident

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:370). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Payments' | navigation/custom | 'history?flat='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:370) |
| 'View flat' | navigation/custom | 'flat?id='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:370) |
| 'Transfer flat account' | navigation/custom | 'handover?flat='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:370) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- f.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:370)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: flats. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-96"></a>

## 96. Join requests — join-requests

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:371). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ['Pending','Approved','Rejected'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:371) |
| c.name | record/navigation row | 'join-review?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:371) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- c.status==='pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:371)
- c.status==='approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:371)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No requests here','','userPlus') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:371)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: claims. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-97"></a>

## 97. Review resident — join-review

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Reject' | navigation/custom | 'join-reject?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Approve resident' | submit | 'approve-claim' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:536) |
| 'Approve resident' | action | 'approveClaim' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${c.id}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:536)
- checkField('verified','I verified this resident and their flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:536)

### Conditional states / visibility

- !c — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- c.status==='pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372)
- c.status==='approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !c\|\|c.status!=='pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:536)
- flat(c.flat)?.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:536)
- !D.verified — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:583)
- !c\|\|c.status!=='pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:583)
- f.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:583)
- c.reason — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:536)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Already reviewed.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:536)
- fail('This flat already has an account. Complete handover first.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:536)
- fail('Confirm that the resident and flat are verified.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:583)
- fail('This request was already reviewed.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:583)
- fail('This flat already has an active account.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:583)

### Empty states, confirmations, feedback and dialogs

- empty('No request selected') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372)
- note('Approve only after verifying the person and their connection to this flat.','warn','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372)
- sheet('Approve flat access',form('approve-claim','<input type="hidden" name="id" value="${c.id}"><div class="card mb">${kv('Resident',c.name)}${kv('Flat',c.flat)}</div>${checkField('verified','I verified this resident and their flat.')}','Approve resident','userPlus')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:536)
- toast('Resident approved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:583)
- note(c.reason,'danger') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:372)

### Navigation and data effects

Navigation targets/expressions: 'join-review?id='+c.id.

Reference state collections accessed: claims, memberRecords. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:approveClaim ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:536)), form:approve-claim ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:931)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-98"></a>

## 98. Reject join request — join-reject

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:373). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Reject request' | submit | 'join-reject' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:373) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${c.id}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:373)
- field('reason','Reason for resident','','textarea','required maxlength="300" placeholder="e.g. Please choose the correct flat"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:373)

### Conditional states / visibility

- !c\|\|c.status!=='pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:584)

### Required validation and role guards

- fail('Already reviewed.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:584)

### Empty states, confirmations, feedback and dialogs

- toast('Request rejected') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:584)

### Navigation and data effects

Navigation targets/expressions: 'join-requests'.

Reference state collections accessed: claims. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:join-reject ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:584)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-99"></a>

## 99. Invite residents — invite

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Copy code' | action | 'copyInvite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374) |
| 'Share' | action | 'shareInvite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374) |
| 'Expiry date' | record/navigation row | 'invite-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374) |
| 'Approval required' | record/navigation row | 'permissions' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374) |
| 'Manage invite' | navigation/custom | 'invite-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374) |

### Inputs and selectable options

- <textarea id="sharetext" readonly> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Conditional states / visibility

- S.apartment.inviteActive — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:374)
- navigator.share — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- e.name==='AbortError' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- toast('Copied to clipboard') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- sheet('Copy / share','<div class="field"><label for="sharetext">Text</label><textarea id="sharetext" readonly>${esc(text)}</textarea></div><p>Select and copy the text.</p>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Navigation and data effects

Navigation targets/expressions: invite-settings.

Reference state collections accessed: apartment. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:copyInvite ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:538)), action:shareInvite ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:538)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-100"></a>

## 100. Invite controls — invite-settings

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:375). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'invite-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:375) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Cancel' | action | 'closeSheet' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Generate' | action | 'confirmNewInvite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'New code' | action | 'regenerateInvite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:375) |
| S.apartment.inviteActive?'Revoke':'Activate' | action | 'toggleInvite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:375) |

### Inputs and selectable options

- field('expiry','Valid until',S.apartment.inviteExpiry,'date','min="${today}" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:375)

### Conditional states / visibility

- D.expiry<today — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:585)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- danger — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)
- !S.apartment.inviteActive — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:541)
- S.apartment.inviteActive — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:541)
- S.apartment.inviteActive — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:375)

### Required validation and role guards

- fail('Choose today or a future date.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:585)
- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:539)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:540)
- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:541)

### Empty states, confirmations, feedback and dialogs

- toast('Invite expiry saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:585)
- note('A new code revokes the previous code. Existing approved accounts keep access.','','key') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:375)
- confirm('Generate new code?','The current code will stop working. Approved accounts keep their access.','Generate','confirmNewInvite') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:539)
- toast('New invite created') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:540)
- sheet(title,'<p>${esc(text)}</p><div class="actions">${actBtn('Cancel','closeSheet','','secondary')}${actBtn(label,action,danger?'trash':'check',danger?'danger':'',attrs)}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)
- toast(S.apartment.inviteActive?'Invite active':'Invite revoked') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:541)

### Navigation and data effects

Navigation targets/expressions: 'invite'.

Reference state collections accessed: apartment. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:invite-settings ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:585)), action:regenerateInvite ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:539)), action:confirmNewInvite ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:540)), action:closeSheet ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)), action:toggleInvite ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:541)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-101"></a>

## 101. Resident handover — handover

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:376). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Start handover' | submit | 'handover' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:376) |

### Inputs and selectable options

- selectField('flat','Flat',S.flats.filter(f=>f.member).map(f=>[f.id,f.id+' · '+f.name]),Q.flat\|\|S.flats.find(f=>f.member)?.id) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:376)
- field('name','Incoming resident name','','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:376)
- field('mobile','Incoming mobile','','tel','required pattern="[6-9][0-9]{9}" maxlength="10"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:376)
- selectField('type','Resident type',['Owner','Tenant'],'Tenant') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:376)
- checkField('confirmed','I have verified the handover with both parties.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:376)

### Conditional states / visibility

- !D.confirmed — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:586)
- !f?.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:586)

### Required validation and role guards

- fail('Confirm that the handover was verified.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:586)
- fail('This flat has no active account to transfer.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:586)

### Empty states, confirmations, feedback and dialogs

- toast('Old access revoked. New resident pending.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:586)
- note('The current login is revoked. The flat ledger remains; the new resident must complete signup and approval.','warn','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:376)

### Navigation and data effects

Navigation targets/expressions: 'join-review?id='+c.id.

Reference state collections accessed: claims, flats. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:handover ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:586)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-102"></a>

## 102. Team access — roles

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:377). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Add' | section navigation | 'role-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:377) |
| r.name | record/navigation row | 'role-form?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:377) |
| 'Permissions' | navigation/custom | 'permissions' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:377) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- r.role==='Admin' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:377)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: permissions.

Reference state collections accessed: roles. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-103"></a>

## 103. Team role — role-form

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:378). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'role' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:378) |

### Inputs and selectable options

- <input type="hidden" name="id" value="${r.id\|\|''}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:378)
- field('name','Name',r.name\|\|'','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:378)
- selectField('flat','Flat',S.flats.filter(f=>f.member).map(f=>f.id),r.flat\|\|'A-101') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:378)
- selectField('role','Role',['Admin','Treasurer'],r.role\|\|'Treasurer') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:378)

### Conditional states / visibility

- old?.role==='Admin'&&D.role!=='Admin'&&S.roles.filter(r=>r.role==='Admin').length===1 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:587)
- old — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:587)

### Required validation and role guards

- fail('Keep at least one Admin account.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:587)

### Empty states, confirmations, feedback and dialogs

- toast('Role saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:587)
- note('Use named admin accounts; do not share passwords. The resident account limit is per flat.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:378)

### Navigation and data effects

Navigation targets/expressions: 'roles'.

Reference state collections accessed: flats, roles. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:role ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:587)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-104"></a>

## 104. Permissions — permissions

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:379). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Shared or dynamic controls only | Inspect page definition | No direct control definition | Page link above |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:379)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- note('A · Admin    T · Treasurer    R · Resident. Resident access is limited to their own flat and public community data.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:379)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-105"></a>

## 105. More — more

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ${S.role==='Resident'?'AR':S.role==='Treasurer'?'KR':'SK'} ${esc(currentProfile().name)} ${esc(S.role)} · ${S.role==='Resident'?esc(S.residentFlat):esc(S.apartment.name)} ${I('chevron')} | inline button | profile | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Apartment' | record/navigation row | 'apartment' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Contacts' | record/navigation row | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Documents' | record/navigation row | 'documents' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Service schedule' | record/navigation row | 'services' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Activity log' | record/navigation row | 'audit' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Appearance' | record/navigation row | 'appearance' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Notifications' | record/navigation row | 'notification-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Automation' | record/navigation row | 'automation' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Apartment plan' | record/navigation row | 'subscription' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Security' | record/navigation row | 'security' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Help & about' | record/navigation row | 'help' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Screen library' | record/navigation row | 'screen-map' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Preview controls' | record/navigation row | 'demo-controls' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Cancel' | action | 'closeSheet' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Sign out' | action | 'confirmLogout' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Sign out' | action | 'logout' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381)
- S.role==='Treasurer' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381)
- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:184)
- S.role==='Admin' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:184)
- isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:381)
- danger — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- confirm('Sign out?','Saved apartment records will remain in this demo.','Sign out','confirmLogout') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:498)
- sheet(title,'<p>${esc(text)}</p><div class="actions">${actBtn('Cancel','closeSheet','','secondary')}${actBtn(label,action,danger?'trash':'check',danger?'danger':'',attrs)}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)

### Navigation and data effects

Navigation targets/expressions: profile; 'login'.

Reference state collections accessed: apartment, profile, residentFlat, role, staffProfiles. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:logout ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:498)), action:confirmLogout ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:498)), action:closeSheet ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-106"></a>

## 106. My profile — profile

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:382). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Edit profile' | navigation/custom | 'profile-edit' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:382) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:184)
- S.role==='Admin' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:184)
- resident — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:382)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: profile-edit.

Reference state collections accessed: apartment, profile, residentFlat, role, staffProfiles. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-107"></a>

## 107. Edit profile — profile-edit

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:383). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'profile' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:383) |

### Inputs and selectable options

- field('name','Name',currentProfile().name,'text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:383)
- field('mobile','Mobile',currentProfile().mobile,'text','maxlength="16"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:383)
- field('email','Email · optional',currentProfile().email,'email','maxlength="100"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:383)

### Conditional states / visibility

- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:588)
- f — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:588)
- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:184)
- S.role==='Admin' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:184)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- toast('Profile saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:588)
- note('Flat and owner / tenant changes require admin approval.','','lock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:383)

### Navigation and data effects

Navigation targets/expressions: 'profile'.

Reference state collections accessed: profile, residentFlat, role, staffProfiles. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:profile ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:588)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-108"></a>

## 108. Notifications — notifications

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:808). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Mark all read' | action | 'readAll' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:808) |
| n.title | record/navigation row | 'notification?id='+n.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:808) |
| 'Send notice' | navigation/custom | 'notice-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:808) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- n.flat&&!isStaff()&&n.flat!==S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:311)
- !isStaff()&&n.target?.startsWith('notice?') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:311)
- nt&&!visibleNotice(nt) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:311)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.status!=='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='All flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience.startsWith('Block ') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='Unpaid flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='Selected flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:806)
- !notificationIsRead(n) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:810)
- notificationIsRead(n) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:808)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:808)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- toast('Inbox marked read') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:810)
- empty('You’re all caught up','','bell') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:808)

### Navigation and data effects

Navigation targets/expressions: notice-form.

Reference state collections accessed: notices, notifications, residentFlat, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:readAll ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:810)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-109"></a>

## 109. Notification — notification

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:809). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Open details' | navigation/custom | n.target | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:809) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !n\|\|!visibleNotification(n)\|\|!(n.role==='all'\|\|n.role===S.role\|\|isStaff()&&n.role==='staff') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:809)
- n.flat&&!isStaff()&&n.flat!==S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:311)
- !isStaff()&&n.target?.startsWith('notice?') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:311)
- nt&&!visibleNotice(nt) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:311)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.status!=='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='All flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience.startsWith('Block ') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='Unpaid flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- n.audience==='Selected flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:312)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !notificationIsRead(n) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:809)
- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:806)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- stateHero('bell',n.title,n.time) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:809)

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: notices, notifications, residentFlat, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-110"></a>

## 110. Notifications — notification-settings

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Push notifications' | preference toggle | 'push' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |
| 'Issue updates' | preference toggle | 'issueNotify' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |
| 'Service reminders' | preference toggle | 'serviceNotify' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |
| 'New bill notifications' | preference toggle | 'dueNotify' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |
| 'Unpaid reminders' | preference toggle | 'reminders' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |
| 'Reminder schedule' | navigation/custom | 'reminder-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |
| 'Save quiet hours' | submit | 'quiet-hours' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386) |

### Inputs and selectable options

- field('quietStart','From',preference('quietStart'),'time') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386)
- field('quietEnd','Until',preference('quietEnd'),'time') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386)

### Conditional states / visibility

- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386)
- S.role==='Resident'&&PERSONAL_KEYS.has(key) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:183)
- S.role==='Resident'&&PERSONAL_KEYS.has(key) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:182)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- toast('Quiet hours saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:589)
- note('Push delivery is simulated in the inbox. SMS and WhatsApp are not part of this release.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:386)

### Navigation and data effects

Navigation targets/expressions: reminder-settings; 'notification-settings'.

Reference state collections accessed: residentFlat, residentPrefs, role, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:quiet-hours ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:589)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-111"></a>

## 111. Reminder schedule — reminder-settings

Group: Settings. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:387). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'reminder-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:387) |
| 'Preview recipients' | navigation/custom | 'reminder-preview' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:387) |

### Inputs and selectable options

- field('days','Days of month',S.settings.reminderDays,'text','required placeholder="5, 9, 11, 15"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:387)

### Conditional states / visibility

- !days.length\|\|days.some(v=>!Number.isInteger(v)\|\|v<1\|\|v>28) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:590)

### Required validation and role guards

- fail('Enter comma-separated days from 1 to 28.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:590)

### Empty states, confirmations, feedback and dialogs

- toast('Reminder schedule saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:590)
- note('Comma-separated days, 1–28. Reminders are paused while payment verification is pending.','','bell') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:387)

### Navigation and data effects

Navigation targets/expressions: 'notification-settings'; reminder-preview.

Reference state collections accessed: settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:reminder-settings ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:590)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-112"></a>

## 112. Appearance — appearance

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:388). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ${I('building')} Light | inline button | themeLight | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:388) |
| ${I('building')} Dark | inline button | themeDark | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:388) |
| 'Use device theme' | action | 'themeSystem' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:388) |
| 'Larger text' | preference toggle | 'largeText' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:388) |
| 'Reduce motion' | preference toggle | 'reduceMotion' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:388) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- theme==='system' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:447)
- matchMedia('(prefers-color-scheme: dark)').matches — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:447)
- S.theme==='light' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:388)
- S.theme==='dark' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:388)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: theme, themeMode. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:themeLight ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:492)), action:themeDark ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:492)), action:themeSystem ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:492)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-113"></a>

## 113. Account security — security

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:389). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Change PIN' | record/navigation row | 'change-pin' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:389) |
| 'Access permissions' | record/navigation row | 'permissions' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:389) |
| 'Data & privacy' | record/navigation row | 'privacy' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:389) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Cancel' | action | 'closeSheet' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Sign out' | action | 'confirmLogout' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Sign out' | action | 'logout' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:389) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- danger — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- confirm('Sign out?','Saved apartment records will remain in this demo.','Sign out','confirmLogout') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:498)
- sheet(title,'<p>${esc(text)}</p><div class="actions">${actBtn('Cancel','closeSheet','','secondary')}${actBtn(label,action,danger?'trash':'check',danger?'danger':'',attrs)}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)
- note('This reference does not provide real authentication. Production access must be enforced by the backend.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:389)

### Navigation and data effects

Navigation targets/expressions: 'login'.

Reference state collections accessed: role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:logout ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:498)), action:confirmLogout ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:498)), action:closeSheet ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-114"></a>

## 114. Change PIN — change-pin

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:390). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save' | submit | 'change-pin' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:390) |

### Inputs and selectable options

- field('current','Current PIN','','password','required pattern="[0-9]{4,6}" maxlength="6"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:390)
- field('pin','New PIN','','password','required pattern="[0-9]{4,6}" maxlength="6"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:390)
- field('confirm','Confirm new PIN','','password','required pattern="[0-9]{4,6}" maxlength="6"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:390)

### Conditional states / visibility

- D.pin!==D.confirm — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:591)
- D.pin===D.current — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:591)

### Required validation and role guards

- fail('The new PINs do not match.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:591)
- fail('Use a different new PIN.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:591)

### Empty states, confirmations, feedback and dialogs

- toast('Demo PIN change flow complete') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:591)
- note('Use sample values only in this prototype.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:390)

### Navigation and data effects

Navigation targets/expressions: 'security'.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:change-pin ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:591)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-115"></a>

## 115. Privacy & access — privacy

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:391). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Your flat only' | record/navigation row | 'permissions' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:391) |
| 'Manual verification' | record/navigation row | 'help' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:391) |
| 'Shared totals' | record/navigation row | 'reports' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:391) |
| 'Private requests' | record/navigation row | 'complaints' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:391) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- note('Demo data is stored locally in this browser. Never upload personal or banking records into a shared demo.','','lock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:391)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-116"></a>

## 116. Automation — automation

Group: Settings. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Monthly dues' | record/navigation row | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392) |
| 'Unpaid reminders' | record/navigation row | 'reminder-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392) |
| 'Recurring expenses' | record/navigation row | 'recurring' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392) |
| 'Service reminders' | record/navigation row | 'services' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392) |
| 'Run automation' | action | 'runAutomation' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392) |
| 'Automation log' | navigation/custom | 'automation-log' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- S.settings.reminders — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !S.bills.some(b=>b.flat===f.id&&b.month===m) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:456)
- S.settings.dueNotify — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:456)
- count — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:456)
- S.settings.reminders&&S.settings.reminderDays.split(',').map(v=>Number(v.trim())).includes(Number(today.slice(-2))) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- !S.notifications.some(n=>n.key===key) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- b.latePolicy?.enabled&&due(b)>0&&!billPending(b)&&!b.lateFee — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- new Date(today+'T12:00:00')>threshold — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- n.status==='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- n.audience?.startsWith('Block ') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- n.audience==='Selected flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- n.audience==='Unpaid flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- !recipients.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- n.push — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- remaining>=0&&remaining<=24*3600000&&!S.notifications.some(n=>n.key===key) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:941)
- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:670)
- refDate()>=S.subscription.firstEnd&&firstInvoice().status==='Offer pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:942)
- !sub.offerOptIn\|\|sub.firstCredit\|\|refs.length<2\|\|!eligibleWindow() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:849)
- inv.status==='Paid' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:850)
- r.flats<=10 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:851)
- r.flats<=30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:851)
- n — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:943)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Already published. Edit and explicitly republish to notify again.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- fail('No active recipients in this audience.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)

### Empty states, confirmations, feedback and dialogs

- toast('Demo tasks processed') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- note('This browser demo only runs tasks while open or when Run is selected. Background jobs belong in the backend.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:392)

### Navigation and data effects

Navigation targets/expressions: 'automation-log'; automation-log.

Reference state collections accessed: audit, bills, events, flats, notices, notifications, offline, recurring, referenceNow, services, settings, subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:runAutomation ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:948)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-117"></a>

## 117. Automation activity — automation-log

Group: Settings. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:393). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| a.action | record/navigation row | 'audit-detail?id='+a.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:393) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No recent runs','','repeat') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:393)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: audit. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-118"></a>

## 118. Activity log — audit

Group: Settings. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:394). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| a.action | record/navigation row | 'audit-detail?id='+a.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:394) |

### Inputs and selectable options

- search('Search activity') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:394)

### Conditional states / visibility

- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No activity found','','history') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:394)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: audit. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-119"></a>

## 119. Activity detail — audit-detail

Group: Settings. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:395). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Shared or dynamic controls only | Inspect page definition | No direct control definition | Page link above |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: audit. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-120"></a>

## 120. Subscription & billing — subscription

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:855). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Refer 2 apartments' | record/navigation row | 'referrals' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:855) |
| 'Invoices & receipts' | record/navigation row | 'subscription-invoices' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:855) |
| 'Plan charges' | record/navigation row | 'subscription-plans' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:855) |
| 'Renewal status' | record/navigation row | 'subscription-renewal' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:855) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- n<=10 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)
- n<=30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)
- refDate()>=s.firstEnd — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:854)
- s.invoices.some(i=>i.period!=='First month'&&subBalance(i)>0) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:854)
- ['Paid','Waived','Refund due','Refunded'].includes(i.status) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:845)
- s.firstCredit — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:854)
- s.offerOptIn — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:854)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- s.firstCredit — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:855)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- note('App subscription only. Residents’ maintenance is a separate payment. No live checkout here.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:855)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: flats, referenceNow, subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-121"></a>

## 121. Help & about — help

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:397). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Payment status' | record/navigation row | 'payment-help' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:397) |
| 'Support' | record/navigation row | 'support' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:397) |
| 'Privacy & access' | record/navigation row | 'privacy' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:397) |
| 'About ApartmentCare' | record/navigation row | 'about' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:397) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-122"></a>

## 122. Payment status guide — payment-help

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:398). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'In review' | record/navigation row | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:398) |
| 'Paid' | record/navigation row | 'history' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:398) |
| 'Rejected' | record/navigation row | 'dues' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:398) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- note('Manual UPI / bank transfer cannot be verified automatically without a bank integration. Receipts follow human approval.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:398)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-123"></a>

## 123. Get help — support

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:399). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Done' | navigation/custom | 'help' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:592) |
| 'Submit request' | submit | 'support' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:399) |
| 'Apartment contacts' | navigation/custom | 'contacts' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:399) |

### Inputs and selectable options

- selectField('topic','Topic',['Payment approval','Account access','Flat mapping','Something else']) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:399)
- field('message','Message','','textarea','required maxlength="1000"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:399)

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- sheet('Request saved','${stateHero('checkCircle','We have your message.','Demo request saved locally.')}<div class="mt">${btn('Done','help','check','full')}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:592)
- stateHero('checkCircle','We have your message.','Demo request saved locally.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:592)
- note('Demo request is stored locally. No external support ticket is sent.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:399)

### Navigation and data effects

Navigation targets/expressions: help; contacts.

Reference state collections accessed: support. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:support ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:592)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-124"></a>

## 124. About — about

Group: Settings. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:400). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Screen library' | navigation/custom | 'screen-map' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:400) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('building','ApartmentCare','Android mobile · UI reference 1.0') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:400)

### Navigation and data effects

Navigation targets/expressions: screen-map.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-125"></a>

## 125. You’re offline — offline

Group: System states. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:401). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Retry connection' | action | 'goOnline' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:401) |
| 'Back to home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:401) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('wifi','Connection paused','Saved information is still available.','amber') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:401)
- toast('Viewing demo data') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:497)

### Navigation and data effects

Navigation targets/expressions: 'home'; home.

Reference state collections accessed: offline. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:goOnline ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:497)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-126"></a>

## 126. Session expired — session-expired

Group: System states. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:402). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Sign in' | navigation/custom | 'login' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:402) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Sign in again','Your session expired. Your records are safe.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:402)

### Navigation and data effects

Navigation targets/expressions: login.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-127"></a>

## 127. Access restricted — access-denied

Group: System states. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:403). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back to home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:403) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('shield','This page is restricted','Use an account with the right permission.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:403)

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-128"></a>

## 128. Nothing here yet — empty-state

Group: System states. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:404). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back to home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:404) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('A fresh start','Your first record will appear here.','folder',btn('Back to home','home','home','secondary full')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:404)

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-129"></a>

## 129. Something went wrong — error-state

Group: System states. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:405). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Try again' | action | 'retryPage' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:405) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('alert','Couldn’t load this page','Try again. Your saved work is still here.','amber') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:405)
- toast('Demo view reloaded') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:497)

### Navigation and data effects

Navigation targets/expressions: 'home'.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:retryPage ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:497)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-130"></a>

## 130. Page not found — not-found

Group: System states. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:406). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Open screen library' | navigation/custom | 'screen-map' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:406) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('search','That page isn’t here','Choose a screen from the library.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:406)

### Navigation and data effects

Navigation targets/expressions: screen-map.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-131"></a>

## 131. Screen library — screen-map

Group: Reference tools. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:407). REFERENCE ONLY: do not ship as production behavior.

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ${String(Object.keys(routes).indexOf(r.id)+1).padStart(3,'0')} ${esc(r.title)} ${r.roles.length===3?'ALL':r.roles.map(r=>r[0]).join(' / ')} ${I('chevron','chevron')} | inline button | mapOpen | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:407) |

### Inputs and selectable options

- search('Search screens') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:407)

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- rs.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:407)
- r&&!r.roles.includes(S.role) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:496)
- S.role==='Resident'&&['proof','payment-status'].includes(id) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:487)
- p — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:487)
- r.roles.length===3 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:407)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: samplePath(d.id).

Reference state collections accessed: payments, residentFlat, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:mapOpen ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:496)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-132"></a>

## 132. Preview controls — demo-controls

Group: Reference tools. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408). REFERENCE ONLY: do not ship as production behavior.

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| r | action | 'setRole' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |
| 'Offline mode' | record/navigation row | 'offline' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |
| 'Session expired' | record/navigation row | 'session-expired' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |
| 'Access denied' | record/navigation row | 'access-denied' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |
| 'Empty state' | record/navigation row | 'empty-state' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |
| 'Error & retry' | record/navigation row | 'error-state' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Cancel' | action | 'closeSheet' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Reset' | action | 'confirmReset' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Reset demo data' | action | 'resetDemo' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !['Admin','Treasurer','Resident'].includes(role) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:446)
- r==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408)
- S.role===r — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408)
- i%3===0 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:154)
- i===1 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:155)
- danger — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- confirm('Reset demo data?','This restores all sample records and clears your local test changes.','Reset','confirmReset','',true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:499)
- toast('Sample data restored') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:500)
- sheet(title,'<p>${esc(text)}</p><div class="actions">${actBtn('Cancel','closeSheet','','secondary')}${actBtn(label,action,danger?'trash':'check',danger?'danger':'',attrs)}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)
- note('Reset restores sample records for every preview role. It does not affect any live account.','','info') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:408)

### Navigation and data effects

Navigation targets/expressions: 'home'.

Reference state collections accessed: residentFlat, role, theme, themeMode. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:setRole ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:492)), action:resetDemo ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:499)), action:confirmReset ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:500)), action:closeSheet ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-133"></a>

## 133. Request attachment — complaint-photo

Group: Services. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:410). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !c\|\|(!isStaff()&&c.flat!==S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:410)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !a — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- a.data?.startsWith('data:image') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)
- a.size — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- empty('No attachment','A file has not been added.','file') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:326)

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: complaints, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-134"></a>

## 134. Loading — loading-state

Group: System states. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:411). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Continue to loaded view' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:411) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-135"></a>

## 135. Events & bookings — events

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'New event' | navigation | 'event-types' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| 'Calendar' | navigation | 'event-calendar' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| 'Parking' | navigation | 'parking-availability' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| 'Rules' | navigation | 'booking-rules' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| ['Upcoming','Mine','Past','All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| 'New event' | navigation/custom | 'event-types' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| 'Requests' | navigation/custom | 'booking-requests' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |
| 'Manage spaces' | navigation/custom | 'resources' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717) |

### Inputs and selectable options

- search('Search events') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717)

### Conditional states / visibility

- f==='Mine' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717)
- f==='Upcoming' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717)
- f==='Past' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No bookings yet','Choose a date and a space.','calendar',btn('New event','event-types','plus','secondary full')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:717)

### Navigation and data effects

Navigation targets/expressions: event-types; booking-requests; resources.

Reference state collections accessed: events, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-136"></a>

## 136. What are you planning? — event-types

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:718). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ${iconBox(i,'violet')} ${esc(t)} | inline button | event-form?type=${encodeURIComponent(t)} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:718) |
| 'Other event' | navigation/custom | 'event-form?type=Other' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:718) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: event-form?type=${encodeURIComponent(t)}; event-form?type=Other.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-137"></a>

## 137. Request an event — event-form

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Check availability' | submit | 'event-draft' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719) |
| 'Booking rules' | navigation/custom | 'booking-rules' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- selectField('type','Event type',['Birthday','Wedding','Family gathering','Festival','Meeting','Guest parking','Other'],e.type\|\|Q.type\|\|'Birthday') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('title','Event name',e.title\|\|'','text','required maxlength="80" placeholder="e.g. Birthday celebration"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- selectField('flat','Organiser flat',S.flats.filter(f=>f.member).map(f=>f.id),e.flat\|\|S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('start','Starts',e.start\|\|'2026-09-22T17:00','datetime-local','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('end','Ends',e.end\|\|'2026-09-22T20:00','datetime-local','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- selectField('space','Common space',[['','Guest parking only'],...commonSpaces().map(r=>[r.id,r.name+' · '+r.capacity+' guests'])],e.space??'SPACE-1') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('guests','Guests',e.guests\|\|20,'number','required min="1" max="'+S.bookingRules.maxGuests+'"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('cars','Car spaces',e.cars\|\|0,'number','min="0" max="'+S.bookingRules.maxCars+'" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('bikes','Bike spaces',e.bikes\|\|0,'number','min="0" max="'+S.bookingRules.maxBikes+'" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('notes','Note · optional',e.notes\|\|'','textarea','maxlength="500"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- checkField('rules','I agree to the booking rules.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719)

### Conditional states / visibility

- Q.id — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719)
- e&&!eventOwner(e) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- e&&['Completed','Cancelled','Rejected'].includes(e.status) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- draft.event&&draft.event.id===(Q.id\|\|'') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719)
- e?.status==='Approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719)
- !D.rules — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:743)
- D.id — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:743)
- old&&!eventOwner(old) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:743)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:699)
- !Number.isInteger(n)\|\|n<min\|\|n>max — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- !flat(e.flat)?.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:700)
- isNaN(start)\|\|isNaN(end)\|\|start<=now\|\|end<=start — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:702)
- (end-start)/3600000>S.bookingRules.maxHours — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:703)
- start>dayLimit — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:704)
- e.space — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:707)
- e.start.slice(0,10)!==e.end.slice(0,10)\|\|(qStart>qEnd?(a<qEnd\|\|b>qStart):(a<qEnd&&b>qStart)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- qStart>qEnd — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- !e.space&&!e.cars&&!e.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:709)
- e.space&&resource(e.space)?.type!=='Common space' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:710)
- old&&e.flat!==old.flat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:743)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)

### Required validation and role guards

- fail('Accept the booking rules.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:743)
- fail('You cannot change this request.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:743)
- requiredText(d.title,'Event name',80) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:699)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- fail(label+' must be between '+min+' and '+max+'.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- fail('Choose an active member flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:700)
- fail('Choose a future start and a later end time.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:702)
- fail('Booking exceeds the maximum duration.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:703)
- fail('Choose a date within '+S.bookingRules.advanceDays+' days.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:704)
- fail('Choose a common-space time outside the quiet hours.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- fail('Choose a common space or request guest parking.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:709)
- fail('Choose a bookable common space.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:710)
- fail('The organiser flat cannot be changed.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:743)

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- empty('This event is closed') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719)
- note('Your approved booking stays reserved until the change is approved.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:719)

### Navigation and data effects

Navigation targets/expressions: home; 'event-availability'; booking-rules.

Reference state collections accessed: bookingRules, events, flats, referenceNow, residentFlat, resources, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:event-draft ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:743)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-138"></a>

## 138. Choose your parking — event-availability

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'New event' | navigation/custom | 'event-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720) |
| 'Review request' | submit | 'event-allocation' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720) |
| 'Change date or space' | navigation/custom | 'event-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720) |

### Inputs and selectable options

- field('cars','Car spaces',e.cars,'number','required min="0" max="'+S.bookingRules.maxCars+'"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720)
- field('bikes','Bike spaces',e.bikes,'number','required min="0" max="'+S.bookingRules.maxBikes+'"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720)

### Conditional states / visibility

- !e — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- c.space&&!availableResource(resource(c.space),c,ignore) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:685)
- !r\|\|!r.enabled\|\|r.type==='Protected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:676)
- r.type==='Private car'&&(!r.consent\|\|new Date(r.shareStart).getTime()>a\|\|new Date(r.shareEnd).getTime()<b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:678)
- S.resourceBlocks.some(x=>[x.resource,...(resource(x.resource)?.blocks\|\|[])].some(id=>[r.id,...(r.blocks\|\|[])].includes(id))&&a<new Date(x.end).getTime()&&new Date(x.start).getTime()<b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:679)
- space&&Number(c.guests)>space.capacity — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:687)
- cars.length<c.cars — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:691)
- bikes.length<c.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:692)
- c.allocation?.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:693)
- allocated — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:694)
- new Set(allocated).size!==allocated.length\|\|own.some(r=>!r\|\|blocked.has(r.id)\|\|!availableResource(r,c,ignore)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:694)
- own.filter(r=>r&&['Guest car','Private car'].includes(r.type)).length!==c.cars\|\|own.filter(r=>r?.type==='Guest bike').length!==c.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:695)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- c.ok — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720)
- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:670)
- c.conflicts.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720)
- !draft.event — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:744)
- !Number.isInteger(n)\|\|n<min\|\|n>max — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- !c.ok — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:744)
- r.type.includes('car') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720)

### Required validation and role guards

- fail('Start an event request.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:744)
- fail(label+' must be between '+min+' and '+max+'.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- fail(c.conflicts.join(' ')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:744)

### Empty states, confirmations, feedback and dialogs

- empty('Start with an event','', 'calendar',btn('New event','event-form','plus')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720)
- note(c.conflicts.join(' '),'warn','alert') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720)
- empty('No parking spaces') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720)
- note('Space is reserved only after admin approval. Availability is checked again.','','lock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:720)

### Navigation and data effects

Navigation targets/expressions: event-form; 'event-review'.

Reference state collections accessed: bookingRules, events, resourceBlocks, resources. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:event-allocation ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:744)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-139"></a>

## 139. Review request — event-review

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:721). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Edit' | navigation/custom | 'event-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:721) |
| 'Submit' | action | 'submitEvent' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:721) |
| 'New event' | navigation/custom | 'event-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:721) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- draft.event — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:721)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- e.pendingChange — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- eventFull(e) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:670)
- e.allocation?.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !d — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:699)
- !Number.isInteger(n)\|\|n<min\|\|n>max — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- !flat(e.flat)?.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:700)
- isNaN(start)\|\|isNaN(end)\|\|start<=now\|\|end<=start — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:702)
- (end-start)/3600000>S.bookingRules.maxHours — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:703)
- start>dayLimit — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:704)
- e.space — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:707)
- e.start.slice(0,10)!==e.end.slice(0,10)\|\|(qStart>qEnd?(a<qEnd\|\|b>qStart):(a<qEnd&&b>qStart)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- qStart>qEnd — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- !e.space&&!e.cars&&!e.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:709)
- e.space&&resource(e.space)?.type!=='Common space' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:710)
- c.space&&!availableResource(resource(c.space),c,ignore) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:685)
- !r\|\|!r.enabled\|\|r.type==='Protected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:676)
- r.type==='Private car'&&(!r.consent\|\|new Date(r.shareStart).getTime()>a\|\|new Date(r.shareEnd).getTime()<b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:678)
- S.resourceBlocks.some(x=>[x.resource,...(resource(x.resource)?.blocks\|\|[])].some(id=>[r.id,...(r.blocks\|\|[])].includes(id))&&a<new Date(x.end).getTime()&&new Date(x.start).getTime()<b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:679)
- space&&Number(c.guests)>space.capacity — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:687)
- cars.length<c.cars — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:691)
- bikes.length<c.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:692)
- c.allocation?.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:693)
- allocated — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:694)
- new Set(allocated).size!==allocated.length\|\|own.some(r=>!r\|\|blocked.has(r.id)\|\|!availableResource(r,c,ignore)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:694)
- own.filter(r=>r&&['Guest car','Private car'].includes(r.type)).length!==c.cars\|\|own.filter(r=>r?.type==='Guest bike').length!==c.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:695)
- !check.ok — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)
- d.id — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)
- old&&!eventOwner(old) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)
- old?.status==='Approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)
- old — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)
- ['Cancelled','Completed','Rejected'].includes(old.status) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Start a request first.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)
- requiredText(d.title,'Event name',80) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:699)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- fail(label+' must be between '+min+' and '+max+'.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- fail('Choose an active member flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:700)
- fail('Choose a future start and a later end time.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:702)
- fail('Booking exceeds the maximum duration.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:703)
- fail('Choose a date within '+S.bookingRules.advanceDays+' days.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:704)
- fail('Choose a common-space time outside the quiet hours.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- fail('Choose a common space or request guest parking.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:709)
- fail('Choose a bookable common space.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:710)
- fail(check.conflicts.join(' ')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)
- fail('Access denied.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)
- fail('Event is closed.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)

### Empty states, confirmations, feedback and dialogs

- empty('No request to review','','calendar',btn('New event','event-form','plus')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:721)

### Navigation and data effects

Navigation targets/expressions: event-form; 'event-submitted?id='+e.id.

Reference state collections accessed: bookingRules, events, offline, referenceNow, residentFlat, resourceBlocks, resources, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:submitEvent ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-140"></a>

## 140. Request sent — event-submitted

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:722). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Track request' | navigation/custom | 'event?id='+(Q.id\|\|draft.lastEvent\|\|'EV-1') | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:722) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('checkCircle','Request sent','Your admin will review the space and parking.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:722)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-141"></a>

## 141. Event details — event

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Alternative available' | record/navigation row | 'event-alternative?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:661) |
| 'Edit request' | navigation/custom | 'event-form?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723) |
| 'Cancel' | navigation/custom | 'event-cancel?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723) |
| 'Review request' | navigation/custom | 'booking-review?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723) |
| 'Complete event' | navigation/custom | 'event-complete?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723) |
| 'Calendar' | navigation/custom | 'event-calendar' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !visibleEvent(e) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- e.pendingChange — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- eventFull(e) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:670)
- e.allocation?.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- e.pendingChange — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723)
- e.offer — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723)
- eventFull(e)&&e.notes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723)
- e.reason&&eventFull(e) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723)
- eventOwner(e)&&!['Cancelled','Rejected','Completed'].includes(e.status) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723)
- isAdmin()&&(e.status==='Pending'\|\|e.pendingChange) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723)
- isAdmin()&&e.status==='Approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- note('Change requested. Your original reservation is still active.','','clock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723)
- note(e.reason,'warn') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:723)

### Navigation and data effects

Navigation targets/expressions: home; event-calendar.

Reference state collections accessed: events, residentFlat, resources, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-142"></a>

## 142. Booking approvals — booking-requests

Group: Community. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:724). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ['Pending','Approved','All'] | filter tabs | 'filter' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:724) |
| eventFull(e)?e.title:e.type+' booking' | record/navigation row | 'event?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:713) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- eventFull(e) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:713)
- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:670)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- e.pendingChange — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:713)
- e.status==='Approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:713)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No requests waiting','','checkCircle') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:724)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: events, residentFlat, resources. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-143"></a>

## 143. Review booking — booking-review

Group: Community. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Offer alternative' | navigation/custom | 'booking-alternative?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725) |
| 'Reject' | navigation/custom | 'booking-reject?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725) |
| 'Approve booking' | submit | 'approve-booking' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- checkField('verified','I checked the space, parking and apartment rules.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725)
- checkField('announce','Post a common-area notice',false) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725)

### Conditional states / visibility

- !e — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- c.space&&!availableResource(resource(c.space),c,ignore) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:685)
- !r\|\|!r.enabled\|\|r.type==='Protected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:676)
- r.type==='Private car'&&(!r.consent\|\|new Date(r.shareStart).getTime()>a\|\|new Date(r.shareEnd).getTime()<b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:678)
- S.resourceBlocks.some(x=>[x.resource,...(resource(x.resource)?.blocks\|\|[])].some(id=>[r.id,...(r.blocks\|\|[])].includes(id))&&a<new Date(x.end).getTime()&&new Date(x.start).getTime()<b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:679)
- space&&Number(c.guests)>space.capacity — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:687)
- cars.length<c.cars — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:691)
- bikes.length<c.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:692)
- c.allocation?.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:693)
- allocated — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:694)
- new Set(allocated).size!==allocated.length\|\|own.some(r=>!r\|\|blocked.has(r.id)\|\|!availableResource(r,c,ignore)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:694)
- own.filter(r=>r&&['Guest car','Private car'].includes(r.type)).length!==c.cars\|\|own.filter(r=>r?.type==='Guest bike').length!==c.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:695)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- e.pendingChange — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- eventFull(e) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:670)
- e.allocation?.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- e.pendingChange — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725)
- chk.ok — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !D.verified — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:745)
- !e\|\|(e.status!=='Pending'&&!e.pendingChange) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:745)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:699)
- !Number.isInteger(n)\|\|n<min\|\|n>max — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- !flat(e.flat)?.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:700)
- isNaN(start)\|\|isNaN(end)\|\|start<=now\|\|end<=start — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:702)
- (end-start)/3600000>S.bookingRules.maxHours — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:703)
- start>dayLimit — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:704)
- e.space — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:707)
- e.start.slice(0,10)!==e.end.slice(0,10)\|\|(qStart>qEnd?(a<qEnd\|\|b>qStart):(a<qEnd&&b>qStart)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- qStart>qEnd — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- !e.space&&!e.cars&&!e.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:709)
- e.space&&resource(e.space)?.type!=='Common space' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:710)
- !check.ok — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:745)
- D.announce — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:745)
- n.status==='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- n.audience?.startsWith('Block ') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- n.audience==='Selected flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- n.audience==='Unpaid flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- !recipients.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- n.push — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:745)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Confirm the space and parking checks.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:745)
- fail('This request is no longer pending.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:745)
- requiredText(d.title,'Event name',80) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:699)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- fail(label+' must be between '+min+' and '+max+'.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- fail('Choose an active member flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:700)
- fail('Choose a future start and a later end time.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:702)
- fail('Booking exceeds the maximum duration.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:703)
- fail('Choose a date within '+S.bookingRules.advanceDays+' days.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:704)
- fail('Choose a common-space time outside the quiet hours.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- fail('Choose a common space or request guest parking.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:709)
- fail('Choose a bookable common space.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:710)
- fail(check.conflicts.join(' ')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:745)
- fail('Already published. Edit and explicitly republish to notify again.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- fail('No active recipients in this audience.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)

### Empty states, confirmations, feedback and dialogs

- empty('Request unavailable') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725)
- note('Space and requested parking are available.','','checkCircle') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725)
- note(chk.conflicts.join(' '),'warn','alert') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:725)
- toast('Booking approved · spaces reserved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:745)

### Navigation and data effects

Navigation targets/expressions: 'event?id='+e.id.

Reference state collections accessed: bills, bookingRules, events, flats, notices, referenceNow, residentFlat, resourceBlocks, resources. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:approve-booking ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:745)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-144"></a>

## 144. Offer an alternative — booking-alternative

Group: Community. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:726). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Send offer' | submit | 'offer-booking' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:726) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- selectField('type','Event type',['Birthday','Wedding','Family gathering','Festival','Meeting','Guest parking','Other'],e.type\|\|Q.type\|\|'Birthday') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('title','Event name',e.title\|\|'','text','required maxlength="80" placeholder="e.g. Birthday celebration"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- selectField('flat','Organiser flat',S.flats.filter(f=>f.member).map(f=>f.id),e.flat\|\|S.residentFlat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('start','Starts',e.start\|\|'2026-09-22T17:00','datetime-local','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('end','Ends',e.end\|\|'2026-09-22T20:00','datetime-local','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- selectField('space','Common space',[['','Guest parking only'],...commonSpaces().map(r=>[r.id,r.name+' · '+r.capacity+' guests'])],e.space??'SPACE-1') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('guests','Guests',e.guests\|\|20,'number','required min="1" max="'+S.bookingRules.maxGuests+'"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('cars','Car spaces',e.cars\|\|0,'number','min="0" max="'+S.bookingRules.maxCars+'" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('bikes','Bike spaces',e.bikes\|\|0,'number','min="0" max="'+S.bookingRules.maxBikes+'" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('notes','Note · optional',e.notes\|\|'','textarea','maxlength="500"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- field('reason','Reason','','textarea','required maxlength="300"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:726)

### Conditional states / visibility

- e — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:726)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !e\|\|(e.status!=='Pending'&&!e.pendingChange&&e.status!=='Alternative offered') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:746)
- D.flat!==e.flat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:746)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:699)
- !Number.isInteger(n)\|\|n<min\|\|n>max — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- !flat(e.flat)?.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:700)
- isNaN(start)\|\|isNaN(end)\|\|start<=now\|\|end<=start — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:702)
- (end-start)/3600000>S.bookingRules.maxHours — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:703)
- start>dayLimit — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:704)
- e.space — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:707)
- e.start.slice(0,10)!==e.end.slice(0,10)\|\|(qStart>qEnd?(a<qEnd\|\|b>qStart):(a<qEnd&&b>qStart)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- qStart>qEnd — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- !e.space&&!e.cars&&!e.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:709)
- e.space&&resource(e.space)?.type!=='Common space' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:710)
- c.space&&!availableResource(resource(c.space),c,ignore) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:685)
- !r\|\|!r.enabled\|\|r.type==='Protected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:676)
- r.type==='Private car'&&(!r.consent\|\|new Date(r.shareStart).getTime()>a\|\|new Date(r.shareEnd).getTime()<b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:678)
- S.resourceBlocks.some(x=>[x.resource,...(resource(x.resource)?.blocks\|\|[])].some(id=>[r.id,...(r.blocks\|\|[])].includes(id))&&a<new Date(x.end).getTime()&&new Date(x.start).getTime()<b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:679)
- space&&Number(c.guests)>space.capacity — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:687)
- cars.length<c.cars — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:691)
- bikes.length<c.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:692)
- c.allocation?.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:693)
- allocated — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:694)
- new Set(allocated).size!==allocated.length\|\|own.some(r=>!r\|\|blocked.has(r.id)\|\|!availableResource(r,c,ignore)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:694)
- own.filter(r=>r&&['Guest car','Private car'].includes(r.type)).length!==c.cars\|\|own.filter(r=>r?.type==='Guest bike').length!==c.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:695)
- !c.ok — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:746)
- e.status!=='Approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:746)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:715)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:746)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('No pending request to change.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:746)
- fail('Keep the original organiser flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:746)
- requiredText(d.title,'Event name',80) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:699)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- fail(label+' must be between '+min+' and '+max+'.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- fail('Choose an active member flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:700)
- fail('Choose a future start and a later end time.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:702)
- fail('Booking exceeds the maximum duration.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:703)
- fail('Choose a date within '+S.bookingRules.advanceDays+' days.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:704)
- fail('Choose a common-space time outside the quiet hours.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- fail('Choose a common space or request guest parking.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:709)
- fail('Choose a bookable common space.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:710)
- requiredText(D.reason,'Reason',300) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:746)
- fail(c.conflicts.join(' ')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:746)

### Empty states, confirmations, feedback and dialogs

- toast('Alternative sent to resident') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:746)
- empty('Request unavailable') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:726)

### Navigation and data effects

Navigation targets/expressions: 'event?id='+e.id.

Reference state collections accessed: bookingRules, events, flats, referenceNow, residentFlat, resourceBlocks, resources. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:offer-booking ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:746)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-145"></a>

## 145. Alternative offered — event-alternative

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:727). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Change request' | navigation/custom | 'event-form?id='+e.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:727) |
| 'Accept' | action | 'acceptEventOffer' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:727) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !e\|\|!eventFull(e)\|\|!e.offer — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:727)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- e.pendingChange — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- eventFull(e) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:670)
- e.allocation?.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- e.flat===S.residentFlat&&S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:727)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !e?.offer\|\|S.role!=='Resident'\|\|e.flat!==S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:761)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:699)
- !Number.isInteger(n)\|\|n<min\|\|n>max — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- !flat(e.flat)?.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:700)
- isNaN(start)\|\|isNaN(end)\|\|start<=now\|\|end<=start — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:702)
- (end-start)/3600000>S.bookingRules.maxHours — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:703)
- start>dayLimit — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:704)
- e.space — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:707)
- e.start.slice(0,10)!==e.end.slice(0,10)\|\|(qStart>qEnd?(a<qEnd\|\|b>qStart):(a<qEnd&&b>qStart)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- qStart>qEnd — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- !e.space&&!e.cars&&!e.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:709)
- e.space&&resource(e.space)?.type!=='Common space' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:710)
- c.space&&!availableResource(resource(c.space),c,ignore) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:685)
- !r\|\|!r.enabled\|\|r.type==='Protected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:676)
- r.type==='Private car'&&(!r.consent\|\|new Date(r.shareStart).getTime()>a\|\|new Date(r.shareEnd).getTime()<b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:678)
- S.resourceBlocks.some(x=>[x.resource,...(resource(x.resource)?.blocks\|\|[])].some(id=>[r.id,...(r.blocks\|\|[])].includes(id))&&a<new Date(x.end).getTime()&&new Date(x.start).getTime()<b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:679)
- space&&Number(c.guests)>space.capacity — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:687)
- cars.length<c.cars — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:691)
- bikes.length<c.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:692)
- c.allocation?.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:693)
- allocated — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:694)
- new Set(allocated).size!==allocated.length\|\|own.some(r=>!r\|\|blocked.has(r.id)\|\|!availableResource(r,c,ignore)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:694)
- own.filter(r=>r&&['Guest car','Private car'].includes(r.type)).length!==c.cars\|\|own.filter(r=>r?.type==='Guest bike').length!==c.bikes — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:695)
- !c.ok — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:761)

### Required validation and role guards

- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:761)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Only the organiser can accept.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:761)
- requiredText(d.title,'Event name',80) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:699)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- fail(label+' must be between '+min+' and '+max+'.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- fail('Choose an active member flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:700)
- fail('Choose a future start and a later end time.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:702)
- fail('Booking exceeds the maximum duration.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:703)
- fail('Choose a date within '+S.bookingRules.advanceDays+' days.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:704)
- fail('Choose a common-space time outside the quiet hours.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:708)
- fail('Choose a common space or request guest parking.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:709)
- fail('Choose a bookable common space.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:710)
- fail('Availability changed. '+c.conflicts.join(' ')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:761)

### Empty states, confirmations, feedback and dialogs

- empty('No alternative offered') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:727)
- note(e.offer.reason\|\|'Review the changed space and time.','','calendar') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:727)
- note('The alternative is not reserved until accepted. Availability is rechecked.','','lock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:727)
- toast('Alternative accepted · reserved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:761)
- note('Waiting for the resident to accept.','','clock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:727)

### Navigation and data effects

Navigation targets/expressions: 'event?id='+id.

Reference state collections accessed: bookingRules, events, offline, referenceNow, residentFlat, resourceBlocks, resources, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:acceptEventOffer ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:761)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-146"></a>

## 146. Reject request — booking-reject

Group: Community. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:728). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Reject request' | submit | 'reject-booking' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:728) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- field('reason','Reason','','textarea','required maxlength="300"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:728)

### Conditional states / visibility

- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !e\|\|(e.status!=='Pending'&&e.status!=='Alternative offered'&&!e.pendingChange) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:747)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- e.pendingChange — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:747)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:747)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Request is no longer pending.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:747)
- requiredText(D.reason,'Reason',300) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:747)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)

### Empty states, confirmations, feedback and dialogs

- toast('Decision saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:747)
- note('For a change request, the existing approved booking stays active.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:728)

### Navigation and data effects

Navigation targets/expressions: 'event?id='+e.id.

Reference state collections accessed: events, referenceNow. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:reject-booking ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:747)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-147"></a>

## 147. Cancel booking — event-cancel

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:729). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Cancel booking' | submit | 'cancel-booking' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:729) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- field('reason','Reason','','textarea','required maxlength="300"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:729)
- checkField('confirmed','Cancel this booking and release the spaces.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:729)

### Conditional states / visibility

- !eventOwner(e) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:729)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- e.pendingChange — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- eventFull(e) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:670)
- e.allocation?.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:714)
- !eventOwner(e) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:748)
- !D.confirmed — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:748)
- ['Cancelled','Completed','Rejected'].includes(e.status) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:748)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)

### Required validation and role guards

- fail('Only the organiser or admin can cancel.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:748)
- fail('Confirm cancellation.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:748)
- fail('Event is already closed.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:748)
- requiredText(D.reason,'Reason',300) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:748)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- toast('Cancelled · reservation released') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:748)

### Navigation and data effects

Navigation targets/expressions: home; 'event?id='+e.id.

Reference state collections accessed: events, referenceNow, residentFlat, resources, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:cancel-booking ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:748)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-148"></a>

## 148. Complete event — event-complete

Group: Community. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:730). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Complete' | submit | 'complete-booking' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:730) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- checkField('checked','The event has ended and spaces are clear.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:730)
- field('note','Completion note','','textarea','maxlength="300"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:730)

### Conditional states / visibility

- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !e\|\|e.status!=='Approved' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:749)
- new Date(S.referenceNow)<new Date(e.end) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:749)
- !D.checked — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:749)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:749)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Only approved events can be completed.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:749)
- fail('The event has not ended. Use the preview clock to test completion.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:749)
- fail('Confirm the spaces are clear.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:749)

### Empty states, confirmations, feedback and dialogs

- toast('Event completed') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:749)

### Navigation and data effects

Navigation targets/expressions: 'event?id='+e.id.

Reference state collections accessed: events, referenceNow. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:complete-booking ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:749)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-149"></a>

## 149. Booking calendar — event-calendar

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:731). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ${new Date(x+'T12:00').toLocaleDateString('en-IN',{weekday:'short'})} ${x.slice(-2)} ${S.events.some(e=>e.date===x&&e.status==='Approved')?' ':''} | inline button | event-calendar?date=${x} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:731) |
| 'Show date' | submit | 'calendar-date' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:731) |
| 'Request a space' | navigation/custom | 'event-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:731) |

### Inputs and selectable options

- field('date','View date',d,'date','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:731)

### Conditional states / visibility

- x===d — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:731)
- S.events.some(e=>e.date===x&&e.status==='Approved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:731)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No bookings on this day','','calendar') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:731)

### Navigation and data effects

Navigation targets/expressions: event-calendar?date=${x}; 'event-calendar?date='+D.date; event-form.

Reference state collections accessed: events, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:calendar-date ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:750)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-150"></a>

## 150. Guest parking — parking-availability

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Check spaces' | submit | 'parking-search' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732) |
| 'Request parking' | navigation/custom | 'event-form?type=Guest%20parking' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732) |
| 'Share my parking' | navigation/custom | 'parking-share' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732) |

### Inputs and selectable options

- field('start','From',c.start,'datetime-local','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732)
- field('end','Until',c.end,'datetime-local','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732)

### Conditional states / visibility

- !D.start\|\|!D.end\|\|D.end<=D.start — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:751)
- !r\|\|!r.enabled\|\|r.type==='Protected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:676)
- r.type==='Private car'&&(!r.consent\|\|new Date(r.shareStart).getTime()>a\|\|new Date(r.shareEnd).getTime()<b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:678)
- S.resourceBlocks.some(x=>[x.resource,...(resource(x.resource)?.blocks\|\|[])].some(id=>[r.id,...(r.blocks\|\|[])].includes(id))&&a<new Date(x.end).getTime()&&new Date(x.start).getTime()<b) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:679)
- ok — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732)
- r.type==='Protected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732)
- r.type==='Guest bike' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- r.type==='Private car'&&!r.consent — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732)
- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732)

### Required validation and role guards

- fail('Choose a valid time range.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:751)

### Empty states, confirmations, feedback and dialogs

- note('Reservation availability only; not live vehicle detection.','','parking') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:732)

### Navigation and data effects

Navigation targets/expressions: event-form?type=Guest%20parking; parking-share.

Reference state collections accessed: bookingRules, events, resourceBlocks, resources, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:parking-search ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:751)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-151"></a>

## 151. Spaces & parking — resources

Group: Community. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Add' | section navigation | 'resource-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733) |
| r.name | record/navigation row | 'resource?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733) |
| 'Block dates' | navigation/custom | 'resource-blocks' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733) |
| 'Rules' | navigation/custom | 'booking-rules' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- r.type==='Common space' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- r.type==='Protected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733)
- r.enabled — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:733)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: resource-blocks; booking-rules.

Reference state collections accessed: resources. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-152"></a>

## 152. Space details — resource

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Edit resource' | navigation/custom | 'resource-form?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !r — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- r.type==='Common space' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734)
- r.enabled — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734)
- r.type==='Private car' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734)
- r.consent — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734)
- r.blocks?.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734)
- isAdmin()&&r.type!=='Protected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('Space unavailable') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734)
- stateHero(r.type==='Common space'?'building':'parking',r.name,r.type) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:734)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: resources. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-153"></a>

## 153. Configure space — resource-form

Group: Community. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save space' | submit | 'resource' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- field('name','Name',r.name\|\|'','text','required maxlength="60"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735)
- selectField('type','Type',['Common space','Guest car','Guest bike','Private car'],r.type\|\|'Common space') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735)
- field('capacity','Maximum guests / capacity',r.capacity\|\|20,'number','required min="1" max="200"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735)
- selectField('owner','Private bay holder',[['','Not applicable'],...S.flats.filter(f=>f.member).map(f=>[f.id,f.id+' · '+f.name])],r.owner\|\|'') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735)
- field('blocks','Also occupies these bay IDs',(r.blocks\|\|[]).join(', '),'text','placeholder="CAR-1, CAR-2"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735)
- checkField('enabled','Available for booking',r.enabled??true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735)

### Conditional states / visibility

- r.type==='Protected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- old?.type==='Protected' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)
- old&&S.events.some(e=>e.status==='Approved'&&claimedResources(e).includes(old.id)&&new Date(e.end)>new Date(S.referenceNow)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)
- blocks.some(id=>!resource(id)\|\|resource(id).type==='Common space'\|\|resource(id).type==='Protected') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)
- D.type==='Private car'&&!flat(D.owner)?.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- !Number.isInteger(n)\|\|n<min\|\|n>max — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- D.type==='Private car' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)
- old?.owner===D.owner — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)
- old — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Protected routes are not bookable.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)
- fail('This resource has approved bookings. Resolve those before changing its configuration.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)
- fail('Use valid parking bay IDs, never a protected route.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)
- fail('Choose the private bay holder.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)
- requiredText(D.name,'Name',60) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- fail(label+' must be between '+min+' and '+max+'.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)

### Empty states, confirmations, feedback and dialogs

- note('Emergency access cannot be made bookable.','warn','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735)
- note('Private bays need holder consent. Existing reservations remain visible.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:735)

### Navigation and data effects

Navigation targets/expressions: 'resources'.

Reference state collections accessed: events, flats, referenceNow, resources. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:resource ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-154"></a>

## 154. Unavailable dates — resource-blocks

Group: Community. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:736). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Add' | section navigation | 'resource-block-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:736) |
| resource(b.resource)?.name | record/navigation row | 'resource-block-form?id='+b.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:736) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:670)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No blocked periods','','calendar') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:736)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: resourceBlocks, resources. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-155"></a>

## 155. Block a space — resource-block-form

Group: Community. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:737). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save blocked time' | submit | 'resource-block' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:737) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Cancel' | action | 'closeSheet' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Remove' | action | 'confirmRemoveResourceBlock' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Remove block' | action | 'removeResourceBlock' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:737) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- selectField('resource','Space',S.resources.filter(r=>r.type!=='Protected').map(r=>[r.id,r.name]),b.resource\|\|'SPACE-1') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:737)
- field('start','From',b.start\|\|'2026-09-24T08:00','datetime-local','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:737)
- field('end','Until',b.end\|\|'2026-09-24T17:00','datetime-local','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:737)
- field('reason','Reason',b.reason\|\|'','text','required maxlength="100"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:737)

### Conditional states / visibility

- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !resource(D.resource)\|\|D.end<=D.start — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:753)
- S.events.some(e=>e.status==='Approved'&&overlaps(candidate,e)&&claimedResources(e).some(id=>[D.resource,...(resource(D.resource)?.blocks\|\|[])].includes(id))) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:753)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- old — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:753)
- b.id — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:737)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- danger — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:753)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Choose a space and valid dates.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:753)
- fail('An approved booking overlaps. Resolve it first.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:753)
- requiredText(D.reason,'Reason',100) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:753)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:762)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:762)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:763)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:763)

### Empty states, confirmations, feedback and dialogs

- confirm('Remove blocked time?','The period will become bookable again.','Remove','confirmRemoveResourceBlock','data-id="${d.id}"',true) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:762)
- sheet(title,'<p>${esc(text)}</p><div class="actions">${actBtn('Cancel','closeSheet','','secondary')}${actBtn(label,action,danger?'trash':'check',danger?'danger':'',attrs)}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)

### Navigation and data effects

Navigation targets/expressions: 'resource-blocks'.

Reference state collections accessed: events, offline, resourceBlocks, resources. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:resource-block ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:753)), action:removeResourceBlock ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:762)), action:confirmRemoveResourceBlock ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:763)), action:closeSheet ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-156"></a>

## 156. Share my parking — parking-share

Group: Community. Reference roles: Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:738). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| r.name | record/navigation row | 'resource?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:661) |
| 'Save availability' | submit | 'share-parking' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:738) |
| 'Stop sharing' | action | 'unshareParking' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:738) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- field('start','Share from',r.shareStart\|\|'2026-09-20T16:00','datetime-local','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:738)
- field('end','Share until',r.shareEnd\|\|'2026-09-20T22:00','datetime-local','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:738)
- checkField('consent','I authorise guest use during these hours.',r.consent) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:738)

### Conditional states / visibility

- !r — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:738)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- r.consent — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:738)
- S.role!=='Resident'\|\|r?.owner!==S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:754)
- !D.consent — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:754)
- !D.start\|\|D.end<=D.start — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:754)
- S.events.some(e=>e.status==='Approved'&&(e.allocation\|\|[]).includes(r.id)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:754)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- S.role!=='Resident'\|\|r?.owner!==S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:764)
- S.events.some(e=>e.status==='Approved'&&(e.allocation\|\|[]).includes(r.id)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:764)

### Required validation and role guards

- fail('Only the bay holder can share it.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:754)
- fail('Consent is required to share the bay.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:754)
- fail('Choose a valid time range.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:754)
- fail('This bay has an approved reservation. Contact the admin before changing it.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:754)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:764)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Only the bay holder can change consent.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:764)
- fail('An approved booking uses this bay. Contact the admin first.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:764)

### Empty states, confirmations, feedback and dialogs

- empty('No private bay linked','Ask the admin to assign your bay.','car') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:738)
- toast('Bay is private') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:764)

### Navigation and data effects

Navigation targets/expressions: 'parking-availability'.

Reference state collections accessed: events, offline, residentFlat, resources, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:share-parking ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:754)), action:unshareParking ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:764)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-157"></a>

## 157. Booking rules — booking-rules

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:739). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Edit rules' | navigation/custom | 'booking-rules-edit' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:739) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:739)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- note('Private bays require consent. Emergency routes must remain clear.','warn','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:739)

### Navigation and data effects

Navigation targets/expressions: booking-rules-edit.

Reference state collections accessed: bookingRules. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-158"></a>

## 158. Booking settings — booking-rules-edit

Group: Community. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save rules' | submit | 'booking-rules' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740) |

### Inputs and selectable options

- field('maxGuests','Maximum guests',r.maxGuests,'number','required min="1" max="200"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740)
- field('maxHours','Maximum hours',r.maxHours,'number','required min="1" max="24"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740)
- field('advanceDays','Book ahead · days',r.advanceDays,'number','required min="1" max="365"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740)
- field('setupMinutes','Setup · minutes',r.setupMinutes,'number','required min="0" max="180"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740)
- field('cleanupMinutes','Cleanup · minutes',r.cleanupMinutes,'number','required min="0" max="180"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740)
- field('quietStart','Quiet hours from',r.quietStart,'time','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740)
- field('quietEnd','Until',r.quietEnd,'time','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740)
- field('maxCars','Max cars',r.maxCars,'number','min="0" max="30" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740)
- field('maxBikes','Max bikes',r.maxBikes,'number','min="0" max="50" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:740)

### Conditional states / visibility

- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !Number.isInteger(n)\|\|n<min\|\|n>max — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- r.quietStart===r.quietEnd — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:755)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:755)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail(label+' must be between '+min+' and '+max+'.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- fail('Set distinct quiet-hour times.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:755)

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: 'booking-rules'.

Reference state collections accessed: bookingRules. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:booking-rules ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:755)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-159"></a>

## 159. Members — members

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:768). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Shared or dynamic controls only | Inspect page definition | No direct control definition | Page link above |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-160"></a>

## 160. Member details — member

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Edit member' | navigation/custom | 'member-form?id='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770) |
| 'Payments' | navigation/custom | 'history?flat='+f.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770) |
| f.member?'Transfer flat account':'Share invite' | navigation/custom | f.member?'handover?flat='+f.id:'invite' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !f\|\|!f.member&&!isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- f.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770)
- isStaff()\|\|f.publicPhone — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770)
- isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)
- stateHero('user',name,f.id+' · '+(f.member?f.type:m?.type\|\|'Owner')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:770)

### Navigation and data effects

Navigation targets/expressions: home.

Reference state collections accessed: committee, memberRecords. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-161"></a>

## 161. Add / edit member — member-form

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:771). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save member' | submit | 'member-record' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:771) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- selectField('flat','Flat',S.flats.filter(f=>!f.member\|\|f.id===Q.id).map(f=>f.id),Q.id\|\|'B-205') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:771)
- field('name','Member name',x.name\|\|'','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:771)
- field('mobile','Mobile number',x.mobile\|\|'','tel',f?.member?'maxlength="16" readonly':'required pattern="[6-9][0-9]{9}" maxlength="10"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:771)
- selectField('type','Resident type',['Owner','Tenant'],x.type\|\|'Owner') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:771)
- checkField('publicPhone','Member consent to show phone',f?.publicPhone\|\|false) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:771)

### Conditional states / visibility

- f?.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:771)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !f — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- D.original&&D.original!==D.flat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- f.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- !D.original — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- D.mobile!==f.mobile — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- f.id===S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- !/^[6-9]\d{9}$/.test(D.mobile) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- old — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Choose a flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- fail('Use handover to change a member’s flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- requiredText(D.name,'Name',80) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- fail('This flat already has an active account.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- fail('Enter a 10-digit mobile number.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)

### Empty states, confirmations, feedback and dialogs

- toast('Member record saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)
- note(f?.member?'Identity changes require verification in the Android implementation.':'Creates an invitation record, not an active login.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:771)

### Navigation and data effects

Navigation targets/expressions: 'member?id='+f.id.

Reference state collections accessed: flats, memberRecords, profile, residentFlat. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:member-record ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-162"></a>

## 162. Apartment committee — committee

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:772). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| isAdmin()?'Add':'' | section navigation | isAdmin()?'committee-form':'' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:772) |
| flat(c.flat)?.name\|\|c.flat | record/navigation row | 'committee-member?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:772) |
| 'App permissions' | navigation/custom | 'roles' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:772) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:772)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- c.title==='Treasurer' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:772)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('Committee not added','','users') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:772)

### Navigation and data effects

Navigation targets/expressions: roles.

Reference state collections accessed: committee. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-163"></a>

## 163. Committee member — committee-member

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:773). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Edit' | navigation/custom | 'committee-form?id='+c.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:773) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Cancel' | action | 'closeSheet' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Archive' | action | 'confirmArchiveCommittee' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Archive' | action | 'archiveCommittee' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:773) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !c — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:773)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- c.active — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:773)
- isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:773)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !c — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:782)
- danger — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:781)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:782)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:782)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Role unavailable.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:782)

### Empty states, confirmations, feedback and dialogs

- empty('Member unavailable') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:773)
- stateHero('shield',flat(c.flat)?.name,c.title) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:773)
- confirm('Archive committee role?','App access and the member account will not change.','Archive','confirmArchiveCommittee','data-id="${d.id}"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:781)
- sheet(title,'<p>${esc(text)}</p><div class="actions">${actBtn('Cancel','closeSheet','','secondary')}${actBtn(label,action,danger?'trash':'check',danger?'danger':'',attrs)}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)

### Navigation and data effects

Navigation targets/expressions: 'committee'.

Reference state collections accessed: committee, offline. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:archiveCommittee ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:781)), action:confirmArchiveCommittee ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:782)), action:closeSheet ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-164"></a>

## 164. Committee role — committee-form

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:774). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save committee role' | submit | 'committee' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:774) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- selectField('flat','Member',S.flats.filter(f=>f.member).map(f=>[f.id,f.name+' · '+f.id]),c.flat\|\|'A-203') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:774)
- selectField('title','Committee title',['President','Vice President','Secretary','Joint Secretary','Treasurer','Committee Member'],c.title\|\|'Secretary') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:774)
- field('start','Term starts',c.start\|\|today,'date','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:774)
- field('end','Term ends',c.end\|\|'2027-03-31','date','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:774)

### Conditional states / visibility

- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !flat(D.flat)?.member — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:778)
- !D.start\|\|D.end<D.start — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:778)
- S.committee.some(c=>c.id!==D.id&&c.active&&c.flat===D.flat&&c.title===D.title) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:778)
- old — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:778)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:778)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Choose an active member.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:778)
- fail('Term end must be on or after its start.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:778)
- fail('This committee role already exists.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:778)

### Empty states, confirmations, feedback and dialogs

- toast('Committee updated') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:778)
- note('A committee title does not grant Admin or payment permissions.','','lock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:774)

### Navigation and data effects

Navigation targets/expressions: 'committee'.

Reference state collections accessed: committee, flats. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:committee ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:778)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-165"></a>

## 165. Approvals — approval-inbox

Group: Overview. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:775). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Maintenance payments' | record/navigation row | 'approvals' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:775) |
| 'New members' | record/navigation row | 'join-requests' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:775) |
| 'Event bookings' | record/navigation row | 'booking-requests' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:775) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:775)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: claims, events, payments. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-166"></a>

## 166. Choose a notice — notice-templates

Group: Community. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:791). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ${iconBox(i,'blue')} ${l} | inline button | notice-form?template=${t} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:791) |
| 'Write a notice' | navigation/custom | 'notice-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:791) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: notice-form?template=${t}; notice-form.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-167"></a>

## 167. Preview notice — notice-preview

Group: Community. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Edit' | navigation/custom | 'notice-form?id='+n.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793) |
| n.sendAt?'Schedule':'Publish' | action | 'publishAnnouncement' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793) |
| 'Keep draft' | action | 'keepNoticeDraft' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !n — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- n.audience?.startsWith('Block ') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- n.audience==='Selected flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- n.audience==='Unpaid flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- n.push — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793)
- n.ackRequired — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793)
- n.sendAt — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793)
- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:670)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !n\|\|n.status==='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:799)
- n.sendAt — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:799)
- n.sendAt<=S.referenceNow — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:799)
- n.status==='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- !recipients.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- n.push — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:799)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:799)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('No unpublished notice to send.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:799)
- fail('Scheduled time has passed; edit or publish now.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:799)
- fail('Already published. Edit and explicitly republish to notify again.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- fail('No active recipients in this audience.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)

### Empty states, confirmations, feedback and dialogs

- empty('No notice draft') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:793)
- toast('Notice scheduled') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:799)
- toast('Published in the demo inbox') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:799)
- toast('Draft saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:801)

### Navigation and data effects

Navigation targets/expressions: 'notice?id='+n.id; 'notices'.

Reference state collections accessed: bills, flats, notices, offline, referenceNow. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:publishAnnouncement ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:799)), action:keepNoticeDraft ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:801)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-168"></a>

## 168. Audience & responses — notice-delivery

Group: Community. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:796). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| flat(id)?.name\|\|id | record/navigation row | 'member?id='+id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:796) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !n — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:796)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- n.audience?.startsWith('Block ') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- n.audience==='Selected flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- n.audience==='Unpaid flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- acks.includes(id) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:796)
- reads.includes(id) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:796)
- n.status==='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:796)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('Notice unavailable') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:796)
- note('Demo inbox only. Queued does not mean delivered or read.','','bell') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:796)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: bills, flats, notices. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-169"></a>

## 169. Maintenance rate history — rate-history

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:818). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| r.amount==null?'Not configured':INR(r.amount)+' / flat' | record/navigation row | 'billing-settings' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:818) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- r.amount==null — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:818)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- r.effective>currentMonth — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:818)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: billingRules. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-170"></a>

## 170. Flat-specific rates — rate-overrides

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:819). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Add' | section navigation | 'rate-override-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:819) |
| r.flat+' · '+INR(r.amount) | record/navigation row | 'rate-override-form?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:819) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('One rate for every flat','Add exceptions only when agreed.','receipt') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:819)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: rateOverrides. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-171"></a>

## 171. Set flat rate — rate-override-form

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:820). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save flat rate' | submit | 'rate-override' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:820) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- selectField('flat','Flat',S.flats.filter(f=>f.active).map(f=>f.id),r.flat\|\|'A-203') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:820)
- amountField('amount','Monthly amount',r.amount\|\|S.settings.amount\|\|'') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:820)
- field('effective','Effective from',r.effective\|\|'2026-10','month','required min="2026-10"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:820)
- field('reason','Reason',r.reason\|\|'','textarea','required maxlength="300"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:820)

### Conditional states / visibility

- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- !flat(D.flat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:824)
- D.effective<=currentMonth\|\|S.bills.some(b=>b.flat===D.flat&&b.month>=D.effective&&(!b.kind\|\|b.kind==='maintenance')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:824)
- !Number.isFinite(num)\|\|num<=0\|\|num>10000000 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- old — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:824)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:824)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- fail('Choose a valid flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:824)
- fail('Choose a future month without generated bills.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:824)
- fail('Enter an amount between ₹0.01 and ₹1,00,00,000.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- requiredText(D.reason,'Reason',300) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:824)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)

### Empty states, confirmations, feedback and dialogs

- note('Existing bills do not change.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:820)

### Navigation and data effects

Navigation targets/expressions: 'rate-overrides'.

Reference state collections accessed: bills, flats, rateOverrides, settings. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:rate-override ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:824)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-172"></a>

## 172. One-time collections — contributions

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:825). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Add' | section navigation | 'contribution-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:825) |
| g.title | record/navigation row | 'contribution?id='+g.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:825) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No one-time collections','Keep repairs and event funds separate from maintenance.','gift') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:825)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: bills, charges. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-173"></a>

## 173. One-time contribution — contribution-form

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:826). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Review collection' | submit | 'contribution-draft' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:826) |

### Inputs and selectable options

- field('title','Collection name','','text','required maxlength="80" placeholder="e.g. Lift repair fund"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:826)
- amountField('amount','Amount per flat','500') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:826)
- selectField('scope','Charge to',['All active flats','Selected flats'],'All active flats') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:826)
- field('flats','Selected flats · optional','','text','placeholder="A-101, A-203"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:826)
- field('due','Due date','2026-09-25','date','required min="2026-09-13"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:826)

### Conditional states / visibility

- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- D.scope==='All active flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:832)
- !fs.length\|\|fs.some(f=>!flat(f)?.active) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:832)
- D.due<refDate() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:832)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- !Number.isFinite(num)\|\|num<=0\|\|num>10000000 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:832)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- fail('Choose valid active flats.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:832)
- fail('Choose today or a future due date.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:832)
- requiredText(D.title,'Name',80) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:832)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- fail('Enter an amount between ₹0.01 and ₹1,00,00,000.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)

### Empty states, confirmations, feedback and dialogs

- note('This does not change monthly maintenance or record any money received.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:826)

### Navigation and data effects

Navigation targets/expressions: 'contribution-preview'.

Reference state collections accessed: flats, referenceNow. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:contribution-draft ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:832)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-174"></a>

## 174. Review contribution — contribution-preview

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:827). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Create dues' | action | 'createContribution' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:827) |
| 'New collection' | navigation/custom | 'contribution-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:827) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- g — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:827)
- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !g — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:835)
- S.charges.some(c=>c.id===g.id) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:835)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:835)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:835)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Start a collection first.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:835)
- fail('Collection already created.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:835)

### Empty states, confirmations, feedback and dialogs

- toast('Dues created · no payment recorded') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:835)
- empty('No contribution draft','','gift',btn('New collection','contribution-form','plus')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:827)

### Navigation and data effects

Navigation targets/expressions: 'contribution?id='+g.id; contribution-form.

Reference state collections accessed: bills, charges, offline. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:createContribution ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:835)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-175"></a>

## 175. Contribution details — contribution

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:828). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Shared or dynamic controls only | Inspect page definition | No direct control definition | Page link above |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !g — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:828)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('Collection unavailable') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:828)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: bills, charges. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-176"></a>

## 176. Opening dues by flat — opening-dues

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:829). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Add' | section navigation | 'opening-due-form' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:829) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No opening dues','Record unpaid balances from before the app.','history') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:829)
- note('An opening due is not a payment and does not increase the cash balance.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:829)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: bills. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-177"></a>

## 177. Add opening due — opening-due-form

Group: Finance. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:830). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Add opening due' | submit | 'opening-due' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:830) |

### Inputs and selectable options

- selectField('flat','Flat',S.flats.filter(f=>f.active).map(f=>f.id),'A-203') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:830)
- amountField('amount','Unpaid amount','') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:830)
- field('asOf','Balance as of',today,'date','required max="2026-09-13"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:830)
- field('reason','Description','','textarea','required maxlength="300" placeholder="Unpaid balance before joining the app"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:830)
- checkField('verified','I checked this opening balance.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:830)

### Conditional states / visibility

- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- !D.verified — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)
- !flat(D.flat) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)
- S.bills.some(b=>b.flat===D.flat&&b.kind==='opening') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)
- !D.asOf\|\|D.asOf>today — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)
- !Number.isFinite(num)\|\|num<=0\|\|num>10000000 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- fail('Confirm that the opening balance was checked.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)
- fail('Choose a valid flat.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)
- fail('This flat already has an opening-due entry. Do not import it twice.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)
- fail('Use a date on or before the demo date.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)
- fail('Enter an amount between ₹0.01 and ₹1,00,00,000.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:452)
- requiredText(D.reason,'Description',300) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)

### Empty states, confirmations, feedback and dialogs

- toast('Opening due added · cash unchanged') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)

### Navigation and data effects

Navigation targets/expressions: 'bill?id='+b.id.

Reference state collections accessed: bills, flats. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:opening-due ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-178"></a>

## 178. Application charges — subscription-plans

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:856). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Shared or dynamic controls only | Inspect page definition | No direct control definition | Page link above |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- c==='B' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:856)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- refPlan().category===c — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:856)
- n<=10 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)
- n<=30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- note('Pricing preview in INR. Tax treatment and live billing configuration must be finalised before launch.','','receipt') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:856)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: flats. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-179"></a>

## 179. Subscription invoices — subscription-invoices

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:857). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| i.id | record/navigation row | 'subscription-invoice?id='+i.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:857) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- i.status==='Paid'\|\|i.status==='Waived' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:857)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-180"></a>

## 180. Application invoice — subscription-invoice

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:858). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save record' | action | 'downloadSubInvoice' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:858) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- ['Paid','Waived','Refund due','Refunded'].includes(i.status) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:845)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !i — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:882)
- content instanceof Blob — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:460)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:882)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Invoice unavailable.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:882)

### Empty states, confirmations, feedback and dialogs

- note('Subscription payments are verified by the service provider, not the apartment treasurer.','','lock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:858)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: apartment, subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:downloadSubInvoice ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:882)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-181"></a>

## 181. Renewal status — subscription-renewal

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:859). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Invoices' | navigation/custom | 'subscription-invoices' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:859) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- refDate()>=s.firstEnd — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:854)
- s.invoices.some(i=>i.period!=='First month'&&subBalance(i)>0) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:854)
- ['Paid','Waived','Refund due','Refunded'].includes(i.status) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:845)
- s.firstCredit — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:854)
- s.offerOptIn — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:854)
- n<=10 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)
- n<=30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('calendar',subscriptionStatus(),'Regular price applies after the first month.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:859)

### Navigation and data effects

Navigation targets/expressions: subscription-invoices.

Reference state collections accessed: flats, referenceNow, subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-182"></a>

## 182. Refer & unlock — referrals

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'View reward' | navigation/custom | 'referral-reward' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Share referral code' | action | 'shareApartmentReferral' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860) |
| 'Activate offer' | navigation/custom | 'referral-offer' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860) |
| 'Track referrals' | navigation/custom | 'referral-progress' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860) |
| 'Offer rules' | navigation/custom | 'referral-rules' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860) |

### Inputs and selectable options

- <textarea id="sharetext" readonly> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Conditional states / visibility

- k<n — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860)
- s.firstCredit — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860)
- s.offerOptIn — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:860)
- navigator.share — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- e.name==='AbortError' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- toast('Copied to clipboard') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- sheet('Copy / share','<div class="field"><label for="sharetext">Text</label><textarea id="sharetext" readonly>${esc(text)}</textarea></div><p>Select and copy the text.</p>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Navigation and data effects

Navigation targets/expressions: referral-reward; referral-offer; referral-progress; referral-rules.

Reference state collections accessed: subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:shareApartmentReferral ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:880)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-183"></a>

## 183. First-month offer — referral-offer

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:861). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Activate offer' | submit | 'referral-opt-in' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:861) |

### Inputs and selectable options

- checkField('terms','I accept the referral offer rules.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:861)

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- n<=10 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)
- n<=30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !D.terms — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:874)
- !eligibleWindow() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:874)
- firstInvoice().status==='Paid' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:874)
- !sub.offerOptIn\|\|sub.firstCredit\|\|refs.length<2\|\|!eligibleWindow() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:849)
- inv.status==='Paid' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:850)
- r.flats<=10 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:851)
- r.flats<=30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:851)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:874)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Accept the offer rules.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:874)
- fail('The first-month offer window has ended.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:874)

### Empty states, confirmations, feedback and dialogs

- stateHero('gift','Refer two. All three benefit.','Install + verified activation required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:861)
- note('No automatic debit. Without two qualifying activations, the first-month subscription remains due.','','receipt') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:861)

### Navigation and data effects

Navigation targets/expressions: 'referrals'.

Reference state collections accessed: flats, referenceNow, subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:referral-opt-in ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:874)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-184"></a>

## 184. Referral progress — referral-progress

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:862). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Add' | section navigation | 'referral-add' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:862) |
| r.name | record/navigation row | 'referral-detail?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:862) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Share code' | action | 'shareApartmentReferral' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:862) |

### Inputs and selectable options

- <textarea id="sharetext" readonly> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- r.stage==='Activated' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:862)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- navigator.share — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- e.name==='AbortError' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No referrals yet','Share your apartment referral code.','users') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:862)
- toast('Copied to clipboard') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)
- sheet('Copy / share','<div class="field"><label for="sharetext">Text</label><textarea id="sharetext" readonly>${esc(text)}</textarea></div><p>Select and copy the text.</p>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:464)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:shareApartmentReferral ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:880)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-185"></a>

## 185. New apartment referral — referral-add

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:863). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Save referral' | submit | 'referral-add' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:863) |

### Inputs and selectable options

- field('name','Apartment name','','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:863)
- field('city','City','','text','required maxlength="80"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:863)
- field('code','Apartment identifier','','text','required maxlength="30" placeholder="e.g. APT-GREEN-02"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:863)
- field('flats','Number of flats',20,'number','min="5" max="50" required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:863)

### Conditional states / visibility

- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- code===S.apartment.id\|\|name.toLowerCase()===S.apartment.name.toLowerCase()&&city.toLowerCase()===S.apartment.city.toLowerCase() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:875)
- S.subscription.referrals.some(r=>r.code===code\|\|r.name.toLowerCase()===name.toLowerCase()&&r.city.toLowerCase()===city.toLowerCase()) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:875)
- !Number.isInteger(n)\|\|n<min\|\|n>max — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:875)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- requiredText(D.name,'Apartment name',80) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:875)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- requiredText(D.city,'City',80) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:875)
- requiredText(D.code,'Apartment identifier',30) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:875)
- fail('Your own apartment cannot be referred.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:875)
- fail('This apartment has already been referred.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:875)
- fail(label+' must be between '+min+' and '+max+'.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)

### Empty states, confirmations, feedback and dialogs

- note('Adding a prospect does not count as activation.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:863)

### Navigation and data effects

Navigation targets/expressions: 'referral-detail?id='+r.id.

Reference state collections accessed: apartment, subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:referral-add ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:875)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-186"></a>

## 186. Referral details — referral-detail

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:864). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Shared or dynamic controls only | Inspect page definition | No direct control definition | Page link above |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !r — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:864)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- r.credit — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:864)
- r.flats<=10 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:864)
- r.flats<=30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:864)
- i<=idx — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:864)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('Referral unavailable') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:864)
- stateHero('building',r.name,r.city) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:864)
- note('Installation alone does not qualify. Activation must be verified by the service provider.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:864)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-187"></a>

## 187. First month reward — referral-reward

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| S.apartment.name | record/navigation row | 'subscription-invoice?id='+firstInvoice().id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865) |
| r.name | record/navigation row | 'referral-detail?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865) |
| 'Next payment date' | navigation/custom | 'subscription-renewal' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865) |
| 'Track referrals' | navigation/custom | 'referral-progress' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- s.firstCredit — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('gift','First month unlocked!','Paid billing starts next month, without extension.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865)
- stateHero('clock','Not unlocked yet',Math.min(validActivations().length,2)+' of 2 qualifying apartments activated.','amber') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:865)

### Navigation and data effects

Navigation targets/expressions: subscription-renewal; referral-progress.

Reference state collections accessed: apartment, subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-188"></a>

## 188. Referral rules — referral-rules

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:866). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Shared or dynamic controls only | Inspect page definition | No direct control definition | Page link above |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-189"></a>

## 189. Subscription overdue — subscription-overdue

Group: Settings. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:868). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'View invoices' | navigation/custom | 'subscription-invoices' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:868) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- n<=10 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)
- n<=30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)
- ['Paid','Waived','Refund due','Refunded'].includes(i.status) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:845)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- stateHero('clock','Payment required','Apartment data is retained.','amber') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:868)
- note('Production entitlement and payment checks must be performed by the backend.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:868)

### Navigation and data effects

Navigation targets/expressions: subscription-invoices.

Reference state collections accessed: flats, subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-190"></a>

## 190. Provider simulation — provider-simulation

Group: Reference tools. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:870). REFERENCE ONLY: do not ship as production behavior.

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| r.name | record/navigation row | 'activation-simulation?id='+r.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:870) |
| 'Move to month 2' | action | 'simulateRenewal' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:870) |
| 'Record subscription payment' | navigation/custom | 'subscription-payment-simulation' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:870) |
| 'Demo clock & automation' | navigation/custom | 'preview-clock' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:870) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- page!=='provider-simulation' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:881)
- !s.invoices.some(i=>i.start===s.firstEnd) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:881)
- n<=10 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)
- n<=30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:660)
- firstInvoice().status==='Offer pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:881)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:881)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Use the reference controls.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:881)

### Empty states, confirmations, feedback and dialogs

- note('REFERENCE ONLY · Simulates trusted service-provider events. Never expose these controls in the Android release.','warn','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:870)
- toast('Month 2 invoice created · no free extension') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:881)

### Navigation and data effects

Navigation targets/expressions: 'subscription'; subscription-payment-simulation; preview-clock.

Reference state collections accessed: flats, referenceNow, subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:simulateRenewal ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:881)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-191"></a>

## 191. Verify activation · demo — activation-simulation

Group: Reference tools. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:871). REFERENCE ONLY: do not ship as production behavior.

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Apply demo event' | submit | 'activation-simulation' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:871) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- selectField('stage','Next state',['Referred','Installed','Setup complete','Activated'],r.stage) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:871)
- checkField('phoneVerified','Admin identity verified',r.phoneVerified) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:871)
- checkField('residentVerified','Setup complete + one approved resident',r.residentVerified) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:871)
- checkField('distinct','Distinct new apartment; not a duplicate',r.distinct) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:871)

### Conditional states / visibility

- !r — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:871)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- page!=='activation-simulation' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)
- !r — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)
- r.credit&&D.stage!=='Activated' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)
- D.stage==='Activated'&&(!D.phoneVerified\|\|!D.residentVerified\|\|!D.distinct) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)
- D.stage==='Activated' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)
- !sub.offerOptIn\|\|sub.firstCredit\|\|refs.length<2\|\|!eligibleWindow() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:849)
- inv.status==='Paid' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:850)
- r.flats<=10 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:851)
- r.flats<=30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:851)
- S.subscription.firstCredit — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Use the provider simulation screen.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)
- fail('Referral unavailable.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)
- fail('A rewarded activation requires provider fraud review, not silent reversal.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)
- fail('All activation checks must pass. Installation alone is insufficient.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)

### Empty states, confirmations, feedback and dialogs

- empty('Referral unavailable') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:871)
- note('Service-provider simulation, not self-approval.','warn','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:871)
- toast('Demo activation updated') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)

### Navigation and data effects

Navigation targets/expressions: S.subscription.firstCredit?'referral-reward':'provider-simulation'.

Reference state collections accessed: referenceNow, subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:activation-simulation ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-192"></a>

## 192. Subscription payment · demo — subscription-payment-simulation

Group: Reference tools. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:872). REFERENCE ONLY: do not ship as production behavior.

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Record demo payment' | submit | 'subscription-payment-simulation' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:872) |

### Inputs and selectable options

- selectField('invoice','Invoice',S.subscription.invoices.filter(i=>subBalance(i)>0).map(i=>[i.id,i.id+' · '+INR(subBalance(i))])) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:872)
- field('reference','Reference','','text','required maxlength="40" placeholder="DEMO-SUB-001"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:872)
- checkField('verified','Provider verified the subscription payment.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:872)

### Conditional states / visibility

- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- page!=='subscription-payment-simulation' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)
- !D.verified — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)
- !i\|\|subBalance(i)<=0 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)
- ['Paid','Waived','Refund due','Refunded'].includes(i.status) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:845)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- S.subscription.invoices.some(x=>x.reference===ref) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Use the reference provider screen.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)
- fail('Confirm provider verification.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)
- fail('No amount is due for this invoice.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)
- requiredText(D.reference,'Reference',40) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- fail('This subscription reference was already recorded.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)

### Empty states, confirmations, feedback and dialogs

- note('Simulates provider-confirmed bank receipt. No money is charged or collected.','warn','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:872)
- toast('Demo payment recorded · not a real charge') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)

### Navigation and data effects

Navigation targets/expressions: 'subscription-invoice?id='+i.id.

Reference state collections accessed: referenceNow, subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:subscription-payment-simulation ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-193"></a>

## 193. Community polls — polls

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:885). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| isAdmin()?'New':'' | section navigation | isAdmin()?'poll-form':'' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:885) |
| p.question | record/navigation row | 'poll?id='+p.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:885) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:885)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No polls yet','','vote') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:885)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: polls. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-194"></a>

## 194. New poll — poll-form

Group: Community. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:886). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Publish poll' | submit | 'poll' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:886) |

### Inputs and selectable options

- field('question','Question','','text','required maxlength="150"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:886)
- field('options','Choices · one per line','Yes\nNo','textarea','required maxlength="500"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:886)
- field('end','Voting closes','2026-09-25','date','required min="2026-09-13"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:886)

### Conditional states / visibility

- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- options.length<2\|\|options.length>5\|\|new Set(options).size!==options.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:894)
- D.end<refDate() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:894)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:894)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Use 2–5 different choices.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:894)
- fail('Choose today or a future date.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:894)
- requiredText(D.question,'Question',150) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:894)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)

### Empty states, confirmations, feedback and dialogs

- note('One vote per flat. Opinion poll, not an official election.','','users') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:886)

### Navigation and data effects

Navigation targets/expressions: 'poll?id='+p.id.

Reference state collections accessed: polls, referenceNow. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:poll ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:894)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-195"></a>

## 195. Vote & results — poll

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| ${esc(x)} ${votes.filter(x=>x===i).length} votes ${v===i?I('checkCircle'):I('vote')} | inline button | castVote | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887) |
| 'Close dialog' | icon control | 'data-act="closeSheet"' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:442) |
| 'Cancel' | action | 'closeSheet' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Close poll' | action | 'confirmClosePoll' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444) |
| 'Close poll' | action | 'closePoll' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !p — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- S.role!=='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:899)
- !p\|\|p.status==='Closed'\|\|p.end<refDate() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:899)
- !Number.isInteger(n)\|\|n<min\|\|n>max — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- !color — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- icon — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:198)
- closed — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887)
- closed\|\|S.role!=='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887)
- v===i — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887)
- S.role!=='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887)
- isAdmin()&&!closed — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- !p — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:901)
- danger — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)

### Required validation and role guards

- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:899)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Vote using a resident flat account.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:899)
- fail('Voting is closed.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:899)
- fail(label+' must be between '+min+' and '+max+'.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:658)
- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:900)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:901)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:901)
- fail('Poll unavailable.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:901)

### Empty states, confirmations, feedback and dialogs

- empty('Poll unavailable') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887)
- toast('Vote saved') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:899)
- note('Vote using a resident flat account. Staff views show results.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887)
- note(closed?'Voting has ended.':'Tap to vote. Change your choice before voting closes.','','vote') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:887)
- confirm('Close this poll?','Voting will stop. Results remain available.','Close poll','confirmClosePoll','data-id="${d.id}"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:900)
- sheet(title,'<p>${esc(text)}</p><div class="actions">${actBtn('Cancel','closeSheet','','secondary')}${actBtn(label,action,danger?'trash':'check',danger?'danger':'',attrs)}</div>') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:444)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: offline, polls, referenceNow, residentFlat, role. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:castVote ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:899)), action:closePoll ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:900)), action:confirmClosePoll ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:901)), action:closeSheet ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-196"></a>

## 196. Vehicles — vehicles

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:888). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| S.role==='Resident'?'Add':'' | section navigation | S.role==='Resident'?'vehicle-form':'' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:888) |
| v.number | record/navigation row | 'vehicle?id='+v.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:888) |

### Inputs and selectable options

- search('Search vehicle or flat') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:888)

### Conditional states / visibility

- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:888)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('No vehicles registered','','car') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:888)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: role, vehicles. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-197"></a>

## 197. Vehicle details — vehicle

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:889). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Vehicle blocking? Notify owner' | navigation/custom | 'vehicle-contact?id='+v.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:889) |
| 'Edit vehicle' | navigation/custom | 'vehicle-form?id='+v.id | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:889) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

- !v — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:889)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- S.role==='Resident'&&v.flat===S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:889)

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- empty('Vehicle unavailable') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:889)
- stateHero('car',v.number,v.type) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:889)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: residentFlat, role, vehicles. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-198"></a>

## 198. My vehicle — vehicle-form

Group: Community. Reference roles: Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:890). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Back home' | navigation/custom | 'home' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223) |
| 'Save vehicle' | submit | 'vehicle' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:890) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- field('number','Registration number',v.number\|\|'','text','required maxlength="20"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:890)
- selectField('type','Vehicle',['Car','Two-wheeler'],v.type\|\|'Car') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:890)
- field('color','Colour',v.color\|\|'','text','required maxlength="30"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:890)

### Conditional states / visibility

- v.id&&v.flat!==S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:890)
- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- S.role!=='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:895)
- old&&old.flat!==S.residentFlat — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:895)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- S.vehicles.some(v=>v.id!==D.id&&v.number.replace(/\s/g,'')===number.replace(/\s/g,'')) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:895)
- old — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:895)

### Required validation and role guards

- fail('Use a resident account.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:895)
- fail('Not your vehicle.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:895)
- requiredText(D.number,'Registration',20) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:895)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)
- fail('Vehicle is already registered.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:895)
- requiredText(D.color,'Colour',30) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:895)

### Empty states, confirmations, feedback and dialogs

- stateHero('lock','Admin access only','Switch preview role to continue.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:223)

### Navigation and data effects

Navigation targets/expressions: home; 'vehicle?id='+v.id.

Reference state collections accessed: residentFlat, role, vehicles. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:vehicle ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:895)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-199"></a>

## 199. Notify vehicle owner — vehicle-contact

Group: Community. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:891). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Notify owner' | submit | 'vehicle-contact' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:891) |

### Inputs and selectable options

- <input type="hidden" name="${esc(n)}" value="${esc(v)}"> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:657)
- selectField('reason','Issue',['Blocking my vehicle','Blocking entry / exit','Lights left on','Other']) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:891)
- field('note','Note · optional','','textarea','maxlength="250"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:891)

### Conditional states / visibility

- !v — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:891)
- text — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:207)
- !v — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:896)
- S.role==='Resident' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:896)
- S.vehicleMessages.some(x=>x.vehicle===v.id&&x.from===from&&x.date===refDate()) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:896)

### Required validation and role guards

- fail('Vehicle unavailable.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:896)
- fail('You already notified this owner today. Contact the admin for urgent follow-up.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:896)

### Empty states, confirmations, feedback and dialogs

- empty('Vehicle unavailable') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:891)
- toast('Owner notified in demo inbox') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:896)
- note('Only the owner and apartment management receive this message.','','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:891)

### Navigation and data effects

Navigation targets/expressions: 'vehicle?id='+v.id.

Reference state collections accessed: referenceNow, residentFlat, role, vehicleMessages, vehicles. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:vehicle-contact ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:896)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-200"></a>

## 200. Committee handover — committee-handover

Group: Administration. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:892). 

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| Open ${I('arrowRight')} | inline button | ${to} | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:892) |
| 'Export handover summary' | action | 'exportHandover' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:892) |

### Inputs and selectable options

- <input type="checkbox" data-handover-task="${key}" aria-label="${title}" ${S.handoverTasks[key]?'checked':''}> — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:892)

### Conditional states / visibility

- sub — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:221)
- S.handoverTasks[key] — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:892)
- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- content instanceof Blob — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:460)
- v — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:902)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:902)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)

### Empty states, confirmations, feedback and dialogs

- stateHero('clipboard','Ready for the next team?','Keep records. Transfer access, not passwords.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:892)

### Navigation and data effects

Navigation targets/expressions: ${to}.

Reference state collections accessed: apartment, events, handoverTasks, payments, referenceNow. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: action:exportHandover ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:902)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-201"></a>

## 201. Demo clock — preview-clock

Group: Reference tools. Reference roles: Admin, Treasurer. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:945). REFERENCE ONLY: do not ship as production behavior.

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Set clock' | submit | 'preview-clock' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:945) |
| 'Run scheduled tasks' | action | 'runAutomation' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:945) |

### Inputs and selectable options

- field('now','Simulation date and time',S.referenceNow,'datetime-local','required') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:945)

### Conditional states / visibility

- !isStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- !D.now\|\|isNaN(new Date(D.now)) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:946)
- S.offline — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- !S.bills.some(b=>b.flat===f.id&&b.month===m) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:456)
- S.settings.dueNotify — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:456)
- count — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:456)
- S.settings.reminders&&S.settings.reminderDays.split(',').map(v=>Number(v.trim())).includes(Number(today.slice(-2))) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- !S.notifications.some(n=>n.key===key) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- b.latePolicy?.enabled&&due(b)>0&&!billPending(b)&&!b.lateFee — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- new Date(today+'T12:00:00')>threshold — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- n.status==='Published' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- n.audience?.startsWith('Block ') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- n.audience==='Selected flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- n.audience==='Unpaid flats' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:786)
- !recipients.length — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- n.push — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- remaining>=0&&remaining<=24*3600000&&!S.notifications.some(n=>n.key===key) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:941)
- !s — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:670)
- refDate()>=S.subscription.firstEnd&&firstInvoice().status==='Offer pending' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:942)
- !sub.offerOptIn\|\|sub.firstCredit\|\|refs.length<2\|\|!eligibleWindow() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:849)
- inv.status==='Paid' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:850)
- r.flats<=10 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:851)
- r.flats<=30 — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:851)
- n — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:943)

### Required validation and role guards

- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:946)
- fail('Admin or Treasurer access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:449)
- fail('Choose a valid date and time.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:946)
- requireStaff() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- requireOnline() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)
- fail('You are offline. Reconnect before saving.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:451)
- fail('Already published. Edit and explicitly republish to notify again.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)
- fail('No active recipients in this audience.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:787)

### Empty states, confirmations, feedback and dialogs

- note('Reference controls only. This clock tests bookings, scheduled notices and referral windows; seeded financial history remains in September 2026.','','clock') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:945)
- toast('Demo clock updated') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:946)
- toast('Demo tasks processed') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)

### Navigation and data effects

Navigation targets/expressions: 'automation-log'.

Reference state collections accessed: audit, bills, events, flats, notices, notifications, offline, recurring, referenceNow, services, settings, subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:preview-clock ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:946)), action:runAutomation ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:948)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-202"></a>

## 202. Included in this reference — release-coverage

Group: Reference tools. Reference roles: Admin, Treasurer, Resident. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:966). REFERENCE ONLY: do not ship as production behavior.

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| t | record/navigation row | to | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:967) |

### Inputs and selectable options

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Conditional states / visibility

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Required validation and role guards

No explicit expression extracted here. Shared/global guards still apply; absence is not permission to omit validation.

### Empty states, confirmations, feedback and dialogs

- note('Interactive browser reference. Identity checks, bank payments, installation verification, background jobs and phone push are simulated; no live service is connected.','','info') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:967)

### Navigation and data effects

Navigation targets/expressions: Shared navigation only / dynamic target in page source.

Reference state collections accessed: No direct reference collection extracted. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: No static handler link extracted. The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

<a id="requirement-203"></a>

## 203. Refund credit · demo — subscription-refund-simulation

Group: Reference tools. Reference roles: Admin. [page definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:976). REFERENCE ONLY: do not ship as production behavior.

### Controls and function targets

| Control / label expression | Function | Target | Definition |
|---|---|---|---|
| 'Record demo refund' | submit | 'subscription-refund-simulation' | [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:976) |

### Inputs and selectable options

- selectField('invoice','First-month invoice',S.subscription.invoices.filter(i=>i.status==='Refund due').map(i=>[i.id,i.id+' · '+INR(i.paidAmount\|\|i.amount)])) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:976)
- field('reference','Refund reference','','text','required maxlength="40"') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:976)
- checkField('verified','Provider confirms the first-month payment was refunded.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:976)

### Conditional states / visibility

- !isAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- page!=='subscription-refund-simulation'\|\|!D.verified — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:977)
- !i\|\|i.status!=='Refund due' — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:977)
- !x — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)

### Required validation and role guards

- requireAdmin() — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:977)
- fail('Admin access is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:450)
- fail('Use the provider simulation and confirm the refund.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:977)
- fail('No refund due for this invoice.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:977)
- requiredText(D.reference,'Refund reference',40) — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:977)
- fail(label+' is required.') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:659)

### Empty states, confirmations, feedback and dialogs

- note('Provider simulation only. No bank refund is sent.','warn','shield') — [source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:976)

### Navigation and data effects

Navigation targets/expressions: 'subscription-invoice?id='+i.id.

Reference state collections accessed: subscription. These are browser model names, **not PostgreSQL tables**. Real API/SQL mappings are tracked separately in PAGEWISE_TRACEABILITY.md.

Linked action/form definitions: form:subscription-refund-simulation ([source](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:977)). The complete handler catalog follows; no handler source is truncated.

Implementation acceptance: every required control/state above needs a native implementation, server validation where applicable, persistence/revisit evidence, negative/retry tests, reference comparison and independent QA. This document makes no completion claim.

## Complete reference action and form behavior catalog

The following is prototype behavior/design intent. It must not be copied as production security, financial verification or provider success. All handlers are included, even when reached only through a dynamically generated control.

### action:fillLoginDemo

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:490)

```javascript
()=>{const f=document.querySelector('form[data-form="login"]');if(!f)return;f.elements.mobile.value='9000000000';f.elements.pin.value='123456';f.querySelector('button[type="submit"]')?.focus();}
```

### action:back

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)

```javascript
()=>goBack()
```

### action:closeSheet

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)

```javascript
()=>closeSheet()
```

### action:openPreview

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:491)

```javascript
()=>previewSheet()
```

### action:setRole

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:492)

```javascript
d=>setRole(d.role)
```

### action:themeLight

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:492)

```javascript
()=>setTheme('light')
```

### action:themeDark

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:492)

```javascript
()=>setTheme('dark')
```

### action:themeSystem

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:492)

```javascript
()=>setTheme('system')
```

### action:toggleTheme

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:492)

```javascript
()=>setTheme(S.theme==='light'?'dark':'light')
```

### action:month

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:493)

```javascript
()=>sheet('Select month',`<div class="card no-pad">${[...months,['2026-10','October 2026']].map(([m,t])=>`<button class="list-row" data-act="selectMonth" data-month="${m}">${iconBox('calendar')}<span class="grow"><strong>${t}</strong></span>${month===m?I('check'):I('chevron')}</button>`).join('')}</div>`)
```

### action:selectMonth

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:494)

```javascript
d=>{month=d.month;closeSheet();render();}
```

### action:filter

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:494)

```javascript
d=>{filters[page+':'+d.key]=d.value;render();}
```

### action:scenarioPay

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:495)

```javascript
()=>{setRole('Resident');const b=ownBill();go(b?'bill?id='+b.id:'dues');}
```

### action:scenarioIssue

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:495)

```javascript
()=>{setRole('Resident');go('issue?id=ISS-1042');}
```

### action:scenarioInvite

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:495)

```javascript
()=>{setRole('Resident');go('join');}
```

### action:mapOpen

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:496)

```javascript
d=>{const r=routes[d.id];if(r&&!r.roles.includes(S.role)){S.role=r.roles[0];persist();}go(samplePath(d.id));}
```

### action:refreshScreen

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:497)

```javascript
()=>render()
```

### action:retryPage

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:497)

```javascript
()=>{go('home');toast('Demo view reloaded');}
```

### action:goOnline

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:497)

```javascript
()=>{S.offline=false;persist();go('home');toast('Viewing demo data');}
```

### action:logout

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:498)

```javascript
()=>confirm('Sign out?','Saved apartment records will remain in this demo.','Sign out','confirmLogout')
```

### action:confirmLogout

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:498)

```javascript
()=>{backStack=[];go('login',true);}
```

### action:resetDemo

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:499)

```javascript
()=>confirm('Reset demo data?','This restores all sample records and clears your local test changes.','Reset','confirmReset','',true)
```

### action:confirmReset

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:500)

```javascript
()=>{const theme=S.theme,mode=S.themeMode;S=makeSeed();S.theme=theme;S.themeMode=mode;persist();selected.clear();filters={};draft={};backStack=[];month=currentMonth;go('home',true);toast('Sample data restored');}
```

### action:exploreDemo

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:501)

```javascript
()=>{setRole('Admin');go('home',true);}
```

### action:approvePayment

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:502)

```javascript
d=>{requireStaff();let p=pay(d.id);if(!p||p.status!=='pending')fail('This payment is no longer pending.');sheet('Approve payment',`<div class="card mb">${kv('Flat',p.flat)}${kv('Amount',INR(p.amount))}${kv('Mode',p.mode)}</div>`+form('approve-single',`<input type="hidden" name="id" value="${p.id}">${checkField('verified',p.mode==='Cash'?'I confirm the cash was received.':'I matched this payment to the bank record.')}`,'Approve & issue receipt','checkCircle'));}
```

### action:selectAllPayments

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:503)

```javascript
()=>{S.payments.filter(p=>p.status==='pending').forEach(p=>selected.add(p.id));render();}
```

### action:reviewBulk

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:504)

```javascript
()=>{requireStaff();if(!selected.size)fail('Select at least one payment.');go('bulk-review');}
```

### action:generateBills

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:505)

```javascript
()=>{requireStaff();requireOnline();let count=createMissingBills(month);save();render();toast(count?count+' bills created':'No duplicates. All bills already exist.');}
```

### action:sendReminders

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:506)

```javascript
()=>{requireStaff();requireOnline();if(!S.settings.reminders)fail('Enable maintenance reminders in notification settings first.');let count=addReminders();save();go('outstanding');toast(count+' demo reminders added to inbox');}
```

### action:copyUPI

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:507)

```javascript
()=>{if(!S.settings.upi)fail('No UPI ID has been added.');return shareText(S.settings.upi,'UPI ID');}
```

### action:downloadProof

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:508)

```javascript
d=>{let p=pay(d.id);if(!p||(!isStaff()&&p.flat!==S.residentFlat))fail('Access denied.');downloadData(p.proof);}
```

### action:exportReceipt

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:509)

```javascript
d=>exportReceipt(pay(d.id))
```

### action:shareReceipt

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:509)

```javascript
d=>{let p=pay(d.id);if(!p||p.status!=='approved'||!isStaff()&&p.flat!==S.residentFlat)fail('Receipt unavailable.');return shareText(receiptText(p),'Payment receipt');}
```

### action:confirmExpense

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:510)

```javascript
d=>{requireStaff();requireOnline();const e=S.expenses.find(x=>x.id===d.id);if(!e||e.paid)fail('This expense is already posted.');if(e.date>today)fail('Future-dated drafts can be confirmed on or after their due date.');confirm('Confirm expense paid?',e.title+' · '+INR(e.amount),'Confirm','postExpense',`data-id="${e.id}"`);}
```

### action:postExpense

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:511)

```javascript
d=>{requireStaff();const e=S.expenses.find(x=>x.id===d.id);if(!e||e.paid)fail('Already confirmed.');e.paid=true;e.reversed=false;postedExpense(e);audit('Expense paid',e.title,'Draft',INR(e.amount));save();go('expense?id='+e.id);toast('Expense posted. Balances updated.');}
```

### action:reverseExpense

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:512)

```javascript
d=>{requireStaff();const e=S.expenses.find(x=>x.id===d.id);if(!e||!e.paid)fail('Expense not posted.');sheet('Reverse this expense?',form('reverse-expense',`<input type="hidden" name="id" value="${e.id}">${note('Balances will be adjusted. The original record and reason remain in the audit trail.','warn')}${field('reason','Reason','','textarea','required maxlength="300"')}`,'Reverse expense','refresh'));}
```

### action:toggleRecurring

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:513)

```javascript
d=>{requireStaff();let r=S.recurring.find(x=>x.id===d.id);r.active=!r.active;audit('Recurring rule changed',r.title,!r.active?'Active':'Paused',r.active?'Active':'Paused');save();render();toast(r.active?'Recurring expense resumed':'Recurring expense paused');}
```

### action:prepareRecurring

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:514)

```javascript
d=>{requireStaff();requireOnline();let r=S.recurring.find(x=>x.id===d.id);let e=prepareRecurring(r);save();go('expense?id='+e.id);toast('Draft ready. Confirm only when paid.');}
```

### action:exportReport

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:515)

```javascript
()=>{downloadCSV('ApartmentCare_'+month+'_summary.csv',reportRows());}
```

### action:exportCollections

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:516)

```javascript
()=>{requireStaff();downloadCSV('ApartmentCare_'+month+'_collections.csv',[['Flat','Resident','Billed','Approved','Outstanding','Status'],...stats().bs.map(b=>[b.flat,flat(b.flat)?.name,b.amount+(b.lateFee||0),billPaid(b),due(b),billStatus(b)])]);}
```

### action:exportCashbook

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:517)

```javascript
()=>{requireStaff();downloadCSV('ApartmentCare_'+month+'_cashbook.csv',[['Date','Entry','Inflow','Outflow'],...S.payments.filter(p=>p.status==='approved'&&p.date.startsWith(month)).map(p=>[p.date,'Maintenance '+p.flat,p.amount,0]),...S.income.filter(e=>e.date.startsWith(month)).map(e=>[e.date,e.title,e.amount,0]),...S.expenses.filter(e=>e.paid&&e.date.startsWith(month)).map(e=>[e.date,e.title,0,e.amount])]);}
```

### action:printReport

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:518)

```javascript
()=>{document.getElementById('printArea').innerHTML=`<h1>${esc(S.apartment.name)}</h1><h2>${monthName(month)} · Monthly summary</h2><table>${reportRows().slice(2).map(([k,v])=>`<tr><td>${esc(k)}</td><td>${typeof v==='number'&&!k.toLowerCase().includes('flat')&&!['In review'].includes(k)?INR(v):esc(v)}</td></tr>`).join('')}</table><p class="small">Local demonstration record · manual payment verification.</p>`;window.print();}
```

### action:shareReport

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:519)

```javascript
()=>shareText(reportRows().map(([k,v])=>k+': '+v).join('\n'),'Monthly summary')
```

### action:affected

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:520)

```javascript
d=>{requireOnline();const i=S.issues.find(x=>x.id===d.id);if(!i)fail('Issue not found.');if(!i.affected.includes(S.residentFlat)){i.affected.push(S.residentFlat);audit('Resident followed incident',i.id,'Not following',S.residentFlat);save();render();toast('You’ll see updates for this issue.');}else toast('You are already following this issue.');}
```

### action:reopenIssue

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:521)

```javascript
d=>{requireOnline();const i=S.issues.find(x=>x.id===d.id);if(i.status!=='Resolved')fail('Only resolved issues can be reopened.');i.status='Open';i.timeline.push({title:'Reopened · problem persists',time:'Today · '+S.role});audit('Issue reopened',i.id,'Resolved','Open');notify('Issue reopened',i.title,'staff','issue?id='+i.id);save();render();toast('Issue reopened');}
```

### action:closeIssue

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:522)

```javascript
d=>{requireStaff();const i=S.issues.find(x=>x.id===d.id);i.status='Closed';i.timeline.push({title:'Closed by '+S.role,time:'Today'});audit('Issue closed',i.id,'Resolved','Closed');save();render();}
```

### action:reopenComplaint

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:523)

```javascript
d=>{const c=S.complaints.find(x=>x.id===d.id);if(!c||!isStaff()&&c.flat!==S.residentFlat)fail('Access denied.');c.status='Open';c.timeline.push({title:'Reopened by resident',time:'Today'});audit('Request reopened',c.id,'Resolved','Open');save();render();toast('Request reopened');}
```

### action:closeComplaint

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:524)

```javascript
d=>{const c=S.complaints.find(x=>x.id===d.id);if(!c||!isStaff()&&c.flat!==S.residentFlat)fail('Access denied.');c.status='Closed';c.timeline.push({title:'Resident confirmed resolution',time:'Today'});audit('Request closed',c.id,'Resolved','Closed');save();render();toast('Marked as fixed');}
```

### action:pinNotice

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:525)

```javascript
d=>{requireStaff();let n=S.notices.find(x=>x.id===d.id);n.pinned=!n.pinned;save();render();toast(n.pinned?'Notice pinned':'Notice unpinned');}
```

### action:publishNotice

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:800)

```javascript
d=>{requireStaff();requireOnline();const n=S.notices.find(n=>n.id===d.id);if(!n)fail('Notice unavailable.');go('notice-preview?id='+n.id);}
```

Earlier definitions retained for wrapper/delegation tracing, not additional active handlers:

[earlier definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:526)

```javascript
d=>{requireStaff();let n=S.notices.find(x=>x.id===d.id);n.status='Published';n.date=today;audit('Notice published',n.title,'Draft','Published');notify('New notice',n.title,'all','notice?id='+n.id);save();render();toast('Notice published in demo inbox');}
```

### action:shareNotice

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:527)

```javascript
d=>{let n=S.notices.find(x=>x.id===d.id);if(!n||!visibleNotice(n))fail('Access denied.');return shareText(n.title+'\n\n'+n.body,'Apartment notice');}
```

### action:readNotice

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:802)

```javascript
d=>{const n=S.notices.find(n=>n.id===d.id);if(!n||!visibleNotice(n))fail('Notice unavailable.');n.reads=n.reads||[];if(!n.reads.includes(S.residentFlat))n.reads.push(S.residentFlat);n.readBy=n.reads.length;save();render();toast('Marked as read');}
```

Earlier definitions retained for wrapper/delegation tracing, not additional active handlers:

[earlier definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:528)

```javascript
d=>{let n=S.notices.find(x=>x.id===d.id);n.readFlats=n.readFlats||[];if(!n.readFlats.includes(S.residentFlat)){n.readFlats.push(S.residentFlat);n.readBy++;}save();toast('Marked as read');}
```

### action:callVendor

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:529)

```javascript
d=>{const v=S.vendors.find(x=>x.id===d.id);callContact(v,'vendor-form?id='+d.id);}
```

### action:callContact

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:529)

```javascript
d=>{const c=S.contacts.find(x=>x.id===d.id);callContact(c,'contact-form?id='+d.id);}
```

### action:copyVendor

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:530)

```javascript
d=>{const c=S.vendors.find(x=>x.id===d.id);if(!c?.phone)fail('No phone number has been added.');return shareText(c.name+' · '+c.phone);}
```

### action:copyContact

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:530)

```javascript
d=>{const c=S.contacts.find(x=>x.id===d.id);if(!c?.phone)fail('No phone number has been added.');return shareText(c.name+' · '+c.phone);}
```

### action:dial

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:531)

```javascript
d=>{location.href='tel:'+d.phone.replace(/[^+0-9]/g,'');}
```

### action:downloadDocument

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:532)

```javascript
d=>{const x=S.docs.find(x=>x.id===d.id);if(!x||!isStaff()&&!x.public)fail('Access denied.');if(x.attachment)downloadData(x.attachment);else doDownload(x.name||x.title+'.txt',x.body||'');}
```

### action:downloadExpenseAttachment

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:533)

```javascript
d=>{const e=S.expenses.find(x=>x.id===d.id);if(!e||!isStaff()&&!e.public)fail('Access denied.');downloadData(e.attachment);}
```

### action:removeDocument

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:534)

```javascript
d=>{requireStaff();confirm('Remove document?','The file will be removed from this local reference.','Remove','deleteDocument',`data-id="${d.id}"`,true);}
```

### action:deleteDocument

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:534)

```javascript
d=>{requireStaff();const x=S.docs.find(x=>x.id===d.id);S.docs=S.docs.filter(x=>x.id!==d.id);audit('Document removed',x.title,'Present','Removed');save();go('documents');toast('Document removed');}
```

### action:selectClaimFlat

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)

```javascript
d=>{draft.claimFlat=d.id;render();}
```

### action:submitClaim

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:535)

```javascript
()=>{requireOnline();if(!draft.signup)fail('Enter your name and mobile first.');if(!draft.claimFlat)fail('Choose a flat.');const f=flat(draft.claimFlat);if(f.member)fail('That flat already has an active account.');if(S.claims.some(c=>c.flat===f.id&&c.status==='pending'))fail('A join request is already pending for this flat.');const c={...draft.signup,id:uid('CL'),flat:f.id,date:today,status:'pending'};S.claims.push(c);S.signup=c.id;notify('Resident join request',c.name+' · '+c.flat,'Admin','join-review?id='+c.id);audit('Resident requested access',c.flat,'Unassigned','Approval pending');save();go('signup-pending');}
```

### action:approveClaim

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:536)

```javascript
d=>{requireAdmin();const c=S.claims.find(x=>x.id===d.id);if(!c||c.status!=='pending')fail('Already reviewed.');if(flat(c.flat)?.member)fail('This flat already has an account. Complete handover first.');sheet('Approve flat access',form('approve-claim',`<input type="hidden" name="id" value="${c.id}"><div class="card mb">${kv('Resident',c.name)}${kv('Flat',c.flat)}</div>${checkField('verified','I verified this resident and their flat.')}`,'Approve resident','userPlus'));}
```

### action:openNewResident

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:537)

```javascript
()=>{const c=S.claims.find(x=>x.id===S.signup);if(!c||c.status!=='approved')fail('Admin approval is pending.');S.residentFlat=c.flat;S.profile.name=c.name;S.profile.mobile=c.mobile;S.profile.type=c.type;setRole('Resident');}
```

### action:copyInvite

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:538)

```javascript
()=>shareText(S.apartment.invite,'Apartment invite')
```

### action:shareInvite

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:538)

```javascript
()=>shareText(`Join ${S.apartment.name}\nInvite code: ${S.apartment.invite}\nValid until ${dateStr(S.apartment.inviteExpiry)}\nYour flat needs admin approval.`,'Apartment invite')
```

### action:regenerateInvite

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:539)

```javascript
()=>{requireAdmin();confirm('Generate new code?','The current code will stop working. Approved accounts keep their access.','Generate','confirmNewInvite');}
```

### action:confirmNewInvite

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:540)

```javascript
()=>{requireAdmin();const old=S.apartment.invite;S.apartment.invite='SAI-'+Math.random().toString(36).slice(2,6).toUpperCase();S.apartment.inviteActive=true;audit('Invite regenerated',S.apartment.name,old,S.apartment.invite);save();go('invite');toast('New invite created');}
```

### action:toggleInvite

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:541)

```javascript
()=>{requireAdmin();S.apartment.inviteActive=!S.apartment.inviteActive;audit('Invite status changed',S.apartment.invite,!S.apartment.inviteActive?'Active':'Revoked',S.apartment.inviteActive?'Active':'Revoked');save();render();toast(S.apartment.inviteActive?'Invite active':'Invite revoked');}
```

### action:readAll

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:810)

```javascript
()=>{S.notifications.filter(n=>(n.role==='all'||n.role===S.role||isStaff()&&n.role==='staff')&&visibleNotification(n)).forEach(n=>{n.readers=n.readers||[];if(!notificationIsRead(n))n.readers.push(readerKey());});save();render();toast('Inbox marked read');}
```

Earlier definitions retained for wrapper/delegation tracing, not additional active handlers:

[earlier definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:542)

```javascript
()=>{S.notifications.filter(n=>(n.role==='all'||n.role===S.role||isStaff()&&n.role==='staff')&&visibleNotification(n)).forEach(n=>n.read=true);save();render();toast('Notifications marked as read');}
```

### action:runAutomation

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:948)

```javascript
()=>{oldRunAutomation();runAdditionalAutomation();save();render();}
```

Earlier definitions retained for wrapper/delegation tracing, not additional active handlers:

[earlier definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:543)

```javascript
()=>{requireStaff();requireOnline();let before=S.audit.length;createMissingBills(currentMonth);if(S.settings.reminders&&S.settings.reminderDays.split(',').map(v=>Number(v.trim())).includes(Number(today.slice(-2)))){S.bills.filter(b=>b.month===currentMonth&&due(b)>0&&!billPending(b)).forEach(b=>{const key='reminder-'+b.id+'-'+today;if(!S.notifications.some(n=>n.key===key)){notify('Maintenance reminder',INR(due(b))+' pending · '+b.flat,'Resident','bill?id='+b.id,b.flat);S.notifications[0].key=key;}});}S.recurring.filter(r=>r.active&&r.next<=today).forEach(prepareRecurring);S.services.filter(s=>s.next<=nextCycle(today,'Monthly')&&S.settings.serviceNotify).forEach(s=>{let key='service-'+s.id+'-'+s.next;if(!S.notifications.some(n=>n.key===key)){notify('Upcoming service',s.title+' · '+dateStr(s.next),'staff','service?id='+s.id);S.notifications[0].key=key;}});S.bills.forEach(b=>{if(b.latePolicy?.enabled&&due(b)>0&&!billPending(b)&&!b.lateFee){let threshold=new Date(b.due+'T12:00:00');threshold.setDate(threshold.getDate()+Number(b.latePolicy.grace));if(new Date(today+'T12:00:00')>threshold){b.lateFee=Number(b.latePolicy.amount);audit('Late fee applied',b.id,0,b.lateFee);}}});audit('Automation run',today,'Manual preview run',(S.audit.length-before)+' record changes');S.audit[0].actor='System';save();go('automation-log');toast('Demo tasks processed');}
```

### form:payment-submit

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:550)

```javascript
async(D,F)=>{const b=bill(D.billId);if(!canBill(b))fail('You cannot submit for this flat.');if(billPending(b))fail('A payment is already in review for this bill.');const amount=validateAmount(D.amount);if(amount>due(b))fail('The amount exceeds the current balance.');if(!F.has('paidConfirm'))fail('Confirm that you have already made the payment.');if(!D.date||D.date>today)fail('Choose a valid payment date.');const ref=safeText(D.ref,40);if(D.mode!=='Cash'&&!ref)fail('Enter the transaction / cheque reference.');if(ref&&S.payments.some(p=>p.ref===ref&&p.mode===D.mode&&p.status!=='rejected'))fail('This payment reference has already been submitted.');const proof=await dataFile(F.get('attachment'));if(S.settings.proofRequired&&D.mode!=='Cash'&&!proof)fail('Your apartment requires a payment proof file.');const p={id:uid('PAY'),bill:b.id,flat:b.flat,amount,mode:D.mode,date:D.date,ref,proof,note:safeText(D.note,300),status:'pending'};S.payments.push(p);audit('Payment submitted',b.flat+' · '+p.id,'Unsubmitted','In review · '+INR(amount));notify('Payment to review',b.flat+' · '+INR(amount),'staff','payment?id='+p.id);save();go('payment-status?id='+p.id);toast('Submitted for verification');}
```

### form:approve-single

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:551)

```javascript
D=>{if(!D.verified)fail('Confirm that you verified the payment.');approveOne(D.id);save();go('receipt?id='+D.id);toast('Payment approved. Receipt created.');}
```

### form:bulk-confirm

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:552)

```javascript
D=>{if(!D.verified)fail('Confirm verification of every selected payment.');const ids=[...selected];if(!ids.length)fail('No payment selected.');const balances={};ids.forEach(id=>{const p=pay(id);if(!p||p.status!=='pending')fail('A selected payment was already reviewed. Refresh the selection.');balances[p.bill]=(balances[p.bill]||0)+p.amount;if(balances[p.bill]>due(bill(p.bill)))fail('Selected total exceeds a bill balance.');});ids.forEach(approveOne);selected.clear();save();go('approvals');toast(ids.length+' payments approved. Receipts ready.');}
```

### form:reject-payment

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:553)

```javascript
D=>{const p=pay(D.id);if(!p||p.status!=='pending')fail('This payment was already reviewed.');if(!safeText(D.reason))fail('Give the resident a reason.');p.status='rejected';p.reason=safeText(D.reasonType)+': '+safeText(D.reason,300);p.approvedAt=today;audit('Payment rejected',p.id,'In review',p.reason);notify('Payment needs correction',p.reason,'Resident','payment-status?id='+p.id,p.flat);save();go('payment?id='+p.id);toast('Rejected. Resident can resubmit.');}
```

### form:billing-settings

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:823)

```javascript
D=>{requireStaff();const amount=validateAmount(D.amount),bd=finiteInt(D.billingDay,1,28,'Billing day'),dd=finiteInt(D.dueDay,bd,28,'Due day');if(!/^\d{4}-\d{2}$/.test(D.effective)||D.effective<currentMonth)fail('Choose the current or a future month.');if(S.bills.some(b=>b.month>=D.effective&&(!b.kind||b.kind==='maintenance')))fail('Bills already exist for that period. Choose a later effective month.');let fee=Number(D.lateFee),grace=Number(D.grace);if(!Number.isFinite(fee)||fee<0||!Number.isInteger(grace)||grace<0||grace>30)fail('Check late fee and grace days.');const r={id:uid('RATE'),effective:D.effective,amount,billingDay:bd,dueDay:dd,by:S.role};S.billingRules.push(r);Object.assign(S.settings,{amount,billingDay:bd,dueDay:dd,lateFee:fee,grace,configured:true});audit('Maintenance rate configured',S.apartment.id,'Previous rule',INR(amount)+' from '+D.effective);save();go('rate-history');toast('Rate saved for future bills');}
```

Earlier definitions retained for wrapper/delegation tracing, not additional active handlers:

[earlier definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:554)

```javascript
D=>{const amount=validateAmount(D.amount),bd=Number(D.billingDay),dd=Number(D.dueDay);if(!Number.isInteger(bd)||bd<1||bd>28||!Number.isInteger(dd)||dd<bd||dd>28)fail('Use billing / due days from 1–28, with due day on or after billing day.');const old=S.settings.amount;Object.assign(S.settings,{amount,billingDay:bd,dueDay:dd,lateFee:Number(D.lateFee)||0,grace:Number(D.grace)||0});audit('Billing settings updated','Future billing',INR(old),INR(amount)+' · due day '+dd);save();go('money');toast('Saved for future bills');}
```

### form:expense

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:555)

```javascript
async(D,F)=>{const amount=validateAmount(D.amount),attachment=await dataFile(F.get('attachment'));if(F.has('paid')&&D.date>today)fail('A paid expense cannot have a future payment date.');let existing=S.expenses.find(e=>e.id===D.id),old=existing?INR(existing.amount)+(existing.paid?' paid':' draft'):'None',e={...(existing||{}),id:existing?.id||uid('EXP'),title:safeText(D.title,80),category:D.category,amount,date:D.date,mode:D.mode,vendor:D.vendor,notes:safeText(D.notes),public:F.has('public'),paid:F.has('paid'),reversed:!!existing?.reversed&&!F.has('paid'),attachment:attachment||existing?.attachment||null};if(existing)Object.assign(existing,e);else S.expenses.unshift(e);if(e.paid)postedExpense(e);audit(existing?'Expense updated':'Expense created',e.title,old,INR(amount)+(e.paid?' paid':' draft'));save();go('expense?id='+e.id);toast(e.paid?'Expense saved. Balances updated.':'Draft saved. Balance unchanged.');}
```

### form:reverse-expense

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:556)

```javascript
D=>{const e=S.expenses.find(e=>e.id===D.id);if(!e?.paid)fail('This expense is not posted.');e.paid=false;e.reversed=true;e.reversalReason=safeText(D.reason,300);audit('Expense reversed',e.title,INR(e.amount)+' paid',e.reversalReason);save();go('expense?id='+e.id);toast('Expense reversed. Balance updated.');}
```

### form:recurring

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:557)

```javascript
D=>{let existing=S.recurring.find(r=>r.id===D.id);const r={id:existing?.id||uid('REC'),title:safeText(D.title,80),category:D.category,amount:validateAmount(D.amount),frequency:D.frequency,next:D.next,vendor:D.vendor,active:!!D.active};if(existing)Object.assign(existing,r);else S.recurring.push(r);audit('Recurring expense saved',r.title,existing?'Edited':'New',r.frequency+' · '+INR(r.amount));save();go('recurring-detail?id='+r.id);toast('Recurring expense saved');}
```

### form:income

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:558)

```javascript
D=>{let existing=S.income.find(e=>e.id===D.id),e={id:existing?.id||uid('INC'),amount:validateAmount(D.amount),title:safeText(D.title,80),date:D.date,mode:D.mode};if(e.date>today)fail('Choose a date on or before today.');if(existing)Object.assign(existing,e);else S.income.push(e);audit('Other income saved',e.title,'Previous entry / new',INR(e.amount));save();go('income');toast('Income saved. Balance updated.');}
```

### form:payment-settings

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:559)

```javascript
D=>{if(D.upi&&!/^[a-zA-Z0-9.\-_]{2,}@[a-zA-Z0-9]{2,}$/.test(D.upi))fail('Enter a valid UPI ID.');if(D.ifsc&&!/^[A-Z]{4}0[A-Z0-9]{6}$/i.test(D.ifsc))fail('Enter an 11-character IFSC.');['payee','upi','bank','account','ifsc'].forEach(k=>S.settings[k]=safeText(D[k],100));S.settings.ifsc=S.settings.ifsc.toUpperCase();audit('Payment account updated','Association account','Previous account','New account details saved');save();go('payee');toast('Account details saved');}
```

### form:opening-balance

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:560)

```javascript
D=>{const n=Number(D.opening);if(!Number.isFinite(n)||n<0)fail('Enter a valid opening balance.');const before=S.apartment.opening;S.apartment.opening=n;audit('Opening balance changed','1 Sep 2026',INR(before),INR(n));save();go('reports');toast('Opening balance updated');}
```

### form:issue-create

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:561)

```javascript
async(D,F)=>{const i={id:uid('ISS'),category:D.category,title:safeText(D.title,80),description:safeText(D.description,1000),scope:D.scope,priority:D.priority,status:'Open',by:S.role==='Resident'?S.residentFlat:S.role,created:today,vendor:'',eta:'',affected:[S.residentFlat],comments:[],timeline:[{title:'Reported by '+(S.role==='Resident'?S.residentFlat:S.role),time:'Today · just now'}],attachment:await dataFile(F.get('attachment'))};S.issues.unshift(i);audit('Issue reported',i.id,'None',i.category+' · Open');notify('New building issue',i.title,'staff','issue?id='+i.id);save();go('issue?id='+i.id);toast('Issue reported');}
```

### form:issue-assign

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:562)

```javascript
D=>{const i=S.issues.find(i=>i.id===D.id);if(!i)fail('Issue no longer exists.');let old=i.status;i.vendor=D.vendor;i.eta=D.eta;i.status='Assigned';i.timeline.push({title:'Assigned to '+S.vendors.find(v=>v.id===D.vendor)?.name,time:'Today · '+S.role});if(D.note)i.comments.push({by:S.role,text:safeText(D.note),time:'Just now'});if(D.notify)notify('Issue update',i.title+' · technician assigned','all','issue?id='+i.id);audit('Issue assigned',i.id,old,'Assigned');save();go('issue?id='+i.id);toast('Vendor assigned');}
```

### form:issue-update

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:563)

```javascript
D=>{const i=S.issues.find(i=>i.id===D.id);let old=i.status;i.status=D.status;i.eta=D.eta;i.timeline.push({title:D.status+' · '+safeText(D.note),time:'Today · '+S.role});i.comments.push({by:S.role,text:safeText(D.note),time:'Just now'});if(D.notify)notify('Building issue '+D.status.toLowerCase(),i.title,'all','issue?id='+i.id);audit('Issue status updated',i.id,old,D.status);save();go('issue?id='+i.id);toast('Status updated');}
```

### form:issue-comment

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:564)

```javascript
D=>{const i=S.issues.find(x=>x.id===D.id);if(!i)fail('Issue no longer exists.');i.comments.push({by:S.role==='Resident'?S.residentFlat:S.role,text:safeText(D.comment),time:'Just now'});audit('Issue comment added',i.id,'',S.role);save();go('issue-comments?id='+i.id);toast('Update posted');}
```

### form:complaint-create

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:565)

```javascript
async(D,F)=>{const f=isStaff()?D.flat:S.residentFlat;if(!flat(f))fail('Choose a valid flat.');const c={id:uid('CMP'),flat:f,category:D.category,title:safeText(D.title,80),description:safeText(D.description,1000),priority:D.priority,status:'Open',vendor:'',date:today,comments:[],timeline:[{title:'Request submitted',time:'Today · just now'}],attachment:await dataFile(F.get('attachment'))};S.complaints.unshift(c);notify('Private request',f+' · '+c.title,'staff','complaint?id='+c.id);audit('Private request created',c.id,'New','Open');save();go('complaint?id='+c.id);toast('Request created');}
```

### form:complaint-comment

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:566)

```javascript
D=>{const c=S.complaints.find(x=>x.id===D.id);if(!c||!isStaff()&&c.flat!==S.residentFlat)fail('Access denied.');c.comments.push({by:S.role==='Resident'?S.residentFlat:S.role,text:safeText(D.comment),time:'Just now'});audit('Request comment added',c.id,'',S.role);save();go('complaint?id='+c.id);toast('Comment saved');}
```

### form:complaint-update

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:567)

```javascript
D=>{const c=S.complaints.find(x=>x.id===D.id);const old=c.status;c.status=D.status;c.vendor=D.vendor;c.comments.push({by:S.role,text:safeText(D.note),time:'Just now'});c.timeline.push({title:D.status+' · '+safeText(D.note),time:'Today'});audit('Request updated',c.id,old,D.status);notify('Your request was updated',c.title+' · '+c.status,'Resident','complaint?id='+c.id,c.flat);save();go('complaint?id='+c.id);toast('Request updated');}
```

### form:service

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:568)

```javascript
D=>{let old=S.services.find(x=>x.id===D.id),s={id:old?.id||uid('SVC'),title:safeText(D.title,80),category:D.category,frequency:D.frequency,next:D.next,vendor:D.vendor,cost:Number(D.cost)||0,history:old?.history||[]};if(old)Object.assign(old,s);else S.services.push(s);audit('Service schedule saved',s.title,old?'Edited':'New',s.next);save();go('service?id='+s.id);toast('Schedule saved');}
```

### form:service-complete

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:569)

```javascript
async(D,F)=>{let s=S.services.find(x=>x.id===D.id);if(!s)fail('Service not found.');if(D.date>today)fail('Completion cannot be in the future.');if(s.history.some(h=>h.date===D.date&&h.cycle===s.next))fail('This service cycle is already completed.');let cost=Number(D.cost)||0;if(cost<0||!Number.isFinite(cost))fail('Enter a valid cost.');let attachment=await dataFile(F.get('attachment')),entry={date:D.date,note:safeText(D.note),cost,cycle:s.next,attachment};if(F.has('expense')&&cost>0){let e={id:uid('EXP'),title:s.title,category:s.category,amount:cost,date:D.date,vendor:s.vendor,mode:'Bank transfer',notes:entry.note,paid:F.has('paid'),public:true,attachment,service:s.id};S.expenses.unshift(e);entry.expenseId=e.id;audit('Service expense created',s.title,'None',INR(cost)+(e.paid?' paid':' draft'));}s.history.unshift(entry);s.next=nextCycle(D.date,s.frequency);audit('Service completed',s.title,entry.cycle,'Next '+s.next);notify('Service completed',s.title,'all','service?id='+s.id);save();go('service?id='+s.id);toast('Completed. Next service scheduled.');}
```

### form:notice

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:797)

```javascript
async(D,F)=>{requireStaff();const selected=(D.selectedFlats||'').split(',').map(x=>x.trim()).filter(Boolean);if(D.audience==='Selected flats'&&(!selected.length||selected.some(id=>!flat(id))))fail('Enter valid flat IDs.');if(D.mode==='Schedule'&&(!D.sendAt||D.sendAt<=S.referenceNow))fail('Choose a future publishing time.');const old=S.notices.find(n=>n.id===D.id),editDraft=old&&old.status!=='Published';const n={id:editDraft?old.id:uid('N'),title:requiredText(D.title,'Title',100),body:requiredText(D.body,'Message',2000),type:D.type,category:D.category,audience:D.audience,selectedFlats:[...new Set(selected)],status:'Draft',date:refDate(),push:F.has('push'),ackRequired:F.has('ack'),pinned:F.has('pinned'),sendAt:D.mode==='Schedule'?D.sendAt:'',reads:[],acknowledged:[],readBy:0,attachment:await dataFile(F.get('attachment'))||old?.attachment||null};if(editDraft)Object.assign(old,n);else S.notices.unshift(n);audit('Notice draft saved',n.id,old?'Edited':'New','Draft');save();go('notice-preview?id='+n.id);}
```

Earlier definitions retained for wrapper/delegation tracing, not additional active handlers:

[earlier definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:570)

```javascript
async(D,F)=>{let old=S.notices.find(n=>n.id===D.id),selectedFlats=(D.selectedFlats||'').split(',').map(s=>s.trim()).filter(Boolean);if(D.audience==='Selected flats'&&(!selectedFlats.length||selectedFlats.some(f=>!flat(f))))fail('Enter valid comma-separated flat numbers.');let attachment=await dataFile(F.get('attachment')),n={id:old?.id||uid('N'),title:safeText(D.title,100),body:safeText(D.body,2000),type:D.type,category:D.category,audience:D.audience,selectedFlats,status:D.status,pinned:F.has('pinned'),date:old?.date||today,readBy:old?.readBy||0,attachment:attachment||old?.attachment||null};if(old)Object.assign(old,n);else S.notices.unshift(n);audit('Notice saved',n.title,old?'Edited':'New',n.status);if(n.status==='Published')notify('New notice',n.title,'all','notice?id='+n.id);save();go('notice?id='+n.id);toast(n.status==='Draft'?'Draft saved':'Notice published');}
```

### form:vendor

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:571)

```javascript
D=>{let v=S.vendors.find(x=>x.id===D.id),n={id:v?.id||uid('V'),name:safeText(D.name,80),category:D.category,contact:safeText(D.contact,80),phone:safeText(D.phone,16),amc:D.amc,notes:safeText(D.notes),public:!!D.public};if(n.phone&&!/^\+?[\d\s-]{7,16}$/.test(n.phone))fail('Enter a valid phone number.');if(v)Object.assign(v,n);else S.vendors.push(n);audit('Vendor saved',n.name,v?'Edited':'New',n.category);save();go('vendor?id='+n.id);toast('Vendor saved');}
```

### form:contact

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:572)

```javascript
D=>{if(!D.verified)fail('Confirm the phone number is verified.');if(!/^\+?[\d\s-]{3,16}$/.test(D.phone))fail('Enter a valid telephone number.');let c=S.contacts.find(x=>x.id===D.id),n={id:c?.id||uid('C'),name:safeText(D.name,80),type:D.type,phone:safeText(D.phone,16),public:true};if(c)Object.assign(c,n);else S.contacts.push(n);audit('Contact saved',n.name,c?'Edited':'New','Verified phone');save();go('contact?id='+n.id);toast('Contact saved');}
```

### form:document

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:573)

```javascript
async(D,F)=>{let old=S.docs.find(d=>d.id===D.id),attachment=await dataFile(F.get('attachment'));if(!attachment&&!old)fail('Choose a file to upload.');let d={...(old||{}),id:old?.id||uid('DOC'),title:safeText(D.title,100),category:D.category,public:F.has('public'),date:old?.date||today,attachment:attachment||old?.attachment};if(old)Object.assign(old,d);else S.docs.unshift(d);audit('Document saved',d.title,old?'Edited':'New',d.public?'Public':'Private');save();go('document?id='+d.id);toast('Document stored in this browser');}
```

### form:login

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:933)

```javascript
D=>{oldLoginHandler(D);if(!S.settings.configured&&isAdmin()){go('setup-done');}}
```

Earlier definitions retained for wrapper/delegation tracing, not additional active handlers:

[earlier definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:574)

```javascript
D=>{if(!/^[6-9]\d{9}$/.test(D.mobile)||!/^\d{4,6}$/.test(D.pin))fail('Use a 10-digit mobile and a 4–6 digit demo PIN.');setRole(D.role);toast('Signed in to the demo');}
```

### form:forgot-pin

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)

```javascript
D=>{draft.resetMobile=D.mobile;go('verify-reset');}
```

### form:verify-reset

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)

```javascript
D=>{if(D.code!=='246810')fail('Use the demo code 246810.');go('reset-pin');}
```

### form:reset-pin

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:575)

```javascript
D=>{if(D.pin!==D.confirm)fail('The PINs do not match.');go(loginPath(draft.signInRole));toast('Demo PIN reset flow complete');}
```

### form:join

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:576)

```javascript
D=>{if(!S.apartment.inviteActive||S.apartment.inviteExpiry<today||D.invite.trim().toUpperCase()!==S.apartment.invite){go('invite-invalid');return;}draft.inviteVerified=true;go('signup');}
```

### form:signup

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:577)

```javascript
D=>{if(!D.consent)fail('Agree to share your details with the apartment admin.');if(!/^[6-9]\d{9}$/.test(D.mobile))fail('Enter a valid 10-digit mobile number.');draft.signup={name:safeText(D.name,80),mobile:D.mobile,type:D.type};go('choose-flat');}
```

### form:admin-signup

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:578)

```javascript
D=>{if(!D.confirm)fail('Confirm you are authorised to set up the apartment.');draft.admin={name:safeText(D.name,80),mobile:D.mobile};go('setup-apartment');}
```

### form:setup-apartment

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:929)

```javascript
D=>{const ref=draft.admin?.apartmentReferral||'';oldSetup(D);newExtras(S,true);S.apartment.id=uid('APT');S.subscription.sourceReferral=safeText(ref,30);firstInvoice().amount=refPlan().price;persist();go('setup-done');}
```

Earlier definitions retained for wrapper/delegation tracing, not additional active handlers:

[earlier definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:579)

```javascript
D=>{const count=Number(D.flats);if(!Number.isInteger(count)||count<5||count>50)fail('This release supports 5–50 flats.');const oldTheme=S.theme,oldThemeMode=S.themeMode,admin=draft.admin;S=makeSeed();S.theme=oldTheme;S.themeMode=oldThemeMode;S.role='Admin';S.staffProfiles={Admin:{name:admin?.name||'Admin',mobile:admin?.mobile||'',email:''}};S.apartment={...S.apartment,name:safeText(D.name,80),city:D.city,address:safeText(D.address,300),pincode:D.pincode,opening:0,blocks:['A'],invite:'APT-'+Math.random().toString(36).slice(2,6).toUpperCase()};S.flats=Array.from({length:count},(_,i)=>({id:'A-'+String(101+i),block:'A',floor:Math.floor(i/5)+1,name:'Unassigned',type:'Owner',member:false,occupied:true,active:true,billing:2000}));['payments','bills','expenses','income','recurring','issues','complaints','services','notices','docs','vendors','claims','audit','notifications'].forEach(k=>S[k]=[]);S.roles=[{id:'ROLE-1',name:admin?.name||'Admin',role:'Admin',flat:S.flats[0].id}];audit('Apartment created',S.apartment.name,'None',count+' flats');persist();go('setup-done');}
```

### form:apartment

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:580)

```javascript
D=>{const old=S.apartment.name;Object.assign(S.apartment,{name:safeText(D.name,80),city:safeText(D.city,80),address:safeText(D.address,300),pincode:D.pincode});audit('Apartment profile updated',S.apartment.name,old,S.apartment.name);save();go('apartment');toast('Apartment saved');}
```

### form:block

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:581)

```javascript
D=>{let name=D.name.trim().toUpperCase();if(S.apartment.blocks.includes(name)&&name!==D.id)fail('This block already exists.');if(D.id){S.apartment.blocks=S.apartment.blocks.map(b=>b===D.id?name:b);S.flats.filter(f=>f.block===D.id).forEach(f=>f.block=name);}else S.apartment.blocks.push(name);audit('Block saved',name,D.id||'New',name);save();go('blocks');toast('Block saved');}
```

### form:flat

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:582)

```javascript
D=>{const existing=flat(D.original),id=D.id.toUpperCase().trim();if(!existing&&flat(id))fail('That flat ID already exists.');if(!existing&&S.flats.length>=50)fail('This reference supports up to 50 flats.');const f={...(existing||{name:'Unassigned',member:false,type:'Owner'}),id,block:D.block,floor:Number(D.floor),occupied:!!D.occupied,active:!!D.active,billing:S.settings.amount};if(existing)Object.assign(existing,f);else S.flats.push(f);audit('Flat saved',id,existing?'Edited':'New',f.active?'Active':'Inactive');save();go('flat?id='+id);toast('Flat saved');}
```

### form:approve-claim

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:931)

```javascript
D=>{oldApproveClaim(D);S.memberRecords=S.memberRecords.filter(m=>flat(m.flat)?.member!==true);save();}
```

Earlier definitions retained for wrapper/delegation tracing, not additional active handlers:

[earlier definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:583)

```javascript
D=>{if(!D.verified)fail('Confirm that the resident and flat are verified.');const c=S.claims.find(c=>c.id===D.id);if(!c||c.status!=='pending')fail('This request was already reviewed.');const f=flat(c.flat);if(f.member)fail('This flat already has an active account.');c.status='approved';Object.assign(f,{member:true,name:c.name,mobile:c.mobile,type:c.type,occupied:true});audit('Resident approved',c.flat,'Account pending',c.name+' · Active');notify('You’re approved',c.flat+' · welcome home','Resident','signup-pending',c.flat);save();go('join-review?id='+c.id);toast('Resident approved');}
```

### form:join-reject

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:584)

```javascript
D=>{const c=S.claims.find(x=>x.id===D.id);if(!c||c.status!=='pending')fail('Already reviewed.');c.status='rejected';c.reason=safeText(D.reason,300);audit('Join request rejected',c.flat,'Pending',c.reason);save();go('join-requests');toast('Request rejected');}
```

### form:invite-settings

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:585)

```javascript
D=>{if(D.expiry<today)fail('Choose today or a future date.');S.apartment.inviteExpiry=D.expiry;audit('Invite expiry changed',S.apartment.invite,'Old expiry',D.expiry);save();go('invite');toast('Invite expiry saved');}
```

### form:handover

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:586)

```javascript
D=>{if(!D.confirmed)fail('Confirm that the handover was verified.');const f=flat(D.flat);if(!f?.member)fail('This flat has no active account to transfer.');const previous=f.name;f.member=false;f.formerResident=previous;f.name='Awaiting handover';const c={id:uid('CL'),name:safeText(D.name,80),mobile:D.mobile,flat:f.id,type:D.type,status:'pending',date:today,handover:true};S.claims.push(c);audit('Resident handover started',f.id,previous+' · active','Previous access revoked; '+c.name+' pending');save();go('join-review?id='+c.id);toast('Old access revoked. New resident pending.');}
```

### form:role

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:587)

```javascript
D=>{let old=S.roles.find(x=>x.id===D.id);if(old?.role==='Admin'&&D.role!=='Admin'&&S.roles.filter(r=>r.role==='Admin').length===1)fail('Keep at least one Admin account.');let r={id:old?.id||uid('ROLE'),name:safeText(D.name,80),flat:D.flat,role:D.role};if(old)Object.assign(old,r);else S.roles.push(r);audit('Team access saved',r.name,old?'Existing':'New',r.role);save();go('roles');toast('Role saved');}
```

### form:profile

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:588)

```javascript
D=>{if(S.role==='Resident'){Object.assign(S.profile,{name:safeText(D.name,80),mobile:safeText(D.mobile,16),email:safeText(D.email,100)});const f=flat(S.residentFlat);if(f)f.name=S.profile.name;}else{S.staffProfiles=S.staffProfiles||{};S.staffProfiles[S.role]={name:safeText(D.name,80),mobile:safeText(D.mobile,16),email:safeText(D.email,100)};}audit('Profile updated',S.role,'Existing','Saved');save();go('profile');toast('Profile saved');}
```

### form:quiet-hours

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:589)

```javascript
D=>{setPreference('quietStart',D.quietStart);setPreference('quietEnd',D.quietEnd);save();go('notification-settings');toast('Quiet hours saved');}
```

### form:reminder-settings

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:590)

```javascript
D=>{let days=D.days.split(',').map(v=>Number(v.trim()));if(!days.length||days.some(v=>!Number.isInteger(v)||v<1||v>28))fail('Enter comma-separated days from 1 to 28.');S.settings.reminderDays=[...new Set(days)].sort((a,b)=>a-b).join(', ');audit('Reminder schedule changed','Maintenance','Previous schedule',S.settings.reminderDays);save();go('notification-settings');toast('Reminder schedule saved');}
```

### form:change-pin

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:591)

```javascript
D=>{if(D.pin!==D.confirm)fail('The new PINs do not match.');if(D.pin===D.current)fail('Use a different new PIN.');go('security');toast('Demo PIN change flow complete');}
```

### form:support

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:592)

```javascript
D=>{S.support=S.support||[];S.support.push({id:uid('SUP'),topic:D.topic,message:safeText(D.message,1000),date:today});save();sheet('Request saved',`${stateHero('checkCircle','We have your message.','Demo request saved locally.')}<div class="mt">${btn('Done','help','check','full')}</div>`);}
```

### form:event-draft

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:743)

```javascript
D=>{if(!D.rules)fail('Accept the booking rules.');let old=D.id?eventObj(D.id):null;if(old&&!eventOwner(old))fail('You cannot change this request.');const e=validateBooking(D);if(old&&e.flat!==old.flat)fail('The organiser flat cannot be changed.');draft.event={...e,id:old?.id||'',status:old?.status||'Draft',allocation:[]};go('event-availability');}
```

### form:event-allocation

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:744)

```javascript
D=>{if(!draft.event)fail('Start an event request.');draft.event.cars=finiteInt(D.cars,0,S.bookingRules.maxCars,'Cars');draft.event.bikes=finiteInt(D.bikes,0,S.bookingRules.maxBikes,'Bikes');draft.event.allocation=[];const c=bookingCheck(draft.event,draft.event.id);if(!c.ok)fail(c.conflicts.join(' '));go('event-review');}
```

### form:approve-booking

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:745)

```javascript
D=>{requireAdmin();if(!D.verified)fail('Confirm the space and parking checks.');let e=eventObj(D.id);if(!e||(e.status!=='Pending'&&!e.pendingChange))fail('This request is no longer pending.');let c=cp(e.pendingChange||e);validateBooking(c);const check=bookingCheck({...c,allocation:[]},e.id);if(!check.ok)fail(check.conflicts.join(' '));Object.assign(e,c,{id:e.id,status:'Approved',allocation:check.allocation});delete e.pendingChange;delete e.offer;bookingLog(e,'Approved by Admin');if(D.announce)bookingNotice(e);save();go('event?id='+e.id);toast('Booking approved · spaces reserved');}
```

### form:offer-booking

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:746)

```javascript
D=>{requireAdmin();const e=eventObj(D.id);if(!e||(e.status!=='Pending'&&!e.pendingChange&&e.status!=='Alternative offered'))fail('No pending request to change.');if(D.flat!==e.flat)fail('Keep the original organiser flat.');const offer=validateBooking(D);offer.allocation=[];offer.reason=requiredText(D.reason,'Reason',300);const c=bookingCheck(offer,e.id);if(!c.ok)fail(c.conflicts.join(' '));e.offer=offer;if(e.status!=='Approved')e.status='Alternative offered';bookingLog(e,'Alternative offered');save();go('event?id='+e.id);toast('Alternative sent to resident');}
```

### form:reject-booking

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:747)

```javascript
D=>{requireAdmin();const e=eventObj(D.id);if(!e||(e.status!=='Pending'&&e.status!=='Alternative offered'&&!e.pendingChange))fail('Request is no longer pending.');const reason=requiredText(D.reason,'Reason',300);if(e.pendingChange){delete e.pendingChange;delete e.offer;}else{e.status='Rejected';e.allocation=[];delete e.offer;}e.reason=reason;bookingLog(e,'Request rejected · '+reason);save();go('event?id='+e.id);toast('Decision saved');}
```

### form:cancel-booking

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:748)

```javascript
D=>{const e=eventObj(D.id);if(!eventOwner(e))fail('Only the organiser or admin can cancel.');if(!D.confirmed)fail('Confirm cancellation.');if(['Cancelled','Completed','Rejected'].includes(e.status))fail('Event is already closed.');e.reason=requiredText(D.reason,'Reason',300);e.status='Cancelled';e.allocation=[];delete e.pendingChange;delete e.offer;bookingLog(e,'Cancelled · '+e.reason);save();go('event?id='+e.id);toast('Cancelled · reservation released');}
```

### form:complete-booking

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:749)

```javascript
D=>{requireAdmin();const e=eventObj(D.id);if(!e||e.status!=='Approved')fail('Only approved events can be completed.');if(new Date(S.referenceNow)<new Date(e.end))fail('The event has not ended. Use the preview clock to test completion.');if(!D.checked)fail('Confirm the spaces are clear.');e.status='Completed';bookingLog(e,'Completed · '+safeText(D.note,300));save();go('event?id='+e.id);toast('Event completed');}
```

### form:calendar-date

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:750)

```javascript
D=>go('event-calendar?date='+D.date)
```

### form:parking-search

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:751)

```javascript
D=>{if(!D.start||!D.end||D.end<=D.start)fail('Choose a valid time range.');draft.parkingSearch={...D,setup:S.bookingRules.setupMinutes,cleanup:S.bookingRules.cleanupMinutes};render();}
```

### form:resource

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:752)

```javascript
D=>{requireAdmin();const old=resource(D.id);if(old?.type==='Protected')fail('Protected routes are not bookable.');if(old&&S.events.some(e=>e.status==='Approved'&&claimedResources(e).includes(old.id)&&new Date(e.end)>new Date(S.referenceNow)))fail('This resource has approved bookings. Resolve those before changing its configuration.');const blocks=(D.blocks||'').split(',').map(x=>x.trim()).filter(Boolean);if(blocks.some(id=>!resource(id)||resource(id).type==='Common space'||resource(id).type==='Protected'))fail('Use valid parking bay IDs, never a protected route.');if(D.type==='Private car'&&!flat(D.owner)?.member)fail('Choose the private bay holder.');const r={id:old?.id||uid('RES'),name:requiredText(D.name,'Name',60),type:D.type,capacity:finiteInt(D.capacity,1,200,'Capacity'),enabled:!!D.enabled,blocks,owner:D.type==='Private car'?D.owner:'',consent:old?.owner===D.owner?old?.consent:false,shareStart:old?.shareStart,shareEnd:old?.shareEnd};if(old)Object.assign(old,r);else S.resources.push(r);audit('Space configured',r.name,old?'Updated':'New',r.type);save();go('resources');}
```

### form:resource-block

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:753)

```javascript
D=>{requireAdmin();if(!resource(D.resource)||D.end<=D.start)fail('Choose a space and valid dates.');const candidate={start:D.start,end:D.end,space:D.resource,setup:0,cleanup:0};if(S.events.some(e=>e.status==='Approved'&&overlaps(candidate,e)&&claimedResources(e).some(id=>[D.resource,...(resource(D.resource)?.blocks||[])].includes(id))))fail('An approved booking overlaps. Resolve it first.');const b={id:D.id||uid('BLOCK'),resource:D.resource,start:D.start,end:D.end,reason:requiredText(D.reason,'Reason',100)};const old=S.resourceBlocks.find(x=>x.id===D.id);if(old)Object.assign(old,b);else S.resourceBlocks.push(b);audit('Space time blocked',resource(b.resource).name,'Available',b.reason);save();go('resource-blocks');}
```

### form:share-parking

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:754)

```javascript
D=>{const r=resource(D.id);if(S.role!=='Resident'||r?.owner!==S.residentFlat)fail('Only the bay holder can share it.');if(!D.consent)fail('Consent is required to share the bay.');if(!D.start||D.end<=D.start)fail('Choose a valid time range.');if(S.events.some(e=>e.status==='Approved'&&(e.allocation||[]).includes(r.id)))fail('This bay has an approved reservation. Contact the admin before changing it.');Object.assign(r,{consent:true,shareStart:D.start,shareEnd:D.end});audit('Private parking shared',r.id,'Private',D.start+' – '+D.end);save();go('parking-availability');}
```

### form:booking-rules

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:755)

```javascript
D=>{requireAdmin();const r={maxGuests:finiteInt(D.maxGuests,1,200,'Guests'),maxHours:finiteInt(D.maxHours,1,24,'Hours'),advanceDays:finiteInt(D.advanceDays,1,365,'Days'),setupMinutes:finiteInt(D.setupMinutes,0,180,'Setup'),cleanupMinutes:finiteInt(D.cleanupMinutes,0,180,'Cleanup'),maxCars:finiteInt(D.maxCars,0,30,'Cars'),maxBikes:finiteInt(D.maxBikes,0,50,'Bikes'),quietStart:D.quietStart,quietEnd:D.quietEnd};if(r.quietStart===r.quietEnd)fail('Set distinct quiet-hour times.');S.bookingRules=r;audit('Booking rules saved','Common areas','Previous','New rules for new requests');save();go('booking-rules');}
```

### action:submitEvent

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:760)

```javascript
()=>{requireOnline();const d=draft.event;if(!d)fail('Start a request first.');validateBooking(d);const check=bookingCheck(d,d.id);if(!check.ok)fail(check.conflicts.join(' '));let old=d.id?eventObj(d.id):null;if(old&&!eventOwner(old))fail('Access denied.');let e;if(old?.status==='Approved'){old.pendingChange={...cp(d),allocation:[]};e=old;bookingLog(e,'Change requested');}else if(old){if(['Cancelled','Completed','Rejected'].includes(old.status))fail('Event is closed.');Object.assign(old,cp(d),{status:'Pending',allocation:[]});delete old.offer;e=old;bookingLog(e,'Request updated');}else{e={...cp(d),id:uid('EV'),status:'Pending',allocation:[],timeline:[{title:'Requested · '+d.flat,time:stamp()}]};S.events.unshift(e);audit('Event requested',e.id,'New','Pending');}notify('Booking to review',e.title,'Admin','booking-review?id='+e.id);draft.lastEvent=e.id;delete draft.event;save();go('event-submitted?id='+e.id);}
```

### action:acceptEventOffer

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:761)

```javascript
d=>{requireOnline();const e=eventObj(d.id);if(!e?.offer||S.role!=='Resident'||e.flat!==S.residentFlat)fail('Only the organiser can accept.');validateBooking(e.offer);const c=bookingCheck({...e.offer,allocation:[]},e.id);if(!c.ok)fail('Availability changed. '+c.conflicts.join(' '));const id=e.id;Object.assign(e,cp(e.offer),{id,status:'Approved',allocation:c.allocation});delete e.offer;delete e.pendingChange;bookingLog(e,'Alternative accepted · booking approved');save();go('event?id='+id);toast('Alternative accepted · reserved');}
```

### action:removeResourceBlock

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:762)

```javascript
d=>{requireAdmin();requireOnline();confirm('Remove blocked time?','The period will become bookable again.','Remove','confirmRemoveResourceBlock',`data-id="${d.id}"`,true);}
```

### action:confirmRemoveResourceBlock

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:763)

```javascript
d=>{requireAdmin();requireOnline();S.resourceBlocks=S.resourceBlocks.filter(x=>x.id!==d.id);audit('Blocked time removed',d.id,'Blocked','Available');save();go('resource-blocks');}
```

### action:unshareParking

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:764)

```javascript
d=>{requireOnline();const r=resource(d.id);if(S.role!=='Resident'||r?.owner!==S.residentFlat)fail('Only the bay holder can change consent.');if(S.events.some(e=>e.status==='Approved'&&(e.allocation||[]).includes(r.id)))fail('An approved booking uses this bay. Contact the admin first.');r.consent=false;audit('Parking sharing stopped',r.id,'Shared','Private');save();render();toast('Bay is private');}
```

### form:member-record

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:777)

```javascript
D=>{requireAdmin();const f=flat(D.flat);if(!f)fail('Choose a flat.');if(D.original&&D.original!==D.flat)fail('Use handover to change a member’s flat.');const name=requiredText(D.name,'Name',80);if(f.member){if(!D.original)fail('This flat already has an active account.');Object.assign(f,{name,type:D.type,publicPhone:!!D.publicPhone});if(D.mobile!==f.mobile)f.pendingMobile=safeText(D.mobile,16);if(f.id===S.residentFlat)S.profile.name=name;}else{if(!/^[6-9]\d{9}$/.test(D.mobile))fail('Enter a 10-digit mobile number.');const m={id:uid('MEM'),flat:f.id,name,mobile:D.mobile,type:D.type,status:'Invited'};let old=S.memberRecords.find(x=>x.flat===f.id);if(old)Object.assign(old,m);else S.memberRecords.push(m);f.publicPhone=!!D.publicPhone;}audit('Member saved',f.id,'Previous',name+' · '+(f.member?'Active':'Invite pending'));save();go('member?id='+f.id);toast('Member record saved');}
```

### form:committee

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:778)

```javascript
D=>{requireAdmin();if(!flat(D.flat)?.member)fail('Choose an active member.');if(!D.start||D.end<D.start)fail('Term end must be on or after its start.');if(S.committee.some(c=>c.id!==D.id&&c.active&&c.flat===D.flat&&c.title===D.title))fail('This committee role already exists.');const c={id:D.id||uid('COM'),flat:D.flat,title:D.title,start:D.start,end:D.end,active:true};let old=S.committee.find(x=>x.id===D.id);if(old)Object.assign(old,c);else S.committee.push(c);audit('Committee role saved',D.flat,'Committee',D.title+' · no permission change');save();go('committee');toast('Committee updated');}
```

### action:archiveCommittee

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:781)

```javascript
d=>{requireAdmin();confirm('Archive committee role?','App access and the member account will not change.','Archive','confirmArchiveCommittee',`data-id="${d.id}"`);}
```

### action:confirmArchiveCommittee

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:782)

```javascript
d=>{requireAdmin();requireOnline();const c=S.committee.find(x=>x.id===d.id);if(!c)fail('Role unavailable.');c.active=false;audit('Committee role archived',c.flat,c.title,'Archived');save();go('committee');}
```

### action:publishAnnouncement

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:799)

```javascript
d=>{requireStaff();requireOnline();const n=S.notices.find(n=>n.id===d.id);if(!n||n.status==='Published')fail('No unpublished notice to send.');if(n.sendAt){if(n.sendAt<=S.referenceNow)fail('Scheduled time has passed; edit or publish now.');n.status='Scheduled';audit('Notice scheduled',n.id,'Draft',n.sendAt);save();go('notice?id='+n.id);toast('Notice scheduled');}else{publishNoticeRecord(n);save();go('notice?id='+n.id);toast('Published in the demo inbox');}}
```

### action:keepNoticeDraft

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:801)

```javascript
d=>{save();go('notices');toast('Draft saved');}
```

### action:ackNotice

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:803)

```javascript
d=>{const n=S.notices.find(n=>n.id===d.id);if(S.role!=='Resident'||!n?.ackRequired||!visibleNotice(n))fail('Acknowledgement is unavailable.');n.reads=n.reads||[];n.acknowledged=n.acknowledged||[];if(!n.reads.includes(S.residentFlat))n.reads.push(S.residentFlat);if(!n.acknowledged.includes(S.residentFlat))n.acknowledged.push(S.residentFlat);audit('Notice acknowledged',n.id,'Not acknowledged',S.residentFlat);save();render();toast('Acknowledged');}
```

### form:rate-override

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:824)

```javascript
D=>{requireStaff();if(!flat(D.flat))fail('Choose a valid flat.');if(D.effective<=currentMonth||S.bills.some(b=>b.flat===D.flat&&b.month>=D.effective&&(!b.kind||b.kind==='maintenance')))fail('Choose a future month without generated bills.');let r={id:D.id||uid('OVR'),flat:D.flat,effective:D.effective,amount:validateAmount(D.amount),reason:requiredText(D.reason,'Reason',300)};const old=S.rateOverrides.find(x=>x.id===D.id);if(old)Object.assign(old,r);else S.rateOverrides.push(r);audit('Flat rate saved',r.flat,'Previous',INR(r.amount)+' · '+r.effective);save();go('rate-overrides');}
```

### form:contribution-draft

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:832)

```javascript
D=>{requireStaff();let fs=D.scope==='All active flats'?S.flats.filter(f=>f.active).map(f=>f.id):(D.flats||'').split(',').map(f=>f.trim()).filter(Boolean);if(!fs.length||fs.some(f=>!flat(f)?.active))fail('Choose valid active flats.');if(D.due<refDate())fail('Choose today or a future due date.');draft.charge={id:uid('CHG'),kind:'contribution',title:requiredText(D.title,'Name',80),amount:validateAmount(D.amount),due:D.due,flats:[...new Set(fs)]};go('contribution-preview');}
```

### form:opening-due

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:833)

```javascript
D=>{requireStaff();if(!D.verified)fail('Confirm that the opening balance was checked.');if(!flat(D.flat))fail('Choose a valid flat.');if(S.bills.some(b=>b.flat===D.flat&&b.kind==='opening'))fail('This flat already has an opening-due entry. Do not import it twice.');if(!D.asOf||D.asOf>today)fail('Use a date on or before the demo date.');const b={id:uid('OPEN'),kind:'opening',flat:D.flat,month:currentMonth,amount:validateAmount(D.amount),lateFee:0,due:today,title:'Opening due · '+requiredText(D.reason,'Description',300),asOf:D.asOf};S.bills.push(b);audit('Opening due added',b.flat,'None',INR(b.amount));notify('Opening balance recorded',INR(b.amount)+' · awaiting payment','Resident','bill?id='+b.id,b.flat);save();go('bill?id='+b.id);toast('Opening due added · cash unchanged');}
```

### action:createContribution

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:835)

```javascript
()=>{requireStaff();requireOnline();let g=draft.charge;if(!g)fail('Start a collection first.');if(S.charges.some(c=>c.id===g.id))fail('Collection already created.');S.charges.push(cp(g));g.flats.forEach(f=>{const b={id:uid('CON'),kind:'contribution',group:g.id,title:g.title,flat:f,amount:g.amount,month:currentMonth,lateFee:0,due:g.due};S.bills.push(b);notify('New contribution',g.title+' · '+INR(g.amount),'Resident','bill?id='+b.id,f);});audit('Contribution created',g.title,'None',g.flats.length+' flats · '+INR(g.amount));delete draft.charge;save();go('contribution?id='+g.id);toast('Dues created · no payment recorded');}
```

### form:referral-opt-in

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:874)

```javascript
D=>{requireAdmin();if(!D.terms)fail('Accept the offer rules.');if(!eligibleWindow())fail('The first-month offer window has ended.');S.subscription.offerOptIn=true;firstInvoice().status=firstInvoice().status==='Paid'?'Paid':'Offer pending';qualifyReferral();audit('Referral offer activated',S.subscription.referralCode,'Not enrolled','Enrolled');save();go('referrals');}
```

### form:referral-add

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:875)

```javascript
D=>{requireAdmin();const name=requiredText(D.name,'Apartment name',80),city=requiredText(D.city,'City',80),code=requiredText(D.code,'Apartment identifier',30).toUpperCase();if(code===S.apartment.id||name.toLowerCase()===S.apartment.name.toLowerCase()&&city.toLowerCase()===S.apartment.city.toLowerCase())fail('Your own apartment cannot be referred.');if(S.subscription.referrals.some(r=>r.code===code||r.name.toLowerCase()===name.toLowerCase()&&r.city.toLowerCase()===city.toLowerCase()))fail('This apartment has already been referred.');let r={id:uid('REF'),name,city,code,flats:finiteInt(D.flats,5,50,'Flats'),stage:'Referred',phoneVerified:false,residentVerified:false,distinct:true,credit:false};S.subscription.referrals.push(r);audit('Apartment referred',r.name,'New','Referred');save();go('referral-detail?id='+r.id);}
```

### form:activation-simulation

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:876)

```javascript
D=>{requireAdmin();if(page!=='activation-simulation')fail('Use the provider simulation screen.');const r=S.subscription.referrals.find(r=>r.id===D.id);if(!r)fail('Referral unavailable.');if(r.credit&&D.stage!=='Activated')fail('A rewarded activation requires provider fraud review, not silent reversal.');if(D.stage==='Activated'&&(!D.phoneVerified||!D.residentVerified||!D.distinct))fail('All activation checks must pass. Installation alone is insufficient.');Object.assign(r,{stage:D.stage,phoneVerified:!!D.phoneVerified,residentVerified:!!D.residentVerified,distinct:!!D.distinct});if(D.stage==='Activated')r.activatedDate=r.activatedDate||refDate();qualifyReferral();audit('Provider event simulated',r.name,'Previous',r.stage);save();go(S.subscription.firstCredit?'referral-reward':'provider-simulation');toast('Demo activation updated');}
```

### form:subscription-payment-simulation

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:877)

```javascript
D=>{requireAdmin();if(page!=='subscription-payment-simulation')fail('Use the reference provider screen.');if(!D.verified)fail('Confirm provider verification.');const i=S.subscription.invoices.find(i=>i.id===D.invoice);if(!i||subBalance(i)<=0)fail('No amount is due for this invoice.');const ref=requiredText(D.reference,'Reference',40);if(S.subscription.invoices.some(x=>x.reference===ref))fail('This subscription reference was already recorded.');i.paidAmount=subBalance(i);i.reference=ref;i.status='Paid';i.paidAt=stamp();audit('Provider subscription receipt simulated',i.id,'Due','Paid');save();go('subscription-invoice?id='+i.id);toast('Demo payment recorded · not a real charge');}
```

### action:shareApartmentReferral

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:880)

```javascript
()=>shareText('Try ApartmentCare for your apartment.\nReferral code: '+S.subscription.referralCode+'\nFirst-month offer needs two new verified apartment activations.\nPrototype invitation — no live installation link.','Apartment referral')
```

### action:simulateRenewal

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:881)

```javascript
()=>{requireAdmin();if(page!=='provider-simulation')fail('Use the reference controls.');const s=S.subscription;S.referenceNow=s.firstEnd+'T09:00';if(!s.invoices.some(i=>i.start===s.firstEnd)){s.invoices.push({id:'SUB-002',period:'Month 2',start:s.firstEnd,end:nextCycle(s.firstEnd,'Monthly'),amount:refPlan().price,discount:0,status:'Due',date:s.firstEnd});s.renewals=1;}if(firstInvoice().status==='Offer pending')firstInvoice().status='Due';s.status='Payment due';audit('Month 2 simulated','Subscription','First month','Normal paid invoice');save();go('subscription');toast('Month 2 invoice created · no free extension');}
```

### action:downloadSubInvoice

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:882)

```javascript
d=>{requireAdmin();const i=S.subscription.invoices.find(i=>i.id===d.id);if(!i)fail('Invoice unavailable.');doDownload(i.id+'-demo.txt',['APARTMENTCARE · DEMONSTRATION RECORD',S.apartment.name,'Software subscription only','Period: '+i.start+' to '+i.end,'Amount: '+INR(i.amount),'Credit: '+INR(i.discount||0),'Status: '+i.status,'Due: '+INR(subBalance(i)),'NOT A TAX INVOICE. NO LIVE PAYMENT.'].join('\n'));}
```

### form:poll

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:894)

```javascript
D=>{requireAdmin();const options=D.options.split('\n').map(x=>x.trim()).filter(Boolean);if(options.length<2||options.length>5||new Set(options).size!==options.length)fail('Use 2–5 different choices.');if(D.end<refDate())fail('Choose today or a future date.');const p={id:uid('POLL'),question:requiredText(D.question,'Question',150),options,end:D.end,status:'Open',votes:{},anonymous:true};S.polls.unshift(p);audit('Poll published',p.id,'New',p.question);notify('Community poll',p.question,'all','poll?id='+p.id);save();go('poll?id='+p.id);}
```

### form:vehicle

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:895)

```javascript
D=>{if(S.role!=='Resident')fail('Use a resident account.');const old=S.vehicles.find(v=>v.id===D.id);if(old&&old.flat!==S.residentFlat)fail('Not your vehicle.');const number=requiredText(D.number,'Registration',20).toUpperCase();if(S.vehicles.some(v=>v.id!==D.id&&v.number.replace(/\s/g,'')===number.replace(/\s/g,'')))fail('Vehicle is already registered.');const v={id:D.id||uid('VEH'),flat:S.residentFlat,number,type:D.type,color:requiredText(D.color,'Colour',30)};if(old)Object.assign(old,v);else S.vehicles.push(v);audit('Vehicle saved',v.id,'Previous',v.flat);save();go('vehicle?id='+v.id);}
```

### form:vehicle-contact

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:896)

```javascript
D=>{const v=S.vehicles.find(v=>v.id===D.id);if(!v)fail('Vehicle unavailable.');const from=S.role==='Resident'?S.residentFlat:S.role;if(S.vehicleMessages.some(x=>x.vehicle===v.id&&x.from===from&&x.date===refDate()))fail('You already notified this owner today. Contact the admin for urgent follow-up.');S.vehicleMessages.push({id:uid('MSG'),vehicle:v.id,from,date:refDate()});notify('Vehicle request',v.number+' · '+D.reason+' · '+safeText(D.note,250),'Resident','vehicle?id='+v.id,v.flat);notify('Vehicle request',v.number+' · '+D.reason,'staff','vehicle?id='+v.id);audit('Vehicle owner notified',v.id,from,D.reason);save();go('vehicle?id='+v.id);toast('Owner notified in demo inbox');}
```

### action:castVote

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:899)

```javascript
d=>{requireOnline();if(S.role!=='Resident')fail('Vote using a resident flat account.');const p=S.polls.find(p=>p.id===d.id);if(!p||p.status==='Closed'||p.end<refDate())fail('Voting is closed.');const i=finiteInt(d.index,0,p.options.length-1,'Choice');p.votes[S.residentFlat]=i;audit('Poll vote recorded',p.id,'Vote','Flat '+S.residentFlat);save();render();toast('Vote saved');}
```

### action:closePoll

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:900)

```javascript
d=>{requireAdmin();confirm('Close this poll?','Voting will stop. Results remain available.','Close poll','confirmClosePoll',`data-id="${d.id}"`);}
```

### action:confirmClosePoll

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:901)

```javascript
d=>{requireAdmin();requireOnline();const p=S.polls.find(p=>p.id===d.id);if(!p)fail('Poll unavailable.');p.status='Closed';audit('Poll closed',p.id,'Open','Closed');save();render();closeSheet();}
```

### action:exportHandover

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:902)

```javascript
()=>{requireAdmin();doDownload('ApartmentCare-handover.txt',['ApartmentCare · Handover reference',S.apartment.name,stamp(),...Object.entries(S.handoverTasks).map(([k,v])=>k+': '+(v?'Reviewed':'Pending')),'Pending payment declarations: '+S.payments.filter(p=>p.status==='pending').length,'Pending event requests: '+S.events.filter(e=>e.status==='Pending'||e.pendingChange).length,'This is a local demo record, not a completed access transfer.'].join('\n'));}
```

### form:preview-clock

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:946)

```javascript
D=>{requireStaff();if(!D.now||isNaN(new Date(D.now)))fail('Choose a valid date and time.');S.referenceNow=D.now;save();toast('Demo clock updated');render();}
```

### action:scenarioEvent

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:962)

```javascript
()=>{setRole('Resident');go('events');}
```

### action:scenarioReferral

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:962)

```javascript
()=>{setRole('Admin');go('referrals');}
```

### form:subscription-refund-simulation

[current handler definition](/D:/ApartmentPilotAI/reference/ApartmentCare_Final_Clickable_UI_v2_1.html:977)

```javascript
D=>{requireAdmin();if(page!=='subscription-refund-simulation'||!D.verified)fail('Use the provider simulation and confirm the refund.');const i=S.subscription.invoices.find(i=>i.id===D.invoice);if(!i||i.status!=='Refund due')fail('No refund due for this invoice.');i.refundReference=requiredText(D.reference,'Refund reference',40);i.status='Refunded';audit('Provider refund simulated',i.id,'Refund due','Refunded');save();go('subscription-invoice?id='+i.id);}
```
