package com.example.plutus_project.display

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.items.Label
import com.example.plutus_project.items.Notebook
import com.example.plutus_project.items.SearchState
import com.example.plutus_project.items.Transaction

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchDisplay(db : NoteDatabaseHelper, notebook: Notebook) {
    val labels = remember { mutableStateListOf<Label>() }
    var searchState by rememberSaveable { mutableStateOf(SearchState.FILLING_SEARCH) }
    var dateBegin by remember { mutableStateOf("Date Begin") }
    var dateEnd by remember { mutableStateOf("Date End") }
    var amountMin by remember { mutableStateOf(0) }
    var amountMax by remember { mutableStateOf(100) }
    var motif by remember { mutableStateOf("") }
    val transactions = remember { mutableStateListOf<Transaction>() }
    when(searchState) {
        SearchState.FILLING_SEARCH -> fillingSearch(dateBegin, dateEnd, amountMin, amountMax, labels, motif, {dateBegin = it}, {dateEnd = it}, {amountMin = it},
            {amountMax = it}, { motif = it }, { searchState = SearchState.ADDING_LABELS }, { searchState = SearchState.SEARCHED })
        SearchState.ADDING_LABELS -> chooseLabelToSearchPage(db, notebook, labels, { if (labels.contains(it)) labels.remove(it) else labels.add(it) }) { searchState = SearchState.FILLING_SEARCH }
        SearchState.SEARCHED -> searchedPage(db, dateBegin, dateEnd, amountMin, amountMax, motif, labels, notebook, { transactions.clear(); transactions.addAll(it); searchState = SearchState.STATS })
                                { searchState = SearchState.FILLING_SEARCH }
        SearchState.STATS -> statsPage(db, transactions) { searchState = SearchState.SEARCHED }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun statsPage(db: NoteDatabaseHelper, transactions: SnapshotStateList<Transaction>, goBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Button(onClick = { goBack() }) {
            Text(text = "Back to searching page")
        }
        Row {
            Text("Total amount : ${calculateTotalAmount(db, transactions)}")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun calculateTotalAmount(db: NoteDatabaseHelper, transactions: SnapshotStateList<Transaction>) : Float {
    var total = 0f
    for (transaction in transactions) {
        val labels = db.getAllLabelsFromTransaction(transaction)
        if (labels.stream().anyMatch { it.text.startsWith("+") }) {
            if (labels.stream().noneMatch { it.text.startsWith("-") }) {
                total+=transaction.amount
            }
        } else {
            if (labels.stream().anyMatch { it.text.startsWith("-") }) {
                total-=transaction.amount
            }
        }
    }
    return total
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun searchedPage(db : NoteDatabaseHelper, dateBegin : String, dateEnd : String, amountMin: Int, amountMax: Int, motif: String, labels : List<Label>,
                 notebook: Notebook, statsPage : (List<Transaction>) -> Unit, goBack: () -> Unit) {
    val transactions = remember { mutableStateOf(db.searchTransactionsFromNotebook(notebook.id, dateBegin, dateEnd, amountMin, amountMax, motif, labels)) }
    var sortingAmount by remember { mutableStateOf("Not sorting by amount") }
    var sortingDate by remember { mutableStateOf("Not sorting by date") }
    Column(Modifier.fillMaxSize()) {
        Row {
            Button(onClick = { goBack() }) {
                Text(text = "Back to searching page")
            }
            Button(onClick = { statsPage(transactions.value) }) {
                Text(text = "Show statistics")
            }
        }
        Row {
            Button(onClick = { changeSortAmount(sortingAmount, transactions)
                { sortingAmount = it } }) {
                Text(text = sortingAmount)
            }
            Button(onClick = { changeSortDate(sortingDate, transactions)
                { sortingDate = it } }) {
                Text(text = sortingDate)
            }
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(items = transactions.value, itemContent = { item ->
                TransactionDisplay(item, db, transactions, notebook)
            })
        }
    }
}

fun changeSortAmount(text : String, transactions : MutableState<List<Transaction>>, onChange : (String) -> Unit) {
    if (text == "Not sorting by amount") {
        transactions.value = transactions.value.sortedBy { it.amount }
        onChange("Sorting by amount ASC")
        return
    }
    if (text == "Sorting by amount ASC") {
        transactions.value = transactions.value.sortedBy { it.amount }.reversed()
        onChange("Sorting by amount DES")
        return
    }
    if (text == "Sorting by amount DES") {
        onChange("Not sorting by amount")
        return;
    }
}

fun changeSortDate(text : String, transactions : MutableState<List<Transaction>>, onChange : (String) -> Unit) {
    if (text == "Not sorting by date") {
        transactions.value = transactions.value.sortedBy { it.dateTime }
        onChange("Sorting by date ASC")
        return
    }
    if (text == "Sorting by date ASC") {
        transactions.value = transactions.value.sortedBy { it.dateTime }.reversed()
        onChange("Sorting by date DES")
        return
    }
    if (text == "Sorting by date DES") {
        onChange("Not sorting by date")
        return;
    }
}

@Composable
fun fillingSearch(beginDate : String, endDate : String, amountMin : Int, amountMax : Int, selectedLabels: List<Label>, motif : String,
                  changeBeginDate : (String) -> Unit, changeEndDate : (String) -> Unit, changeAmountMin : (Int) -> Unit,
                  changeAmountMax : (Int) -> Unit, changeMotif : (String) -> Unit, chooseLabel : () -> Unit, search : () -> Unit) {
    Column {
        Row {
            Button(onClick = { chooseLabel() }) {
                Text(text = "Choose labels")
            }
            Text(text = "${selectedLabels.size} label(s) selected")
        }

        Box(modifier = Modifier.fillMaxWidth()){
            timePicker(beginDate) {
                changeBeginDate(it)
            }
        }
        Box(modifier = Modifier.fillMaxWidth()){
            timePicker(endDate) {
                changeEndDate(it)
            }
        }
        Box(modifier = Modifier.fillMaxWidth()){
            drawAmount(amountMin.toString(),onAmountChange = {
                changeAmountMin(if (it == "") 0 else it.toInt())
            })
            Text("Amount min")
        }
        Box(modifier = Modifier.fillMaxWidth()){
            drawAmount(amountMax.toString(),onAmountChange = {
                changeAmountMax(if (it == "") 0 else it.toInt())
            })
            Text("Amount max")
        }
        Box(modifier = Modifier.fillMaxWidth()){
            drawMotif(motif,onMotifChange = { changeMotif(it) })
        }
        Button(onClick = {search()}) {
            Text("Search")
        }
    }
}

@Composable
fun chooseLabelToSearchPage(db : NoteDatabaseHelper, notebook: Notebook, selectedLabels : List<Label>, addLabel: (Label) -> Unit, goBack : () -> Unit) {
    var text by remember { mutableStateOf("") }
    val labels = remember { mutableStateOf(db.getAllLabelsFromNotebook(notebook.id)) }
    Column(Modifier.fillMaxSize()) {
        Button(onClick = { goBack() }) {
            Text(text = "Go back")
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = labels.value, itemContent = { item ->
                Box(Modifier.background(if (selectedLabels.contains(item)) Color.Green else Color.Red )) {
                    labelDisplay(item, db, notebook, labels, addLabel)
                }
            })
        }
    }
}