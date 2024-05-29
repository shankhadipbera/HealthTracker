package com.shankha.healthtracker

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "contacts.db"
        const val TABLE_NAME = "contacts"
        const val COLUMN_ID = "ID"
        const val COLUMN_NAME = "NAME"
        const val COLUMN_MOBILE = "MOBILE"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_MOBILE TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addContact(name: String, mobile: String): Boolean {
        if (contactExists(mobile)) {
            return false
        }
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_MOBILE, mobile)
        }
        val result = db.insert(TABLE_NAME, null, contentValues)
        return result != -1L
    }

    private fun contactExists(mobile: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM $TABLE_NAME WHERE $COLUMN_MOBILE = ?", arrayOf(mobile))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getAllContacts(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
    fun deleteContact(name: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_NAME = ?", arrayOf(name))
        return result > 0
    }
}