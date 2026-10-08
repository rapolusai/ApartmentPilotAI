# Invite controls — bounded visual comparison

Status: **Independent QA passed the bounded Invite-controls light-phone comparison at approximately 96.3/100 on current APK SHA256 `542E950ED741B7A25697D61F5223851733C3D0CCA3A6FB0779819E77ADF9EA38`.** Fresh code-free phone screenshots confirmed score transfer with full date legibility and Community selection from both entry paths; separate tablet responsive QA passed without a tablet visual-percentage claim. This is a light-theme comparison of the frozen v2.1 reference phone panel against one Pixel_10_Pro Android 37 emulator at 1080×2400/480dpi. It is not whole-app, physical-device, dark-theme-phone or accessibility acceptance.

Reference: `reference/ApartmentCare_Final_Clickable_UI_v2_1.html`, SHA256 `FF5554324E630F3B63235EC723E094BE6CDFF02DBF434898BED5D6F290F3AE28`, screenshot `test-results/reference-invite-settings-light.png`. Independent current screenshots: `test-results/qa-invite-542-phone-home-settings.png` and `test-results/qa-invite-542-phone-community-settings.png`; the score originated on `test-results/qa-invite-final-home-invite-controls.png` and unchanged pixel runs/geometry/color transferred on current APK `542E950E...`. Dynamic clock, date value and Android status/gesture glyphs were excluded; their layout remains visible in both images.

The reference phone's inner CSS width is 414px; the native display width is 1080 physical pixels at density 3. The width-normalization factor is `1080/414 = 2.60869565` physical pixels per reference CSS pixel, or `0.86956522` Android dp/sp per reference CSS pixel. For geometry sizes, error is `abs(native − scaled reference) / scaled reference`. For vertical positions, both screens are measured from the top of their content viewport (reference y=860, native y=303), and error is the difference divided by the common 1080px width; this prevents different status/gesture bars and aspect ratios from dominating form alignment. Measured colors use interior pixels, not antialiased edges. Values below are rounded, so the reported score is approximate.

| Geometry check | Reference normalized to native | Native observed | Error |
|---|---:|---:|---:|
| Appbar height | 56.5dp | 58dp | 2.62% |
| Form left inset | 52.2px | 55px | 5.42% |
| Date field height | 122.6px | 150px | 22.35% |
| Hint card height | 146.1px | 141px | 3.48% |
| Save fill height | 122.6px | 132px | 7.66% |
| New code fill height | 107.0px | 115px | 7.52% |
| Bottom navigation height | 164.3px | 149px | 9.34% |
| Attribution footer height | 73.0px | 66px | 9.64% |
| Selected nav-pill width | 140.9px | 140px | 0.62% |
| Date field top within viewport | 83.5px | 103px | 1.81% of width |
| Hint top within viewport | 250.4px | 297px | 4.32% of width |
| Save top within viewport | 474.8px | 460px | 1.37% of width |
| Action row top within viewport | 652.2px | 642px | 0.94% of width |

Corrected geometry mean error: **approximately 5.9299%**. Native positions and heights came from the emulator hierarchy and screenshot; reference blocks came from frozen CSS and screenshot interior-color runs. The larger date field is the largest residual size mismatch, but unlike the superseded 44dp field the date value is fully readable. Independent QA reproduced date legibility after fresh render and after the date picker was dismissed.

| Typography/icon check | Reference equivalent | Native source size | Error |
|---|---:|---:|---:|
| Appbar title | 15.65sp | 16sp | 2.22% |
| Field label | 9.57sp | 10sp | 4.55% |
| Field value | 11.30sp | 11.5sp | 1.73% |
| Hint text | 8.70sp | 9sp | 3.50% |
| Button label | 11.30sp | 11.5sp | 1.73% |
| Nav label | 7.83sp | 8sp | 2.22% |
| Footer text | 9.13sp | 9sp | 1.43% |
| Key icon | 13.91dp | 14dp | 0.63% |
| Action icon | 15.65dp | 16dp | 2.22% |
| Nav icon | 17.39dp | 18dp | 3.50% |

Typography/icon-size mean error: **2.3727%**. This measures nominal sizes, not font-glyph or vector-path pixel identity. The native and reference interior colors matched for page background `#f6f7f3`, white navigation/footer, pine Save `#174c40`, soft hint `#f0f3ed`, lime active nav `#dfefa7` and danger `#fdece6`. Both show Back/title/theme, Valid until/date, key hint, Save, New code/Revoke, five navigation entries and the exact attribution **Powered By @Rapolu`s** in that order. These six colors plus route structure/order form the categorical score, **8/8**.

Scoring rubric: 50% geometry, 30% typography/icon size, 20% categorical color/order, plus hard gates for full date-glyph readability and correct selected Community tab from both Home and Community entry paths. The corrected phone calculation is approximately `0.50×(100−5.9299) + 0.30×(100−2.3727) + 0.20×100 ≈ 96.3`. Rounded measurements do not justify an exact hundredth. The earlier 97.03 Lead score was invalidated by clipped glyphs and wrong selected tab. Native glyph/vector-path identity, tablet/physical-device and whole-app parity are outside this bounded rubric. Functional, security and database acceptance gates are separately recorded in `TEST_EVIDENCE.md`.

Physical-device transfer is separately bounded: current checksum-matched debug APK passed Lead and independent QA Invite-controls checks on a real Xiaomi Pad 7 Android 16 tablet in light/dark portrait/landscape, with fully legible date/actions and Community selection. The original light portrait tablet settings were restored. This is qualitative responsive and functional evidence, not a new measured 95% visual score for a tablet, a physical-phone result or whole-app reference parity. Code-free screenshots are listed in `TEST_EVIDENCE.md`.
