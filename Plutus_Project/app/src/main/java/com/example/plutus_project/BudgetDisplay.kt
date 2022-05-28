package com.example.plutus_project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.items.Budget
import com.example.plutus_project.items.BudgetState
import com.example.plutus_project.items.Label


@Composable
fun pageState(db : NoteDatabaseHelper) {
    var gameState by rememberSaveable { mutableStateOf(BudgetState.ADDING_BUDGET) }
    when(gameState) {
        BudgetState.ADDING_BUDGET -> budgetPage(db)
        BudgetState.CHOOSING_LABEL -> chooseLabel(db)
    }
}

@Composable
fun budgetPage(db : NoteDatabaseHelper) {
    val budgets = remember { mutableStateOf(db.getAllBudgets()) }
    Column(Modifier.fillMaxSize()) {
        newBudget(db)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = budgets.value, itemContent = { item ->
                BudgetDisplay(item, db, budgets)
            })
        }
    }
}

@Composable
fun newBudget(db : NoteDatabaseHelper) {
    var amount by remember { mutableStateOf(0f) }
    var label by remember { mutableStateOf(Label(0, "")) }
    Row(Modifier.fillMaxSize()) {
        // TODO : Choose label
        TextField(value = "$amount", onValueChange = { amount = if (it == "") 0f else it.toFloat() }, Modifier.background(Color.Transparent),
            placeholder = { Text(text = "Amount",color = Color.Gray) }
        )
        Button(onClick = { db.addBudget(amount, label) }) {
            Text(text = "Add")
        }
    }


}

@Composable
fun BudgetDisplay(item : Budget, db : NoteDatabaseHelper, budgets : MutableState<List<Budget>>) {
    // TODO : display + remove button
}

@Composable
fun chooseLabel(db : NoteDatabaseHelper) {
    var text by remember { mutableStateOf("") }
    var labels = remember { mutableStateOf(db.getAllLabels()) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth()) {
            TextField(value = text, onValueChange = { text = it }, Modifier.background(Color.Transparent),
                placeholder = { Text(text = "Label",color = Color.Gray) }
            )
            Button(onClick = { db.addLabel(text); labels.value = db.getAllLabels() }) {
                Text(text = "Add")
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = labels.value, itemContent = { item ->
                LabelDisplay(item, db, labels)
            })
        }
    }
}

@Composable
fun LabelDisplay(item : Label, db : NoteDatabaseHelper, labels : MutableState<List<Label>>) {
    // TODO : Display + select label on click
}