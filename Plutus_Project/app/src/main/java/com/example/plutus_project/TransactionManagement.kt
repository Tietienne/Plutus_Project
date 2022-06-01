package com.example.plutus_project

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val transactions = remember { mutableStateOf(db.getAllTransactions()) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.LightGray)) {
        Text(text = "Your Transactions", modifier = Modifier.fillMaxWidth(), fontSize = 24.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(15.dp))
        Button(onClick = { onCreateTransaction.value = true }, modifier = Modifier.align(Alignment.End)) {
            Text(text = "New Transaction")
        }
        Spacer(modifier = Modifier.height(15.dp))
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionDisplay(
    transaction: Transaction,
    db: NoteDatabaseHelper,
    transactions: MutableState<List<Transaction>>
) {
    //TODO("Not yet implemented")
    val onDuplicate = remember{ mutableStateOf(false)}
    val onRemove = remember{ mutableStateOf(false)}
    val onModifier = remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxSize()
            .padding(0.dp, 5.dp)
            .background(Color.White)
            .clickable { onModifier.value = true }) {
        Box(modifier = Modifier
            .fillMaxHeight()
            .weight(1 / 7f)){
            Text(text = transaction.id.toString())

        }
        Box(modifier = Modifier
            .fillMaxHeight()
            .weight(3 / 7f)){
            Column(
            ) {
                Text(text = transaction.text.uppercase())
                Text(text = transaction.dateTime)
            }
        }
        Box(modifier = Modifier
            .fillMaxHeight()
            .weight(1 / 7f)){
            Text(text = transaction.amount.toString() + " " + transaction.currency)

        }

        Box(modifier = Modifier
            .fillMaxHeight()
            .weight(1 / 7f)){
            Button(onClick = { onDuplicate.value = true }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Duplicate")
            }
        }

        Box(modifier = Modifier
            .fillMaxHeight()
            .weight(1 / 7f)){
            Button(onClick = { onRemove.value = true }) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "remove")
            }
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


    if (onModifier.value){
        Dialog(onDismissRequest = { onModifier.value = false }) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                ModifyTransaction(transaction, db,
                    { state -> onModifier.value = state },
                    { values -> transactions.value = values})
            }
        }
    }

}


@Composable
fun ModifyTransaction(transaction: Transaction, db: NoteDatabaseHelper, onConfirm: (Boolean) -> Unit, onUpdate: (List<Transaction>) -> Unit){

    var amount by remember { mutableStateOf(transaction.amount) }
    var currency by remember { mutableStateOf(transaction.currency) }
    var date by remember { mutableStateOf(transaction.dateTime) }
    var motif by remember { mutableStateOf(transaction.text)}

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(6f)) {
                    drawAmount(amount.toString(), onAmountChange = {
                        amount = if (it == "")
                            0
                        else
                            it.toInt()
                    })
                }
                Column(Modifier.weight(4f)) {
                    drawCurrency(currency, onCurrencyChange = { currency = it })
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            timePicker(date) {
                date = it
            }
        }

        Box(modifier = Modifier.fillMaxWidth()){
            drawMotif(motif,onMotifChange = {motif = it})
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.Center){
            val context = LocalContext.current
            Button(onClick = {
                var result = db.updateTransaction(transaction.id,date, amount, currency, motif, transaction.notebookId)
                Toast.makeText(context,"$date + $amount + $currency + $motif + $result", Toast.LENGTH_SHORT).show()
                onUpdate(db.getAllTransactions())
                onConfirm(false)
            }) {
                Text(text = "Modifier")
            }
        }
    }

}
