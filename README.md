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
### Running the Project

→ Open the project in Android Studio.

→ Ensure google-services.json is inside the app/ directory.

→ Sync Gradle and run the app on a physical device.

→ Grant camera permission.

→ Sign up and register your face in the Attendance tab.

→ The app will automatically recognize and mark attendance.

### Tech Stack

💜 Kotlin

🧩 MVVM + Repository Pattern

🎨 Material Design 3 (XML UI)

📷 CameraX

🧠 Google ML Kit

🔐 Firebase Auth

☁️ Firestore & 💾 Room Database


## Contributors
<!-- Contributors list -->
<table>
  <tr>
    <td align="center">
      <a href="https://github.com/sight09">
        <img src="https://avatars.githubusercontent.com/u/203522914?v=4" width="100px;" alt="User1"/>
      </a>
      <br />
      <a href="https://github.com/sight09">sight09</a>
      <br />
      <a href="https://x.com/Sightzeronine">Twitter</a> | <a href="https://www.linkedin.com/in/amanuel-alemu-zewdu/">LinkedIn</a>
    </td>
    <td align="center">
      <a href="https://github.com/demme-dev">
        <img src="https://avatars.githubusercontent.com/u/258595748?v=4" width="100px;" alt="User2"/>
      </a>
      <br />
      <a href="https://github.com/demme-dev">demme-dev</a>
      <br />
      <a href="#">Twitter</a> | <a href="#">LinkedIn</a>
    </td>
    <!-- Repeat for more contributors -->
  </tr>
</table>

