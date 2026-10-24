package com.example.thuchanh2.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.thuchanh2.data.entity.Employee

class EmployeeDao(private val db: SQLiteDatabase) {
    fun getEmployeeById(id: Int): Employee? {
        val cursor = db.query(
            "Employee",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val emp = Employee(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                code = cursor.getString(cursor.getColumnIndexOrThrow("code")),
                fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                hourlyRate = cursor.getDouble(cursor.getColumnIndexOrThrow("hourlyRate")),
                accountId = cursor.getInt(cursor.getColumnIndexOrThrow("accountId"))
            )
            cursor.close()
            emp
        } else {
            cursor.close()
            null
        }
    }

    fun getAllEmployees(): List<Employee> {
        val list = mutableListOf<Employee>()
        val cursor = db.rawQuery("SELECT * FROM Employee", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Employee(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        code = cursor.getString(cursor.getColumnIndexOrThrow("code")),
                        fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname")),
                        email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        hourlyRate = cursor.getDouble(cursor.getColumnIndexOrThrow("hourlyRate")),
                        accountId = cursor.getInt(cursor.getColumnIndexOrThrow("accountId"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun searchEmployees(keyword: String): List<Employee> {
        val list = mutableListOf<Employee>()
        val cursor = db.rawQuery(
            "SELECT * FROM Employee WHERE fullname LIKE ? OR code LIKE ?",
            arrayOf("%$keyword%", "%$keyword%")
        )
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Employee(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        code = cursor.getString(cursor.getColumnIndexOrThrow("code")),
                        fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname")),
                        email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        hourlyRate = cursor.getDouble(cursor.getColumnIndexOrThrow("hourlyRate")),
                        accountId = cursor.getInt(cursor.getColumnIndexOrThrow("accountId"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun insertEmployee(employee: Employee): Long {
        val values = ContentValues().apply {
            put("code", employee.code)
            put("fullname", employee.fullname)
            put("email", employee.email)
            put("phone", employee.phone)
            put("hourlyRate", employee.hourlyRate)
            put("accountId", employee.accountId)
        }
        return db.insert("Employee", null, values)
    }

    fun updateEmployee(employee: Employee): Int {
        val values = ContentValues().apply {
            put("code", employee.code)
            put("fullname", employee.fullname)
            put("email", employee.email)
            put("phone", employee.phone)
            put("hourlyRate", employee.hourlyRate)
            put("accountId", employee.accountId)
        }
        return db.update("Employee", values, "id = ?", arrayOf(employee.id.toString()))
    }

    fun deleteEmployee(employee: Employee): Int {
        return db.delete("Employee", "id = ?", arrayOf(employee.id.toString()))
    }
}