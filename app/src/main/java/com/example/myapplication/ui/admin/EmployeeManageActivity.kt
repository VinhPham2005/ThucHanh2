package com.example.myapplication.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.entity.Account
import com.example.myapplication.data.entity.Employee
import com.example.myapplication.databinding.ActivityEmployeeManageBinding
import com.example.myapplication.ui.common.EmployeeAdapter

class EmployeeManageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeManageBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: EmployeeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Quản lý Hồ sơ Nhân viên"

        db = AppDatabase.getInstance(this)

        setupRecyclerView()
        loadEmployees()

        binding.btnAddEmployee.setOnClickListener {
            handleAddEmployee()
        }
    }

    private fun setupRecyclerView() {
        adapter = EmployeeAdapter(emptyList()) { emp ->
            db.employeeDao().deleteEmployee(emp.id)
            Toast.makeText(this, "Đã xóa nhân viên ${emp.fullName}", Toast.LENGTH_SHORT).show()
            loadEmployees()
        }
        binding.rvEmployeeList.layoutManager = LinearLayoutManager(this)
        binding.rvEmployeeList.adapter = adapter
    }

    private fun loadEmployees() {
        val list = db.employeeDao().getAllEmployees()
        adapter.updateData(list)
    }

    private fun handleAddEmployee() {
        val code = binding.edtEmpCode.text.toString().trim()
        val name = binding.edtFullName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val phone = binding.edtPhone.text.toString().trim()
        val username = binding.edtUsername.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val hourlyRateStr = binding.edtHourlyRate.text.toString().trim()

        if (code.isEmpty() || name.isEmpty() || username.isEmpty() || password.isEmpty() || hourlyRateStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền các thông tin bắt buộc!", Toast.LENGTH_SHORT).show()
            return
        }

        val hourlyRate = hourlyRateStr.toDoubleOrNull()
        if (hourlyRate == null || hourlyRate <= 0) {
            Toast.makeText(this, "Lương giờ không hợp lệ!", Toast.LENGTH_SHORT).show()
            return
        }

        val newAcc = Account(
            username = username,
            password = password,
            role = "EMPLOYEE"
        )
        val accId = db.accountDao().insertAccount(newAcc).toInt()
        if (accId <= 0) {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại hoặc lỗi tạo tài khoản!", Toast.LENGTH_SHORT).show()
            return
        }

        val newEmp = Employee(
            code = code,
            fullName = name,
            email = email,
            phone = phone,
            hourlyRate = hourlyRate,
            accountId = accId
        )
        db.employeeDao().insertEmployee(newEmp)

        Toast.makeText(this, "Đã thêm nhân viên $name!", Toast.LENGTH_SHORT).show()

        binding.edtEmpCode.text?.clear()
        binding.edtFullName.text?.clear()
        binding.edtEmail.text?.clear()
        binding.edtPhone.text?.clear()
        binding.edtUsername.text?.clear()
        binding.edtPassword.text?.clear()
        binding.edtHourlyRate.text?.clear()

        loadEmployees()
    }
}