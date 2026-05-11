package com.facetrack.attendance.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_table")
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String = "",
    val name: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Present",
    val isSynced: Boolean = false
)