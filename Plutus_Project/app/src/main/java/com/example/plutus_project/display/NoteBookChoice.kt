package com.example.plutus_project.display

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.items.Label
import com.example.plutus_project.items.Notebook
import java.io.File

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun NoteBookChoice(db : NoteDatabaseHelper, showNote : (Notebook) -> Unit) {
    val openCreate = remember { mutableStateOf(false) }
    val notebooks = remember { mutableStateOf(db.getAllNotebooks())}
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Plutus", modifier = Modifier.fillMaxWidth(), fontSize = 30.sp, textAlign = TextAlign.Center)
        Button(onClick = { openCreate.value = true }, modifier = Modifier.align(Alignment.End)) {
            Text(text = "Add")
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = notebooks.value, itemContent = { item ->
                NotebookDisplay(item, db, notebooks, showNote)
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
                    Button(onClick = { db.addNotebook(text.text); notebooks.value = db.getAllNotebooks(); openCreate.value = false }, enabled = notebooks.value.stream().noneMatch {n -> n.name == text.text }) {
                        Text(text = "Create")
                    }
                }
            }
        }
    }
}

@Composable
fun NotebookDisplay(notebook: Notebook, db : NoteDatabaseHelper, notebooks : MutableState<List<Notebook>>, showNote : (Notebook) -> Unit) {
    val openDuplicate = remember { mutableStateOf(false) }
    val openRemove = remember { mutableStateOf(false) }
    val openExport = remember { mutableStateOf(false) }
    val labels = remember { mutableStateListOf<Label>() }
    Row(Modifier.fillMaxSize().border(1.dp, Color.Black).clickable { showNote(notebook) }) {
        Text(text = notebook.name)
        Button(onClick = { openExport.value = true }) {
            Text(text = "Export")
        }
        Button(onClick = { openDuplicate.value = true }) {
            Text(text = "Duplicate")
        }
        Button(onClick = { openRemove.value = true }) {
            Text(text = "Remove")
        }
    }
    val dialogWidth = 400.dp
    val dialogHeight = 400.dp
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
                    Button(onClick = {
                        val transactions = db.getTransactionsFromNotebook(notebook.id)
                        val budgets = db.getAllBudgetsFromNotebook(notebook.id)
                        val newNotebookId = db.addNotebook(text.text)
                        for (transaction in transactions) {
                            val labels = db.getAllLabelsFromTransaction(transaction)
                            val newTransactionId = db.addTransaction(transaction.dateTime, transaction.amount, transaction.currency, transaction.text, newNotebookId)
                            for (label in labels) {
                                db.addLabelToTransaction(newTransactionId, label.text)
                            }
                        }
                        for (budget in budgets) {
                            db.addBudget(budget.value, budget.date, budget.label, newNotebookId)
                        }
                        notebooks.value = db.getAllNotebooks()
                        openDuplicate.value = false }) {
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
    if (openExport.value) {
        Dialog(onDismissRequest = { openExport.value = false }) {
            Box(
                Modifier
                    .size(dialogWidth, dialogHeight)
                    .background(Color.White)) {
                Column(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxWidth()) {
                        Text("Choose the labeled transactions to export : ")
                    }
                    Box(Modifier.fillMaxWidth().weight(2f/3f)) {
                        chooseLabelToSearchPage(
                            db,
                            notebook,
                            labels,
                            { if (labels.contains(it)) labels.remove(it) else labels.add(it) }) {
                            openExport.value = false
                        }
                    }
                    Box(Modifier.fillMaxWidth().weight(1f/3f)) {
                        Button(onClick = { exportFile(notebook.name); openExport.value = false }) {
                            Text(text = "Export : Not working for now")
                        }
                    }
                }
            }
        }
    }
}

fun exportFile(name : String) {
    val file = File("$name.txt")
    //file.createNewFile() // Not Working
}