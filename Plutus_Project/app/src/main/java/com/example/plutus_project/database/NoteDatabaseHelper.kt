package com.example.plutus_project.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.plutus_project.items.Budget
import com.example.plutus_project.items.Label
import com.example.plutus_project.items.Notebook
import com.example.plutus_project.items.Transaction

class NoteDatabaseHelper(
    context: Context, DATABASE_NAME: String
) : SQLiteOpenHelper(context, DATABASE_NAME, null, CURRENT_VERSION) {

    companion object {
        const val CURRENT_VERSION = 1

        const val NOTEBOOK_TABLE = """CREATE TABLE Notebook(id INTEGER primary key autoincrement NOT NULL, name TEXT NOT NULL);"""
        const val OPERATION_TABLE = """CREATE TABLE Operation(id INTEGER primary key autoincrement NOT NULL, text TEXT, date TEXT, value REAL, currency TEXT, location TEXT, notebook_id INTEGER NOT NULL, FOREIGN KEY (notebook_id) REFERENCES Notebook (id) ON DELETE CASCADE);"""
        const val LABEL_TABLE = """CREATE TABLE Label(id INTEGER primary key autoincrement NOT NULL, text TEXT);"""
        const val OP_LAB_TABLE = """CREATE TABLE OpLab(operation_id INTEGER NOT NULL, label_id INTEGER NOT NULL, FOREIGN KEY (label_id) REFERENCES Label (id) ON DELETE CASCADE, FOREIGN KEY (operation_id) REFERENCES Operation (id) ON DELETE CASCADE);"""
        const val BUDGET_TABLE = """CREATE TABLE Budget(id INTEGER primary key autoincrement NOT NULL, value REAL, date TEXT, label_id INTEGER NOT NULL, notebook_id INTEGER NOT NULL, FOREIGN KEY (label_id) REFERENCES Label (id) ON DELETE CASCADE, FOREIGN KEY (notebook_id) REFERENCES Notebook (id) ON DELETE CASCADE);"""
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(NOTEBOOK_TABLE)
        db.execSQL(OPERATION_TABLE)
        db.execSQL(LABEL_TABLE)
        db.execSQL(OP_LAB_TABLE)
        db.execSQL(BUDGET_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // to be implemented if the schema changes from oldVersion to newVersion
        // we use ALTER TABLE SQL statements to transform the tables
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        if (db != null && !db.isReadOnly) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;")
        }
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

    fun removeNotebook(id : Int) {
        val writable_db = this.writableDatabase
        writable_db.execSQL("DELETE FROM Notebook WHERE id =$id")
    }

    fun getAllTransactionsFromNotebook(notebookId: Int): List<Transaction> {
        val readableDB = this.readableDatabase
        val transactions = ArrayList<Transaction>()
        val selectQuery = "SELECT * FROM Operation WHERE notebook_id = $notebookId"
        val cursor : Cursor = readableDB.rawQuery(selectQuery,null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()){
//id INTEGER primary key autoincrement NOT NULL, text TEXT, date TEXT, value REAL, currency TEXT, location TEXT, notebook_id INTEGER NOT NULL, FOREIGN KEY (notebook_id) REFERENCES Notebook (id) ON DELETE CASCADE);"""

                    val id = Integer.parseInt(cursor.getString(0))
                    val motif = cursor.getString(1)
                    val date = cursor.getString(2)
                    val amount = Integer.parseInt(cursor.getString(3))
//                    val amount = 0
                    val currency = cursor.getString(4)
                    val notebookId = Integer.parseInt(cursor.getString(6))
//                    val notebookId = 0
                    transactions.add(Transaction(id,date,amount,currency,motif,notebookId))
                }
            }
        }
        return transactions
    }

    fun addTransaction(dateTime: String, amount : Int, currency: String, text : String, notebookId : Int): Int{
        val writableDB = this.writableDatabase
        val values = ContentValues().apply {
//            date TEXT, value REAL, currency TEXT, location TEXT, notebook_id
            put("text",text)
            put("date",dateTime)
            put("value",amount)
            put("currency",currency)
            put("notebook_id",notebookId)
        }
        return writableDB.insert("Operation",null,values).toInt()
    }

    fun updateTransaction( id: Int, dateTime: String, amount : Int, currency: String, text : String, notebookId : Int):Int{
        val writableDB = this.writableDatabase
        val values = ContentValues().apply {
            put("text",text)
            put("date",dateTime)
            put("value",amount)
            put("currency",currency)
            put("notebook_id",notebookId)
        }
        return writableDB.update("Operation",values,"id=$id", null)
    }

    fun removeTransaction(id: Int){
        val writableDB = this.writableDatabase
        writableDB.execSQL("DELETE FROM Operation WHERE id = $id")
    }

    fun addLabel(text : String) : Int {
        val writable_db = this.writableDatabase
        val values = ContentValues().apply {
            put("text", text)
        }
        return writable_db.insert("Label", null, values).toInt()
    }

    fun removeLabel(id : Int) {
        val writable_db = this.writableDatabase
        writable_db.execSQL("DELETE FROM Label WHERE id =$id")
    }

    fun getLabel(id : Int) : Label {
        val readable_db = this.readableDatabase
        val selectQuery = "SELECT * FROM Label WHERE id=$id"
        val cursor: Cursor = readable_db.rawQuery(selectQuery, null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()) {
                    val labelId = Integer.parseInt(cursor.getString(0))
                    val labelText = cursor.getString(1)
                    return Label(labelId, labelText)
                }
            }
        }
        return Label(-1, "")
    }

    fun getLabel(text : String) : Label {
        val readable_db = this.readableDatabase
        val selectQuery = "SELECT * FROM Label WHERE text='$text'"
        val cursor: Cursor = readable_db.rawQuery(selectQuery, null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()) {
                    val labelId = Integer.parseInt(cursor.getString(0))
                    val labelText = cursor.getString(1)
                    return Label(labelId, labelText)
                }
            }
        }
        return Label(-1, "")
    }

    fun getAllLabels() : List<Label> {
        val readable_db = this.readableDatabase
        val labels = ArrayList<Label>()
        val selectQuery = "SELECT * FROM Label"
        val cursor: Cursor = readable_db.rawQuery(selectQuery, null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()) {
                    val labelId = Integer.parseInt(cursor.getString(0))
                    val labelText = cursor.getString(1)
                    labels.add(Label(labelId, labelText))
                }
            }
        }
        return labels
    }

    fun getAllLabelsFromNotebook(notebookId: Int) : List<Label> {
        val readable_db = this.readableDatabase
        val labels = ArrayList<Label>()
        val selectQuery = "SELECT * FROM Label INNER JOIN OpLab on label_id = Label.id INNER JOIN Operation on operation_id = Operation.id WHERE notebook_id = $notebookId"
        val cursor: Cursor = readable_db.rawQuery(selectQuery, null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()) {
                    val labelId = Integer.parseInt(cursor.getString(0))
                    val labelText = cursor.getString(1)
                    labels.add(Label(labelId, labelText))
                }
            }
        }
        return labels
    }

    fun getAllLabelsFromTransaction(transaction : Transaction) : List<Label> {
        if (transaction.id == -1) {
            return ArrayList()
        }
        val readable_db = this.readableDatabase
        val labels = ArrayList<Label>()
        val selectQuery = "SELECT * FROM Label INNER JOIN OpLab on id = label_id WHERE operation_id = ${transaction.id}"
        val cursor: Cursor = readable_db.rawQuery(selectQuery, null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()) {
                    val labelId = Integer.parseInt(cursor.getString(0))
                    val labelText = cursor.getString(1)
                    labels.add(Label(labelId, labelText))
                }
            }
        }
        return labels
    }

    fun addBudget(value : Float, date : String, label : Label, notebookId: Int) : Int {
        val writable_db = this.writableDatabase
        val values = ContentValues().apply {
            put("value", value)
            put("date", date)
            put("label_id", label.id)
            put("notebook_id", notebookId)
        }
        return writable_db.insert("Budget", null, values).toInt()
    }

    fun getAllBudgets() : List<Budget> {
        val readable_db = this.readableDatabase
        val budgets = ArrayList<Budget>()
        val selectQuery = "SELECT * FROM Budget"
        val cursor: Cursor = readable_db.rawQuery(selectQuery, null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()) {
                    val budgetId = Integer.parseInt(cursor.getString(0))
                    val budgetValue = cursor.getString(1).toFloat()
                    val budgetDate = cursor.getString(2)
                    val label = getLabel(Integer.parseInt(cursor.getString(3)))
                    budgets.add(Budget(budgetId, budgetValue, budgetDate, label))
                }
            }
        }
        return budgets
    }

    fun removeBudget(id : Int) {
        val writable_db = this.writableDatabase
        writable_db.execSQL("DELETE FROM Budget WHERE id =$id")
    }

    fun addLabelToTransaction(operation_id : Int, text : String) : Int {
        var label_id = getLabel(text).id
        if (label_id == -1) {
            label_id = addLabel(text)
        }
        if (isLabelOfTransaction(operation_id, label_id)) {
            return -1
        }
        val writable_db = this.writableDatabase
        val values = ContentValues().apply {
            put("operation_id", operation_id)
            put("label_id", label_id)
        }
        return writable_db.insert("OpLab", null, values).toInt()
    }

    fun isLabelOfTransaction(operation_id: Int, label_id : Int) : Boolean {
        val readable_db = this.readableDatabase
        val selectQuery = "SELECT * FROM OpLab WHERE operation_id = $operation_id AND label_id = $label_id"
        val cursor: Cursor = readable_db.rawQuery(selectQuery, null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()) {
                    return true
                }
            }
        }
        return false
    }

    fun searchTransactionsFromNotebook(notebookId: Int, dateBegin : String, dateEnd : String, amountMin : Int, amountMax : Int, motif : String, labels : List<Label>) : List<Transaction> {
        val readable_db = this.readableDatabase
        val transactions = ArrayList<Transaction>()
        val selectQuery = if (labels.isNotEmpty()) {
            "SELECT * FROM Operation INNER JOIN OpLab on operation_id = id WHERE notebook_id = $notebookId AND date <= '$dateEnd' AND date >= '$dateBegin' AND value <= '$amountMax' AND value >= '$amountMin' AND text LIKE '%$motif%' AND label_id IN " + labels.joinToString(", ", "(", ")")
        } else {
            "SELECT * FROM Operation WHERE notebook_id = $notebookId AND date <= '$dateEnd' AND date >= '$dateBegin' AND value <= '$amountMax' AND value >= '$amountMin'"
        }
        val cursor: Cursor = readable_db.rawQuery(selectQuery, null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()) {
                    val id = Integer.parseInt(cursor.getString(0))
                    val text = cursor.getString(1)
                    val date = cursor.getString(2)
                    val amount = Integer.parseInt(cursor.getString(3))
                    val currency = cursor.getString(4)
                    val notebookId = Integer.parseInt(cursor.getString(6))
                    transactions.add(Transaction(id,date,amount,currency,text,notebookId))
                }
            }
        }
        return transactions
    }

    fun removeLabelOfTransaction(label_id : Int, operation_id: Int) {
        val writable_db = this.writableDatabase
        writable_db.execSQL("DELETE FROM OpLab WHERE label_id =$label_id AND operation_id = $operation_id")
    }

    fun getTransactionsFromNotebook(notebookId: Int) : List<Transaction> {
        val readableDB = this.readableDatabase
        val transactions = ArrayList<Transaction>()
        val selectQuery = "SELECT * FROM Operation WHERE notebook_id = $notebookId"
        val cursor : Cursor = readableDB.rawQuery(selectQuery,null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()){
                    val id = Integer.parseInt(cursor.getString(0))
                    val motif = cursor.getString(1)
                    val date = cursor.getString(2)
                    val amount = Integer.parseInt(cursor.getString(3))
                    val currency = cursor.getString(4)
                    val notebook_id = Integer.parseInt(cursor.getString(6))
                    transactions.add(Transaction(id,date,amount,currency,motif,notebook_id))
                }
            }
        }
        return transactions
    }

    fun getAllBudgetsFromNotebook(notebookId: Int) : List<Budget> {
        val readableDB = this.readableDatabase
        val budgets = ArrayList<Budget>()
        val selectQuery = "SELECT * FROM Budget WHERE notebook_id = $notebookId"
        val cursor : Cursor = readableDB.rawQuery(selectQuery,null)
        cursor.use { c ->
            with(c) {
                while (moveToNext()){
                    val id = Integer.parseInt(cursor.getString(0))
                    val value = cursor.getString(1).toFloat()
                    val date = cursor.getString(2)
                    val label = getLabel(Integer.parseInt(cursor.getString(3)))
                    budgets.add(Budget(id, value, date, label))
                }
            }
        }
        return budgets
    }
}