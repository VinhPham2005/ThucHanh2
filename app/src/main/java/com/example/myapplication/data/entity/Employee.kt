package com.example.myapplication.data.entity

data class Employee(
    val id: Int = 0,
    val code: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val hourlyRate: Double,
    val accountId: Int
)