# Screen inventory — reference 2.1

203 routes. This includes app pages, subpages, system states and reference-only tooling. Bottom sheets use the common dialog component and shared footer.

A = Admin; T = Treasurer; R = Resident. Permissions shown here belong to the prototype; the backend must independently enforce production permissions.

## Overview

| # | Route | Screen title | Roles | Entry / auth screen |
|---:|---|---|---|---|
| 1 | `home` | Home | A/T/R | No |
| 165 | `approval-inbox` | Approvals | A/T | No |

## Finance

| # | Route | Screen title | Roles | Entry / auth screen |
|---:|---|---|---|---|
| 2 | `money` | Money | A/T | No |
| 3 | `dues` | Maintenance | A/T/R | No |
| 4 | `bill` | Maintenance bill | A/T/R | No |
| 5 | `payee` | Payment details | A/T/R | No |
| 6 | `mark-paid` | Payment details | A/T/R | No |
| 7 | `payment-status` | Payment status | A/T/R | No |
| 8 | `approvals` | Payment approvals | A/T | No |
| 9 | `payment` | Review payment | A/T | No |
| 10 | `proof` | Payment proof | A/T/R | No |
| 11 | `bulk-approval` | Select payments | A/T | No |
| 12 | `bulk-review` | Confirm approval | A/T | No |
| 13 | `payment-reject` | Reject payment | A/T | No |
| 14 | `receipt` | Payment receipt | A/T/R | No |
| 15 | `history` | Payment history | A/T/R | No |
| 16 | `outstanding` | Unpaid flats | A/T | No |
| 17 | `reminder-preview` | Send reminders | A/T | No |
| 18 | `billing-settings` | Maintenance amount | A/T | No |
| 19 | `bill-preview` | Generate monthly dues | A/T | No |
| 20 | `expenses` | Expenses | A/T/R | No |
| 21 | `expense` | Expense detail | A/T/R | No |
| 22 | `expense-proof` | Expense attachment | A/T/R | No |
| 23 | `expense-form` | Expense | A/T | No |
| 24 | `recurring` | Recurring expenses | A/T | No |
| 25 | `recurring-detail` | Recurring expense | A/T | No |
| 26 | `recurring-form` | Recurring setup | A/T | No |
| 32 | `income` | Other income | A/T | No |
| 33 | `income-form` | Income entry | A/T | No |
| 169 | `rate-history` | Maintenance rate history | A/T | No |
| 170 | `rate-overrides` | Flat-specific rates | A/T | No |
| 171 | `rate-override-form` | Set flat rate | A/T | No |
| 172 | `contributions` | One-time collections | A/T | No |
| 173 | `contribution-form` | One-time contribution | A/T | No |
| 174 | `contribution-preview` | Review contribution | A/T | No |
| 175 | `contribution` | Contribution details | A/T | No |
| 176 | `opening-dues` | Opening dues by flat | A/T | No |
| 177 | `opening-due-form` | Add opening due | A/T | No |

## Reports

| # | Route | Screen title | Roles | Entry / auth screen |
|---:|---|---|---|---|
| 27 | `reports` | Monthly summary | A/T/R | No |
| 28 | `expense-report` | Expense breakdown | A/T/R | No |
| 29 | `collection-report` | Collection report | A/T | No |
| 30 | `transparency` | Resident finance view | A/T | No |
| 31 | `export-report` | Export report | A/T/R | No |
| 34 | `cashbook` | Cashbook | A/T | No |

## Settings

| # | Route | Screen title | Roles | Entry / auth screen |
|---:|---|---|---|---|
| 35 | `finance-settings` | Financial controls | A/T | No |
| 36 | `payment-settings` | Association account | A/T | No |
| 37 | `opening-balance` | Opening balance | A | No |
| 104 | `permissions` | Permissions | A/T/R | No |
| 105 | `more` | More | A/T/R | No |
| 106 | `profile` | My profile | A/T/R | No |
| 107 | `profile-edit` | Edit profile | A/T/R | No |
| 108 | `notifications` | Notifications | A/T/R | No |
| 109 | `notification` | Notification | A/T/R | No |
| 110 | `notification-settings` | Notifications | A/T/R | No |
| 111 | `reminder-settings` | Reminder schedule | A/T | No |
| 112 | `appearance` | Appearance | A/T/R | No |
| 113 | `security` | Account security | A/T/R | No |
| 114 | `change-pin` | Change PIN | A/T/R | No |
| 115 | `privacy` | Privacy & access | A/T/R | No |
| 116 | `automation` | Automation | A/T | No |
| 117 | `automation-log` | Automation activity | A/T | No |
| 118 | `audit` | Activity log | A/T | No |
| 119 | `audit-detail` | Activity detail | A/T | No |
| 120 | `subscription` | Subscription & billing | A | No |
| 121 | `help` | Help & about | A/T/R | No |
| 122 | `payment-help` | Payment status guide | A/T/R | No |
| 123 | `support` | Get help | A/T/R | No |
| 124 | `about` | About | A/T/R | No |
| 178 | `subscription-plans` | Application charges | A | No |
| 179 | `subscription-invoices` | Subscription invoices | A | No |
| 180 | `subscription-invoice` | Application invoice | A | No |
| 181 | `subscription-renewal` | Renewal status | A | No |
| 182 | `referrals` | Refer & unlock | A | No |
| 183 | `referral-offer` | First-month offer | A | No |
| 184 | `referral-progress` | Referral progress | A | No |
| 185 | `referral-add` | New apartment referral | A | No |
| 186 | `referral-detail` | Referral details | A | No |
| 187 | `referral-reward` | First month reward | A | No |
| 188 | `referral-rules` | Referral rules | A | No |
| 189 | `subscription-overdue` | Subscription overdue | A | No |

