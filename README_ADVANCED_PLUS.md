# Smart ACB — Advanced Plus (Android Java)

## Added in this revision
- Three portal roles: Citizen, Officer, Admin (existing role-aware login flow).
- Complaint categories, department and district fields, anonymous-reporting request, validation, and local reference IDs.
- Status workflow: Submitted → Under Review → In Progress → Resolved / Rejected.
- Local audit trail records each initial submission and status transition with timestamp, actor label and note.
- Staff dashboard summary counts for total, submitted, in-progress and resolved cases.
- Staff can tap a case to update its status or view the case history.
- Database schema migration to version 3 preserves existing complaint records and seeds an initial history event for older records.

## Build
1. Open the inner `SmartAntiCurruptionBeuroPlatform` folder (contains `settings.gradle`, `build.gradle`, and `app/`).
2. Set `local.properties` `sdk.dir` to your installed Android SDK path (do not commit this machine-specific file).
3. In Terminal run `./gradlew assembleDebug` (Windows: `.\gradlew.bat assembleDebug`).
4. APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Configure demo staff
In `LoginActivity.java`, update `OFFICER_EMAILS` and `ADMIN_EMAILS` with approved email addresses, then create those users in Firebase Authentication. Do not share passwords in code or chat.

## Critical limits
This is still a local academic prototype. SQLite records do not sync across devices. The role allowlist and role stored in SharedPreferences are client-side checks and are not production security. For real use, implement a trusted backend with server-enforced authorization/custom claims, Firestore rules, per-user complaint access, secure evidence upload, immutable server-side audit logs, rate limiting, privacy/retention controls, and an official case-handling policy. Do not present this app as an official government service without authorization.


## Evidence and location features added
- Citizen complaint form now has an optional camera evidence button. It uses the Android camera app and saves the returned preview image privately in the app's internal files directory.
- Citizen can request current location; app asks runtime location permission and stores the last known latitude/longitude with the complaint when available.
- Complaint tracking now displays the saved coordinates, evidence filename, and status-change history.
- Database schema version 4 adds nullable latitude, longitude and evidence path columns; existing complaints are preserved through migration.

## Important evidence/privacy limitations
- The standard Android camera intent returns a preview bitmap on many devices, not the full-resolution original. A production implementation should use CameraX and encrypted/private evidence storage.
- The location is the device's last known location and may be stale or unavailable; the UI labels it as optional and user-triggered.
- Evidence and location remain on this device and are not uploaded to staff. They will not sync to another phone until a secured backend and evidence storage are implemented.
- Only submit evidence you are authorized to share. Protect reporter identity and sensitive evidence; this academic prototype is not an official reporting channel.
