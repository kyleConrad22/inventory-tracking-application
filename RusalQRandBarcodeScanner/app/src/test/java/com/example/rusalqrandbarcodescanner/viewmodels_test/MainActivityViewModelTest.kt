package com.example.rusalqrandbarcodescanner.viewmodels_test

import androidx.compose.runtime.MutableState
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.MainCoroutineExtension
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import org.junit.Assert.*
import org.mockito.kotlin.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
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

    @AfterEach
    fun validate() {
        validateMockitoUsage()
    }

    // Used to initialize test list of RusalItems of a given size
    private fun getTestList(size : Int) : List<RusalItem> {
        val mutableList = mutableListOf<RusalItem>()

        var i = 0;
        while (i < size) {
            mutableList.add(RusalItem(barcode = "Test${i++}"))
        }
        return mutableList.toList()
    }

    @Nested
    inner class RefreshTest {

        @Test
        fun `calls all internal methods when called`() {
            val viewModelSpy = Mockito.spy(viewModel)

            doReturn(Job()).`when`(viewModelSpy).updateAddedItemCount()
            doReturn(Job()).`when`(viewModelSpy).updateAddedItems()
            doReturn(Job()).`when`(viewModelSpy).updateReceivedItemCount()
            doReturn(Job()).`when`(viewModelSpy).updateInboundItemCount()

            viewModelSpy.refresh()
            verify(viewModelSpy).updateAddedItemCount()
            verify(viewModelSpy).updateAddedItems()
            verify(viewModelSpy).updateReceivedItemCount()
            verify(viewModelSpy).updateInboundItemCount()
        }

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

        @BeforeEach
        fun setup() {
            viewModel.barge.value = "Test"
        }

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

        @BeforeEach
        fun setup() {
            viewModel.barge.value = "Test"
        }

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
    inner class RecreateSessionTest {

        private lateinit var fieldValues : List<MutableState<String>>
        private lateinit var receptionValues : List<MutableState<String>>
        private lateinit var shipmentValues : List<MutableState<String>>

        @BeforeEach
        fun setup() {
            fieldValues = listOf(viewModel.barge, viewModel.checker, viewModel.workOrder, viewModel.loadNum, viewModel.loader, viewModel.quantity, viewModel.bl, viewModel.pieceCount)
            receptionValues = listOf(viewModel.barge, viewModel.checker)
            shipmentValues = listOf(viewModel.workOrder, viewModel.loadNum, viewModel.loader, viewModel.bl, viewModel.pieceCount)
        }

        @Test
        fun `restore session as shipment given loadTime is non-empty and receptionTime is empty`() {

            fieldValues.forEach { assertEquals("", it.value) }

            viewModel.recreateSession(
                RusalItem(loader = "Test", loadNum = "Test", workOrder = "Test", blNum = "Test", quantity = "Test", loadTime = "Test", barcode = "Test", barge = "Test", checker = "Test", receptionDate = "")
            )

            receptionValues.forEach { assertEquals("", it.value) }
            shipmentValues.forEach { assertEquals("Test", it.value) }
            assertEquals("10", viewModel.quantity.value)
        }

        @Test
        fun `restore session as shipment given loadTime is non-empty and receptionTime is non-empty`() {

            fieldValues.forEach { assertEquals("", it.value) }

            viewModel.recreateSession(
                RusalItem(loader = "Test", loadNum = "Test", workOrder = "Test", blNum = "Test", quantity = "Test", loadTime = "Test", barcode = "Test", barge = "Test", checker = "Test", receptionDate = "Test")
            )

            receptionValues.forEach { assertEquals("", it.value) }
            shipmentValues.forEach { assertEquals("Test", it.value) }
            assertEquals("10", viewModel.quantity.value)
        }

        @Test
        fun `restore session as reception given loadTime is empty and receptionTime is non-empty`() {
            fieldValues.forEach { assertEquals("", it.value) }

            viewModel.recreateSession(
                RusalItem(loader = "Test", loadNum = "Test", workOrder = "Test", blNum = "Test", quantity = "Test", loadTime = "", barcode = "Test", barge = "Test", checker = "Test", receptionDate = "Test")
            )

            receptionValues.forEach{ assertEquals("Test", it.value) }
            shipmentValues.forEach { assertEquals("", it.value) }
            assertEquals("", viewModel.quantity.value)
        }

        @Test
        fun `DO SOMETHING given loadTime is empty and receptionTime is empty`() {
            /* TODO - determine logic to be taken in this case and write appropriate test case */
        }
    }

    @Nested
    inner class InsertTest {

        @Test
        fun `calls repo insert method for supplied item`() = runBlockingTest {
            val item = RusalItem(barcode = "Test")
            viewModel.insert(item)
            verify(repo).insert(item)
            verifyNoMoreInteractions(repo)
        }
    }

    @Nested
    inner class ShipmentTest {

        @BeforeEach
        fun setup() {
            viewModel.sessionType.value = SessionType.SHIPMENT
        }

        @Nested
        inner class UpdateDisplayedItemListTest {

            @Test
            fun `equal to addedItems for addedItems size less than 10`() {

                viewModel.addedItems.value = getTestList(9)
                assertEquals(listOf<RusalItem>(), viewModel.displayedItems.value)
                viewModel.updateDisplayedItemList()
                assertEquals(viewModel.addedItems.value, viewModel.displayedItems.value)
            }

            @Test
            fun `equal to addedItems for addedItems size equal to 10`() {

                viewModel.addedItems.value = getTestList(10)
                assertEquals(listOf<RusalItem>(), viewModel.displayedItems.value)
                viewModel.updateDisplayedItemList()
                assertEquals(viewModel.addedItems.value, viewModel.displayedItems.value)
            }

            @Test
            fun `equal to addedItems for addedItems size greater than 10`() {

                viewModel.addedItems.value = getTestList(11)
                assertEquals(listOf<RusalItem>(), viewModel.displayedItems.value)
                viewModel.updateDisplayedItemList()
                assertEquals(viewModel.addedItems.value, viewModel.displayedItems.value)
            }

        }

        @Nested
        inner class SetDisplayRemoveEntryContentTest {

            @Test
            fun `updates to false when quantity is empty`() {
                viewModel.quantity.value = ""
                viewModel.addedItemCount.value = 1

                assertFalse(viewModel.displayRemoveEntryContent.value)
                viewModel.setDisplayRemoveEntryContent()
                assertFalse(viewModel.displayRemoveEntryContent.value)
            }

            @Test
            fun `updates to false when quantity is 0`() {
                viewModel.quantity.value = "0"
                viewModel.addedItemCount.value = 1

                assertFalse(viewModel.displayRemoveEntryContent.value)
                viewModel.setDisplayRemoveEntryContent()
                assertFalse(viewModel.displayRemoveEntryContent.value)
            }

            @Test
            fun `updates to false when quantity is greater than 0 and addedItemCount is 0`() {
                viewModel.quantity.value = "1"
                viewModel.addedItemCount.value = 0

                assertFalse(viewModel.displayRemoveEntryContent.value)
                viewModel.setDisplayRemoveEntryContent()
                assertFalse(viewModel.displayRemoveEntryContent.value)
            }

            @Test
            fun `updates to true when quantity is greater than 0 and addedItemCount is greater than 0`() {
                viewModel.quantity.value = "1"
                viewModel.addedItemCount.value = 1

                assertFalse(viewModel.displayRemoveEntryContent.value)
                viewModel.setDisplayRemoveEntryContent()
                assertTrue(viewModel.displayRemoveEntryContent.value)
            }

            @Test
            fun `updates to false when quantity is not an integer`() {
                viewModel.quantity.value = "f"
                viewModel.addedItemCount.value = 1

                assertFalse(viewModel.displayRemoveEntryContent.value)
                viewModel.setDisplayRemoveEntryContent()
                assertFalse(viewModel.displayRemoveEntryContent.value)
            }
        }

        @Nested
        inner class RemoveAllAddedItemsTest {

            private lateinit var testItemList : List<RusalItem>

            @BeforeEach
            fun setup() = runBlockingTest {
                doReturn(listOf<RusalItem>()).`when`(repo).getAddedItems()
            }

            @Test
            fun `delete item from repo given it is a new item`() = runBlockingTest {
                testItemList = listOf(RusalItem(barcode = "n"))
                viewModel.addedItems.value = testItemList

                viewModel.removeAllAddedItems()
                verify(repo).delete(testItemList[0])
                verify(repo, never()).removeItemFromShipment(anyString())
                verify(repo, never()).removeItemFromReception(anyString())
            }

            @Test
            fun `delete item from repo given it is an unidentified item`() = runBlockingTest {
                testItemList = listOf(RusalItem(barcode = "u"))
                viewModel.addedItems.value = testItemList

                viewModel.removeAllAddedItems()
                verify(repo).delete(testItemList[0])
                verify(repo, never()).removeItemFromReception(anyString())
                verify(repo, never()).removeItemFromShipment(anyString())
            }

            @Test
            fun `delete multiple items from repo given items are either new or unidentified`() = runBlockingTest {
                testItemList = listOf(RusalItem(barcode = "n"), RusalItem(barcode = "u"))
                viewModel.addedItems.value = testItemList

                viewModel.removeAllAddedItems()
                verify(repo).delete(testItemList[0])
                verify(repo).delete(testItemList[1])
                verify(repo, never()).removeItemFromShipment(anyString())
                verify(repo, never()).removeItemFromReception(anyString())
            }

            @Test
            fun `call removeItemFromShipment given item is neither new nor unidentified`() = runBlockingTest {
                testItemList = listOf(RusalItem(barcode = "Test", heatNum = "1"))
                viewModel.addedItems.value = testItemList

                viewModel.removeAllAddedItems()
                verify(repo, never()).delete(any())
                verify(repo).removeItemFromShipment("1")
                verify(repo, never()).removeItemFromReception(anyString())
            }

            @Test
            fun `call removeItemFromShipment multiple times given items are neither new nor unidentified`() = runBlockingTest {
                testItemList = listOf(RusalItem(barcode = "Test1", heatNum = "1"), RusalItem(barcode = "Test2", heatNum = "2"))
                viewModel.addedItems.value = testItemList

                viewModel.removeAllAddedItems()
                verify(repo, never()).delete(any())
                listOf("1", "2").forEach { verify(repo).removeItemFromShipment(it) }
                verify(repo, never()).removeItemFromReception(anyString())

            }
        }
    }

    @Nested
    inner class ReceptionTest {

        @BeforeEach
        fun setup() {
            viewModel.sessionType.value = SessionType.RECEPTION
        }

        @Nested
        inner class UpdateDisplayedItemsListTest {

            @Test
            fun `set equal to addedItems given addedItems size is less than 10`() {

                viewModel.addedItems.value = getTestList(9)
                assertEquals(listOf<RusalItem>(), viewModel.displayedItems.value)
                viewModel.updateDisplayedItemList()
                assertEquals(viewModel.addedItems.value, viewModel.displayedItems.value)
            }

            @Test
            fun `set equal to addedItems given addedItems size is equal to 10`() {

                viewModel.addedItems.value = getTestList(10)
                assertEquals(listOf<RusalItem>(), viewModel.displayedItems.value)
                viewModel.updateDisplayedItemList()
                assertEquals(viewModel.addedItems.value, viewModel.displayedItems.value)
            }

            @Test
            fun `set equal to last 10 items from addedItems given addedItems size is greater than 10`() {

                viewModel.addedItems.value = getTestList(11)
                assertEquals(listOf<RusalItem>(), viewModel.displayedItems.value)
                viewModel.updateDisplayedItemList()
                assertEquals(viewModel.addedItems.value.subList(viewModel.addedItems.value.size - 10, viewModel.addedItems.value.size), viewModel.displayedItems.value)
            }

        }

        @Nested
        inner class SetDisplayRemoveEntryContentTest {

            @Test
            fun `updates to false in all cases`() {
                assertFalse(viewModel.displayRemoveEntryContent.value)
                viewModel.setDisplayRemoveEntryContent()
                assertFalse(viewModel.displayRemoveEntryContent.value)
            }
        }

        @Nested
        inner class RemoveAllAddedItemsTest {

            private lateinit var testItemList : List<RusalItem>

            @BeforeEach
            fun setup() = runBlockingTest {
                doReturn(listOf<RusalItem>()).`when`(repo).getAddedItems()
            }

            @Test
            fun `delete item from repo given it is a new item`() = runBlockingTest {
                testItemList = listOf(RusalItem(barcode = "n"))
                viewModel.addedItems.value = testItemList

                viewModel.removeAllAddedItems()
                verify(repo).delete(testItemList[0])
                verify(repo, times(0)).removeItemFromShipment(anyString())
                verify(repo, times(0)).removeItemFromReception(anyString())
            }

            @Test
            fun `delete item from repo given it is an unidentified item`() = runBlockingTest {
                testItemList = listOf(RusalItem(barcode = "u"))
                viewModel.addedItems.value = testItemList

                viewModel.removeAllAddedItems()
                verify(repo).delete(testItemList[0])
                verify(repo, times(0)).removeItemFromReception(anyString())
                verify(repo, times(0)).removeItemFromShipment(anyString())
            }

            @Test
            fun `delete multiple items from repo given items are either new or unidentified`() = runBlockingTest {
                testItemList = listOf(RusalItem(barcode = "n"), RusalItem(barcode = "u"))
                viewModel.addedItems.value = testItemList

                viewModel.removeAllAddedItems()
                verify(repo).delete(testItemList[0])
                verify(repo).delete(testItemList[1])
                verify(repo, never()).removeItemFromShipment(anyString())
                verify(repo, times(0)).removeItemFromReception(anyString())
            }

            @Test
            fun `call removeItemFromReception given item is neither new nor unidentified`() = runBlockingTest {
                testItemList = listOf(RusalItem(barcode = "Test", heatNum = "1"))
                viewModel.addedItems.value = testItemList

                viewModel.removeAllAddedItems()
                verify(repo, times(0)).delete(any())
                verify(repo).removeItemFromReception("1")
                verify(repo, times(0)).removeItemFromShipment(anyString())
            }

            @Test
            fun `call removeItemFromReception multiple times given items are all neither new nor unidentified`() = runBlockingTest {
                testItemList = listOf(RusalItem(barcode = "Test1", heatNum = "1"), RusalItem(barcode = "Test2", heatNum = "2"))
                viewModel.addedItems.value = testItemList

                viewModel.removeAllAddedItems()
                verify(repo, times(0)).delete(any())
                listOf("1", "2").forEach { verify(repo).removeItemFromReception(it) }
                verify(repo, times(0)).removeItemFromShipment(anyString())
            }
        }

    }

}