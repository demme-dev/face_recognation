# FaceTrack Attendance App

## Overview
FaceTrack Attendance is a Kotlin-based Android application using MVVM architecture, CameraX, ML Kit, Firebase, and Room Database for a production-ready face recognition attendance system.

## Features
- **Firebase Auth**: Secure login and signup.
- **Face Registration**: Captures front, left, and right face views.
- **Real-time Recognition**: Uses CameraX and ML Kit for face detection.
- **Offline Support**: Room database stores attendance locally and syncs to Firestore when online.
- **Admin View**: (Integrated in fragments) View all attendance records.

## Firebase Setup Instructions
1. Go to [Firebase Console](https://console.firebase.google.com/).
2. Create a new project named `FaceTrack Attendance`.
3. Add an Android App with package name `com.facetrack.attendance`.
4. Download the `google-services.json` file and place it in the `app/` directory.
5. Enable **Authentication** with Email/Password provider.
6. Enable **Cloud Firestore** in test mode or with appropriate rules.
7. Firestore Rules:
   ```
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if request.auth != null;
       }
     }
   }
   ```

## Running the Project
1. Open the project in the latest Android Studio.
2. Ensure you have the `google-services.json` file in the `app/` folder.
3. Sync Gradle.
4. Run the app on a physical device (CameraX works best on real hardware).
5. Grant Camera permission when prompted.
6. Sign up, then go to the Attendance tab to register your face.
7. Once registered, the app will automatically recognize you and mark attendance.

## Tech Stack
- **Language**: Kotlin
- **Architecture**: MVVM + Repository Pattern
- **UI**: XML / Material Design 3
- **Camera**: CameraX
- **Face Detection**: Google ML Kit
- **Auth**: Firebase Auth
- **Database**: Firestore (Cloud) & Room (Local)
