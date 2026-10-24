package com.example.thuchanh2.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.thuchanh2.R
import com.example.thuchanh2.utils.SessionManager

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        sessionManager = SessionManager(this)
    }
}