## Services

| # | Route | Screen title | Roles | Entry / auth screen |
|---:|---|---|---|---|
| 38 | `issues` | Building issues | A/T/R | No |
| 39 | `issue-categories` | What needs attention? | A/T/R | No |
| 40 | `issue-form` | Report an issue | A/T/R | No |
| 41 | `similar-issues` | Similar issues | A/T/R | No |
| 42 | `issue` | Issue detail | A/T/R | No |
| 43 | `affected` | Affected flats | A/T | No |
| 44 | `issue-assign` | Assign service | A/T | No |
| 45 | `issue-update` | Update issue | A/T | No |
| 46 | `issue-comments` | Issue updates | A/T/R | No |
| 47 | `issue-photo` | Issue photo | A/T/R | No |
| 48 | `complaints` | My requests | A/T/R | No |
| 49 | `complaint-form` | Private request | A/T/R | No |
| 50 | `complaint` | Request detail | A/T/R | No |
| 51 | `complaint-update` | Manage request | A/T | No |
| 52 | `services` | Service schedule | A/T/R | No |
| 53 | `service` | Scheduled service | A/T/R | No |
| 54 | `service-form` | Schedule service | A/T | No |
| 55 | `service-complete` | Complete service | A/T | No |
| 56 | `service-history` | Service history | A/T/R | No |
| 133 | `complaint-photo` | Request attachment | A/T/R | No |

## Community

| # | Route | Screen title | Roles | Entry / auth screen |
|---:|---|---|---|---|
| 57 | `community` | Community | A/T/R | No |
| 58 | `notices` | Notice board | A/T/R | No |
| 59 | `notice` | Notice | A/T/R | No |
| 60 | `notice-form` | Send notice | A/T | No |
| 61 | `notice-attachment` | Notice attachment | A/T/R | No |
| 62 | `vendors` | Service vendors | A/T/R | No |
| 63 | `vendor` | Vendor profile | A/T/R | No |
| 64 | `vendor-form` | Vendor details | A/T | No |
| 65 | `contacts` | Important contacts | A/T/R | No |
| 66 | `contact` | Contact details | A/T/R | No |
| 67 | `contact-form` | Edit contact | A/T | No |
| 68 | `documents` | Apartment files | A/T/R | No |
| 69 | `document` | Document | A/T/R | No |
| 70 | `document-form` | Upload document | A/T | No |
| 135 | `events` | Events & bookings | A/T/R | No |
| 136 | `event-types` | What are you planning? | A/T/R | No |
| 137 | `event-form` | Request an event | A/T/R | No |
| 138 | `event-availability` | Choose your parking | A/T/R | No |
| 139 | `event-review` | Review request | A/T/R | No |
| 140 | `event-submitted` | Request sent | A/T/R | No |
| 141 | `event` | Event details | A/T/R | No |
| 142 | `booking-requests` | Booking approvals | A | No |
| 143 | `booking-review` | Review booking | A | No |
| 144 | `booking-alternative` | Offer an alternative | A | No |
| 145 | `event-alternative` | Alternative offered | A/T/R | No |
| 146 | `booking-reject` | Reject request | A | No |
| 147 | `event-cancel` | Cancel booking | A/T/R | No |
| 148 | `event-complete` | Complete event | A | No |
| 149 | `event-calendar` | Booking calendar | A/T/R | No |
| 150 | `parking-availability` | Guest parking | A/T/R | No |
| 151 | `resources` | Spaces & parking | A | No |
| 152 | `resource` | Space details | A/T/R | No |
| 153 | `resource-form` | Configure space | A | No |
| 154 | `resource-blocks` | Unavailable dates | A | No |
| 155 | `resource-block-form` | Block a space | A | No |
| 156 | `parking-share` | Share my parking | R | No |
| 157 | `booking-rules` | Booking rules | A/T/R | No |
| 158 | `booking-rules-edit` | Booking settings | A | No |
| 159 | `members` | Members | A/T/R | No |
| 160 | `member` | Member details | A/T/R | No |
| 162 | `committee` | Apartment committee | A/T/R | No |
| 163 | `committee-member` | Committee member | A/T/R | No |
| 166 | `notice-templates` | Choose a notice | A/T | No |
| 167 | `notice-preview` | Preview notice | A/T | No |
| 168 | `notice-delivery` | Audience & responses | A/T | No |
| 193 | `polls` | Community polls | A/T/R | No |
| 194 | `poll-form` | New poll | A | No |
| 195 | `poll` | Vote & results | A/T/R | No |
| 196 | `vehicles` | Vehicles | A/T/R | No |
| 197 | `vehicle` | Vehicle details | A/T/R | No |
| 198 | `vehicle-form` | My vehicle | R | No |
| 199 | `vehicle-contact` | Notify vehicle owner | A/T/R | No |

