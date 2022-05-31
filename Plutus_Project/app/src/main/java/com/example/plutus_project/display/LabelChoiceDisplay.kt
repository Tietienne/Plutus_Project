package com.example.plutus_project.display

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.example.plutus_project.items.Label

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LabelChoiceEditor() {
    var labels = ArrayList<Label>()
    labels.add(Label(-1, "test"))
    AutoCompleteBox(labels, {label -> LabelAutoCompleteItem(label)}) {
        var value by remember { mutableStateOf("") }
        val view = LocalView.current

        onItemSelected { label ->
            value = label.text
            filter(value)
            view.clearFocus()
        }

        TextField(value = value, onValueChange = { value = it; filter(value) },
            Modifier
                .background(Color.Transparent)
                .fillMaxWidth()
                .onFocusChanged { isSearching = it.isFocused },
            placeholder = { Text(text = "Étiquette",color = Color.Gray)}
        )
    }
}

@Composable
fun LabelAutoCompleteItem(label: Label) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = label.text)
    }
}