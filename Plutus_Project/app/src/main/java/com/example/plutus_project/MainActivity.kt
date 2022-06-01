package com.example.plutus_project

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.items.AppState
import com.example.plutus_project.items.BudgetState
import com.example.plutus_project.items.Notebook
import com.example.plutus_project.items.Transaction
import com.example.plutus_project.ui.theme.Plutus_ProjectTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Plutus_ProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val db = NoteDatabaseHelper(LocalContext.current, "plutusDb")
                    pageState(db)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun pageState(db : NoteDatabaseHelper) {
    val list = ArrayList<Notebook>()
    list.add(Notebook(-1, ""))
    var appState by rememberSaveable { mutableStateOf(AppState.CHOOSING_NOTE) }
    val notebook by rememberSaveable { mutableStateOf(list) }
    when(appState) {
        AppState.CHOOSING_NOTE -> NoteBookChoice(db) { notebook[0] = it ; appState = AppState.SHOW_NOTE }
        AppState.SHOW_NOTE -> TransactionManagement(db, notebook[0]) { appState = AppState.SEARCHING }
        AppState.SEARCHING -> SearchDisplay(db)
        AppState.BUDGET -> budgetPageState(db)
    }
}