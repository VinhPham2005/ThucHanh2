package com.example.myapplication.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.data.dao.AccountDao
import com.example.myapplication.data.dao.EmployeeDao
import com.example.myapplication.data.dao.SalaryDao
import com.example.myapplication.data.dao.WorkLogDao

class AppDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context, "employee_salary.db", null, 1) {

    fun accountDao(): AccountDao = AccountDao(writableDatabase)
    fun employeeDao(): EmployeeDao = EmployeeDao(writableDatabase)
    fun workLogDao(): WorkLogDao = WorkLogDao(writableDatabase)
    fun salaryDao(): SalaryDao = SalaryDao(writableDatabase)

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE Account (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE Employee (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                code TEXT NOT NULL UNIQUE,
                fullname TEXT NOT NULL,
                email TEXT,
                phone TEXT,
                hourlyRate REAL NOT NULL,
                accountId INTEGER NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE WorkLog (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employeeId INTEGER NOT NULL,
                workDate TEXT NOT NULL,
                checkInTime TEXT NOT NULL,
                checkOutTime TEXT,
                totalHours REAL DEFAULT 0.0,
                status TEXT NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE Salary (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employeeId INTEGER NOT NULL,
                month INTEGER NOT NULL,
                year INTEGER NOT NULL,
                totalHours REAL DEFAULT 0.0,
                overtimeHours REAL DEFAULT 0.0,
                totalSalary REAL DEFAULT 0.0,
                status TEXT NOT NULL
            )
        """)

        db.execSQL("INSERT INTO Account (username, password, role) VALUES ('admin', '123', 'ADMIN')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Account")
        db.execSQL("DROP TABLE IF EXISTS Employee")
        db.execSQL("DROP TABLE IF EXISTS WorkLog")
        db.execSQL("DROP TABLE IF EXISTS Salary")
        onCreate(db)
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDatabase(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}