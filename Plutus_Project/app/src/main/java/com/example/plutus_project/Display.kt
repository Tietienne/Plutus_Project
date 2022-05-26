package com.example.plutus_project

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plutus_project.items.Transaction
import java.util.*

@Composable
fun TransactionEditor(transaction: Transaction, onTransactionChange: (Transaction) -> Unit){

    var amount by remember { mutableStateOf(transaction.amount) }
    var currency by remember { mutableStateOf(transaction.currency) }
    var date by remember { mutableStateOf(transaction.dateTime) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth()){
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(6f)) {
                    drawAmount(amount,onAmountChange = {amount = it})
                }
                Column(Modifier.weight(4f)) {
                    drawCurrency(currency, onCurrencyChange = {currency = it})                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth()){
//            val mContext = LocalContext.current
            timePicker(date,onDateChange = {date = it})
        }
        Box(modifier = Modifier.fillMaxWidth()){
            drawMotif()
        }
        Spacer(modifier = Modifier.height(150.dp))
        Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.Center){
            Button(onClick = { /*TODO add to database*/
                var transaction = Transaction(transaction.id,date,amount,currency);
                print(transaction);
            }) {
                Text(text = "Valider")
            }
        }
    }
}



@Composable
fun drawAmount(amount: Int, onAmountChange : (Int) -> Unit){
//    var amount by remember { mutableStateOf(TextFieldValue("")) }
    TextField(value = "$amount", onValueChange = {onAmountChange(it.toInt())}, Modifier.background(Color.Transparent),
        placeholder = { Text(text = "Montant",color = Color.Gray)}
    )
}

@Composable
fun drawCurrency(currency: String, onCurrencyChange : (String) -> Unit){

    var expanded by remember{ mutableStateOf(false)}
    val items = listOf("EUR", "DOLLAR", "RMB", "D", "E", "F")
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
//                text = items[selectedIndex],
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
    val mDate = remember { mutableStateOf("") }

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
//            mDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
            mDate.value = date
            onDateChange(mDate.value);
        }, mYear, mMonth, mDay
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
fun drawMotif(){
    var motif by remember { mutableStateOf(TextFieldValue("")) }
    TextField(value = motif, onValueChange = {motif = it},
        Modifier
            .background(Color.Transparent)
            .fillMaxWidth(),
        placeholder = { Text(text = "Motif",color = Color.Gray)}
    )
}





