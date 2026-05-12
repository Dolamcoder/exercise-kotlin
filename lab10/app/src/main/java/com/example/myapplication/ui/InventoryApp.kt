package com.example.myapplication.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.item.ItemDetailsScreen
import com.example.myapplication.ui.item.ItemDetailsViewModel
import com.example.myapplication.ui.item.ItemEditScreen
import com.example.myapplication.ui.item.ItemEntryScreen

@Composable
fun InventoryApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = InventoryDestination.Home.route,
        modifier = modifier,
    ) {
        composable(route = InventoryDestination.Home.route) {
            HomeScreen(
                onItemClick = { itemId ->
                    navController.navigate(InventoryDestination.ItemDetails.createRoute(itemId))
                },
                onAddItemClick = {
                    navController.navigate(InventoryDestination.ItemEntry.route)
                },
            )
        }
        composable(route = InventoryDestination.ItemEntry.route) {
            ItemEntryScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = InventoryDestination.ItemDetails.routeWithArgs,
            arguments = listOf(navArgument(ItemDetailsViewModel.ITEM_ID_ARG) { type = NavType.IntType }),
        ) {
            ItemDetailsScreen(
                onBack = { navController.popBackStack() },
                onEditItem = { itemId ->
                    navController.navigate(InventoryDestination.ItemEdit.createRoute(itemId))
                },
                onDeleteItem = {
                    navController.navigate(InventoryDestination.Home.route) {
                        popUpTo(InventoryDestination.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(
            route = InventoryDestination.ItemEdit.routeWithArgs,
            arguments = listOf(navArgument(ItemDetailsViewModel.ITEM_ID_ARG) { type = NavType.IntType }),
        ) {
            ItemEditScreen(onBack = { navController.popBackStack() })
        }
    }
}

sealed class InventoryDestination(val route: String) {
    data object Home : InventoryDestination("home")

    data object ItemEntry : InventoryDestination("item_entry")

    data object ItemDetails : InventoryDestination("item_details") {
        const val routeWithArgs = "item_details/{${ItemDetailsViewModel.ITEM_ID_ARG}}"

        fun createRoute(itemId: Int): String = "item_details/$itemId"
    }

    data object ItemEdit : InventoryDestination("item_edit") {
        const val routeWithArgs = "item_edit/{${ItemDetailsViewModel.ITEM_ID_ARG}}"

        fun createRoute(itemId: Int): String = "item_edit/$itemId"
    }
}

