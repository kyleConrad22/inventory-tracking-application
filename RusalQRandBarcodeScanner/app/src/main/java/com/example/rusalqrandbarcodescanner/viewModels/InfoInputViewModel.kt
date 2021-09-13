package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import java.lang.IllegalArgumentException

class InfoInputViewModel(private val repository : UserInputRepository) : ViewModel() {
    private val currentInput = repository.currentInput.asLiveData()

    fun isLoad() : LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()
        mediatorLiveData.addSource(currentInput) { it ->
            mediatorLiveData.removeSource(currentInput)
            mediatorLiveData.value = it[0].type == "Load"
        }
        return mediatorLiveData
    }

    class InfoInputViewModelFactory(private val repository : UserInputRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(InfoInputViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InfoInputViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}