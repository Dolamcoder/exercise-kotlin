package com.example.myapplication.ui.item

import com.example.myapplication.data.Item
import java.text.NumberFormat

/**
 * UI state and mapping helpers shared by entry/edit/details screens.
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false,
)

data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val quantity: String = "",
)

fun ItemDetails.toItem(): Item = Item(
    id = id,
    name = name,
    price = price.toDoubleOrNull() ?: 0.0,
    quantity = quantity.toIntOrNull() ?: 0,
)

fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = toItemDetails(),
    isEntryValid = isEntryValid,
)

fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    price = price.toString(),
    quantity = quantity.toString(),
)

fun Item.formattedPrice(): String = NumberFormat.getCurrencyInstance().format(price)

