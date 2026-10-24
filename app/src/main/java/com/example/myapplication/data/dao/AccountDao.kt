package com.example.myapplication.data.dao

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.myapplication.data.entity.Account

class AccountDao(private val db: SQLiteDatabase) {

    fun login(username: String, pass: String): Account? {
        val cursor = db.rawQuery(
            "SELECT * FROM Account WHERE username = ? AND password = ? LIMIT 1",
            arrayOf(username, pass)
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

    fun getAccountById(id: Int): Account? {
        val cursor = db.rawQuery(
            "SELECT * FROM Account WHERE id = ? LIMIT 1",
            arrayOf(id.toString())
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

    fun getAllAccounts(): List<Account> {
        val list = mutableListOf<Account>()
        val cursor = db.rawQuery("SELECT * FROM Account ORDER BY id DESC", null)
        while (cursor.moveToNext()) {
            list.add(
                Account(
                    id = cursor.getInt(0),
                    username = cursor.getString(1),
                    password = cursor.getString(2),
                    role = cursor.getString(3)
                )
            )
        }
        cursor.close()
        return list
    }
}