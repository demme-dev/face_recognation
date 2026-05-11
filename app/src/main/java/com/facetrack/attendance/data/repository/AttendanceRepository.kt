package com.facetrack.attendance.data.repository

import com.facetrack.attendance.data.local.AttendanceDao
import com.facetrack.attendance.model.Attendance
import com.facetrack.attendance.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class AttendanceRepository(
    private val attendanceDao: AttendanceDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    val allAttendanceLocal: Flow<List<Attendance>> = attendanceDao.getAllAttendance()

    suspend fun markAttendance(attendance: Attendance) {
        attendanceDao.insert(attendance)
        try {
            firestore.collection("attendance").add(attendance).await()
            attendanceDao.update(attendance.copy(isSynced = true))
        } catch (e: Exception) {
            // Stay unsynced in local DB
        }
    }

    suspend fun syncOfflineAttendance() {
        val unsynced = attendanceDao.getUnsyncedAttendance()
        for (attendance in unsynced) {
            try {
                firestore.collection("attendance").add(attendance).await()
                attendanceDao.update(attendance.copy(isSynced = true))
            } catch (e: Exception) {
                break // Stop if network still down
            }
        }
    }

    suspend fun getUserProfile(uid: String): User? {
        return try {
            firestore.collection("users").document(uid).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserProfile(user: User) {
        firestore.collection("users").document(user.uid).set(user).await()
    }
    
    suspend fun getAllUsers(): List<User> {
        return try {
            firestore.collection("users").get().await().toObjects(User::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllAttendanceFromFirestore(): List<Attendance> {
        return try {
            firestore.collection("attendance").get().await().toObjects(Attendance::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}