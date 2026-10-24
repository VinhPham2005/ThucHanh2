package com.example.myapplication.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.ui.admin.AdminDashboardActivity
import com.example.myapplication.ui.employee.EmployeeDashboardActivity
import com.example.myapplication.utils.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Đăng nhập hệ thống"

        db = AppDatabase.getInstance(this)
        session = SessionManager(this)

        // 1. Kiểm tra nếu phiên đăng nhập vẫn còn thì chuyển thẳng vào màn hình chức năng theo Role
        if (session.isLoggedIn()) {
            navigateByRole(session.getRole())
            return
        }

        // 2. Xử lý sự kiện bấm nút Đăng nhập
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val username = binding.edtUsername.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu!", Toast.LENGTH_SHORT).show()
            return
        }

        // Truy vấn tài khoản qua AccountDao
        val account = db.accountDao().login(username, password)
        if (account == null) {
            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
            return
        }

        // Nếu là EMPLOYEE, lấy thêm employeeId từ bảng Employee
        var employeeId = -1
        if (account.role == "EMPLOYEE") {
            val emp = db.employeeDao().getEmployeeByAccountId(account.id)
            if (emp == null) {
                Toast.makeText(this, "Lỗi: Tài khoản nhân viên này chưa có hồ sơ trong bảng Employee!", Toast.LENGTH_LONG).show()
                return
            }
            employeeId = emp.id
        }

        // Lưu phiên đăng nhập sử dụng đúng hàm của SessionManager bạn cung cấp
        session.saveLoginSession(
            accountId = account.id,
            employeeId = employeeId,
            username = account.username,
            role = account.role
        )

        Toast.makeText(this, "Đăng nhập thành công với quyền: ${account.role}", Toast.LENGTH_SHORT).show()
        navigateByRole(account.role)
    }

    private fun navigateByRole(role: String?) {
        when (role) {
            "ADMIN" -> {
                startActivity(Intent(this, AdminDashboardActivity::class.java))
                finish()
            }
            "EMPLOYEE" -> {
                startActivity(Intent(this, EmployeeDashboardActivity::class.java))
                finish()
            }
            else -> {
                session.logout()
            }
        }
    }
}