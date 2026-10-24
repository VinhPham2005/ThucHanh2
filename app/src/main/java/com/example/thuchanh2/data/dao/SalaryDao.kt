package com.example.thuchanh2.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.thuchanh2.data.entity.Salary

class SalaryDao(private val db: SQLiteDatabase) {

    fun getSalaryRecord(empId: Int, month: Int, year: Int): Salary? {
        val cursor = db.rawQuery(
            "SELECT * FROM Salary WHERE employeeId = ? AND month = ? AND year = ? LIMIT 1",
            arrayOf(empId.toString(), month.toString(), year.toString())
        )
        var sal: Salary? = null
        if (cursor.moveToFirst()) {
            sal = Salary(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                employeeId = cursor.getInt(cursor.getColumnIndexOrThrow("employeeId")),
                month = cursor.getInt(cursor.getColumnIndexOrThrow("month")),
                year = cursor.getInt(cursor.getColumnIndexOrThrow("year")),
                totalHours = cursor.getDouble(cursor.getColumnIndexOrThrow("totalHours")),
                overtimeHours = cursor.getDouble(cursor.getColumnIndexOrThrow("overtimeHours")),
                totalSalary = cursor.getDouble(cursor.getColumnIndexOrThrow("totalSalary")),
                status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
            )
        }
        cursor.close()
        return sal
    }

    fun insertSalary(salary: Salary): Long {
        val values = ContentValues().apply {
            put("employeeId", salary.employeeId)
            put("month", salary.month)
            put("year", salary.year)
            put("totalHours", salary.totalHours)
            put("overtimeHours", salary.overtimeHours)
            put("totalSalary", salary.totalSalary)
            put("status", salary.status)
        }
        return db.insert("Salary", null, values)
    }

    fun updateSalary(salary: Salary): Int {
        val values = ContentValues().apply {
            put("totalHours", salary.totalHours)
            put("overtimeHours", salary.overtimeHours)
            put("totalSalary", salary.totalSalary)
            put("status", salary.status)
        }
        return db.update("Salary", values, "id = ?", arrayOf(salary.id.toString()))
    }

    fun getEmployeeSalaries(empId: Int): List<Salary> {
        val list = mutableListOf<Salary>()
        val cursor = db.rawQuery(
            "SELECT * FROM Salary WHERE employeeId = ? ORDER BY year DESC, month DESC",
            arrayOf(empId.toString())
        )
        while (cursor.moveToNext()) {
            list.add(
                Salary(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    employeeId = cursor.getInt(cursor.getColumnIndexOrThrow("employeeId")),
                    month = cursor.getInt(cursor.getColumnIndexOrThrow("month")),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow("year")),
                    totalHours = cursor.getDouble(cursor.getColumnIndexOrThrow("totalHours")),
                    overtimeHours = cursor.getDouble(cursor.getColumnIndexOrThrow("overtimeHours")),
                    totalSalary = cursor.getDouble(cursor.getColumnIndexOrThrow("totalSalary")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                )
            )
        }
        cursor.close()
        return list
    }

    fun getAllSalaries(): List<Salary> {
        val list = mutableListOf<Salary>()
        val cursor = db.rawQuery("SELECT * FROM Salary ORDER BY year DESC, month DESC", null)
        while (cursor.moveToNext()) {
            list.add(
                Salary(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    employeeId = cursor.getInt(cursor.getColumnIndexOrThrow("employeeId")),
                    month = cursor.getInt(cursor.getColumnIndexOrThrow("month")),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow("year")),
                    totalHours = cursor.getDouble(cursor.getColumnIndexOrThrow("totalHours")),
                    overtimeHours = cursor.getDouble(cursor.getColumnIndexOrThrow("overtimeHours")),
                    totalSalary = cursor.getDouble(cursor.getColumnIndexOrThrow("totalSalary")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                )
            )
        }
        cursor.close()
        return list
    }
}
