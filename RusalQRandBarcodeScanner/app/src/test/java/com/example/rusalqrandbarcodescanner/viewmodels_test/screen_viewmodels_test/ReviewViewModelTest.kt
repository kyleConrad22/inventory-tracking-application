package com.example.rusalqrandbarcodescanner.viewmodels_test.screen_viewmodels_test

import androidx.compose.runtime.mutableStateOf
import com.example.rusalqrandbarcodescanner.MainCoroutineExtension
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ReviewViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class, MainCoroutineExtension::class)
class ReviewViewModelTest {

    private lateinit var viewModel : ReviewViewModel

    @Mock
    private lateinit var repo : InventoryRepository

    @Mock
    private lateinit var mainActivityVM: MainActivityViewModel

    @BeforeEach
    fun setup() {
        viewModel = ReviewViewModel(repo, mainActivityVM)
    }

    @Nested
    inner class RemoveItemTest {

        @Test
        fun `verify new items are deleted`() = runBlockingTest {
            viewModel.removeItem(RusalItem(barcode = "n"))

            verify(repo).delete(any())
        }

        @Test
        fun `verify unidentified items are deleted`() = runBlockingTest {
            viewModel.removeItem(RusalItem(barcode = "u"))

            verify(repo).delete(any())
        }

        @Test
        fun `verify items are removed from shipment if sessionType is shipment`() = runBlockingTest {
            doReturn(mutableStateOf(SessionType.SHIPMENT)).`when`(mainActivityVM).sessionType

            viewModel.removeItem(RusalItem(barcode = "1"))
            verify(repo).removeItemFromShipment(anyString())
        }

        @Test
        fun `verify items are removed from reception if sessionType is reception`() = runBlockingTest {
            doReturn(mutableStateOf(SessionType.RECEPTION)).`when`(mainActivityVM).sessionType

            viewModel.removeItem(RusalItem(barcode = "1"))
            verify(repo).removeItemFromReception(anyString())
        }
    }

    @Nested
    inner class ClearAddedItemsTest {


        @Test
        fun `verify internal functions are called as expected`() = runBlockingTest {
            doNothing().`when`(repo).removeAllAddedItems()
            doNothing().`when`(mainActivityVM).refresh()

            viewModel.clearAddedItems()
            verify(repo).removeAllAddedItems()
            verify(mainActivityVM).refresh()
        }
    }

    @Nested
    inner class InitiateUpdateTest {
        /* TODO - Refactor classes which call methods from singleton such that singleton is passed as a parameter value, currently is untestable */
    }
}