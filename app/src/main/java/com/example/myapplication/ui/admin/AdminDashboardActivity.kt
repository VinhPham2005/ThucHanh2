package com.example.myapplication.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityAdminDashboardBinding
import com.example.myapplication.ui.auth.LoginActivity
import com.example.myapplication.utils.SessionManager

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Bảng điều khiển Admin"

        session = SessionManager(this)

        // Kiểm tra phân quyền: Chỉ cho phép ADMIN truy cập
        if (!session.isLoggedIn() || session.getRole() != "ADMIN") {
            Toast.makeText(this, "Bạn không có quyền truy cập!", Toast.LENGTH_SHORT).show()
            logout()
            return
        }

        // Hiển thị thông tin đọc từ SessionManager của bạn
        binding.tvAdminWelcome.text = "Xin chào, Quản trị viên!"
        binding.tvAdminSessionInfo.text = "Tài khoản: ${session.getUsername()} | Vai trò: ${session.getRole()}"

        binding.btnManageEmployees.setOnClickListener {
            startActivity(Intent(this, EmployeeManageActivity::class.java))
        }

        binding.btnManageSalaries.setOnClickListener {
            startActivity(Intent(this, AdminSalaryActivity::class.java))
        }

        binding.btnAdminLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        session.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}