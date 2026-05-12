package com.example.myapplication

import android.content.Context
import com.example.myapplication.data.InventoryDatabase
import com.example.myapplication.data.ItemsRepository
import com.example.myapplication.data.OfflineItemsRepository

interface AppContainer {
    val itemsRepository: ItemsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(InventoryDatabase.getDatabase(context).itemDao())
    }
}

