package com.example.thuchanh2.data.entity

data class Salary(
    val id: Int = 0,
    val employeeId: Int,
    val month: Int,
    val year: Int,
    val totalHours: Double,
    val overtimeHours: Double,
    val totalSalary: Double,
    val status: String         // "PENDING" hoặc "PAID"
)