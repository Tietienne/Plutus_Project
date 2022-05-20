package com.example.plutus_project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.plutus_project.database.DatabaseRequests

@Composable
fun NoteBookChoice(db : DatabaseRequests) {
    println(db.getAllNotebooks())
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(5) { index ->
            Text(text = "Hello $index!")
        }
    }
}