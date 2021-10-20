package com.example.rusalqrandbarcodescanner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database (entities = arrayOf(RusalItem::class), version = 17)
abstract class CodeDatabase: RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao

    private class CodeDatabaseCallback (
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate (db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val inventoryDao = database.inventoryDao()
                    inventoryDao.deleteAll()
                }
            }
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: CodeDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CodeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CodeDatabase::class.java,
                    "scannedCodes"
                )
                .addCallback(CodeDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}