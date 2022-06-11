package com.example.plutus_project

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.display.NoteBookChoice
import com.example.plutus_project.display.SearchDisplay
import com.example.plutus_project.display.TransactionManagement
import com.example.plutus_project.display.budgetPageState
import com.example.plutus_project.items.AppState
import com.example.plutus_project.items.Notebook
import com.example.plutus_project.ui.theme.Plutus_ProjectTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Plutus_ProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    LocalContext.current.deleteDatabase("plutusDb")
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
    var appState by rememberSaveable { mutableStateOf(AppState.CHOOSING_NOTE) }
    var notebook by remember { mutableStateOf(Notebook(-1, "")) }
    Scaffold(
        topBar = { TopAppBar(title = {
            Row(Modifier.fillMaxWidth()) {
                Button(onClick = { notebook = Notebook(-1, ""); appState = AppState.CHOOSING_NOTE }, modifier = Modifier.weight(1f/4f), enabled = notebook.id != -1) {
                    Text("Choose Notebook")
                }
                Button(onClick = { appState = AppState.SHOW_NOTE }, modifier = Modifier.weight(1f/4f), enabled = notebook.id != -1) {
                    Text("Transactions")
                }
                Button(onClick = { appState = AppState.SEARCHING }, modifier = Modifier.weight(1f/4f), enabled = notebook.id != -1) {
                    Text("Search")
                }
                Button(onClick = { appState = AppState.BUDGET }, modifier = Modifier.weight(1f/4f), enabled = notebook.id != -1) {
                    Text("Budgets")
                }
            }
        }) },
        content = {
                    when(appState) {
                        AppState.CHOOSING_NOTE -> NoteBookChoice(db) { notebook = it ; appState = AppState.SHOW_NOTE }
                        AppState.SHOW_NOTE -> TransactionManagement(db, notebook)
                        AppState.SEARCHING -> SearchDisplay(db, notebook)
                        AppState.BUDGET -> budgetPageState(db, notebook)
                    }
                  }
    )
}