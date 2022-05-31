package com.example.plutus_project

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
import com.example.plutus_project.items.Notebook
import com.example.plutus_project.items.Transaction

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionManagement(db : NoteDatabaseHelper, notebook : Notebook){

    val onCreateTransaction = remember{ mutableStateOf(false)}
    //FIXME getAllTransactions()
    val transactions = remember { mutableStateOf(db.getAllTransactions()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Transaction Management", modifier = Modifier.fillMaxWidth(), fontSize = 30.sp, textAlign = TextAlign.Center)
        Button(onClick = { onCreateTransaction.value = true }, modifier = Modifier.align(Alignment.End)) {
            Text(text = "Add")
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = transactions.value, itemContent = { item ->
                TransactionDisplay(item, db, transactions)
            })
        }
    }

    val dialogWidth = 400.dp
    val dialogHeight = 600.dp

    if (onCreateTransaction.value) {

        Dialog(onDismissRequest = { onCreateTransaction.value = false }) {

            Box(
                Modifier
                    .size(dialogWidth, dialogHeight)
                    .background(Color.White)
            ) {
                var transaction = Transaction(-1, "Aujourd'hui", 0, "EUR", "", notebook.id)
                TransactionEditor(
                    transaction = transaction,
                    onTransactionChange = { transaction = it },
                    db
                ) {
                    onCreateTransaction.value = false
                    transactions.value = db.getAllTransactions()
                }
            }
        }
    }

}

@Composable
fun TransactionDisplay(
    transaction: Transaction,
    db: NoteDatabaseHelper,
    transactions: MutableState<List<Transaction>>
) {
    //TODO("Not yet implemented")
    val onDuplicate = remember{ mutableStateOf(false)}
    val onRemove = remember{ mutableStateOf(false)}

    Row(Modifier.fillMaxSize().border(1.dp, Color.Black)) {
        Text(text = transaction.id.toString())
        Button(onClick = { onDuplicate.value = true }) {
            Text(text = "Duplicate")
        }
        Button(onClick = { onRemove.value = true }) {
            Text(text = "Remove")
        }
    }
    val dialogWidth = 200.dp
    val dialogHeight = 200.dp
    var text by remember { mutableStateOf(TextFieldValue("")) }
    if (onDuplicate.value) {
        Dialog(onDismissRequest = { onDuplicate.value = false }) {
            Box(
                Modifier
                    .size(dialogWidth, dialogHeight)
                    .background(Color.White)) {
                Column(Modifier.fillMaxSize()) {
                    TextField(value = text, onValueChange = {newText -> text = newText},
                        label = { Text(text = "Duplicated Notebook name") }, placeholder = { Text(text = "Write notebook's name") })
                    Button(onClick = { /* TODO : duplicate notebook */ onDuplicate.value = false }) {
                        Text(text = "Duplicate")
                    }
                }
            }
        }
    }

    if (onRemove.value) {
        Dialog(onDismissRequest = { onRemove.value = false }) {
            Box(
                Modifier
                    .size(dialogWidth, dialogHeight)
                    .background(Color.White)) {
                Column(Modifier.fillMaxSize()) {
                    Text("Do you really want to remove this Notebook : ${transaction.id.toString()}")
                    Button(onClick = { db.removeNotebook(transaction.id); transactions.value = db.getAllTransactions() ; onRemove.value = false }) {
                        Text(text = "Remove", color = Color.Red)
                    }
                }
            }
        }
    }
}
