package com.example.thuchanh2.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.thuchanh2.databinding.ActivityAdminDashboardBinding
import com.example.thuchanh2.ui.auth.LoginActivity
import com.example.thuchanh2.utils.SessionManager

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Quản Trị Viên (Admin)"

        sessionManager = SessionManager(this)
        val username = sessionManager.getUsername()
        binding.tvWelcomeAdmin.text = "Xin chào, Admin: ${username ?: "Quản trị viên"}"

        binding.cardManageEmployees.setOnClickListener {
            startActivity(Intent(this, EmployeeManageActivity::class.java))
        }

        binding.cardManageSalaries.setOnClickListener {
            startActivity(Intent(this, AdminSalaryActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
