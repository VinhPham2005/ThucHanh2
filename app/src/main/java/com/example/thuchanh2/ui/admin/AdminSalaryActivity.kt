package com.example.thuchanh2.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thuchanh2.data.AppDatabase
import com.example.thuchanh2.data.entity.Salary
import com.example.thuchanh2.databinding.ActivityAdminSalaryBinding
import com.example.thuchanh2.ui.common.SalaryAdapter
import com.example.thuchanh2.utils.WorkHelper
import java.text.NumberFormat
import java.util.Locale

class AdminSalaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminSalaryBinding
    private lateinit var db: AppDatabase
    private lateinit var salaryAdapter: SalaryAdapter
    private var isFilteredByMonth = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSalaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Quản lý Bảng lương (Admin)"

        db = AppDatabase.getInstance(this)

        binding.edtMonth.setText(WorkHelper.getCurrentMonth().toString())
        binding.edtYear.setText(WorkHelper.getCurrentYear().toString())

        setupRecyclerView()
        loadSalaryRecords()

        binding.btnCalculateAll.setOnClickListener {
            val (month, year) = getValidMonthYear() ?: return@setOnClickListener
            val count = WorkHelper.calculateSalaryForAllEmployees(db, month, year)
            isFilteredByMonth = true
            loadSalaryRecords(month, year)
            Toast.makeText(
                this,
                "Đã tính lương tháng $month/$year cho $count nhân viên!",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnFilterMonth.setOnClickListener {
            val (month, year) = getValidMonthYear() ?: return@setOnClickListener
            isFilteredByMonth = true
            loadSalaryRecords(month, year)
        }

        binding.btnShowAll.setOnClickListener {
            isFilteredByMonth = false
            loadSalaryRecords()
        }
    }

    private fun setupRecyclerView() {
        salaryAdapter = SalaryAdapter(
            salaryList = emptyList(),
            employeeMap = emptyMap(),
            isAdmin = true,
            onToggleStatusClick = { salary ->
                toggleSalaryStatus(salary)
            }
        )
        binding.rvSalaries.layoutManager = LinearLayoutManager(this)
        binding.rvSalaries.adapter = salaryAdapter
    }

    private fun toggleSalaryStatus(salary: Salary) {
        val newStatus = if (salary.status == "PENDING") "PAID" else "PENDING"
        db.salaryDao().updateSalary(salary.copy(status = newStatus))
        Toast.makeText(this, "Đã cập nhật trạng thái: $newStatus", Toast.LENGTH_SHORT).show()

        if (isFilteredByMonth) {
            val month = binding.edtMonth.text.toString().toIntOrNull()
            val year = binding.edtYear.text.toString().toIntOrNull()
            loadSalaryRecords(month, year)
        } else {
            loadSalaryRecords()
        }
    }

    private fun loadSalaryRecords(filterMonth: Int? = null, filterYear: Int? = null) {
        val employees = db.employeeDao().getAllEmployees()
        val empMap = employees.associateBy { it.id }

        var salaries = db.salaryDao().getAllSalaries()
        if (filterMonth != null && filterYear != null) {
            salaries = salaries.filter { it.month == filterMonth && it.year == filterYear }
        }

        salaryAdapter.updateData(salaries, empMap)

        val totalPayout = salaries.sumOf { it.totalSalary }
        val formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(totalPayout)
        binding.tvSummaryCount.text =
            "Số bản ghi: ${salaries.size} | Tổng quỹ lương: $formattedTotal đ"
    }

    private fun getValidMonthYear(): Pair<Int, Int>? {
        val month = binding.edtMonth.text.toString().trim().toIntOrNull()
        val year = binding.edtYear.text.toString().trim().toIntOrNull()

        if (month == null || month !in 1..12) {
            Toast.makeText(this, "Tháng không hợp lệ (1 - 12)!", Toast.LENGTH_SHORT).show()
            return null
        }
        if (year == null || year < 2000) {
            Toast.makeText(this, "Năm không hợp lệ!", Toast.LENGTH_SHORT).show()
            return null
        }
        return Pair(month, year)
    }
}
