# ApartmentPilotAI development rules

Work within this repository, never the unrelated ParkingPilotAI project. Root on the user's computer is D:\ApartmentPilotAI.
- Preserve reference/ApartmentCare_Final_Clickable_UI_v2_1.html bytes and shared attribution "Powered By @Rapolu`s".
- Native Android Java/XML; no HTML/WebView substitution and no fake successful API calls.
- Backend Java21/SpringBoot/PostgreSQL. Derive tenant/role from server authentication, never from a client role selector or supplied tenantId.
- Never store DB passwords, tokens, provider secrets or release keys in source. Never expose a development database publicly.
- Use additive new migrations. Never edit an applied V1/V2, wipe schema history or reset data to hide errors.
- Preserve the user's functioning Gradle/SDK/JDK and IntelliJ local configuration when applying source updates.
- Approved payments/expenses require audited reversals, not deletion or silent rewriting. Serialise financial/booking decisions by tenant; protect retries and races.
- Parent-check every attachment/download; no private record access by guessed UUID.
- No automated bank verification of manual maintenance payments. No apartment Admin can self-verify software-subscription money or referee installation.
- Disabled/unconfigured OTP/FCM/checkout must be labelled unconfigured, not passed or silently mocked as production success.
- Distinguish code present, framework build passed, database tests passed, Android runtime passed and visual parity passed.
- Current source: increment02. Read status/evidence; complete acceptance gates before a release. No claims of unattended work or 100% completion from route counts.
