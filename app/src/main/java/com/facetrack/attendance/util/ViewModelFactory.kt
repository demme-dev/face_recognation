package com.facetrack.attendance.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.facetrack.attendance.data.repository.AttendanceRepository
import com.facetrack.attendance.ui.attendance.AttendanceViewModel

class ViewModelFactory(private val repository: AttendanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}