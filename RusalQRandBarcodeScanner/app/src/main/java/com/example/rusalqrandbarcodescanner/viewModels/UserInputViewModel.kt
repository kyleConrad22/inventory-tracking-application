package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class UserInputViewModel(private val repository: UserInputRepository): ViewModel() {

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun insert(userInput: UserInput) = viewModelScope.launch {
        repository.insert(userInput)
    }

    fun delete(userInput: UserInput) = viewModelScope.launch {
        repository.delete(userInput)
    }

    val loader: MutableLiveData<String> = MutableLiveData("")
    val order: MutableLiveData<String> = MutableLiveData("")
    val load: MutableLiveData<String> = MutableLiveData("")
    val bundles: MutableLiveData<String> = MutableLiveData("")
    val bl: MutableLiveData<String> = MutableLiveData("")
    val heat: MutableLiveData<String> = MutableLiveData("")
    val quantity: MutableLiveData<String> = MutableLiveData("")

    private val triggerLoader = MutableLiveData<Unit>()

    fun refresh() {
        triggerLoader.value = Unit
    }

    fun isLoad(): LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()
        mediatorLiveData.addSource(load) { it ->
            mediatorLiveData.value = (it != null && it != "")
        }
        return mediatorLiveData
    }

    fun isReception(): LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()
        mediatorLiveData.addSource(load) { it ->
            mediatorLiveData.value = (it != null && it != "")
        }
        return mediatorLiveData
    }

    class UserInputViewModelFactory(private val repository : UserInputRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(UserInputViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserInputViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}