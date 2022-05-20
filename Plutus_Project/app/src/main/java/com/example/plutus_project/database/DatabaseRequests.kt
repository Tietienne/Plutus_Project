package com.example.plutus_project.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase


class DatabaseRequests(context : Context) {
    private val DATABASE_NAME = "plutus_db"
    private val db = NoteDatabaseHelper(context, DATABASE_NAME).writableDatabase

    fun getAllNotebooks() : List<Int> {
        val notebooks = ArrayList<Int>()
        val selectQuery = "SELECT  * FROM Notebook"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()) {
                    val notebookId = Integer.parseInt(cursor.getString(0))
                    notebooks.add(notebookId)
                }
            }
        }
        return notebooks
    }
}