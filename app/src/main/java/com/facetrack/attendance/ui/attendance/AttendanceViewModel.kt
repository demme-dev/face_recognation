package com.facetrack.attendance.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facetrack.attendance.data.repository.AttendanceRepository
import com.facetrack.attendance.model.Attendance
import com.facetrack.attendance.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _attendanceMarked = MutableStateFlow(false)
    val attendanceMarked: StateFlow<Boolean> = _attendanceMarked

    fun loadUser(uid: String) {
        viewModelScope.launch {
            _currentUser.value = repository.getUserProfile(uid)
        }
    }

    fun registerFace(uid: String, embeddings: List<Float>) {
        viewModelScope.launch {
            val user = repository.getUserProfile(uid) ?: return@launch
            val updatedUser = user.copy(faceEmbeddings = embeddings)
            repository.saveUserProfile(updatedUser)
            _currentUser.value = updatedUser
        }
    }

    fun markAttendance(uid: String, name: String) {
        if (_attendanceMarked.value) return
        _attendanceMarked.value = true // Set this immediately to stop other frames
        
        viewModelScope.launch {
            val attendance = Attendance(userId = uid, name = name, timestamp = System.currentTimeMillis())
            repository.markAttendance(attendance)
        }
    }
}