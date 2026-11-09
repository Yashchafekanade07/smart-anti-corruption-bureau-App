# 🛡️ Smart Anti-Corruption Bureau App

**A Digital Platform for Transparent Complaint Reporting and Case Management**

Smart Anti-Corruption Bureau (Smart ACB) is an Android-based application designed to simplify corruption complaint reporting, improve transparency, and support complaint tracking through dedicated Citizen, Officer, and Admin portals.

The application provides a structured workflow for submitting complaints, managing cases, tracking complaint status, and maintaining complaint history.

## 🚀 Project Overview

The main objective of Smart ACB is to provide a digital platform where citizens can submit complaints and authorized staff can manage and monitor complaint progress.

The application focuses on transparency, accountability, and simplified complaint management.

## ✨ Key Features

### 👤 1. Citizen Portal
- Citizen registration and login.
- Submit corruption complaints.
- Select complaint categories.
- Enter department and district details.
- Optional anonymous reporting request.
- Optional location capture.
- Optional camera evidence attachment.
- Track complaint status and history.

### 👮 2. Officer Portal
- Dedicated officer login.
- View and manage complaints.
- Update complaint status.
- Add status notes.
- View complaint history.
- Monitor case progress.

### 🛡️ 3. Admin Portal
- Separate admin login.
- Staff-oriented complaint management.
- Dashboard with complaint statistics.
- Monitor submitted, in-progress, and resolved cases.

### 📍 4. Location & Evidence Features
- Optional current-location capture.
- Store latitude and longitude with complaints when available.
- Camera-based evidence attachment.
- Display evidence information and complaint history.

### 📊 5. Complaint Tracking
- Unique local complaint reference IDs.
- Complaint status tracking.
- Timestamp-based status history.
- Audit trail of complaint status transitions.

## 🔄 Complaint Status Workflow

**Submitted → Under Review → In Progress → Resolved / Rejected**

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java | Android application development |
| XML | User interface design |
| Android Studio | Development environment |
| SQLite | Local complaint database |
| Firebase Authentication | User authentication |
| Android SDK | Mobile application framework |
| Gradle | Build automation |
| Android Location APIs | Optional location capture |
| Camera Intent | Evidence image capture |

## 🏗️ Application Architecture

```text
Smart ACB Android Application
          |
    Authentication
          |
   Role-based Portals
          |
  ---------------------
  |         |         |
Citizen   Officer    Admin
 Portal    Portal    Portal
  |         |         |
  ---------------------
          |
  Complaint Management
          |
  ---------------------
  |         |         |
 SQLite   Evidence   Location
 Database  Images    Details
          |
   Complaint Tracking
          |
   Status History
```

## 📱 Application Modules

1. Authentication Module
2. Citizen Registration Module
3. Complaint Submission Module
4. Complaint Management Module
5. Officer Dashboard
6. Admin Dashboard
7. Location Capture Module
8. Evidence Management Module
9. Complaint Tracking Module
10. Status History Module

## ⚙️ Installation & Setup

### Prerequisites
- Android Studio
- Java Development Kit
- Android SDK
- Firebase project configured for Authentication

### Steps

1. Clone the repository:

```bash
git clone https://github.com/Yashchafekanade07/smart-anti-corruption-bureau-App.git
```

2. Open the project in Android Studio.

3. Open the inner `SmartAntiCurruptionBeuroPlatform` project folder containing `settings.gradle`, `build.gradle`, and `app/`.

4. Configure your Android SDK path in `local.properties`.

5. Configure Firebase Authentication if required.

6. Sync the Gradle project.

7. Build the application:

```bash
./gradlew assembleDebug
```

For Windows:

```powershell
.\gradlew.bat assembleDebug
```

8. Install and run the application on an Android device or emulator.

## 📸 Screenshots

Add screenshots of the following application screens:

- Login and Registration Screen
- Citizen Dashboard
- Complaint Submission Screen
- Complaint Tracking Screen
- Officer Dashboard
- Admin Dashboard

## 🔐 Security & Project Limitations

This project is an academic prototype.

- Complaint records are stored locally using SQLite.
- Local complaint records do not automatically synchronize between devices.
- Role-based access checks in the current prototype are client-side and are not production-grade authorization.
- Evidence and location information are stored locally and are not uploaded to staff.
- A production implementation requires a secure backend, server-side authorization, protected evidence storage, and appropriate privacy controls.

**This application is not an official government reporting service.**

## 🎯 Project Objectives

- Encourage digital complaint reporting.
- Improve transparency in complaint handling.
- Simplify complaint tracking.
- Support structured case management.
- Explore Android development and mobile database integration.

## 🔮 Future Scope

- Cloud-based complaint synchronization.
- Secure backend integration.
- Real-time complaint status notifications.
- Advanced evidence management.
- Server-side role-based authorization.
- Secure location and document storage.
- Analytics and reporting dashboard.

## 👨‍💻 Developer

**Yash Chafekanade**

B.Tech – Artificial Intelligence & Data Science

Maharashtra Institute of Technology, Chhatrapati Sambhajinagar

GitHub: [@Yashchafekanade07](https://github.com/Yashchafekanade07)

## 📌 Project Repository

[Smart Anti-Corruption Bureau App – GitHub](https://github.com/Yashchafekanade07/smart-anti-corruption-bureau-App)

---

⭐ If you find this project interesting, consider giving it a star!

**Building technology for transparency, accountability, and a better tomorrow.**
