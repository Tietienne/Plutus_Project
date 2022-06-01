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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.items.AppState
import com.example.plutus_project.items.Label
import com.example.plutus_project.items.Notebook
import com.example.plutus_project.items.SearchState
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchDisplay(db : NoteDatabaseHelper) {
    val labels = remember { mutableStateListOf<Label>() }
    var searchState by rememberSaveable { mutableStateOf(SearchState.FILLING_SEARCH) }
    var dateBegin by remember { mutableStateOf("Date Begin") }
    var dateEnd by remember { mutableStateOf("Date End") }
    var amountMin by remember { mutableStateOf(0) }
    var amountMax by remember { mutableStateOf(100) }
    var motif by remember { mutableStateOf("") }
    when(searchState) {
        SearchState.FILLING_SEARCH -> fillingSearch(dateBegin, dateEnd, amountMin, amountMax, labels, motif, {dateBegin = it}, {dateEnd = it}, {amountMin = it},
            {amountMax = it}, { motif = it }, { searchState = SearchState.ADDING_LABELS }) { searchState = SearchState.SEARCHED }
        SearchState.ADDING_LABELS -> chooseLabelToSearchPage(db, labels, { if (labels.contains(it)) labels.remove(it) else labels.add(it) }) { searchState = SearchState.FILLING_SEARCH }
        SearchState.SEARCHED -> searchedPage(db, dateBegin, dateEnd, amountMin, amountMax, motif, labels, { searchState = SearchState.STATS }) { searchState = SearchState.FILLING_SEARCH }
        SearchState.STATS -> chooseLabelToSearchPage(db, labels, { if (labels.contains(it)) labels.remove(it) else labels.add(it) }) { searchState = SearchState.FILLING_SEARCH }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun searchedPage(db : NoteDatabaseHelper, dateBegin : String, dateEnd : String, amountMin: Int, amountMax: Int, motif: String, labels : List<Label>,
                 statsPage : () -> Unit, goBack: () -> Unit) {
    val transactions = remember { mutableStateOf(db.searchTransactions(dateBegin, dateEnd, amountMin, amountMax, motif, labels)) }
    Column(Modifier.fillMaxSize()) {
        Row {
            Button(onClick = { goBack() }) {
                Text(text = "Back to searching page")
            }
            Button(onClick = { statsPage() }) {
                Text(text = "Show statistics")
            }
        }
        Row {
            Button(onClick = { /* TODO : Sort by amount*/ }) {
                Text(text = "Not sorting by amount")
            }
            Button(onClick = { /* TODO : Sort by date*/ }) {
                Text(text = "Not sorting by date")
            }
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(items = transactions.value, itemContent = { item ->
                TransactionDisplay(item, db, transactions)
            })
        }
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
fun chooseLabelToSearchPage(db : NoteDatabaseHelper, selectedLabels : List<Label>, addLabel: (Label) -> Unit, goBack : () -> Unit) {
    var text by remember { mutableStateOf("") }
    val labels = remember { mutableStateOf(db.getAllLabels()) }
    Column(Modifier.fillMaxSize()) {
        Button(onClick = { goBack() }) {
            Text(text = "Go back")
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = labels.value, itemContent = { item ->
                Box(Modifier.background(if (selectedLabels.contains(item)) Color.Green else Color.Red )) {
                    labelDisplay(item, db, labels, addLabel)
                }
            })
        }
    }
}