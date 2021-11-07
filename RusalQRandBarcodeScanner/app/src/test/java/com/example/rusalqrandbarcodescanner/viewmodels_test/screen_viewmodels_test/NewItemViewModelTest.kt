package com.example.rusalqrandbarcodescanner.viewmodels_test.screen_viewmodels_test

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.rusalqrandbarcodescanner.MainCoroutineExtension
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.NewItemViewModel
import org.junit.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.validateMockitoUsage
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class, MainCoroutineExtension::class)
class NewItemViewModelTest {

    private lateinit var viewModel : NewItemViewModel

    @Mock
    private lateinit var repo : InventoryRepository

    @Mock
    private lateinit var mainActivityVM : MainActivityViewModel

    @BeforeEach
    fun setup() {
        doReturn(mutableStateOf("")).`when`(mainActivityVM).heatNum
        doReturn(RusalItem(barcode = "")).`when`(mainActivityVM).scannedItem
        viewModel = NewItemViewModel(mainActivityVM, repo)
    }

    @AfterEach
    fun validate() {
        validateMockitoUsage()
    }

    @Nested
    inner class TrySettingItemValuesViaScanTest {

        @Test
        fun `sets scannedItem and fields equivalent to main activity scannedItem fields given main activity scannedItem has a non-empty barcode`() {
            doReturn(RusalItem(barcode = "Test", grade = "Test", netWeightKg = "Test", grossWeightKg = "Test", quantity = "Test")).`when`(mainActivityVM).scannedItem

            viewModel.trySettingItemValuesViaScan()
            assertEquals(mainActivityVM.scannedItem, viewModel.scannedItem)
            assertEquals("Test", viewModel.scannedItemQuantity.value)
            assertEquals("Test", viewModel.scannedItemNetWeight.value)
            assertEquals("Test", viewModel.scannedItemGrossWeight.value)
            assertEquals("Test", viewModel.scannedItemGrade.value)
        }

        @Test
        fun `does not set scannedItem and fields to main activity scannedItem fields given main activity scannedItem has an empty barcode`() {
            doReturn(RusalItem(barcode = "", grade = "Test", quantity = "Test", grossWeightKg = "Test", netWeightKg = "Test")).`when`(mainActivityVM).scannedItem

            viewModel.trySettingItemValuesViaScan()

            assertEquals(RusalItem(barcode = ""), viewModel.scannedItem)
            assertEquals("", viewModel.scannedItemGrade.value)
            assertEquals("", viewModel.scannedItemGrossWeight.value)
            assertEquals("", viewModel.scannedItemNetWeight.value)
            assertEquals("", viewModel.scannedItemQuantity.value)

        }
    }

    @Nested
    inner class RefreshTest {

        private lateinit var inputFields : List<MutableState<String>>

        @BeforeEach
        fun setup() {
            inputFields = listOf(viewModel.scannedItemGrade, viewModel.scannedItemGrossWeight, viewModel.scannedItemMark, viewModel.scannedItemNetWeight, viewModel.scannedItemQuantity)
            inputFields.forEach { it.value = "Test" }
            viewModel.isValidMark = true
            viewModel.isValidQuantity = true
            viewModel.isValidNetWeight = true
            viewModel.isValidGrossWeight = true
        }

        @Test
        fun `sets isConfirmAdditionVisible false if any of the input fields are empty`() {

            inputFields.forEach {
                it.value = ""
                viewModel.refresh()
                assertFalse(viewModel.isConfirmAdditionVisible.value)
                it.value = ""
            }
        }

        @Test
        fun `sets isConfirmAdditionVisible false if any of the inputs are invalid`() {
            viewModel.isValidMark = false
            viewModel.refresh()
            assertFalse(viewModel.isConfirmAdditionVisible.value)
            viewModel.isValidMark = true

            viewModel.isValidGrossWeight = false
            viewModel.refresh()
            assertFalse(viewModel.isConfirmAdditionVisible.value)
            viewModel.isValidGrossWeight = true

            viewModel.isValidNetWeight = false
            viewModel.refresh()
            assertFalse(viewModel.isConfirmAdditionVisible.value)
            viewModel.isValidNetWeight = true

            viewModel.isValidQuantity = false
            viewModel.refresh()
            assertFalse(viewModel.isConfirmAdditionVisible.value)
            viewModel.isValidQuantity = true
        }

        @Test
        fun `sets isConfirmAdditionVisible true when all input fields are non-empty and are all inputs are valid`() {
            viewModel.refresh()
            assertTrue(viewModel.isConfirmAdditionVisible.value)
        }
    }

