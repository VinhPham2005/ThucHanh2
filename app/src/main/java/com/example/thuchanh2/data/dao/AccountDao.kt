package com.example.thuchanh2.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.thuchanh2.data.entity.Account

class AccountDao(private val db: SQLiteDatabase) {
    fun login(user: String, pass: String): Account? {
        val cursor = db.rawQuery(
            "SELECT * FROM Account WHERE username = ? AND password = ? LIMIT 1",
            arrayOf(user, pass)
        )
        var account: Account? = null
        if (cursor.moveToFirst()) {
            account = Account(
                id = cursor.getInt(0),
                username = cursor.getString(1),
                password = cursor.getString(2),
                role = cursor.getString(3)
            )
        }
        cursor.close()
        return account
    }

    fun insertAccount(account: Account): Long {
        val values = ContentValues().apply {
            put("username", account.username)
            put("password", account.password)
            put("role", account.role)
        }
        return db.insert("Account", null, values)
    }

    fun deleteAccount(accountId: Int): Int {
        return db.delete("Account", "id = ?", arrayOf(accountId.toString()))
    }
}
