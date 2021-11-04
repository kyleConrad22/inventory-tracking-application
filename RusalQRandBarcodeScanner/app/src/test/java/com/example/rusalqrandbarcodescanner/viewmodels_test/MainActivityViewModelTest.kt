package com.example.rusalqrandbarcodescanner.viewmodels_test

import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.MainCoroutineExtension
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class, MainCoroutineExtension::class)
class MainActivityViewModelTest {

    private lateinit var viewModel : MainActivityViewModel

    @Mock
    private lateinit var application : CodeApplication

    @Mock
    private lateinit var repo : InventoryRepository

    @BeforeEach
    fun setup() {
        viewModel = MainActivityViewModel(application = application, repo = repo)

    }

    @Nested
    inner class ReceptionTest {

        @BeforeEach
        fun setup() {
            viewModel.sessionType.value = SessionType.RECEPTION
        }

        @Nested
        inner class UpdateAddedItemCountTest {

            @ExperimentalCoroutinesApi
            @Test
            fun `updates to correct value given repository has updated number of added items`() = runBlockingTest {

                doReturn(1).`when`(repo).getNumberOfAddedItems()

                assertEquals(0, viewModel.addedItemCount.value)
                viewModel.updateAddedItemCount()
                assertEquals(1, viewModel.addedItemCount.value)
            }

            @ExperimentalCoroutinesApi
            @Test
            fun `does not update given repository has not updated number of added items`() = runBlockingTest {
                viewModel.addedItemCount.value = 1
                doReturn(1).`when`(repo).getNumberOfAddedItems()

                viewModel.updateAddedItemCount()
                assertEquals(1, viewModel.addedItemCount.value)
            }
        }
    }

}