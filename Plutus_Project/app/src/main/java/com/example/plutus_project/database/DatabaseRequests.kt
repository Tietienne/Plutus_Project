package com.example.plutus_project.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.plutus_project.items.Notebook


class DatabaseRequests(context : Context) {
    private val DATABASE_NAME = "plutus_db"
    private val db = NoteDatabaseHelper(context, DATABASE_NAME).writableDatabase

    fun getAllNotebooks() : List<Notebook> {
        val notebooks = ArrayList<Notebook>()
        val selectQuery = "SELECT * FROM Notebook"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()) {
                    val notebookId = Integer.parseInt(cursor.getString(0))
                    val notebookName = cursor.getString(1)
                    notebooks.add(Notebook(notebookId, notebookName))
                }
            }
        }
        return notebooks
    }

    fun addNotebook(name : String) : Int {
        val values = ContentValues().apply {
            put("name", "test")
        }
        return db.insert("Notebook", null, values).toInt()
    }
}