package com.example.plutus_project.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.plutus_project.items.Notebook

class NoteDatabaseHelper(
    context: Context, DATABASE_NAME: String
) : SQLiteOpenHelper(context, DATABASE_NAME, null, CURRENT_VERSION) {

    companion object {
        const val CURRENT_VERSION = 1

        const val NOTEBOOK_TABLE = """CREATE TABLE Notebook(id INTEGER primary key autoincrement NOT NULL, name TEXT NOT NULL);"""
        const val OPERATION_TABLE = """CREATE TABLE Operation(id INTEGER primary key autoincrement NOT NULL, text TEXT, date TEXT, value REAL, currency TEXT, location TEXT, notebook_id INTEGER NOT NULL, FOREIGN KEY (notebook_id) REFERENCES Notebook (id));"""
        const val LABEL_TABLE = """CREATE TABLE Label(id INTEGER primary key autoincrement NOT NULL, text TEXT, operation_id INTEGER NOT NULL, FOREIGN KEY (operation_id) REFERENCES Operation (id));"""
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(NOTEBOOK_TABLE)
        db.execSQL(OPERATION_TABLE)
        db.execSQL(LABEL_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // to be implemented if the schema changes from oldVersion to newVersion
        // we use ALTER TABLE SQL statements to transform the tables
    }

    fun getAllNotebooks() : List<Notebook> {
        val readable_db = this.readableDatabase
        val notebooks = ArrayList<Notebook>()
        val selectQuery = "SELECT * FROM Notebook"
        val cursor: Cursor = readable_db.rawQuery(selectQuery, null)
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
        val writable_db = this.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
        }
        return writable_db.insert("Notebook", null, values).toInt()
    }
}