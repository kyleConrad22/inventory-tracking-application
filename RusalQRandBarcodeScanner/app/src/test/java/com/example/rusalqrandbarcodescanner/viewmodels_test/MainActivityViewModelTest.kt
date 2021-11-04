package com.example.rusalqrandbarcodescanner.viewmodels_test

import androidx.compose.runtime.MutableState
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
        viewModel.barge.value = "Test"

    }

    @Nested
    inner class ClearInputFieldsTest {

        private lateinit var inputFields : List<MutableState<String>>

        @BeforeEach
        fun setup() {
            inputFields = listOf(
                viewModel.quantity, viewModel.heatNum, viewModel.workOrder, viewModel.barge, viewModel.pieceCount, viewModel.bl,
                viewModel.loadNum, viewModel.loader, viewModel.checker
            )

            inputFields.forEach { it.value = "Test" }
        }

        @Test
        fun `clear all fields when function is called`() {
            viewModel.clearInputFields()
            inputFields.forEach {
                assertEquals("", it.value)
            }
        }
    }

    @Nested
    inner class UpdateReceivedItemCountTest {

        @Test
        fun `updates to correct value given there exist received items for given barge`() = runBlockingTest {
            doReturn(3).`when`(repo).getReceivedItemCount("Test")

            assertEquals(0, viewModel.receivedItemCount.value)
            viewModel.updateReceivedItemCount()
            assertEquals(3, viewModel.receivedItemCount.value)
        }

        @Test
        fun `does not update given there do not exist received items for given barge`() = runBlockingTest {
            doReturn(0).`when`(repo).getReceivedItemCount("Test")

            assertEquals(0, viewModel.receivedItemCount.value)
            viewModel.updateReceivedItemCount()
            assertEquals(0, viewModel.receivedItemCount.value)
        }
    }

    @Nested
    inner class UpdateAddedItemCountTest {

        @Test
        fun `updates to correct value given repository has updated number of added items`() = runBlockingTest {
            doReturn(1).`when`(repo).getNumberOfAddedItems()

            assertEquals(0, viewModel.addedItemCount.value)
            viewModel.updateAddedItemCount()
            assertEquals(1, viewModel.addedItemCount.value)
        }

        @Test
        fun `does not update given repository has not updated number of added items`() = runBlockingTest {
            viewModel.addedItemCount.value = 1
            doReturn(1).`when`(repo).getNumberOfAddedItems()

            assertEquals(1, viewModel.addedItemCount.value)
            viewModel.updateAddedItemCount()
            assertEquals(1, viewModel.addedItemCount.value)
        }
    }

    @Nested
    inner class UpdateInboundItemCountTest {

        @Test
        fun `updates given there exist inbound items on given barge`() = runBlockingTest {
            doReturn(3).`when`(repo).getInboundItemCount("Test")

            assertEquals(0, viewModel.inboundItemCount.value)
            viewModel.updateInboundItemCount()
            assertEquals(3, viewModel.inboundItemCount.value)
        }

        @Test
        fun `does not update given there do not exist inbound items on given barge`() = runBlockingTest {
            doReturn(0).`when`(repo).getInboundItemCount("Test")

            assertEquals(0, viewModel.inboundItemCount.value)
            viewModel.updateInboundItemCount()
            assertEquals(0, viewModel.inboundItemCount.value)
        }
    }

    @Nested
    inner class ShipmentTest {

        @BeforeEach
        fun setup() {
            viewModel.sessionType.value = SessionType.SHIPMENT
        }

    }

    @Nested
    inner class ReceptionTest {

        @BeforeEach
        fun setup() {
            viewModel.sessionType.value = SessionType.RECEPTION
        }

    }

}