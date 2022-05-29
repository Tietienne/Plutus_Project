package com.example.plutus_project

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.items.Transaction
import java.util.*

@Composable
fun TransactionEditor(transaction: Transaction, onTransactionChange: (Transaction) -> Unit,db : NoteDatabaseHelper){

    var amount by remember { mutableStateOf(transaction.amount) }
    var currency by remember { mutableStateOf(transaction.currency) }
    var date by remember { mutableStateOf(transaction.dateTime) }
    var motif by remember { mutableStateOf(transaction.text)}

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
                    drawCurrency(currency, onCurrencyChange = {currency = it})                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth()){
            timePicker(date,onDateChange = {date = it})
        }
        Box(modifier = Modifier.fillMaxWidth()){
            drawMotif(motif,onMotifChange = {motif = it})
        }
        Spacer(modifier = Modifier.height(150.dp))
        Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.Center){
            val context = LocalContext.current
            Button(onClick = {
                /*TODO add to database*/
                var result = db.addTransaction(transaction = transaction);
                Toast.makeText(context,"$date + $amount + $currency + $motif + $result", Toast.LENGTH_SHORT).show()
            }) {
                Text(text = "Valider")
            }
        }
    }
}



@Composable
fun drawAmount(amount: String, onAmountChange : (String) -> Unit){
//    var amount by remember { mutableStateOf(TextFieldValue("")) }
    TextField(value = "$amount", onValueChange = {onAmountChange(it)}, Modifier.background(Color.Transparent),
        placeholder = { Text(text = "Montant",color = Color.Gray)}
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


@OptIn(ExperimentalComposeUiApi::class)
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

    // Declaring a string value to
    // store date in string format
    val mDate = remember { mutableStateOf(date) }

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
            onDateChange(mDate.value)
        }, Integer.valueOf(mDate.value.split("/")[2]), Integer.valueOf(mDate.value.split("/")[1])-1, Integer.valueOf(mDate.value.split("/")[0])
    )

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
        Text(text = " Date: ${mDate.value}", fontSize = 22.sp, textAlign = TextAlign.Center,modifier = Modifier.padding(10.dp))

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





