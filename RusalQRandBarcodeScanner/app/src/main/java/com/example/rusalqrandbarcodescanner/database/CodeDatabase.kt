package com.example.rusalqrandbarcodescanner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database (entities = arrayOf(ScannedCode::class, CurrentInventoryLineItem::class), version = 4)
abstract class CodeDatabase: RoomDatabase() {
    abstract fun scannedCodeDao(): ScannedCodeDao
    abstract fun currentInventoryDao(): CurrentInventoryDao

    private class CodeDatabaseCallback (
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate (db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val scannedCodeDao = database.scannedCodeDao()
                    val currentInventoryDao = database.currentInventoryDao()
                    scannedCodeDao.deleteAll()
                    currentInventoryDao.deleteAll()
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