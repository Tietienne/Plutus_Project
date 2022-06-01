package com.example.plutus_project

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.display.NoteBookChoice
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
        AppState.SHOW_NOTE -> TransactionManagement(db, notebook[0], { appState = AppState.SEARCHING }, { appState = AppState.BUDGET }, { appState = AppState.CHOOSING_NOTE })
        AppState.SEARCHING -> SearchDisplay(db, { appState = AppState.SHOW_NOTE }, { appState = AppState.BUDGET }, { appState = AppState.CHOOSING_NOTE })
        AppState.BUDGET -> budgetPageState(db, { appState = AppState.CHOOSING_NOTE }, { appState = AppState.SHOW_NOTE }, { appState = AppState.SEARCHING })
    }
}