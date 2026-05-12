package com.example.myapplication.ui.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemEntryViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {
    private val _itemUiState = MutableStateFlow(ItemUiState())
    val itemUiState: StateFlow<ItemUiState> = _itemUiState.asStateFlow()

    fun updateUiState(itemDetails: ItemDetails) {
        _itemUiState.update {
            it.copy(
                itemDetails = itemDetails,
                isEntryValid = validateInput(itemDetails),
            )
        }
    }

    fun saveItem() {
        if (!validateInput()) return
        viewModelScope.launch {
            itemsRepository.insertItem(_itemUiState.value.itemDetails.toItem())
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

