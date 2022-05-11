package com.example.plutus_project

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
            }
            
        }
        
        
        
    }
}