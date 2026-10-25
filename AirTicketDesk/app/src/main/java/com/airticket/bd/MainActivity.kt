package com.airticket.bd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.airticket.bd.ui.theme.AirTicketTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Customers : Screen("customers", "Customers", Icons.Default.People)
    object NewTicket : Screen("new_ticket", "Sell Ticket", Icons.Default.FlightTakeoff)
    object Sales : Screen("sales", "Sales", Icons.Default.ReceiptLong)
    object Due : Screen("due", "Dues", Icons.Default.AccountBalanceWallet)
    object Payments : Screen("payments", "Payments", Icons.Default.Payment)
    object Expenses : Screen("expenses", "Expenses", Icons.Default.TrendingDown)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AirTicketTheme {
                MainAppScaffold()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavScreens = listOf(
        Screen.Dashboard,
        Screen.Customers,
        Screen.NewTicket,
        Screen.Due,
        Screen.Settings
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AirTicket Desk — Dev Travels") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Dashboard.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardComposeView()
            }
            composable(Screen.Customers.route) {
                CustomerListComposeView()
            }
            composable(Screen.NewTicket.route) {
                NewTicketSaleComposeView()
            }
            composable(Screen.Sales.route) {
                SalesListComposeView()
            }
            composable(Screen.Due.route) {
                DueManagementComposeView()
            }
            composable(Screen.Payments.route) {
                PaymentHistoryComposeView()
            }
            composable(Screen.Expenses.route) {
                ExpenseComposeView()
            }
            composable(Screen.Settings.route) {
                SettingsComposeView()
            }
        }
    }
}

@Composable
fun DashboardComposeView() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("AirTicket Desk Dashboard", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Offline SQLite database active. Integer poisha arithmetic.", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable fun CustomerListComposeView() { Box(Modifier.fillMaxSize()) { Text("Customer Ledger Active", Modifier.padding(16.dp)) } }
@Composable fun NewTicketSaleComposeView() { Box(Modifier.fillMaxSize()) { Text("Ticket Entry Active", Modifier.padding(16.dp)) } }
@Composable fun SalesListComposeView() { Box(Modifier.fillMaxSize()) { Text("Sales Records Active", Modifier.padding(16.dp)) } }
@Composable fun DueManagementComposeView() { Box(Modifier.fillMaxSize()) { Text("Due Collection Active", Modifier.padding(16.dp)) } }
@Composable fun PaymentHistoryComposeView() { Box(Modifier.fillMaxSize()) { Text("Payment Vouchers Active", Modifier.padding(16.dp)) } }
@Composable fun ExpenseComposeView() { Box(Modifier.fillMaxSize()) { Text("Expense Tracking Active", Modifier.padding(16.dp)) } }
@Composable fun SettingsComposeView() { Box(Modifier.fillMaxSize()) { Text("Agency Settings & Backup Active", Modifier.padding(16.dp)) } }