package com.example.myapplication.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.myapplication.data.entity.Salary

class SalaryDao(private val db: SQLiteDatabase) {
    fun getSalaryRecord(empId: Int, m: Int, y: Int): Salary? {
        val cursor = db.rawQuery(
            "SELECT * FROM Salary WHERE employeeId = ? AND month = ? AND year = ? LIMIT 1",
            arrayOf(empId.toString(), m.toString(), y.toString())
        )
        var sal: Salary? = null
        if (cursor.moveToFirst()) {
            sal = Salary(
                id = cursor.getInt(0),
                employeeId = cursor.getInt(1),
                month = cursor.getInt(2),
                year = cursor.getInt(3),
                totalHours = cursor.getDouble(4),
                overtimeHours = cursor.getDouble(5),
                totalSalary = cursor.getDouble(6),
                status = cursor.getString(7)
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
                    id = cursor.getInt(0),
                    employeeId = cursor.getInt(1),
                    month = cursor.getInt(2),
                    year = cursor.getInt(3),
                    totalHours = cursor.getDouble(4),
                    overtimeHours = cursor.getDouble(5),
                    totalSalary = cursor.getDouble(6),
                    status = cursor.getString(7)
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
                    id = cursor.getInt(0),
                    employeeId = cursor.getInt(1),
                    month = cursor.getInt(2),
                    year = cursor.getInt(3),
                    totalHours = cursor.getDouble(4),
                    overtimeHours = cursor.getDouble(5),
                    totalSalary = cursor.getDouble(6),
                    status = cursor.getString(7)
                )
            )
        }
        cursor.close()
        return list
    }
}