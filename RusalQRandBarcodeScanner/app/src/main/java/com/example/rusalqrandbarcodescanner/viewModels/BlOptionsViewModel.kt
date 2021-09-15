package com.example.rusalqrandbarcodescanner.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class BlOptionsViewModel(private val userRepo : UserInputRepository, private val invRepo : CurrentInventoryRepository) : ViewModel() {
    val list : List<String> = listOf()
    val loading = mutableStateOf(true)
    val blList = mutableStateOf(list)

    private suspend fun getHeat() : String? {
        var result : String? = null
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                result = userRepo.getInputSuspend()?.get(0)?.heatNum
            }
        }
        println(value.await())
        return result
    }

    fun setBlList() {
        loading.value = true
        val list = mutableListOf<String>()
        GlobalScope.launch(Dispatchers.Main) {
            val repoData = getHeat()?.let { findByBaseHeat(it) }
            if (!repoData.isNullOrEmpty()) {
                for (lineItem in repoData) {
                    if (list.find { it == lineItem.blNum } == null) {
                        Log.d("DEBUG", lineItem.blNum.toString())
                        list.add(lineItem.blNum!!)
                    }
                }
            }
            blList.value = list.toList()
            loading.value = false
        }
    }

    private suspend fun findByBaseHeat(heat : String) : List<CurrentInventoryLineItem>? {
        var result : List<CurrentInventoryLineItem>? = null
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                result = invRepo.findByBaseHeat("%$heat%")
            }
        }
        println(value.await())
        return result
    }

    class BlOptionsViewModelFactory(private val userRepo : UserInputRepository, private val invRepo : CurrentInventoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(BlOptionsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BlOptionsViewModel(userRepo, invRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}