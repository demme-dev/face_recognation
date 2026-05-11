package com.facetrack.attendance.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val isAdmin: Boolean = false,
    val faceEmbeddings: List<Float>? = null
)