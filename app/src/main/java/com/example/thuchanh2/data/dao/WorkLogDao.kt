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
                id = cursor.getInt(0),
                employeeId = cursor.getInt(1),
                workDate = cursor.getString(2),
                checkInTime = cursor.getString(3),
                checkOutTime = cursor.getString(4),
                totalHours = cursor.getDouble(5),
                status = cursor.getString(6)
            )
        }
        cursor.close()
        return log
    }

    fun insertWorkLog(log: WorkLog): Long {
        val values = ContentValues().apply {
            put("employeeId", log.employeeId)
            put("workDate", log.workDate)
            put("checkInTime", log.checkInTime)
            put("checkOutTime", log.checkOutTime)
            put("totalHours", log.totalHours)
            put("status", log.status)
        }
        return db.insert("WorkLog", null, values)
    }

    fun updateWorkLog(log: WorkLog): Int {
        val values = ContentValues().apply {
            put("checkOutTime", log.checkOutTime)
            put("totalHours", log.totalHours)
            put("status", log.status)
        }
        return db.update("WorkLog", values, "id = ?", arrayOf(log.id.toString()))
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
                    id = cursor.getInt(0),
                    employeeId = cursor.getInt(1),
                    workDate = cursor.getString(2),
                    checkInTime = cursor.getString(3),
                    checkOutTime = cursor.getString(4),
                    totalHours = cursor.getDouble(5),
                    status = cursor.getString(6)
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
                    id = cursor.getInt(0),
                    employeeId = cursor.getInt(1),
                    workDate = cursor.getString(2),
                    checkInTime = cursor.getString(3),
                    checkOutTime = cursor.getString(4),
                    totalHours = cursor.getDouble(5),
                    status = cursor.getString(6)
                )
            )
        }
        cursor.close()
        return list
    }
}
