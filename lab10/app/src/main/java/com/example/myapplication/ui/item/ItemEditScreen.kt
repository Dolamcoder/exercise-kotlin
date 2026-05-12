package com.example.myapplication.ui.item

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.ui.inventoryApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEditScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemEditViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ItemEditViewModel(
                    this.createSavedStateHandle(),
                    inventoryApplication().container.itemsRepository,
                )
            }
        },
    ),
) {
    val itemUiState by viewModel.itemUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Edit Item") }) },
        modifier = modifier,
    ) { innerPadding ->
        ItemInputBody(
            itemUiState = itemUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                viewModel.updateItem()
                onBack()
            },
            modifier = Modifier.padding(innerPadding),
        )
    }
}


