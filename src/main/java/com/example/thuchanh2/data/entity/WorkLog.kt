package com.example.thuchanh2.data.entity

data class WorkLog(
    val id: Int = 0,
    val employeeId: Int,
    val workDate: String,
    val checkInTime: String,
    val checkOutTime: String?,
    val totalHours: Double,
    val status: String
)
