package com.example.plutus_project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.items.Budget

@Composable
fun budgetPage(db : NoteDatabaseHelper) {
    val budgets = remember { mutableStateOf(db.getAllBudgets()) }
    Column(Modifier.fillMaxSize()) {
        newBudget()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = budgets.value, itemContent = { item ->
                BudgetDisplay(item, db, budgets)
            })
        }
    }
}

@Composable
fun newBudget() {

}

@Composable
fun BudgetDisplay(item : Budget, db : NoteDatabaseHelper, budgets : MutableState<List<Budget>>) {

}