package com.example.rusalqrandbarcodescanner

import android.app.Application
import com.example.rusalqrandbarcodescanner.database.CodeDatabase
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CodeApplication: Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { CodeDatabase.getDatabase(this, applicationScope)}
    val repository by lazy { CodeRepository(database.scannedCodeDao()) }
    val invRepository by lazy { CurrentInventoryRepository(database.currentInventoryDao()) }
    val userRepository by lazy { UserInputRepository(database.userInputDao()) }
}