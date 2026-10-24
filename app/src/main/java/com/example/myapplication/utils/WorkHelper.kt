package com.example.myapplication.utils

import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.entity.Salary
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.round

object WorkHelper {

    const val STANDARD_DAILY_HOURS = 8.0
    const val OVERTIME_RATE_MULTIPLIER = 1.5

    fun getCurrentDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    fun getCurrentTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    fun getCurrentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    fun calculateWorkingHours(checkIn: String, checkOut: String): Double {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.isLenient = false
            val inDate = sdf.parse(checkIn) ?: return -1.0
            val outDate = sdf.parse(checkOut) ?: return -1.0

            val diffMillis = outDate.time - inDate.time
            if (diffMillis <= 0) return -1.0

            val hours = diffMillis.toDouble() / (1000.0 * 60.0 * 60.0)
            roundTwoDecimals(hours)
        } catch (e: Exception) {
            -1.0
        }
    }

    fun processCheckOutAndAutoCalculate(
        db: AppDatabase,
        empId: Int,
        workDate: String,
        checkOutTime: String
    ): Result<Double> {
        val workLogDao = db.workLogDao()
        val log = workLogDao.getWorkLogByDate(empId, workDate)
            ?: return Result.failure(Exception("Bạn chưa Check-in trong ngày $workDate!"))

        if (log.status == "COMPLETED") {
            return Result.failure(Exception("Bạn đã hoàn thành Check-out cho ngày $workDate rồi!"))
        }

        val totalHours = calculateWorkingHours(log.checkInTime, checkOutTime)
        if (totalHours <= 0) {
            return Result.failure(Exception("Giờ Check-out ($checkOutTime) phải lớn hơn giờ Check-in (${log.checkInTime})!"))
        }

        val updatedLog = log.copy(
            checkOutTime = checkOutTime,
            totalHours = totalHours,
            status = "COMPLETED"
        )
        workLogDao.updateWorkLog(updatedLog)

        val parts = workDate.split("-")
        if (parts.size >= 2) {
            val year = parts[0].toIntOrNull() ?: getCurrentYear()
            val month = parts[1].toIntOrNull() ?: getCurrentMonth()
            calculateAndSaveMonthlySalary(db, empId, month, year)
        }

        return Result.success(totalHours)
    }

    fun calculateAndSaveMonthlySalary(db: AppDatabase, empId: Int, month: Int, year: Int): Salary? {
        val employee = db.employeeDao().getEmployeeById(empId) ?: return null
        val yearMonth = String.format(Locale.getDefault(), "%04d-%02d", year, month)
        val logs = db.workLogDao().getMonthlyCompletedLogs(empId, yearMonth)

        var totalHours = 0.0
        var overtimeHours = 0.0

        for (log in logs) {
            totalHours += log.totalHours
            if (log.totalHours > STANDARD_DAILY_HOURS) {
                overtimeHours += (log.totalHours - STANDARD_DAILY_HOURS)
            }
        }

        totalHours = roundTwoDecimals(totalHours)
        overtimeHours = roundTwoDecimals(overtimeHours)
        val regularHours = max(0.0, roundTwoDecimals(totalHours - overtimeHours))

        val regularPay = regularHours * employee.hourlyRate
        val overtimePay = overtimeHours * employee.hourlyRate * OVERTIME_RATE_MULTIPLIER
        val totalSalary = roundTwoDecimals(regularPay + overtimePay)

        val salaryDao = db.salaryDao()
        val existingSalary = salaryDao.getSalaryRecord(empId, month, year)

        return if (existingSalary == null) {
            val newSalary = Salary(
                employeeId = empId,
                month = month,
                year = year,
                totalHours = totalHours,
                overtimeHours = overtimeHours,
                totalSalary = totalSalary,
                status = "PENDING"
            )
            val newId = salaryDao.insertSalary(newSalary).toInt()
            newSalary.copy(id = newId)
        } else {
            val updatedSalary = existingSalary.copy(
                totalHours = totalHours,
                overtimeHours = overtimeHours,
                totalSalary = totalSalary
            )
            salaryDao.updateSalary(updatedSalary)
            updatedSalary
        }
    }

    fun calculateSalaryForAllEmployees(db: AppDatabase, month: Int, year: Int): Int {
        val employees = db.employeeDao().getAllEmployees()
        for (emp in employees) {
            calculateAndSaveMonthlySalary(db, emp.id, month, year)
        }
        return employees.size
    }

    private fun roundTwoDecimals(value: Double): Double {
        return round(value * 100.0) / 100.0
    }
}