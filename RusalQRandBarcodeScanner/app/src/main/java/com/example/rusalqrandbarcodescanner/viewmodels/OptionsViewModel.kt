package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class OptionsViewModel(private val userRepository : UserInputRepository, private val codeRepository : CodeRepository) : ViewModel() {
    val currentInput = userRepository.currentInput.asLiveData()
    private val count: LiveData<Int> = codeRepository.count.asLiveData()

    val loading = mutableStateOf(false)
    val displayCountButtons = mutableStateOf(count.value != null && count.value!! > 0)
    val displayRemoveEntry = mutableStateOf(count.value != null && currentInput.value != null && Integer.parseInt(currentInput.value!![0].bundleQuantity!!) - count.value!! > 0)

    fun deleteAll() = viewModelScope.launch {
        codeRepository.deleteAll()
    }

    fun isLoad() : LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()
        if (currentInput.value != null) {
            mediatorLiveData.value = (currentInput.value!![0].type == "Load")
        } else {
            mediatorLiveData.addSource(currentInput) { it ->
                mediatorLiveData.removeSource(currentInput)
                mediatorLiveData.value =
                    when {
                        it.isEmpty() -> {
                            null
                        }
                        it[0].type == "Load" -> {
                            true
                        }
                        it[0].type == "Reception" -> {
                            false
                        }
                        else -> {
                            null
                        }
                    }
                loading.value = mediatorLiveData.value == null
            }
        }
        return mediatorLiveData
    }

    class OptionsViewModelFactory(private val userRepository : UserInputRepository, private val codeRepository : CodeRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(OptionsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OptionsViewModel(userRepository, codeRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}