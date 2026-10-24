package com.example.thuchanh2.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.thuchanh2.data.entity.Account

class AccountDao(private val db: SQLiteDatabase) {

    fun login(username: String, password: String): Account? {
        val cursor = db.rawQuery(
            "SELECT * FROM Account WHERE username = ? AND password = ? LIMIT 1",
            arrayOf(username, password)
        )
        var account: Account? = null
        if (cursor.moveToFirst()) {
            account = Account(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
            )
        }
        cursor.close()
        return account
    }

    fun getAccountById(accountId: Int): Account? {
        val cursor = db.rawQuery(
            "SELECT * FROM Account WHERE id = ? LIMIT 1",
            arrayOf(accountId.toString())
        )
        var account: Account? = null
        if (cursor.moveToFirst()) {
            account = Account(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
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
