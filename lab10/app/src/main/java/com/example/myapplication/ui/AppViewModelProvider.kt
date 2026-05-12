package com.example.myapplication.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.InventoryApplication

fun CreationExtras.inventoryApplication(): InventoryApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication




