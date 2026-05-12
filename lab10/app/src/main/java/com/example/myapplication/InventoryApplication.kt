package com.example.myapplication

import android.app.Application

/**
 * [Application] subclass that is used to provide global singletons using an
 * in-process [AppContainer] instance.
 */
class InventoryApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}

