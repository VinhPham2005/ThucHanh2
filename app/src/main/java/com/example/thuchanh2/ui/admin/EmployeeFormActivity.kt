package com.example.thuchanh2.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thuchanh2.data.AppDatabase
import com.example.thuchanh2.data.entity.Account
import com.example.thuchanh2.data.entity.Employee
import com.example.thuchanh2.databinding.ActivityEmployeeFormBinding

class EmployeeFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeFormBinding
    private lateinit var db: AppDatabase
    private var currentEmpId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = AppDatabase.getInstance(this)
        currentEmpId = intent.getIntExtra("EMP_ID", -1)

        if (currentEmpId != -1) {
            supportActionBar?.title = "Sửa Thông Tin Nhân Viên"
            loadEmployeeData()
            binding.btnDelete.visibility = android.view.View.VISIBLE
        } else {
            supportActionBar?.title = "Thêm Nhân Viên Mới"
        }

        binding.btnSave.setOnClickListener { saveEmployee() }
        binding.btnDelete.setOnClickListener { deleteEmployee() }
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadEmployeeData() {
        val emp = db.employeeDao().getEmployeeById(currentEmpId) ?: return
        binding.edtCode.setText(emp.code)
        binding.edtName.setText(emp.fullname)
        binding.edtEmail.setText(emp.email)
        binding.edtPhone.setText(emp.phone)
        binding.edtRate.setText(emp.hourlyRate.toString())
        binding.edtCode.isEnabled = false // Không cho sửa mã NV
    }

    private fun saveEmployee() {
        val code = binding.edtCode.text.toString().trim()
        val name = binding.edtName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val phone = binding.edtPhone.text.toString().trim()
        val rateStr = binding.edtRate.text.toString().trim()

        if (code.isEmpty() || name.isEmpty() || rateStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show()
            return
        }

        val rate = rateStr.toDoubleOrNull() ?: 0.0

        if (currentEmpId == -1) {
            // Thêm mới: Tạo Account trước
            val accId = db.accountDao().insertAccount(Account(username = code, password = "123", role = "EMPLOYEE"))
            val newEmp = Employee(code = code, fullname = name, email = email, phone = phone, hourlyRate = rate, accountId = accId.toInt())
            db.employeeDao().insertEmployee(newEmp)
            Toast.makeText(this, "Đã thêm NV. Tài khoản: $code / MK: 123", Toast.LENGTH_LONG).show()
        } else {
            // Cập nhật
            val existingEmp = db.employeeDao().getEmployeeById(currentEmpId)!!
            db.employeeDao().updateEmployee(existingEmp.copy(fullname = name, email = email, phone = phone, hourlyRate = rate))
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun deleteEmployee() {
        val emp = db.employeeDao().getEmployeeById(currentEmpId) ?: return
        db.employeeDao().deleteEmployee(emp)
        db.accountDao().deleteAccount(emp.accountId) // Xóa luôn tài khoản
        Toast.makeText(this, "Đã xóa nhân viên!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
