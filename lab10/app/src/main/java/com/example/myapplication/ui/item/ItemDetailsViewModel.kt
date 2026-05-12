package com.example.myapplication.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ItemDetailsUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val outOfStock: Boolean = true,
)

class ItemDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
) : ViewModel() {
    private val itemId: Int = checkNotNull(savedStateHandle[ITEM_ID_ARG])

    val uiState: StateFlow<ItemDetailsUiState> =
        itemsRepository.getItemStream(itemId)
            .filterNotNull()
            .map {
                ItemDetailsUiState(
                    itemDetails = it.toItemDetails(),
                    outOfStock = it.quantity <= 0,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = ItemDetailsUiState(),
            )

    fun reduceQuantityByOne() {
        viewModelScope.launch {
            val currentItem = uiState.value.itemDetails.toItem()
            if (currentItem.quantity > 0) {
                itemsRepository.updateItem(currentItem.copy(quantity = currentItem.quantity - 1))
            }
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            itemsRepository.deleteItem(uiState.value.itemDetails.toItem())
        }
    }

    companion object {
        const val ITEM_ID_ARG = "itemId"
    }
}


