package com.example.plutus_project

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun DrawTransaction(){
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth()){
            Row(Modifier.fillMaxWidth()) {
                var amount by remember { mutableStateOf(TextFieldValue("")) }
                TextField(value = amount, onValueChange = {amount = it},
                    placeholder = { Text(text = "Montant")}
                )
                var expanded by remember{ mutableStateOf(false)}
                val items = listOf("A", "B", "C", "D", "E", "F")
                //val disabledValue = "B"
                var selectedIndex by remember { mutableStateOf(0) }
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(items[selectedIndex],modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { expanded = true }))
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items.forEachIndexed { index, s ->
                            DropdownMenuItem(onClick = {
                                selectedIndex = index
                                expanded = false
                            }) {
                                //val disabledText = if (s == disabledValue) {
                                //    " (Disabled)"
                                //} else {
                                //   ""
                                //}
                                //Text(text = s + disabledText)
                                Text(text = s)
                            }
                        }
                    }
                }
            }
            
        }
        
        
        
    }
}