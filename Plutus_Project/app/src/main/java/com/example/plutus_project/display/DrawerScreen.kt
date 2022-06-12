package com.example.plutus_project.display


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plutus_project.database.NoteDatabaseHelper
import com.example.plutus_project.items.AppState
import com.example.plutus_project.items.Notebook
import kotlinx.coroutines.launch

open class DrawerScreen(val title: String, val route: String) {

    object Home : DrawerScreen("Home","home")
    object Notebook : DrawerScreen("Notebook","notebook")
    object Search : DrawerScreen( "Search","search")
    object Budget : DrawerScreen( "Budget","budget")


    private val screens = listOf(
        DrawerScreen.Home,
        DrawerScreen.Notebook,
        DrawerScreen.Search,
        DrawerScreen.Budget

    )

    @Composable
    fun Drawer(
        modifier: Modifier = Modifier,
        onDestinationClicked: (route: String) -> Unit){
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 24.dp, top = 48.dp)
        ) {
            screens.forEach{ screen ->
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = screen.title,
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.clickable {
                        onDestinationClicked(screen.route)
                    }
                )
            }
        }
    }

    @Composable
    fun TopBar(title: String, buttonIcon: ImageVector, onButtonChecked: () -> Unit){
        TopAppBar(
            title = {
                Text(text = title)
            },

            navigationIcon = {
                IconButton(onClick = { onButtonChecked() }) {
                    Icon(imageVector = buttonIcon, contentDescription = "topBar")
                }
            },
            backgroundColor = MaterialTheme.colors.primaryVariant
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun AppMainScreen(db : NoteDatabaseHelper) {
        var appState by rememberSaveable { mutableStateOf(AppState.CHOOSING_NOTE) }
        var notebook by remember { mutableStateOf(Notebook(-1, "")) }

        val navController = rememberNavController()
        Surface(color = MaterialTheme.colors.background) {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val openDrawer = {
                scope.launch {
                    drawerState.open()
                }
            }
            ModalDrawer(
                drawerState = drawerState,
                gesturesEnabled = drawerState.isOpen,
                drawerContent = {
                    Drawer(
                        onDestinationClicked = { route ->
                            scope.launch {
                                drawerState.close()
                            }
                            navController.navigate(route) {
//                                popUpTo = navController.graph.startDestinationId
                                launchSingleTop = true
                            }
                        }
                    )
                }
            ) {
                NavHost(
                    navController = navController,
                    startDestination = DrawerScreen.Home.route
                ) {
                    composable(DrawerScreen.Home.route) {

                        Column(modifier = Modifier.fillMaxSize()) {

                            TopBar(
                                title = "Home",
                                buttonIcon = Icons.Filled.Menu,
                                onButtonChecked = {openDrawer()}
                            )
                            NoteBookChoice(db) { notebook = it ; appState = AppState.SHOW_NOTE }
                        }
                    }

                    composable(DrawerScreen.Notebook.route) {
                        Column(modifier = Modifier.fillMaxSize()) {

                            TopBar(
                                title = "Notebook",
                                buttonIcon = Icons.Filled.ArrowBack,
                                onButtonChecked = {openDrawer()}
                            )

                            TransactionManagement(db, notebook)
                        }

                    }

                    composable(DrawerScreen.Search.route) {
                        Column(modifier = Modifier.fillMaxSize()) {

                            TopBar(
                                title = "Search",
                                buttonIcon = Icons.Filled.ArrowBack,
                                onButtonChecked = {openDrawer()}
                            )

                            SearchDisplay(db, notebook)
//                            budgetPageState(db, notebook)
                        }
                    }

                    composable(DrawerScreen.Budget.route) {
                        Column(modifier = Modifier.fillMaxSize()) {

                            TopBar(
                                title = "Budget",
                                buttonIcon = Icons.Filled.ArrowBack,
                                onButtonChecked = {openDrawer()}
                            )

                            budgetPageState(db, notebook)
                        }
                    }

                }
            }
        }
    }


}