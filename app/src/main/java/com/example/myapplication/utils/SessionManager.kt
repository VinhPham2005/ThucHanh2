package com.example.myapplication.utils

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

    fun saveLoginSession(accountId: Int, employeeId: Int, username: String, role: String) {
        prefs.edit().apply {
            putInt("ACCOUNT_ID", accountId)
            putInt("EMPLOYEE_ID", employeeId)
            putString("USERNAME", username)
            putString("ROLE", role)
            putBoolean("IS_LOGGED_IN", true)
            apply()
        }
    }

    fun getRole(): String? = prefs.getString("ROLE", null)
    fun getEmployeeId(): Int = prefs.getInt("EMPLOYEE_ID", -1)
    fun getUsername(): String? = prefs.getString("USERNAME", "")
    fun isLoggedIn(): Boolean = prefs.getBoolean("IS_LOGGED_IN", false)

    fun logout() {
        prefs.edit().clear().apply()
    }
}