## Onboarding

| # | Route | Screen title | Roles | Entry / auth screen |
|---:|---|---|---|---|
| 71 | `welcome` | ApartmentCare | A/T/R | Yes |
| 72 | `login` | Choose login | A/T/R | Yes |
| 73 | `admin-login` | Admin login | A/T/R | Yes |
| 74 | `treasurer-login` | Treasurer login | A/T/R | Yes |
| 75 | `resident-login` | Resident login | A/T/R | Yes |
| 76 | `forgot-pin` | Reset PIN | A/T/R | Yes |
| 77 | `verify-reset` | Verify number | A/T/R | Yes |
| 78 | `reset-pin` | Set new PIN | A/T/R | Yes |
| 79 | `join` | Join your apartment | A/T/R | Yes |
| 80 | `invite-invalid` | Invite unavailable | A/T/R | Yes |
| 81 | `signup` | Create your account | A/T/R | Yes |
| 82 | `choose-flat` | Choose your flat | A/T/R | Yes |
| 83 | `signup-pending` | Join request | A/T/R | Yes |
| 84 | `admin-signup` | Create apartment | A/T/R | Yes |
| 85 | `setup-apartment` | Apartment details | A/T/R | Yes |
| 86 | `setup-done` | Apartment ready | A/T/R | Yes |

## Administration

| # | Route | Screen title | Roles | Entry / auth screen |
|---:|---|---|---|---|
| 87 | `apartment` | Apartment | A | No |
| 88 | `apartment-edit` | Apartment profile | A | No |
| 89 | `blocks` | Blocks | A | No |
| 90 | `block-form` | Block details | A | No |
| 91 | `flats` | Flats | A | No |
| 92 | `flat` | Flat details | A | No |
| 93 | `flat-form` | Flat setup | A | No |
| 94 | `residents` | Members | A | No |
| 95 | `resident` | Resident profile | A | No |
| 96 | `join-requests` | Join requests | A | No |
| 97 | `join-review` | Review resident | A | No |
| 98 | `join-reject` | Reject join request | A | No |
| 99 | `invite` | Invite residents | A | No |
| 100 | `invite-settings` | Invite controls | A | No |
| 101 | `handover` | Resident handover | A | No |
| 102 | `roles` | Team access | A | No |
| 103 | `role-form` | Team role | A | No |
| 161 | `member-form` | Add / edit member | A | No |
| 164 | `committee-form` | Committee role | A | No |
| 200 | `committee-handover` | Committee handover | A | No |

## System states

| # | Route | Screen title | Roles | Entry / auth screen |
|---:|---|---|---|---|
| 125 | `offline` | You’re offline | A/T/R | No |
| 126 | `session-expired` | Session expired | A/T/R | Yes |
| 127 | `access-denied` | Access restricted | A/T/R | No |
| 128 | `empty-state` | Nothing here yet | A/T/R | No |
| 129 | `error-state` | Something went wrong | A/T/R | No |
| 130 | `not-found` | Page not found | A/T/R | No |
| 134 | `loading-state` | Loading | A/T/R | No |

## Reference tools

| # | Route | Screen title | Roles | Entry / auth screen |
|---:|---|---|---|---|
| 131 | `screen-map` | Screen library | A/T/R | No |
| 132 | `demo-controls` | Preview controls | A/T/R | No |
| 190 | `provider-simulation` | Provider simulation | A | No |
| 191 | `activation-simulation` | Verify activation · demo | A | No |
| 192 | `subscription-payment-simulation` | Subscription payment · demo | A | No |
| 201 | `preview-clock` | Demo clock | A/T | No |
| 202 | `release-coverage` | Included in this reference | A/T/R | No |
| 203 | `subscription-refund-simulation` | Refund credit · demo | A | No |
