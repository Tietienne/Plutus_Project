package com.example.plutus_project

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.plutus_project.database.NoteDatabaseHelper
import kotlinx.coroutines.currentCoroutineContext
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import kotlin.coroutines.coroutineContext

@RunWith(AndroidJUnit4::class)
class ExampleUnitTest {

    lateinit var instrumentationContext: Context
    private val DATABASE_NAME = "test_db"

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun addNotebook() {
        val db = resetAndGetDatabase()
        val values = ContentValues().apply {
            put("id", 1)
            put("name", "test")
        }
        val newRowId = db.insert("Notebook", null, values)
        assertNotEquals(newRowId, -1)

        val projection = arrayOf("id")
        val selection = "id = ?"
        val selectionArgs = arrayOf(newRowId.toString())
        val cursor = db.query(
            "Notebook",   // The table to query
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        cursor.use { c ->
            with(c) {
                while (moveToNext()) {
                    val itemId = getLong(getColumnIndexOrThrow("id"))
                    assertEquals(itemId, newRowId)
                }
            }
        }
    }

    private fun resetAndGetDatabase() : SQLiteDatabase {
        instrumentationContext.deleteDatabase(DATABASE_NAME)
        val dbHelper = NoteDatabaseHelper(instrumentationContext, DATABASE_NAME)
        return dbHelper.writableDatabase
    }
}