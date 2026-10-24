package com.example.thuchanh2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thuchanh2.data.AppDatabase
import com.example.thuchanh2.ui.admin.EmployeeManageActivity
import com.example.thuchanh2.ui.employee.EmployeeDashboardActivity
import com.example.thuchanh2.utils.SessionManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnAdminMode).setOnClickListener {
            startActivity(Intent(this, EmployeeManageActivity::class.java))
        }

        findViewById<Button>(R.id.btnEmployeeMode).setOnClickListener {
            val db = AppDatabase.getInstance(this)
            val session = SessionManager(this)
            
            // Giả lập đăng nhập cho nhân viên đầu tiên trong database để test
            val employees = db.employeeDao().getAllEmployees()
            if (employees.isNotEmpty()) {
                session.saveLoginSession(employees[0].accountId, employees[0].id, employees[0].code, "EMPLOYEE")
                startActivity(Intent(this, EmployeeDashboardActivity::class.java))
            } else {
                Toast.makeText(this, "Chưa có nhân viên nào! Hãy vào Admin để thêm nhân viên trước.", Toast.LENGTH_LONG).show()
            }
        }
    }
}