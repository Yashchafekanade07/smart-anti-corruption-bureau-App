# Smart Anti-Corruption Bureau Platform — updated project

## Changes included
- Complaint validation and a confirmation dialog that displays the reference ID.
- SQLite complaint status and submission timestamp, including a database migration for existing installs.
- Complaint tracking displays the current saved status and submitted date; invalid IDs are handled safely.
- Complaint list displays status/date and opens the tracking screen when tapped.
- Dedicated Firebase registration screen stores the display name in the Firebase Auth profile.
- Local admin dashboard for updating complaint statuses.
- Main dashboard links to the local admin demo.

## Open and build
1. Extract this ZIP to a normal folder.
2. Open the folder containing `settings.gradle` in Android Studio.
3. Use JDK 17 for Gradle.
4. Allow Gradle Sync to finish, then run on an emulator or Android phone.
5. To create a debug APK, choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. Android Studio normally saves the debug APK at `app/build/outputs/apk/debug/app-debug.apk`.

## Important prototype limitations
- Complaint data is stored in SQLite on the current device. It is not synchronized between a citizen phone and an admin phone.
- The admin dashboard is explicitly a local classroom demo and has no secure server-side admin role. Do not use it to handle real or sensitive complaints.
- The AI label in the existing design does not mean an AI model is integrated.
- For a real deployment, use a cloud database with security rules, verified admin authorization, audit logs, secure evidence upload, and a privacy/legal review.
- Firebase Authentication requires a correctly configured Firebase project and Email/Password sign-in enabled in the Firebase Console.

## Build verification
The source XML files were parsed successfully. An automated Gradle build could not be completed in this environment because the Gradle distribution download requires internet access and the network was unavailable. Please run Gradle Sync and Build APK in Android Studio to verify against your installed SDK.
