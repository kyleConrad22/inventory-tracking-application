package com.example.rusalqrandbarcodescanner.viewmodels_test.screen_viewmodels_test

import androidx.compose.runtime.mutableStateOf
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.Barge
import com.example.rusalqrandbarcodescanner.domain.models.Bl
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.InfoInputViewModel
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn

@ExtendWith(MockitoExtension::class)
class InfoInputViewModelTest {

    private fun Char.repeat(count : Int) : String = this.toString().repeat(count)

    private lateinit var viewModel: InfoInputViewModel

    @Mock
    private lateinit var repo: InventoryRepository

    @Mock
    private lateinit var mainActivityVM: MainActivityViewModel

    @BeforeEach
    fun setup() {
        doReturn(mutableStateOf(SessionType.GENERAL)).`when`(mainActivityVM).sessionType
        doReturn(mutableStateOf("")).`when`(mainActivityVM).barge
        doReturn(mutableStateOf("")).`when`(mainActivityVM).checker
        viewModel = InfoInputViewModel(mainActivityVM, repo)
    }

    @Nested
    inner class GetUniqueBargeListTest {

        @Test
        fun `returns an empty list when supplied an empty list`() {
            Assert.assertEquals(listOf<Barge>(), viewModel.getUniqueBargeList(listOf()))
        }

        @Test
        fun `returns list with single entry when supplied list with single entry`() {
            Assert.assertEquals(listOf(Barge("Test")),
                viewModel.getUniqueBargeList(listOf(RusalItem(barcode = "Test", barge = "Test"))))
        }

        @Test
        fun `returns list without duplicates when supplied list with duplicate barge entries`() {
            Assert.assertEquals(listOf(Barge("Test")),
                viewModel.getUniqueBargeList(listOf(RusalItem(barcode = "Test", barge = "Test"),
                    RusalItem(barcode = "Test", barge = "Test"))))
        }

        @Test
        fun `returns list with x unique entries when supplied list with x unique entries`() {
            Assert.assertEquals(listOf(Barge("Test1"), Barge("Test2"), Barge("Test3")),
                viewModel.getUniqueBargeList(listOf(RusalItem(barcode = "Test", barge = "Test1"),
                    RusalItem(barcode = "Test", barge = "Test2"),
                    RusalItem(barcode = "Test", barge = "Test3"))))
        }
    }

    @Nested
    inner class GetUniqueBlListTest {

        @Test
        fun `returns an empty list when supplied an empty list`() {
            Assert.assertEquals(listOf<Bl>(), viewModel.getUniqueBlList(listOf()))
        }

        @Test
        fun `returns list with a single entry when supplied list with a single entry`() {
            Assert.assertEquals(listOf(Bl("Test")),
                viewModel.getUniqueBlList(listOf(RusalItem(barcode = "Test", blNum = "Test"))))
        }

        @Test
        fun `returns a list without duplicates when supplied a list with duplicate BL numbers`() {
            Assert.assertEquals(listOf(Bl("Test")),
                viewModel.getUniqueBlList(listOf(RusalItem(barcode = "Test", blNum = "Test"),
                    RusalItem(barcode = "Test", blNum = "Test"))))
        }

        @Test
        fun `returns a list with x unique entries when supplied a list with x unique entries`() {
            Assert.assertEquals(listOf(Bl("Test1"), Bl("Test2"), Bl("Test3")),
                viewModel.getUniqueBlList(listOf(RusalItem(barcode = "Test", blNum = "Test1"),
                    RusalItem(barcode = "Test", blNum = "Test2"),
                    RusalItem(barcode = "Test", blNum = "Test3"))))
        }
    }

