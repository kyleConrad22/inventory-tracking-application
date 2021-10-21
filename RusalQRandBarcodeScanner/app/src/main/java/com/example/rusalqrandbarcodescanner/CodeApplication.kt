package com.example.rusalqrandbarcodescanner

import android.app.Application
import com.example.rusalqrandbarcodescanner.database.CodeDatabase
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CodeApplication: Application() {


    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { CodeDatabase.getDatabase(this, applicationScope)}
    val invRepository by lazy { InventoryRepository(database.inventoryDao()) }

}