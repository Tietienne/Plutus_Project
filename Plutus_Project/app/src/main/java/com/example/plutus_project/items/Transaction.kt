package com.example.plutus_project.items

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import java.time.LocalDate
import java.time.LocalDateTime

class Transaction(var id: Int, var dateTime: String, var amount : Int, var currency: String) {
}