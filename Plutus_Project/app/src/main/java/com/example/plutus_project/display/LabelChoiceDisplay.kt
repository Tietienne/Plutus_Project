package com.example.plutus_project.display

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.plutus_project.items.Label

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LabelChoiceEditor(currentLabel : String, onChangeLabel : (String) -> Unit) {
    var labels = ArrayList<Label>()
    labels.add(Label(-1, "test"))
    AutoCompleteBox(labels, {label -> LabelAutoCompleteItem(label)}) {
        val view = LocalView.current

        onItemSelected { label ->
            onChangeLabel(label.text)
            filter(label.text)
            view.clearFocus()
        }
        val keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        val keyboardActions = KeyboardActions(
            onDone = {
                view.clearFocus()
            }
        )

        TextField(value = currentLabel, onValueChange = { onChangeLabel(it); filter(it) }, keyboardOptions = keyboardOptions, keyboardActions = keyboardActions,
            modifier = Modifier
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