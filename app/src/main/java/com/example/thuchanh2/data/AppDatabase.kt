package com.example.thuchanh2.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.thuchanh2.data.dao.AccountDao
import com.example.thuchanh2.data.dao.EmployeeDao
import com.example.thuchanh2.data.dao.SalaryDao
import com.example.thuchanh2.data.dao.WorkLogDao

class AppDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context, "employee_salary.db", null, 1) {

    // Khởi tạo 4 DAO riêng biệt
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

        // Tự động tạo tài khoản Admin mặc định: user = admin, pass = 123
        db.execSQL("INSERT INTO Account (username, password, role) VALUES ('admin', '123', 'ADMIN')")
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        // Đảm bảo có tài khoản Admin
        val adminCursor = db.rawQuery("SELECT id FROM Account WHERE username = 'admin'", null)
        if (!adminCursor.moveToFirst()) {
            db.execSQL("INSERT INTO Account (username, password, role) VALUES ('admin', '123', 'ADMIN')")
        }
        adminCursor.close()

        // Đảm bảo có sẵn 1 tài khoản nhân viên mẫu để test ngay lập tức: NV01 / 123
        val empCursor = db.rawQuery("SELECT id FROM Account WHERE username = 'NV01'", null)
        if (!empCursor.moveToFirst()) {
            db.execSQL("INSERT INTO Account (username, password, role) VALUES ('NV01', '123', 'EMPLOYEE')")
            val idCursor = db.rawQuery("SELECT id FROM Account WHERE username = 'NV01'", null)
            if (idCursor.moveToFirst()) {
                val accId = idCursor.getInt(0)
                db.execSQL("INSERT INTO Employee (code, fullname, email, phone, hourlyRate, accountId) VALUES ('NV01', 'Nguyễn Văn A', 'nva@company.com', '0912345678', 50000.0, $accId)")
            }
            idCursor.close()
        }
        empCursor.close()
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
