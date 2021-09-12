package com.example.rusalqrandbarcodescanner.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInputDao {
    @Query("SELECT * FROM userInput")
    fun getValue(): Flow<List<UserInput>>

    @Query("DELETE FROM userInput")
    suspend fun deleteAll()

    @Update
    suspend fun update(vararg  userInput: UserInput)

    @Insert
    suspend fun insert(vararg userInput: UserInput)

    @Delete
    suspend fun delete(vararg userInput: UserInput)
}