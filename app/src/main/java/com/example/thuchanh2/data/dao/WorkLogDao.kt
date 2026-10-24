package com.example.thuchanh2.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.thuchanh2.data.entity.WorkLog

class WorkLogDao(private val db: SQLiteDatabase) {
    fun getWorkLogByDate(empId: Int, date: String): WorkLog? {
        val cursor = db.query(
            "WorkLog",
            null,
            "employeeId = ? AND workDate = ?",
            arrayOf(empId.toString(), date),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val log = WorkLog(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                employeeId = cursor.getInt(cursor.getColumnIndexOrThrow("employeeId")),
                workDate = cursor.getString(cursor.getColumnIndexOrThrow("workDate")),
                checkInTime = cursor.getString(cursor.getColumnIndexOrThrow("checkInTime")),
                checkOutTime = cursor.getString(cursor.getColumnIndexOrThrow("checkOutTime")),
                totalHours = cursor.getDouble(cursor.getColumnIndexOrThrow("totalHours")),
                status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
            )
            cursor.close()
            log
        } else {
            cursor.close()
            null
        }
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
}