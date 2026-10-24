package com.example.thuchanh2.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.thuchanh2.data.entity.WorkLog

class WorkLogDao(private val db: SQLiteDatabase) {

    fun getWorkLogByDate(empId: Int, date: String): WorkLog? {
        val cursor = db.rawQuery(
            "SELECT * FROM WorkLog WHERE employeeId = ? AND workDate = ? LIMIT 1",
            arrayOf(empId.toString(), date)
        )
        var log: WorkLog? = null
        if (cursor.moveToFirst()) {
            log = WorkLog(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                employeeId = cursor.getInt(cursor.getColumnIndexOrThrow("employeeId")),
                workDate = cursor.getString(cursor.getColumnIndexOrThrow("workDate")),
                checkInTime = cursor.getString(cursor.getColumnIndexOrThrow("checkInTime")),
                checkOutTime = cursor.getString(cursor.getColumnIndexOrThrow("checkOutTime")),
                totalHours = cursor.getDouble(cursor.getColumnIndexOrThrow("totalHours")),
                status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
            )
        }
        cursor.close()
        return log
    }

    fun insertWorkLog(workLog: WorkLog): Long {
        val values = ContentValues().apply {
            put("employeeId", workLog.employeeId)
            put("workDate", workLog.workDate)
            put("checkInTime", workLog.checkInTime)
            put("checkOutTime", workLog.checkOutTime)
            put("totalHours", workLog.totalHours)
            put("status", workLog.status)
        }
        return db.insert("WorkLog", null, values)
    }

    fun updateWorkLog(workLog: WorkLog): Int {
        val values = ContentValues().apply {
            put("checkOutTime", workLog.checkOutTime)
            put("totalHours", workLog.totalHours)
            put("status", workLog.status)
        }
        return db.update("WorkLog", values, "id = ?", arrayOf(workLog.id.toString()))
    }

    fun getAttendanceHistory(empId: Int): List<WorkLog> {
        val list = mutableListOf<WorkLog>()
        val cursor = db.rawQuery(
            "SELECT * FROM WorkLog WHERE employeeId = ? ORDER BY workDate DESC",
            arrayOf(empId.toString())
        )
        while (cursor.moveToNext()) {
            list.add(
                WorkLog(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    employeeId = cursor.getInt(cursor.getColumnIndexOrThrow("employeeId")),
                    workDate = cursor.getString(cursor.getColumnIndexOrThrow("workDate")),
                    checkInTime = cursor.getString(cursor.getColumnIndexOrThrow("checkInTime")),
                    checkOutTime = cursor.getString(cursor.getColumnIndexOrThrow("checkOutTime")),
                    totalHours = cursor.getDouble(cursor.getColumnIndexOrThrow("totalHours")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                )
            )
        }
        cursor.close()
        return list
    }

    fun getMonthlyCompletedLogs(empId: Int, yearMonth: String): List<WorkLog> {
        val list = mutableListOf<WorkLog>()
        val cursor = db.rawQuery(
            "SELECT * FROM WorkLog WHERE employeeId = ? AND workDate LIKE ? AND status = 'COMPLETED'",
            arrayOf(empId.toString(), "$yearMonth%")
        )
        while (cursor.moveToNext()) {
            list.add(
                WorkLog(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    employeeId = cursor.getInt(cursor.getColumnIndexOrThrow("employeeId")),
                    workDate = cursor.getString(cursor.getColumnIndexOrThrow("workDate")),
                    checkInTime = cursor.getString(cursor.getColumnIndexOrThrow("checkInTime")),
                    checkOutTime = cursor.getString(cursor.getColumnIndexOrThrow("checkOutTime")),
                    totalHours = cursor.getDouble(cursor.getColumnIndexOrThrow("totalHours")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                )
            )
        }
        cursor.close()
        return list
    }
}
