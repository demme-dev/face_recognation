package com.facetrack.attendance.data.local

import androidx.room.*
import com.facetrack.attendance.model.Attendance
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: Attendance)

    @Query("SELECT * FROM attendance_table ORDER BY timestamp DESC")
    fun getAllAttendance(): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance_table WHERE isSynced = 0")
    suspend fun getUnsyncedAttendance(): List<Attendance>

    @Update
    suspend fun update(attendance: Attendance)
}