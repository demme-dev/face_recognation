# 📸 FaceTrack Attendance App

## Overview
FaceTrack Attendance is a Kotlin-based Android app built with MVVM architecture, CameraX, ML Kit, Firebase, and Room Database for a production-ready face recognition attendance system.

## Features
- 🔐 Firebase Authentication for secure login and signup  
- 🙂 Face registration (front, left, and right views)  
- 📷 Real-time recognition using CameraX and ML Kit  
- 📡 Offline support with Room database and Firestore sync  
- 👨‍💼 Admin view to monitor attendance records  

## Firebase Setup Instructions
1. Go to the Firebase Console.  
2. Create a project named **FaceTrack Attendance**.  
3. Add an Android app with package name `com.facetrack.attendance`.  
4. Download `google-services.json` and place it in the `app/` folder.  
5. Enable **Email/Password Authentication**.  
6. Enable **Cloud Firestore**.  
7. Use the following Firestore rules:
   ```js
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if request.auth != null;
       }
     }
   }
Running the Project
Open the project in Android Studio.
Ensure google-services.json is inside the app/ directory.
Sync Gradle and run the app on a physical device.
Grant camera permission.
Sign up and register your face in the Attendance tab.
The app will automatically recognize and mark attendance.
Tech Stack
💜 Kotlin
🧩 MVVM + Repository Pattern
🎨 Material Design 3 (XML UI)
📷 CameraX
🧠 Google ML Kit
🔐 Firebase Auth
☁️ Firestore & 💾 Room Database
