package com.example.plutus_project.display

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.items.Budget
import com.example.plutus_project.items.BudgetState
import com.example.plutus_project.items.Label
import com.example.plutus_project.items.Notebook
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun budgetPageState(db : NoteDatabaseHelper, notebook: Notebook) {
    val time = LocalDateTime.now()
    val currentDate = "${time.dayOfMonth}/${time.monthValue}/${time.year}"

    var budgetState by rememberSaveable { mutableStateOf(BudgetState.ADDING_BUDGET) }
    var label by remember { mutableStateOf(Label(0, "")) }
    var amount by remember { mutableStateOf(0f) }
    var date by remember { mutableStateOf(currentDate) }
    when(budgetState) {
        BudgetState.ADDING_BUDGET -> budgetPage(db, label, notebook, amount, date, { budgetState = BudgetState.CHOOSING_LABEL }, {amount = it}, {date = it})
        BudgetState.CHOOSING_LABEL -> chooseLabelPage(db, notebook) { label = it; budgetState = BudgetState.ADDING_BUDGET }
    }
}

@Composable
fun budgetPage(db : NoteDatabaseHelper, label : Label, notebook: Notebook, amount : Float, date : String, chooseLabel: () -> Unit, changeAmount: (Float) -> Unit, changeDate: (String) -> Unit) {
    val budgets = remember { mutableStateOf(db.getAllBudgetsFromNotebook(notebook.id)) }
    Column(Modifier.fillMaxSize()) {
        newBudget(db, label, notebook, budgets, amount, date, chooseLabel, changeAmount, changeDate)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = budgets.value, itemContent = { item ->
                budgetDisplay(item, db, notebook, budgets)
            })
        }
    }
}

@Composable
fun newBudget(db : NoteDatabaseHelper, label : Label, notebook: Notebook, budgets : MutableState<List<Budget>>, amount : Float, date : String, chooseLabel : () -> Unit, changeAmount: (Float) -> Unit, changeDate: (String) -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        Button(onClick = { chooseLabel() }) {
            Text(text = if (label.text != "") label.text else "Choose label")
        }
        TextField(value = "$amount", onValueChange = {
            changeAmount(try {
                it.toFloat()
            } catch (e : NumberFormatException) {
                0f
            })
        }, Modifier.background(Color.Transparent),
            placeholder = { Text(text = "Amount",color = Color.Gray) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
    Row(Modifier.fillMaxWidth()) {
        timePicker(date, changeDate)
    }
    Row(Modifier.fillMaxWidth()) {
        Button(
            onClick = { db.addBudget(amount, date, label, notebook.id); budgets.value = db.getAllBudgetsFromNotebook(notebook.id) },
            enabled = label.text != ""
        ) {
            Text(text = "Add")
        }
    }
}

@Composable
fun budgetDisplay(item : Budget, db : NoteDatabaseHelper, notebook: Notebook, budgets : MutableState<List<Budget>>) {
    val openRemove = remember { mutableStateOf(false) }
    Row(Modifier.fillMaxSize().border(1.dp, Color.Black)) {
        Text(text = "${item.label.text} : ${item.value} until ${item.date}")
        Button(onClick = { openRemove.value = true }) {
            Text(text = "Remove")
        }
    }
    val dialogWidth = 200.dp
    val dialogHeight = 200.dp
    if (openRemove.value) {
        Dialog(onDismissRequest = { openRemove.value = false }) {
            Box(
                Modifier
                    .size(dialogWidth, dialogHeight)
                    .background(Color.White)) {
                Column(Modifier.fillMaxSize()) {
                    Text("Do you really want to remove this Budget : ${item.label.text} : ${item.value}")
                    Button(onClick = { db.removeBudget(item.id); budgets.value = db.getAllBudgetsFromNotebook(notebook.id) ; openRemove.value = false }) {
                        Text(text = "Remove", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun chooseLabelPage(db : NoteDatabaseHelper, notebook: Notebook, chooseLabel: (Label) -> Unit) {
    var text by remember { mutableStateOf("") }
    val labels = remember { mutableStateOf(db.getAllLabelsFromNotebook(notebook.id)) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth()) {
            TextField(value = text, onValueChange = { text = it }, Modifier.background(Color.Transparent),
                placeholder = { Text(text = "Label",color = Color.Gray) }
            )
            Button(onClick = { db.addLabel(text); labels.value = db.getAllLabelsFromNotebook(notebook.id) }) {
                Text(text = "Add")
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = labels.value, itemContent = { item ->
                labelDisplay(item, db, notebook, labels, chooseLabel)
            })
        }
    }
}

@Composable
fun labelDisplay(item : Label, db : NoteDatabaseHelper, notebook: Notebook, labels : MutableState<List<Label>>, chooseLabel: (Label) -> Unit) {
    val openRemove = remember { mutableStateOf(false) }
    Row(Modifier.fillMaxSize().border(1.dp, Color.Black).clickable { chooseLabel(item) }) {
        Text(text = item.text)
        Button(onClick = { openRemove.value = true }) {
            Text(text = "Remove")
        }
    }
    val dialogWidth = 200.dp
    val dialogHeight = 200.dp
    if (openRemove.value) {
        Dialog(onDismissRequest = { openRemove.value = false }) {
            Box(
                Modifier
                    .size(dialogWidth, dialogHeight)
                    .background(Color.White)) {
                Column(Modifier.fillMaxSize()) {
                    Text("Do you really want to remove this Label : ${item.text}")
                    Button(onClick = { db.removeLabel(item.id); labels.value = db.getAllLabelsFromNotebook(notebook.id) ; openRemove.value = false }) {
                        Text(text = "Remove", color = Color.Red)
                    }
                }
            }
        }
    }
}