package com.example.plutus_project

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.example.plutus_project.database.DatabaseRequests
import com.example.plutus_project.items.Notebook

@Composable
fun NoteBookChoice(db : DatabaseRequests) {
    val openCreate = remember { mutableStateOf(false) }
    val notebooks = remember { mutableListOf(db.getAllNotebooks())}
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Plutus", modifier = Modifier.fillMaxWidth(), fontSize = 30.sp, textAlign = TextAlign.Center)
        Button(onClick = { openCreate.value = true }, modifier = Modifier.align(Alignment.End)) {
            Text(text = "Add")
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = notebooks, itemContent = { item ->
                Text(text = "test")
            })
        }
    }
    val dialogWidth = 200.dp
    val dialogHeight = 200.dp
    var text by remember { mutableStateOf(TextFieldValue("")) }
    if (openCreate.value) {
        Dialog(onDismissRequest = { openCreate.value = false }) {
            Box(
                Modifier
                    .size(dialogWidth, dialogHeight)
                    .background(Color.White)) {
                Column(Modifier.fillMaxSize()) {
                    TextField(value = text, onValueChange = {newText -> text = newText},
                        label = { Text(text = "Notebook name") }, placeholder = { Text(text = "Write notebook's name") })
                    Button(onClick = { val newId = db.addNotebook(text.text); notebooks.add(listOf(Notebook(newId, text.text))); openCreate.value = false }) {
                        Text(text = "Create")
                    }
                }
            }
        }
    }
}