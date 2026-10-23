# Smart ACB – Citizen, Officer and Admin portals

## What's added
- Login screen has three portal choices: Citizen, Officer, Admin.
- Citizen self-registration remains available only in the Citizen portal.
- Officer/Admin login checks an explicit email allowlist in `LoginActivity.java` before Firebase sign-in.
- The home screen shows citizen complaint actions to signed-in citizens and case management to approved staff roles.
- Staff can open the existing local dashboard and update complaint status.

## Configure staff accounts for the demo
1. In Firebase Console → Authentication → Sign-in method, enable Email/Password.
2. Create Firebase Authentication users for your approved staff emails.
3. In `LoginActivity.java`, update `OFFICER_EMAILS` and `ADMIN_EMAILS` with the exact approved email addresses. The demo placeholders are `officer@smartacb.demo` and `admin@smartacb.demo`; they are not automatically created accounts.
4. Build and install the app again.

## Important limitations
This is an academic/local prototype. Complaints are stored in SQLite on one device. They do not sync between a citizen's phone and an officer's phone. The email allowlist and role stored in SharedPreferences are client-side UX gates, not secure authorization. Do not use this version for real or sensitive complaints. A production version needs a trusted backend (for example Firebase Cloud Firestore with server-enforced custom claims/security rules), per-user complaint ownership, audit logs, secure evidence storage, and server-side role checks. Never place private credentials or service-account keys in the APK.
