package com.example.myapplication.ui.employee

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.databinding.ActivityEmployeeSalaryBinding
import com.example.myapplication.ui.common.SalaryAdapter
import com.example.myapplication.utils.SessionManager
import com.example.myapplication.utils.WorkHelper
import java.text.NumberFormat
import java.util.Locale

class EmployeeSalaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeSalaryBinding
    private lateinit var db: AppDatabase
    private lateinit var salaryAdapter: SalaryAdapter
    private var employeeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeSalaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Chi tiết Lương & Giờ làm"

        db = AppDatabase.getInstance(this)
        val session = SessionManager(this)
        employeeId = session.getEmployeeId()

        if (employeeId == -1) {
            Toast.makeText(this, "Không tìm thấy hồ sơ nhân viên!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val currentMonth = WorkHelper.getCurrentMonth()
        val currentYear = WorkHelper.getCurrentYear()
        binding.edtEmpMonth.setText(currentMonth.toString())
        binding.edtEmpYear.setText(currentYear.toString())

        setupRecyclerView()

        updateAndDisplaySalary(currentMonth, currentYear)

        binding.btnRefreshSalary.setOnClickListener {
            val m = binding.edtEmpMonth.text.toString().trim().toIntOrNull()
            val y = binding.edtEmpYear.text.toString().trim().toIntOrNull()
            if (m == null || m !in 1..12 || y == null || y < 2000) {
                Toast.makeText(this, "Vui lòng nhập Tháng (1-12) và Năm hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            updateAndDisplaySalary(m, y)
        }
    }

    private fun setupRecyclerView() {
        salaryAdapter = SalaryAdapter(
            salaryList = emptyList(),
            employeeMap = emptyMap(),
            isAdmin = false
        )
        binding.rvEmployeeSalaries.layoutManager = LinearLayoutManager(this)
        binding.rvEmployeeSalaries.adapter = salaryAdapter
    }

    private fun updateAndDisplaySalary(month: Int, year: Int) {
        val emp = db.employeeDao().getEmployeeById(employeeId) ?: return
        val currencyFormat = NumberFormat.getNumberInstance(Locale.US)

        val selectedSalary = WorkHelper.calculateAndSaveMonthlySalary(db, employeeId, month, year)

        binding.tvCardTitle.text = String.format("CHI TIẾT LƯƠNG THÁNG %02d/%d", month, year)
        binding.tvEmpInfo.text =
            "NV: ${emp.code} - ${emp.fullName} | Đơn giá: ${currencyFormat.format(emp.hourlyRate)} đ/giờ"

        if (selectedSalary != null) {
            binding.tvCurrentTotalHours.text = String.format("Tổng giờ: %.2fh", selectedSalary.totalHours)
            binding.tvCurrentOvertime.text = String.format("Tăng ca (OT): %.2fh", selectedSalary.overtimeHours)
            binding.tvCurrentTotalSalary.text =
                "TỔNG LƯƠNG: ${currencyFormat.format(selectedSalary.totalSalary)} đ (${selectedSalary.status})"
        }

        val allMySalaries = db.salaryDao().getEmployeeSalaries(employeeId)
        salaryAdapter.updateData(allMySalaries, mapOf(emp.id to emp))
    }
}