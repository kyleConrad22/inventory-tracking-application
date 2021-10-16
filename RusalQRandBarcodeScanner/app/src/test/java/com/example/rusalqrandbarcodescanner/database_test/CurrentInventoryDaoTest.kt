package com.example.rusalqrandbarcodescanner.database_test

import com.example.rusalqrandbarcodescanner.database.CurrentInventoryDao
import com.example.rusalqrandbarcodescanner.database.RusalItem
import kotlinx.coroutines.runBlocking
import org.junit.Test

class CurrentInventoryDaoTest {
    private lateinit var invRepo : CurrentInventoryDao

    @Test
    fun insertTest() {
        runBlocking {
            invRepo.deleteAll()
            val testItem = RusalItem(heatNum = "1720161", packageNum = "17", barcode = "1235336")
        }
    }
}