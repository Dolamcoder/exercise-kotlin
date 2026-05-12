package com.example.myapplication.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.ui.inventoryApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailsScreen(
    onBack: () -> Unit,
    onEditItem: (Int) -> Unit,
    onDeleteItem: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemDetailsViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ItemDetailsViewModel(
                    this.createSavedStateHandle(),
                    inventoryApplication().container.itemsRepository,
                )
            }
        },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Item Details") }) },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = uiState.itemDetails.name)
            Text(text = "Price: ${uiState.itemDetails.toItem().formattedPrice()}")
            Text(text = "Quantity: ${uiState.itemDetails.quantity}")

            Button(
                onClick = viewModel::reduceQuantityByOne,
                enabled = !uiState.outOfStock,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Sell")
            }
            OutlinedButton(
                onClick = { onEditItem(uiState.itemDetails.id) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Edit")
            }
            OutlinedButton(
                onClick = {
                    viewModel.deleteItem()
                    onDeleteItem()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Delete")
            }
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Back")
            }
        }
    }
}


