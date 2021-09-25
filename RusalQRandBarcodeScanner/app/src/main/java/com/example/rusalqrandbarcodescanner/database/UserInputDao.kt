package com.example.rusalqrandbarcodescanner.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInputDao {
    @Query("SELECT * FROM userInput")
    fun getValue(): Flow<List<UserInput>>

    @Query("SELECT * FROM userInput")
    suspend fun getInputSuspend() : List<UserInput>

    @Query("DELETE FROM userInput")
    suspend fun deleteAll()

    @Query("UPDATE userInput SET heatNum =:searchHeat WHERE id =:searchId")
    suspend fun updateHeat(searchHeat : String, searchId : String)

    @Update
    suspend fun update(vararg  userInput: UserInput)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg userInput: UserInput)

    @Delete
    suspend fun delete(vararg userInput: UserInput)
}