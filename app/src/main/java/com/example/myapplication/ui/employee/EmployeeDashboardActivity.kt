package com.example.myapplication.ui.employee

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.entity.WorkLog
import com.example.myapplication.databinding.ActivityEmployeeDashboardBinding
import com.example.myapplication.ui.auth.LoginActivity
import com.example.myapplication.utils.SessionManager
import com.example.myapplication.utils.WorkHelper
import java.text.NumberFormat
import java.util.Locale

class EmployeeDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmployeeDashboardBinding
    private lateinit var db: AppDatabase
    private lateinit var session: SessionManager
    private var employeeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Trang cá nhân Nhân viên"

        db = AppDatabase.getInstance(this)
        session = SessionManager(this)

        if (!session.isLoggedIn() || session.getRole() != "EMPLOYEE") {
            Toast.makeText(this, "Bạn chưa đăng nhập hoặc không có quyền truy cập!", Toast.LENGTH_SHORT).show()
            logout()
            return
        }

        employeeId = session.getEmployeeId()
        if (employeeId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID hồ sơ nhân viên!", Toast.LENGTH_SHORT).show()
            logout()
            return
        }

        setupEmployeeInfo()
        setupDateTimeInput()
        checkTodayLogStatus(binding.edtWorkDate.text.toString().trim())

        binding.btnSyncNowTime.setOnClickListener {
            setupDateTimeInput()
            checkTodayLogStatus(binding.edtWorkDate.text.toString().trim())
        }

        binding.btnCheckIn.setOnClickListener {
            handleCheckIn()
        }

        binding.btnCheckOut.setOnClickListener {
            handleCheckOut()
        }

        binding.btnAttendanceHistory.setOnClickListener {
            Toast.makeText(this, "Lịch sử điểm danh được tự động cập nhật trong mục Lương!", Toast.LENGTH_SHORT).show()
        }

        binding.btnMySalary.setOnClickListener {
            startActivity(Intent(this, EmployeeSalaryActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun setupEmployeeInfo() {
        val emp = db.employeeDao().getEmployeeById(employeeId)
        if (emp != null) {
            val currencyFormat = NumberFormat.getNumberInstance(Locale.US)
            binding.tvWelcome.text = "Xin chào, ${emp.fullName}!"
            binding.tvEmployeeDetails.text = "Mã NV: ${emp.code} | Lương theo giờ: ${currencyFormat.format(emp.hourlyRate)} đ/h"
        } else {
            binding.tvWelcome.text = "Xin chào, ${session.getUsername()}!"
            binding.tvEmployeeDetails.text = "Mã NV: --- | Lương theo giờ: ---"
        }
    }

    private fun setupDateTimeInput() {
        binding.edtWorkDate.setText(WorkHelper.getCurrentDate())
        binding.edtWorkTime.setText(WorkHelper.getCurrentTime())
    }

    private fun checkTodayLogStatus(date: String) {
        val log = db.workLogDao().getWorkLogByDate(employeeId, date)
        if (log == null) {
            binding.tvTodayStatus.text = "Trạng thái: Chưa Check-in"
            binding.tvTodayLogDetails.text = "Check-in: --:-- | Check-out: --:-- | Tổng giờ: 0.0h"
        } else {
            binding.tvTodayStatus.text = "Trạng thái: ${log.status}"
            val checkOutText = log.checkOutTime ?: "--:--"
            binding.tvTodayLogDetails.text = "Check-in: ${log.checkInTime} | Check-out: $checkOutText | Tổng giờ: ${log.totalHours}h"
        }
    }

    private fun handleCheckIn() {
        val date = binding.edtWorkDate.text.toString().trim()
        val time = binding.edtWorkTime.text.toString().trim()

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Ngày và Giờ Check-in!", Toast.LENGTH_SHORT).show()
            return
        }

        val existingLog = db.workLogDao().getWorkLogByDate(employeeId, date)
        if (existingLog != null) {
            Toast.makeText(this, "Bạn đã Check-in cho ngày $date rồi!", Toast.LENGTH_SHORT).show()
            checkTodayLogStatus(date)
            return
        }

        val newLog = WorkLog(
            employeeId = employeeId,
            workDate = date,
            checkInTime = time,
            checkOutTime = null,
            totalHours = 0.0,
            status = "INCOMPLETE"
        )
        db.workLogDao().insertWorkLog(newLog)
        Toast.makeText(this, "Check-in thành công lúc $time ngày $date!", Toast.LENGTH_SHORT).show()
        checkTodayLogStatus(date)
    }

    private fun handleCheckOut() {
        val date = binding.edtWorkDate.text.toString().trim()
        val time = binding.edtWorkTime.text.toString().trim()

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Ngày và Giờ Check-out!", Toast.LENGTH_SHORT).show()
            return
        }

        val result = WorkHelper.processCheckOutAndAutoCalculate(db, employeeId, date, time)
        result.onSuccess { hours ->
            Toast.makeText(this, "Check-out thành công! Tổng giờ làm: ${hours}h", Toast.LENGTH_SHORT).show()
            checkTodayLogStatus(date)
        }.onFailure { exception ->
            Toast.makeText(this, exception.message ?: "Lỗi Check-out!", Toast.LENGTH_SHORT).show()
            checkTodayLogStatus(date)
        }
    }

    private fun logout() {
        session.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}