    @Nested
    inner class ReceiveNewItemTest {

        private lateinit var viewModelSpy : NewItemViewModel

        @BeforeEach
        fun setup() = runBlockingTest {
            viewModelSpy = Mockito.spy(viewModel)
            listOf(viewModelSpy.scannedItemQuantity, viewModelSpy.scannedItemNetWeight, viewModelSpy.scannedItemMark, viewModelSpy.scannedItemGrossWeight, viewModelSpy.scannedItemGrade).forEach {
                it.value = "Test"
            }
            doReturn(mutableStateOf("Test")).`when`(mainActivityVM).barge
            doReturn(Job()).`when`(viewModelSpy).addItem(any())
        }

        @Test
        fun `does not set scannedItem fields to input values, given barcode is not empty`() {
            viewModelSpy.scannedItem = RusalItem(barcode = "Test")

            viewModelSpy.receiveNewItem()
            assertEquals(RusalItem(barcode = "Test", blNum = "N/A", mark = "Test", barge = "Test", lot = "N/A"), viewModelSpy.scannedItem)
            verify(viewModelSpy).addItem(any())
        }

        @Test
        fun `sets scannedItem fields to input values and barcode as a new item barcode, given barcode is empty and it is not a base heat`() {
            viewModelSpy.scannedItem = RusalItem(barcode = "")
            viewModelSpy.heat = "12345678"

            viewModelSpy.receiveNewItem()
            assertEquals(RusalItem(barcode = "${viewModelSpy.heat}n", heatNum = viewModelSpy.heat, blNum = "N/A", mark = "Test", barge = "Test", lot = "N/A", grade = "Test", grossWeightKg = "Test", netWeightKg = "Test", quantity = "Test"), viewModelSpy.scannedItem)
            verify(viewModelSpy).addItem(any())
        }

        @Test
        fun `sets scannedItem fields to input values and barcode as an unidentified item barcode, given barcode is empty and it is a base heat`() = runBlockingTest {
            viewModelSpy.scannedItem = RusalItem(barcode = "")
            viewModelSpy.heat = "123456"
            doReturn(0).`when`(viewModelSpy).getNumberOfUnidentifiedBundles(anyString())

            viewModelSpy.receiveNewItem()
            assertEquals(RusalItem(barcode = "${viewModelSpy.heat}u1", heatNum = viewModelSpy.heat, blNum = "N/A", mark = "Test", barge = "Test", grade = "Test", quantity = "Test", netWeightKg = "Test", grossWeightKg = "Test", lot = "N/A"), viewModelSpy.scannedItem)
            verify(viewModelSpy).addItem(any())
        }

    }

    @Nested
    inner class ValidateMarkTest {

        private fun Char.repeat(count : Int) : String = this.toString().repeat(count)

        @Test
        fun `sets false for length greater than 30`() {

            viewModel.validateMark('A'.repeat(31))
            assertFalse(viewModel.isValidMark)
        }

        @Test
        fun `sets false for length equal to 30`() {

            viewModel.validateMark('A'.repeat(30))
            assertFalse(viewModel.isValidMark)
        }

        @Test
        fun `sets true for length less than 30`() {

            viewModel.validateMark('A'.repeat(29))
            assertTrue(viewModel.isValidMark)
        }
    }

    @Nested
    inner class ValidateGrossWeightTest {

        @Test
        fun `sets false for length greater than 5`() {

            viewModel.validateGrossWeight("123456")
            assertFalse(viewModel.isValidGrossWeight)
        }

        @Test
        fun `sets false for length equal to 5`() {

            viewModel.validateGrossWeight("12345")
            assertFalse(viewModel.isValidGrossWeight)
        }

        @Test
        fun `sets true for length less than 5 and is a valid integer`() {

            viewModel.validateGrossWeight("1234")
            assertTrue(viewModel.isValidGrossWeight)
        }

        @Test
        fun `sets false for length less than 5 and is not a valid integer`() {

            for (i in 32..47) {
                viewModel.validateGrossWeight("123${i.toChar()}")
                assertFalse(viewModel.isValidGrossWeight)
            }
            for (i in 58..126) {
                viewModel.validateGrossWeight("123${i.toChar()}")
                assertFalse(viewModel.isValidGrossWeight)
            }
        }
    }

