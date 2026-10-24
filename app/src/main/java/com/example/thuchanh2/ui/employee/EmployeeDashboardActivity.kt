package com.example.thuchanh2.ui.employee

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.thuchanh2.utils.SessionManager

class EmployeeDashboardActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
    }
}
