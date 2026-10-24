package com.example.myapplication.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.myapplication.data.entity.Employee

class EmployeeDao(private val db: SQLiteDatabase) {

    fun getEmployeeById(empId: Int): Employee? {
        val cursor = db.rawQuery(
            "SELECT * FROM Employee WHERE id = ? LIMIT 1",
            arrayOf(empId.toString())
        )
        var emp: Employee? = null
        if (cursor.moveToFirst()) {
            emp = Employee(
                id = cursor.getInt(0),
                code = cursor.getString(1),
                fullName = cursor.getString(2),
                email = cursor.getString(3),
                phone = cursor.getString(4),
                hourlyRate = cursor.getDouble(5),
                accountId = cursor.getInt(6)
            )
        }
        cursor.close()
        return emp
    }

    fun getEmployeeByAccountId(accId: Int): Employee? {
        val cursor = db.rawQuery(
            "SELECT * FROM Employee WHERE accountId = ? LIMIT 1",
            arrayOf(accId.toString())
        )
        var emp: Employee? = null
        if (cursor.moveToFirst()) {
            emp = Employee(
                id = cursor.getInt(0),
                code = cursor.getString(1),
                fullName = cursor.getString(2),
                email = cursor.getString(3),
                phone = cursor.getString(4),
                hourlyRate = cursor.getDouble(5),
                accountId = cursor.getInt(6)
            )
        }
        cursor.close()
        return emp
    }

    fun getAllEmployees(): List<Employee> {
        val list = mutableListOf<Employee>()
        val cursor = db.rawQuery("SELECT * FROM Employee ORDER BY id DESC", null)
        while (cursor.moveToNext()) {
            list.add(
                Employee(
                    id = cursor.getInt(0),
                    code = cursor.getString(1),
                    fullName = cursor.getString(2),
                    email = cursor.getString(3),
                    phone = cursor.getString(4),
                    hourlyRate = cursor.getDouble(5),
                    accountId = cursor.getInt(6)
                )
            )
        }
        cursor.close()
        return list
    }

    fun insertEmployee(emp: Employee): Long {
        val values = ContentValues().apply {
            put("code", emp.code)
            put("fullname", emp.fullName)
            put("email", emp.email)
            put("phone", emp.phone)
            put("hourlyRate", emp.hourlyRate)
            put("accountId", emp.accountId)
        }
        return db.insert("Employee", null, values)
    }

    fun updateEmployee(emp: Employee): Int {
        val values = ContentValues().apply {
            put("code", emp.code)
            put("fullname", emp.fullName)
            put("email", emp.email)
            put("phone", emp.phone)
            put("hourlyRate", emp.hourlyRate)
        }
        return db.update("Employee", values, "id = ?", arrayOf(emp.id.toString()))
    }

    fun deleteEmployee(empId: Int): Int {
        return db.delete("Employee", "id = ?", arrayOf(empId.toString()))
    }
}