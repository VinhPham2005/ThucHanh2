package com.example.thuchanh2.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

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

    fun getAccountId(): Int = prefs.getInt("ACCOUNT_ID", -1)
    fun getEmployeeId(): Int = prefs.getInt("EMPLOYEE_ID", -1)
    fun getUsername(): String? = prefs.getString("USERNAME", "")
    fun getRole(): String? = prefs.getString("ROLE", null)
    fun isLoggedIn(): Boolean = prefs.getBoolean("IS_LOGGED_IN", false)

    fun logout() {
        prefs.edit().clear().apply()
    }
}
