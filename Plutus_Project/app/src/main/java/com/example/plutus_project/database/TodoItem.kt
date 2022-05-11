package com.example.plutus_project.database

@Entity(tableName = "my_todo_list")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    var itemId: Long = 0L,

    @ColumnInfo(name = "item_name")
    val itemName: String,

    @ColumnInfo(name = "is_completed")
    var isDone: Boolean = false
)