    @Nested
    inner class ShipmentTest {



        @BeforeEach
        fun setup() {
            doReturn(mutableStateOf(SessionType.SHIPMENT)).`when`(mainActivityVM).sessionType
        }

        @Nested
        inner class RefreshTest {


            @BeforeEach
            fun setup() {
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).loader
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).loadNum
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).quantity
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).bl
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).pieceCount
                doReturn(mutableStateOf("123456789")).`when`(mainActivityVM).workOrder

                viewModel.isValidPieceCount = true
                viewModel.isValidBl = true
                viewModel.isValidLoad = true
                viewModel.isValidOrder = true
                viewModel.isValidLoader = true
                viewModel.isValidQuantity = true
            }

            @Test
            fun `sets displayConfirmButton to true when all input fields are non-empty and all inputs are valid`() {

                viewModel.refresh()
                assertTrue(viewModel.displayConfirmButton)
            }

            @Test
            fun `sets displayConfirmButton to false when any input field is empty`() {

                doReturn(mutableStateOf("")).`when`(mainActivityVM).loader
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).loader

                doReturn(mutableStateOf("")).`when`(mainActivityVM).loadNum
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).loadNum

                doReturn(mutableStateOf("")).`when`(mainActivityVM).workOrder
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                doReturn(mutableStateOf("123456789")).`when`(mainActivityVM).workOrder

                doReturn(mutableStateOf("")).`when`(mainActivityVM).bl
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).bl

                doReturn(mutableStateOf("")).`when`(mainActivityVM).pieceCount
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).pieceCount

                doReturn(mutableStateOf("")).`when`(mainActivityVM).quantity
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).quantity
            }

            @Test
            fun `sets displayConfirmButton to false when any input is invalid`() {

                viewModel.isValidQuantity = false
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                viewModel.isValidQuantity = true

                viewModel.isValidLoader = false
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                viewModel.isValidLoader = true

                viewModel.isValidLoad = false
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                viewModel.isValidLoad = true

                viewModel.isValidOrder = false
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                viewModel.isValidOrder = true

                viewModel.isValidPieceCount = false
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                viewModel.isValidPieceCount = true
            }
        }
    }

    @Nested
    inner class ReceptionTest {

        @BeforeEach
        fun setup() {
            doReturn(mutableStateOf(SessionType.RECEPTION)).`when`(mainActivityVM).sessionType
        }

        @Nested
        inner class RefreshTest {

            @BeforeEach
            fun setup() {
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).checker
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).barge
                viewModel.isValidChecker = true
            }

            @Test
            fun `sets displayConfirmButton to true when all input fields are non-empty and all inputs are valid`() {

                viewModel.refresh()
                assertTrue(viewModel.displayConfirmButton)
            }

            @Test
            fun `sets displayConfirmButton to false when any input field is empty`() {

                doReturn(mutableStateOf("")).`when`(mainActivityVM).barge
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM.checker)

                doReturn(mutableStateOf("")).`when`(mainActivityVM).checker
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                doReturn(mutableStateOf("Test")).`when`(mainActivityVM).checker
            }

            @Test
            fun `sets displayConfirmButton to false when any input is invalid`() {

                viewModel.isValidChecker = false
                viewModel.refresh()
                assertFalse(viewModel.displayConfirmButton)
                viewModel.isValidChecker = true
            }
        }
    }

    @Nested
    inner class OnConfirmTest {

        private lateinit var viewModelSpy : InfoInputViewModel

        @BeforeEach
        fun setup() {
            viewModelSpy = Mockito.spy(viewModel)
            doNothing().`when`(viewModelSpy).validateBarge()
            doNothing().`when`(viewModelSpy).validateBl()
            viewModelSpy.isValidBl = true
            viewModelSpy.isValidBarge = true
        }

        @Test
        fun `returns true when both barge and bl are valid`() {
            assertTrue(viewModelSpy.onConfirmValidator())
        }

        @Test
        fun `returns false when either barge or bl are invalid`() {

            viewModelSpy.isValidBarge = false
            assertFalse(viewModelSpy.onConfirmValidator())
            viewModelSpy.isValidBarge = true

            viewModelSpy.isValidBl = false
            assertFalse(viewModelSpy.onConfirmValidator())
            viewModelSpy.isValidBl = true
        }
    }

    @Nested
    inner class ValidateLoadTest {

        @Test
        fun `sets false when input length is greater than 3`() {

            viewModel.validateLoad("1234")
            assertFalse(viewModel.isValidLoad)
        }

        @Test
        fun `sets false when input length is equal to 3`() {

            viewModel.validateLoad("123")
            assertFalse(viewModel.isValidLoad)
        }

        @Test
        fun `sets true when input length is less than 3 and is a valid integer`() {

            viewModel.validateLoad("12")
            assertTrue(viewModel.isValidLoad)
        }

        @Test
        fun `sets false when input is not a valid integer`() {

            for (i in 32..47) {
                viewModel.validateLoad("1${i.toChar()}")
                assertFalse(viewModel.isValidLoad)
            }
            for (i in 58..126) {
                viewModel.validateLoad("1${i.toChar()}")
                assertFalse(viewModel.isValidLoad)
            }
        }
    }

    @Nested
    inner class ValidateLoaderTest {

        @Test
        fun `sets false when input length is greater than 30`() {

            viewModel.validateLoader('A'.repeat(31))
            assertFalse(viewModel.isValidLoader)
        }

        @Test
        fun `sets false when input length is equal to 30`() {

            viewModel.validateLoader('A'.repeat(30))
            assertFalse(viewModel.isValidLoader)
        }

        @Test
        fun `sets true when input length is less than 30`() {

            viewModel.validateLoader('A'.repeat(29))
            assertTrue(viewModel.isValidLoader)
        }

    }

    @Nested
    inner class ValidateOrderTest {
        /* TODO - Write tests for validation of Work Order input, once work order input validation method is written */
    }

    @Nested
    inner class ValidateCheckerTest {

        @Test
        fun `sets false when input length is greater than 30`() {

            viewModel.validateChecker('A'.repeat(31))
            assertFalse(viewModel.isValidChecker)
        }

        @Test
        fun `sets false when input length is equal to 30`() {

            viewModel.validateChecker('A'.repeat(30))
            assertFalse(viewModel.isValidChecker)
        }

        @Test
        fun `sets true when input length is less than 30`() {

            viewModel.validateChecker('A'.repeat(29))
            assertTrue(viewModel.isValidChecker)
        }
    }

    @Nested
    inner class ValidateBlTest {

        private lateinit var viewModelSpy : InfoInputViewModel

        @BeforeEach
        fun setup() {
            viewModelSpy = Mockito.spy(viewModel)
            doReturn(mutableStateOf("Test")).`when`(mainActivityVM).bl
        }

        @Test
        fun `sets true when input is contained within blList`() {
            doReturn(mutableStateOf(listOf(Bl("Test"), Bl("Test1")))).`when`(viewModelSpy).blList

            viewModelSpy.validateBl()
            assertTrue(viewModelSpy.isValidBl)
        }

        @Test
        fun `sets false when input is not contained with blList`() {
            doReturn(mutableStateOf(listOf(Bl("Test1"), Bl("Test2"), Bl("Test3")))).`when`(viewModelSpy).blList

            viewModelSpy.validateBl()
            assertFalse(viewModelSpy.isValidBl)

        }
    }

    @Nested
    inner class ValidateBargeTest {

        private lateinit var viewModelSpy : InfoInputViewModel

        @BeforeEach
        fun setup() {
            viewModelSpy = Mockito.spy(viewModelSpy)
            doReturn(mutableStateOf("Test")).`when`(mainActivityVM).barge
        }

        @Test
        fun `sets true when input is contained within bargeList`() {
            doReturn(mutableStateOf(listOf(Barge("Test"), Barge("Test1")))).`when`(viewModelSpy).bargeList

            viewModelSpy.validateBarge()
            assertTrue(viewModelSpy.isValidBarge)
        }

        @Test
        fun `sets false when input is not contained within bargeList`() {
            doReturn(mutableStateOf(listOf(Barge("Test1"), Barge("Test2"), Barge("Test3")))).`when`(viewModelSpy).bargeList

            viewModelSpy.validateBarge()
            assertFalse(viewModelSpy.isValidBarge)
        }
    }

    @Nested
    inner class ValidatePieceCountTest {

        @Test
        fun `sets false when input length is greater than 3`() {

            viewModel.validatePieceCount("1234")
            assertFalse(viewModel.isValidPieceCount)
        }

        @Test
        fun `sets false when input length is equal to 3`() {

            viewModel.validatePieceCount("123")
            assertFalse(viewModel.isValidPieceCount)
        }

        @Test
        fun `sets true when input length is less than 3 and is a valid integer`() {

            viewModel.validatePieceCount("12")
            assertTrue(viewModel.isValidPieceCount)
        }

        @Test
        fun `sets false when input is not a valid integer`() {

            for (i in 32..47) {
                viewModel.validatePieceCount("1${i.toChar()}")
                assertFalse(viewModel.isValidPieceCount)
            }
            for (i in 58..126) {
                viewModel.validatePieceCount("1${i.toChar()}")
                assertFalse(viewModel.isValidPieceCount)
            }
        }
    }

    @Nested
    inner class ValidateQuantityTest {

        @Test
        fun `sets true when input is a valid integer`() {

            for (i in 48..57) {
                viewModel.validateQuantity(i.toChar().toString())
                assertTrue(viewModel.isValidQuantity)
            }
        }

        @Test
        fun `sets false when input is not a valid integer`() {
            for (i in 32..47) {
                viewModel.validateQuantity(i.toChar().toString())
                assertFalse(viewModel.isValidQuantity)
            }
            for (i in 58..126) {
                viewModel.validateQuantity(i.toChar().toString())
                assertFalse(viewModel.isValidQuantity)
            }
        }

    }

}