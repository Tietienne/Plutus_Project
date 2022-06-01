package com.example.plutus_project.display

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.items.Label
import com.example.plutus_project.items.Transaction
import java.time.LocalDateTime
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionEditor(transaction: Transaction,db : NoteDatabaseHelper, onConfirm: (Boolean) -> Unit, onAddingTransaction : () -> Unit){

    var amount by remember { mutableStateOf(transaction.amount) }
    var currency by remember { mutableStateOf(transaction.currency) }
    var date by remember { mutableStateOf(transaction.dateTime) }
    var motif by remember { mutableStateOf(transaction.text)}
    val labels = remember { mutableStateOf(db.getAllLabelsFromTransaction(transaction)) }
    var currentLabel by remember { mutableStateOf("") }

    if (date == "Aujourd'hui") {
        val time = LocalDateTime.now()
        date = "${time.year}-${time.monthValue.toString().padStart(2, '0')}-${time.dayOfMonth.toString().padStart(2, '0')}"
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth()){
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(6f)) {
                    drawAmount(amount.toString(),onAmountChange = {
                        amount = if (it == "")
                            0
                        else
                            it.toInt()
                        })
                }
                Column(Modifier.weight(4f)) {
                    drawCurrency(currency, onCurrencyChange = {currency = it})
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth()){
            timePicker(date) {
                date = it
            }
        }
        Box(modifier = Modifier.fillMaxWidth()){
            drawMotif(motif,onMotifChange = {motif = it})
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            drawLabelChoice(currentLabel) { currentLabel = it }
        }
        Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.CenterEnd) {
            Button(onClick = {
                if (currentLabel == "") {
                    return@Button
                }
                if (transaction.id != -1) {
                    db.addLabelToTransaction(operation_id = transaction.id, currentLabel)
                    labels.value = db.getAllLabelsFromTransaction(transaction)
                } else {
                    if (!labels.value.contains(Label(-1, currentLabel))) {
                        labels.value = labels.value.plus(Label(-1, currentLabel))
                    }
                }
            }) {
                Text(text = "Ajouter étiquette")
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
            displayAllLabels(labels) {
                if (transaction.id != -1) {
                    db.removeLabelOfTransaction(it.id, transaction.id)
                    labels.value = db.getAllLabelsFromTransaction(transaction)
                } else {
                    labels.value = labels.value.minus(it)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.Center){
            LocalContext.current
            Button(onClick = {
                if (transaction.id == -1) {
                    val new_transaction_id = db.addTransaction(date, amount, currency, motif, transaction.notebookId)
                    for (label in labels.value) {
                        db.addLabelToTransaction(operation_id = new_transaction_id, label.text)
                    }
                } else {
                    db.updateTransaction(transaction.id,date, amount, currency, motif, transaction.notebookId)
                    onConfirm(false)
                }
                onAddingTransaction()
            }) {
                Text(text = "Valider")
            }
        }
    }
}



@Composable
fun drawAmount(amount: String, onAmountChange : (String) -> Unit){
//    var amount by remember { mutableStateOf(TextFieldValue("")) }
    TextField(value = amount, onValueChange = {onAmountChange(it)}, Modifier.background(Color.Transparent),
        placeholder = { Text(text = "Montant",color = Color.Gray)},
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun drawCurrency(currency: String, onCurrencyChange : (String) -> Unit){

    var expanded by remember{ mutableStateOf(false)}
    val items = listOf("EUR", "GBP", "RMB", "HKD", "USD")
    var selectedIndex by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.Center) {
        Row(
            Modifier
                .clickable {
                    expanded = !expanded
                }
                .padding(5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currency,
                fontSize = 18.sp,)
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxSize()
        ) {
            items.forEachIndexed{ index,value ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    onCurrencyChange(value)
                }) {
                    Text(text = value)
                }
            }
        }
    }
}


@Composable
fun timePicker(date : String, onDateChange : (String) -> Unit){

    // Fetching the Local Context
    val mContext = LocalContext.current

    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            onDateChange("$mYear-${(mMonth+1).toString().padStart(2, '0')}-${(mDayOfMonth).toString().padStart(2, '0')}")
        }, mYear, mMonth, mDay
    )

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
        Text(text = date, fontSize = 18.sp, textAlign = TextAlign.Center,modifier = Modifier.padding(10.dp),color = Color.Gray)

        Box(Modifier.clickable {
            mDatePickerDialog.show()
        }) {
            Icon(imageVector = Icons.Filled.DateRange, contentDescription = "")
        }
    }
}

@Composable
fun drawMotif(motif: String, onMotifChange : (String) -> Unit){
    TextField(value = motif, onValueChange = {onMotifChange(it)},
        Modifier
            .background(Color.Transparent)
            .fillMaxWidth(),
        placeholder = { Text(text = "Motif",color = Color.Gray)}
    )
}


@Composable
fun drawLabelChoice(currentLabel: String, onChangeLabel : (String) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.Center){
        LabelChoiceEditor(currentLabel, onChangeLabel)
    }
}

@Composable
fun displayAllLabels(labels: MutableState<List<Label>>, removeLabel: (Label) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = labels.value, itemContent = { item ->
            LabelDisplay(item, removeLabel)
        })
    }
}

@Composable
fun LabelDisplay(label : Label, removeLabel : (Label) -> Unit) {
    Row(Modifier.fillMaxSize().border(1.dp, Color.Black)) {
        Text(text = label.text)
        Button(onClick = { removeLabel(label) }) {
            Text(text = "Remove")
        }
    }
}