    @Nested
    inner class ValidateNetWeightTest {

        @Test
        fun `sets false for length greater than 5`() {

            viewModel.validateNetWeight("123456")
            assertFalse(viewModel.isValidNetWeight)
        }

        @Test
        fun `sets false for length equal to 5`() {

            viewModel.validateNetWeight("12345")
            assertFalse(viewModel.isValidNetWeight)
        }

        @Test
        fun `sets true for length less than 5 and is a valid integer`() {

            viewModel.validateNetWeight("1234")
            assertTrue(viewModel.isValidNetWeight)
        }

        @Test
        fun `sets false for length less than 5 and is not a valid integer`() {

            for (i in 32..47) {
                viewModel.validateNetWeight("123${i.toChar()}")
                assertFalse(viewModel.isValidNetWeight)
            }
            for (i in 58..126) {
                viewModel.validateNetWeight("123${i.toChar()}")
                assertFalse(viewModel.isValidNetWeight)
            }
        }

    }

    @Nested
    inner class ValidateQuantityTest {

        @Test
        fun `sets false for length greater than 3`() {

            viewModel.validateQuantity("1234")
            assertFalse(viewModel.isValidQuantity)
        }

        @Test
        fun `sets false for length equal to 3`() {

            viewModel.validateQuantity("123")
            assertFalse(viewModel.isValidQuantity)
        }

        @Test
        fun `sets true for length less than 3 and is a valid integer`() {

            viewModel.validateQuantity("12")
            assertTrue(viewModel.isValidQuantity)
        }

        @Test
        fun `sets false for length less than 2 and is not a valid integer`() {

            for (i in 32..47) {
                viewModel.validateQuantity("1${i.toChar()}")
                assertFalse(viewModel.isValidQuantity)
            }
            for (i in 58..126) {
                viewModel.validateQuantity("1${i.toChar()}")
                assertFalse(viewModel.isValidQuantity)
            }
        }
    }

    @Nested
    inner class GetNumberOfUnidentifiedBundlesTest {

        @Test
        fun `returns 0 when repository returns null`() = runBlockingTest {
            doReturn(null).`when`(repo).findByBarcodes("123456u")

            assertEquals(0, viewModel.getNumberOfUnidentifiedBundles("123456"))
        }

        @Test
        fun `returns 0 if there are no unidentified bundles for the given heat`() = runBlockingTest {
            doReturn(listOf<RusalItem>()).`when`(repo).findByBarcodes("123456u")

            assertEquals(0, viewModel.getNumberOfUnidentifiedBundles("123456"))
        }

        @Test
        fun `returns 1 if there is only a single unidentified bundle for the given heat`() = runBlockingTest {
            doReturn(listOf(RusalItem(barcode = "Test"))).`when`(repo).findByBarcodes("123456u")

            assertEquals(1, viewModel.getNumberOfUnidentifiedBundles("123456"))
        }

        @Test
        fun `returns x if there are x unidentified bundles for the given heat`() = runBlockingTest {
            doReturn(listOf(RusalItem(barcode = "Test"), RusalItem(barcode = "Test"))).`when`(repo).findByBarcodes("123456u")

            assertEquals(2, viewModel.getNumberOfUnidentifiedBundles("123456"))
        }
    }

    @Nested
    inner class AddItemTest {
        @Test
        fun `verify all expected functions are called when item is added`() = runBlockingTest {
            val item = RusalItem(barcode = "Test")
            doReturn(mutableStateOf("Test")).`when`(mainActivityVM).checker

            viewModel.addItem(item)
            verify(repo).insert(item)
            verify(repo).updateIsAddedStatusViaBarcode(true, item.barcode)
            verify(repo).updateReceptionFieldsViaBarcode(anyString(), anyString(), anyString())
            verify(mainActivityVM).refresh()
        }
    }

}