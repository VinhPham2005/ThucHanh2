package com.example.thuchanh2.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.thuchanh2.data.entity.Account

class AccountDao(private val db: SQLiteDatabase) {
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