package com.example.rusalqrandbarcodescanner

import android.app.Application
import com.example.rusalqrandbarcodescanner.database.CodeDatabase
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CodeApplication: Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { CodeDatabase.getDatabase(this, applicationScope)}
    val invRepository by lazy { InventoryRepository(database.inventoryDao()) }
    val userRepository by lazy { UserInputRepository(database.userInputDao()) }
}