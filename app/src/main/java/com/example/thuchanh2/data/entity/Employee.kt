package com.example.thuchanh2.data.entity

data class Employee(
    val id: Int = 0,
    val code: String,
    val fullname: String,
    val email: String,
    val phone: String,
    val hourlyRate: Double,
    val accountId: Int
) {
    val fullName: String
        get() = fullname
}
