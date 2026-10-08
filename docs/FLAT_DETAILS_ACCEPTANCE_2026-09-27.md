# Flat search and details — 27 September 2026

Status: Lead bounded functional checkpoint passed on phone and tablet emulators. Independent QA remains on hold. Visual score and wider failure/accessibility acceptance remain open.

## Scope and changes

- `FeatureScreens.java`: added native flat/resident search, block filtering, a three-column grid with compact two-line labels/statuses, and a separate server-backed flat details page. Details expose edit, flat-scoped payment history, current dues and active-account handover actions where applicable. The search/block selection is retained as a page draft through nested navigation.
- `CommunityService.java`, `OperationsController.java`: added the Admin-only tenant-scoped detail endpoint. It derives the tenant and role from authentication and parent-checks the requested flat.
- `FlatDetailsTest.java`: checks authenticated tenant/id parent selection and denies Resident/Treasurer enumeration.
- `Test-Flat-Details.ps1`: 29 localhost HTTP/PostgreSQL checks for existence, malformed/foreign IDs, member status changes, payment filtering and role boundaries.
- `Test-Flats-Emulator.ps1`: 29 native checks for search by flat/resident, block selection, no-results state, detail values/actions, edit prefill and Android Back. Emulator serial validation prevents targeting a physical device.

Screenshot inspection found the original tile button wrapped status labels to three lines and clipped the last line. The tile text was reduced to a readable two-line layout and the corrected build was retested on both form factors.

## Evidence

- Backend unit/regression build: 56/56 passed, including flat detail and payment history tests.
- Android debug build/unit tests: BUILD SUCCESS; 26/26 tests passed.
- Real localhost HTTP/PostgreSQL Flat details/history suite: 29/29 passed with fixture `FLAT QA 11066703`.
- Native phone emulator (`emulator-5580`): 29/29 on APK SHA256 `B95799DB38A36BD10C61A7761B0A6AF000B5EC76785610E94635289D99F276AB`; fixture `FLATS UI 912f101c`.
- Native tablet emulator (`emulator-5582`): 29/29 on the same APK; fixture `FLATS UI a59238fb`.
- Registration, invite, account approval, flat status and payment isolation were backed by the server/database. The suite uses synthetic retained tenants; test credentials were not printed.
- The existing schema remained at V8; no new migration or database reset was needed.
- Frozen-reference visual comparison at >=95%, independent QA and broader HTTP failure/retry/accessibility coverage remain pending. The tile screenshot was inspected for text clipping, which is not a scored reference parity review.

## Test setup notes

The first API attempt could not create an invite because the local server process omitted the existing ignored invite-display key. Restarting the local-only backend with that key in process environment completed the suite; the key was not printed or saved. The initial native script used an overly broad Home assertion (matching the Home navigation icon while on Apartment) and was corrected to verify the page/account. Android's popup list does not mark the block rows clickable in UIAutomator, so the bounded test taps the visible option bounds and verifies the filtered result. These setup/test-locator failures were not counted as product passes.

The physical phone `5ee2fea` was not installed or targeted. ParkingPilotAI, its data, and the existing unrelated listener on port 8080 were not changed; only emulator mappings point 8080 to the ApartmentPilot backend at 8081.

## Completion

This is a bounded Lead functional checkpoint, not an independent-QA or release pass. Fully accepted overall inventory remains 0% while visual, provider, broader scenario and QA gates remain open.
