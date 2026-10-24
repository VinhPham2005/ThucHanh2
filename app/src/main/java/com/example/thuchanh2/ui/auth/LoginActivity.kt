package com.example.thuchanh2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thuchanh2.databinding.ActivityLoginBinding
import com.example.thuchanh2.utils.SessionManager
import com.example.thuchanh2.ui.admin.AdminDashboardActivity
import com.example.thuchanh2.ui.employee.EmployeeDashboardActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            navigateBasedOnRole(sessionManager.getRole())
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.edtUsername.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when {
                username == "admin" && password == "123" -> {
                    sessionManager.saveLoginSession(accountId = 1, employeeId = 0, username = username, role = "ADMIN")
                    Toast.makeText(this, "Đăng nhập thành công (Admin)", Toast.LENGTH_SHORT).show()
                    navigateBasedOnRole("ADMIN")
                }
                username == "nv01" && password == "123" -> {
                    sessionManager.saveLoginSession(accountId = 2, employeeId = 101, username = username, role = "EMPLOYEE")
                    Toast.makeText(this, "Đăng nhập thành công (Nhân viên)", Toast.LENGTH_SHORT).show()
                    navigateBasedOnRole("EMPLOYEE")
                }
                else -> {
                    Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateBasedOnRole(role: String?) {
        val intent = when (role) {
            "ADMIN" -> Intent(this, AdminDashboardActivity::class.java)
            "EMPLOYEE" -> Intent(this, EmployeeDashboardActivity::class.java)
            else -> null
        }

        if (intent != null) {
            startActivity(intent)
            finish()
        }
    }
}
