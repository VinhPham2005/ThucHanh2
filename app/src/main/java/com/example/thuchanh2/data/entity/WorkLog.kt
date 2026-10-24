package com.example.thuchanh2.data.entity

data class WorkLog(
    val id: Int = 0,
    val employeeId: Int,
    val workDate: String,      // Định dạng: "yyyy-MM-dd"
    val checkInTime: String,   // Định dạng: "HH:mm"
    val checkOutTime: String?, // Null khi mới check-in
    val totalHours: Double,    // Mặc định 0.0 khi mới check-in
    val status: String         // "INCOMPLETE" hoặc "COMPLETED"
)