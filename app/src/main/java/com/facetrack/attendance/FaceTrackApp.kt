package com.facetrack.attendance

import android.app.Application
import com.google.firebase.FirebaseApp

class FaceTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}