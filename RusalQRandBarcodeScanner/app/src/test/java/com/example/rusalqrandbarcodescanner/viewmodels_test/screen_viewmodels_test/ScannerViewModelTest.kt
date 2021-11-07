package com.example.rusalqrandbarcodescanner.viewmodels_test.screen_viewmodels_test

import androidx.compose.runtime.mutableStateOf
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.util.QRParser
import com.example.rusalqrandbarcodescanner.util.inputvalidation.QRValidator
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ScannerState
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ScannerViewModel
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
class ScannerViewModelTest {

    private lateinit var viewModel : ScannerViewModel

    @Mock
    private lateinit var mainActivityVM : MainActivityViewModel

    @BeforeEach
    fun setup() {
        viewModel = ScannerViewModel(mainActivityVM)
    }

    @AfterEach
    fun validate() {
        validateMockitoUsage()
    }

    @Nested
    inner class OnScanTest {

        private lateinit var viewModelSpy : ScannerViewModel

        @BeforeEach
        fun setup() {
            viewModelSpy = Mockito.spy(viewModel)
            doNothing().`when`(viewModelSpy).checkIsValid(anyString())
        }

        @Test
        fun `sets heat to scannedItem heat given that the rawValue is a valid QR code`() {
            viewModelSpy.uiState.value = ScannerState.ValidScan

            Mockito.mockStatic(QRParser::class.java).use { qrParser ->
                qrParser.`when`<Any>{ QRParser.parseRusalCode(anyString()) }.thenReturn(RusalItem(barcode = "Test", heatNum = "Test"))
                viewModelSpy.onScan("Test") { /* Ignore */ }
                verify(mainActivityVM).heatNum
            }

        }

        @Test
        fun `does not set heat to scannedItem heat given that rawValue is not a valid QR code`() {
            viewModelSpy.uiState.value = ScannerState.InvalidScan

            viewModelSpy.onScan("Test") { /* Ignore */ }
            verify(mainActivityVM, times(0)).heatNum
        }

        @Test
        fun `sets mainActivity scannedItem value to scannedItem value given rawValue is a valid QR code and the sessionType is a reception`() {
            viewModelSpy.uiState.value = ScannerState.ValidScan
            doReturn(mutableStateOf(SessionType.RECEPTION)).`when`(mainActivityVM).sessionType

            Mockito.mockStatic(QRParser::class.java).use { qrParser ->
                qrParser.`when`<Any>{ QRParser.parseRusalCode(anyString()) }.thenReturn(RusalItem(barcode = "Test", heatNum = "Test"))
                viewModelSpy.onScan("Test") { /* Ignore */ }
                verify(mainActivityVM).scannedItem
            }
        }
    }

    @Nested
    inner class CheckIsValidTest {

        @Test
        fun `set to ValidScan when supplied rawValue is a valid QR code`() {
            Mockito.mockStatic(QRValidator::class.java).use { qrValidator ->
                qrValidator.`when`<Any>{ QRValidator.isValidRusalCode(anyString()) }.thenReturn(true)
                viewModel.checkIsValid("Test")
                assertEquals(ScannerState.ValidScan, viewModel.uiState.value)
            }

        }

        @Test
        fun `set to InvalidScan when supplied rawValue is an invalid QR code`() {
            Mockito.mockStatic(QRValidator::class.java).use { qrValidator ->
                qrValidator.`when`<Any>{ QRValidator.isValidRusalCode(anyString()) }.thenReturn(false)
                viewModel.checkIsValid("Test")
                assertEquals(ScannerState.InvalidScan, viewModel.uiState.value)
            }
        }
    }
}