package com.example.plutus_project

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.items.Notebook

@Composable
fun NoteBookChoice(db : NoteDatabaseHelper) {
    val openCreate = remember { mutableStateOf(false) }
    val notebooks = remember { mutableStateOf(db.getAllNotebooks())}
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Plutus", modifier = Modifier.fillMaxWidth(), fontSize = 30.sp, textAlign = TextAlign.Center)
        Button(onClick = { openCreate.value = true }, modifier = Modifier.align(Alignment.End)) {
            Text(text = "Add")
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = notebooks.value, itemContent = { item ->
                NotebookDisplay(item, db, notebooks)
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
                    Button(onClick = { val newId = db.addNotebook(text.text); notebooks.value = db.getAllNotebooks(); openCreate.value = false }) {
                        Text(text = "Create")
                    }
                }
            }
        }
    }
}

@Composable
fun NotebookDisplay(notebook: Notebook, db : NoteDatabaseHelper, notebooks : MutableState<List<Notebook>>) {
    val openDuplicate = remember { mutableStateOf(false) }
    val openRemove = remember { mutableStateOf(false) }
    Row(Modifier.fillMaxSize().border(1.dp, Color.Black).clickable { /* TODO : Change window */ }) {
        Text(text = notebook.name)
        Button(onClick = { openDuplicate.value = true }) {
            Text(text = "Duplicate")
        }
        Button(onClick = { openRemove.value = true }) {
            Text(text = "Remove")
        }
    }
    val dialogWidth = 200.dp
    val dialogHeight = 200.dp
    var text by remember { mutableStateOf(TextFieldValue("")) }
    if (openDuplicate.value) {
        Dialog(onDismissRequest = { openDuplicate.value = false }) {
            Box(
                Modifier
                    .size(dialogWidth, dialogHeight)
                    .background(Color.White)) {
                Column(Modifier.fillMaxSize()) {
                    TextField(value = text, onValueChange = {newText -> text = newText},
                        label = { Text(text = "Duplicated Notebook name") }, placeholder = { Text(text = "Write notebook's name") })
                    Button(onClick = { /* TODO */ openDuplicate.value = false }) {
                        Text(text = "Duplicate")
                    }
                }
            }
        }
    }
    if (openRemove.value) {
        Dialog(onDismissRequest = { openRemove.value = false }) {
            Box(
                Modifier
                    .size(dialogWidth, dialogHeight)
                    .background(Color.White)) {
                Column(Modifier.fillMaxSize()) {
                    Text("Do you really want to remove this Notebook : ${notebook.name}")
                    Button(onClick = { db.removeNotebook(notebook.id); notebooks.value = db.getAllNotebooks() ; openRemove.value = false }) {
                        Text(text = "Remove", color = Color.Red)
                    }
                }
            }
        }
    }
}