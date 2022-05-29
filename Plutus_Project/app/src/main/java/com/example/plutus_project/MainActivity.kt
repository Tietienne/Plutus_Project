package com.example.plutus_project

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.plutus_project.database.NoteDatabaseHelper
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
                    //NoteBookChoice(db)
                    pageState(db)
                    //DefaultPreview()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Plutus_ProjectTheme {
//        DrawTransaction()
        val db = NoteDatabaseHelper(LocalContext.current, "plutusDb")
//        db.addNotebook("myNotebook")
//        var notebooks = db.getAllNotebooks()
//        var id = notebooks[0].id;
//        var transaction = Transaction(1,"Aujourd'hui",0,"EUR","",id)
//        TransactionEditor(transaction = transaction, onTransactionChange = {transaction = it },db)

//        NoteBookChoice(db)
        TransactionManagement(db)
    }
}