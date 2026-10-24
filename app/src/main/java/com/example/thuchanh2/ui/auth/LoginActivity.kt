package com.example.thuchanh2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thuchanh2.data.AppDatabase
import com.example.thuchanh2.databinding.ActivityLoginBinding
import com.example.thuchanh2.ui.admin.AdminDashboardActivity
import com.example.thuchanh2.ui.employee.EmployeeDashboardActivity
import com.example.thuchanh2.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        db = AppDatabase.getInstance(this)

        // Kiểm tra phiên đăng nhập đã lưu trong SharedPreferences
        if (sessionManager.isLoggedIn()) {
            navigateBasedOnRole(sessionManager.getRole())
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Đăng Nhập"

        // Nút điền nhanh tài khoản Admin
        binding.btnQuickAdmin.setOnClickListener {
            binding.edtUsername.setText("admin")
            binding.edtPassword.setText("123")
        }

        // Nút điền nhanh tài khoản Nhân viên
        binding.btnQuickEmployee.setOnClickListener {
            binding.edtUsername.setText("NV01")
            binding.edtPassword.setText("123")
        }

        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val username = binding.edtUsername.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu!", Toast.LENGTH_SHORT).show()
            return
        }

        val account = db.accountDao().login(username, password)
        if (account == null) {
            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
            return
        }

        if (account.role.equals("ADMIN", ignoreCase = true)) {
            // Lưu thông tin đăng nhập Admin vào SharedPreferences
            sessionManager.saveLoginSession(account.id, -1, account.username, "ADMIN")
            Toast.makeText(this, "Đăng nhập thành công quyền Quản trị viên (Admin)!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        } else {
            // Tìm hồ sơ nhân viên tương ứng với tài khoản
            val employee = db.employeeDao().getEmployeeByAccountId(account.id)
            if (employee == null) {
                Toast.makeText(this, "Lỗi: Tài khoản chưa được liên kết với hồ sơ nhân viên!", Toast.LENGTH_LONG).show()
                return
            }

            // Lưu thông tin đăng nhập Nhân viên vào SharedPreferences
            sessionManager.saveLoginSession(account.id, employee.id, employee.code, "EMPLOYEE")
            Toast.makeText(this, "Đăng nhập thành công! Xin chào ${employee.fullname}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, EmployeeDashboardActivity::class.java))
            finish()
        }
    }

    private fun navigateBasedOnRole(role: String?) {
        if (role.equals("ADMIN", ignoreCase = true)) {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        } else {
            startActivity(Intent(this, EmployeeDashboardActivity::class.java))
        }
        finish()
    }
}
