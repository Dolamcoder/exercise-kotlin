package com.example.myapplication.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
) : ViewModel() {
    private val itemId: Int = checkNotNull(savedStateHandle[ItemDetailsViewModel.ITEM_ID_ARG])

    private val _itemUiState = MutableStateFlow(ItemUiState())
    val itemUiState: StateFlow<ItemUiState> = _itemUiState.asStateFlow()

    init {
        viewModelScope.launch {
            val item = itemsRepository.getItemStream(itemId).filterNotNull().first()
            _itemUiState.value = item.toItemUiState(isEntryValid = true)
        }
    }

    fun updateUiState(itemDetails: ItemDetails) {
        _itemUiState.update {
            it.copy(
                itemDetails = itemDetails,
                isEntryValid = validateInput(itemDetails),
            )
        }
    }

    fun updateItem() {
        if (!validateInput()) return
        viewModelScope.launch {
            itemsRepository.updateItem(_itemUiState.value.itemDetails.toItem())
        }
    }

    private fun validateInput(uiState: ItemDetails = _itemUiState.value.itemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() &&
                price.isNotBlank() &&
                price.toDoubleOrNull() != null &&
                quantity.isNotBlank() &&
                quantity.toIntOrNull() != null
        }
    }
}

