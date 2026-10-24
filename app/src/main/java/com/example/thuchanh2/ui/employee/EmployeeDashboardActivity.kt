package com.example.thuchanh2.ui.employee

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.thuchanh2.data.AppDatabase
import com.example.thuchanh2.data.entity.WorkLog
import com.example.thuchanh2.databinding.ActivityEmployeeDashboardBinding
import com.example.thuchanh2.utils.SessionManager
import com.example.thuchanh2.utils.WorkHelper

class EmployeeDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeDashboardBinding
    private lateinit var db: AppDatabase
    private lateinit var session: SessionManager
    private var empId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Khu vực Nhân viên"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = AppDatabase.getInstance(this)
        session = SessionManager(this)
        empId = session.getEmployeeId()

        val emp = db.employeeDao().getEmployeeById(empId)
        binding.tvWelcome.text = "Xin chào, ${emp?.fullname ?: "Nhân viên"}!"

        binding.btnCheckIn.setOnClickListener { handleCheckIn() }
        binding.btnCheckOut.setOnClickListener { handleCheckOut() }

        binding.btnSalary.setOnClickListener {
            startActivity(Intent(this, EmployeeSalaryActivity::class.java))
        }

        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, AttendanceHistoryActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun handleCheckIn() {
        val today = WorkHelper.getCurrentDate()
        val existingLog = db.workLogDao().getWorkLogByDate(empId, today)

        if (existingLog != null) {
            Toast.makeText(this, "Lỗi: Hôm nay bạn đã Check-in lúc ${existingLog.checkInTime} rồi!", Toast.LENGTH_SHORT).show()
            return
        }

        val newLog = WorkLog(
            employeeId = empId,
            workDate = today,
            checkInTime = WorkHelper.getCurrentTime(),
            checkOutTime = null,
            totalHours = 0.0,
            status = "INCOMPLETE"
        )
        db.workLogDao().insertWorkLog(newLog)
        Toast.makeText(this, "Check-in thành công lúc ${newLog.checkInTime}!", Toast.LENGTH_SHORT).show()
    }

    private fun handleCheckOut() {
        val today = WorkHelper.getCurrentDate()
        val log = db.workLogDao().getWorkLogByDate(empId, today)

        if (log == null) {
            Toast.makeText(this, "Lỗi: Bạn chưa Check-in hôm nay!", Toast.LENGTH_SHORT).show()
            return
        }
        if (log.status == "COMPLETED") {
            Toast.makeText(this, "Lỗi: Bạn đã Check-out hôm nay rồi!", Toast.LENGTH_SHORT).show()
            return
        }

        val nowTime = WorkHelper.getCurrentTime()
        val totalHours = WorkHelper.calculateWorkingHours(log.checkInTime, nowTime)

        if (totalHours < 0) {
            Toast.makeText(this, "Lỗi: Giờ Check-out không hợp lệ!", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedLog = log.copy(
            checkOutTime = nowTime,
            totalHours = totalHours,
            status = "COMPLETED"
        )
        db.workLogDao().updateWorkLog(updatedLog)

        // Tự động cập nhật bảng lương tháng
        val parts = today.split("-")
        if (parts.size >= 2) {
            val year = parts[0].toIntOrNull() ?: WorkHelper.getCurrentYear()
            val month = parts[1].toIntOrNull() ?: WorkHelper.getCurrentMonth()
            WorkHelper.calculateAndSaveMonthlySalary(db, empId, month, year)
        }

        Toast.makeText(this, "Check-out thành công! Tổng giờ: $totalHours", Toast.LENGTH_LONG).show()
    }
}
