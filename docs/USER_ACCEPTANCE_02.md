# User acceptance — local synthetic data only

Mark each scenario PASS only after observing the correct Android result AND stored backend state. A disabled button or success toast alone is not evidence.

| Scenario | Expected result |
|---|---|
| Existing Admin login after update | Same tenant/records; correct role; footer and theme retained |
| Change PIN | Current PIN required, six-digit new PIN, all old tokens revoked; re-login required |
| Register/approve resident | Resident cannot access data before approval; no extra account per flat |
| Manual member + committee title | Directory entry/title never grants app login/permissions |
| Other apartment isolation | Guessed ticket/bill/document/booking IDs denied |
| Monthly rate change | Existing bills unchanged, future effective month uses new rate |
| Flat override | Only selected flat/future eligible bills change |
| Contribution and opening due | Separate bill kinds; monthly maintenance not overwritten |
| Manual UPI partial payment | Pending until verified, mandatory proof enforced at approval when configured, receipt created only once |
| Reverse approved payment | Original receipt retained as reversed; outstanding restored; refund/adjustment recorded in cashbook |
| Paid/draft/recurring expense | Draft excluded; paid amount/mode accurate; recurring catch-up does not duplicate its cycle after date editing |
| Expense reversal | One reversing movement; original record remains auditable |
| Private complaint | Only its flat and staff can view; cannot broadcast private complaint |
| Building issue | Category, follow count, vendor, ETA, transitions/history and optional whole-apartment notification work |
| Service completion | Same scheduled cycle cannot post twice; expense optional and draft/paid correct; next date advances |
| Targeted notice | Only staff and selected recipients can open; read differs from acknowledgement; repeated publish no duplicate inbox |
| Scheduled notice | Appears only when due; attachment added before publication |
| File visibility | Private file denied even with ID; unsupported/oversized bytes rejected; duplicate upload not duplicated |
| Event request | Resident chooses event/time/resources, sees pending rather than booked |
| Booking conflict | Two simultaneous approvals for one bay permit only one |
| Alternative | Capacity not silently reduced; organiser explicitly accepts; recheck on acceptance |
| Owner's private space | Admin cannot substitute owner consent; release window and approved reservations protected |
| Event uses parking area | Linked conflicts prevent simultaneous guest parking use |
| Cancellation/blackout | Approved resource releases on cancellation; blackout cannot invalidate an existing booking |
| Poll | One vote per flat; changed option does not create a second vote; closed poll rejects votes |
| Vehicle contact | Other residents do not receive phone numbers; message only to the appropriate flat inbox |
| Subscriptions | ₹99/149/249 is application fee; no maintenance funds appear in software invoices |
| Referrals | Self/circular/multiple-referrer abuse denied; installation button alone not qualifying; only independent operator can verify |
| Reward | All three eligible first invoices waived once; future months unchanged; previously paid first invoice creates refund review only |
| Encrypted session | Process restart retains valid encrypted session; logout/revocation invalidates it; no PIN in preferences |
| Light/dark/native layout | Small phone, dialogs, form keyboard, attachments and footer checked against reference |

Live OTP, FCM, store payment and provider-install checks are **NOT CONFIGURED**, not PASS. Native screenshot parity and the complete 203-route acceptance inventory remain open.
