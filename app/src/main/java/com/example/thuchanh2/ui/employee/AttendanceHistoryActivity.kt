package com.example.thuchanh2.ui.employee

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thuchanh2.data.AppDatabase
import com.example.thuchanh2.data.entity.WorkLog
import com.example.thuchanh2.databinding.ActivityAttendanceHistoryBinding
import com.example.thuchanh2.ui.common.AttendanceAdapter
import com.example.thuchanh2.utils.SessionManager
import com.example.thuchanh2.utils.WorkHelper
import java.util.Locale

class AttendanceHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceHistoryBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: AttendanceAdapter
    private var employeeId: Int = -1
    private var allLogs: List<WorkLog> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Lịch sử Điểm danh"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = AppDatabase.getInstance(this)
        val session = SessionManager(this)

        employeeId = intent.getIntExtra("EMP_ID", -1)
        if (employeeId == -1) {
            employeeId = session.getEmployeeId()
        }

        if (employeeId == -1) {
            Toast.makeText(this, "Không tìm thấy hồ sơ nhân viên!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val emp = db.employeeDao().getEmployeeById(employeeId)
        binding.tvEmployeeInfo.text = if (emp != null) {
            "Nhân viên: ${emp.code} - ${emp.fullname}"
        } else {
            "Mã NV #$employeeId"
        }

        binding.edtFilterMonth.setText(WorkHelper.getCurrentMonth().toString())
        binding.edtFilterYear.setText(WorkHelper.getCurrentYear().toString())

        setupRecyclerView()
        loadData()

        binding.btnFilter.setOnClickListener {
            val month = binding.edtFilterMonth.text.toString().trim().toIntOrNull()
            val year = binding.edtFilterYear.text.toString().trim().toIntOrNull()

            if (month == null || month !in 1..12 || year == null || year < 2000) {
                Toast.makeText(this, "Vui lòng nhập Tháng (1-12) và Năm hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefix = String.format(Locale.getDefault(), "%04d-%02d", year, month)
            val filtered = allLogs.filter { it.workDate.startsWith(prefix) }
            displayLogs(filtered)
        }

        binding.btnShowAll.setOnClickListener {
            displayLogs(allLogs)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupRecyclerView() {
        adapter = AttendanceAdapter(emptyList())
        binding.rvAttendance.layoutManager = LinearLayoutManager(this)
        binding.rvAttendance.adapter = adapter
    }

    private fun loadData() {
        allLogs = db.workLogDao().getAttendanceHistory(employeeId)
        displayLogs(allLogs)
    }

    private fun displayLogs(logs: List<WorkLog>) {
        adapter.updateData(logs)

        val totalDays = logs.size
        val totalHours = logs.sumOf { it.totalHours }
        val overtimeHours = logs.filter { it.totalHours > 8.0 }.sumOf { it.totalHours - 8.0 }

        binding.tvSummaryDays.text = "Tổng ca: $totalDays"
        binding.tvSummaryHours.text = String.format(Locale.getDefault(), "Tổng giờ: %.2fh", totalHours)
        binding.tvSummaryOvertime.text = String.format(Locale.getDefault(), "OT: %.2fh", overtimeHours)

        if (logs.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.tvEmptyState.visibility = View.GONE
        }
    }
}
