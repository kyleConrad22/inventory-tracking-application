package com.example.rusalqrandbarcodescanner.database_test

import com.example.rusalqrandbarcodescanner.database.InventoryDao
import com.example.rusalqrandbarcodescanner.database.RusalItem
import kotlinx.coroutines.runBlocking
import org.junit.Test

class InventoryDaoTest {
    private lateinit var invRepo : InventoryDao

    @Test
    fun insertTest() {
        runBlocking {
            invRepo.deleteAll()
            val testItem = RusalItem(heatNum = "1720161", packageNum = "17", barcode = "1235336")
        }
    }
}