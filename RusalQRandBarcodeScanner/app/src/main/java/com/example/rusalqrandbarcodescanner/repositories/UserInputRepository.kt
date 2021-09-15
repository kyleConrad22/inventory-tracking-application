package com.example.rusalqrandbarcodescanner.repositories

import androidx.annotation.WorkerThread
import androidx.room.EmptyResultSetException
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.database.UserInputDao
import kotlinx.coroutines.flow.Flow

class UserInputRepository(private val userInputDao: UserInputDao) {
    val currentInput: Flow<List<UserInput>> = userInputDao.getValue()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getInputSuspend() : List<UserInput>? {
        return try {
            userInputDao.getInputSuspend()
        } catch (exc : EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        userInputDao.deleteAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(userInput: UserInput) {
        userInputDao.delete(userInput)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(userInput: UserInput){
        userInputDao.insert(userInput)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(userInput: UserInput) {
        userInputDao.update(userInput)
    }
}