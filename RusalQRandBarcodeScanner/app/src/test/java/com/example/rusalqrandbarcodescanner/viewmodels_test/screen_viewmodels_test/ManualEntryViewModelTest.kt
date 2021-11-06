package com.example.rusalqrandbarcodescanner.viewmodels_test.screen_viewmodels_test

import androidx.compose.runtime.mutableStateOf
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ManualEntryViewModel
import org.junit.Assert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class ManualEntryViewModelTest {

    private lateinit var viewModel : ManualEntryViewModel

    @Mock
    private lateinit var mainActivityVM : MainActivityViewModel

    @BeforeEach
    fun setup() {
        viewModel = ManualEntryViewModel(mainActivityVM)
    }

    @Nested
    inner class RefreshTest {

        @Test
        fun `calls setDisplayButtonVis when called`() {
            val viewModelSpy = Mockito.spy(viewModel)
            doNothing().`when`(viewModelSpy).setSearchButtonVis()

            viewModelSpy.refresh()
            verify(viewModelSpy).setSearchButtonVis()
        }
    }

    @Nested
    inner class SetDisplayButtonVisTest {

        @Test
        fun `set to false when input length is less than 6`() {
            doReturn(mutableStateOf("12345")).`when`(mainActivityVM).heatNum

            assertFalse(viewModel.displaySearchButton.value)
            viewModel.setSearchButtonVis()
            assertFalse(viewModel.displaySearchButton.value)
        }

        @Test
        fun `set to true when input length is equal to 6`() {
            doReturn(mutableStateOf("123456")).`when`(mainActivityVM).heatNum

            assertFalse((viewModel.displaySearchButton.value))
            viewModel.setSearchButtonVis()
            assertTrue(viewModel.displaySearchButton.value)
        }

        @Test
        fun `set to true when input length is greater than 6`() {
            doReturn(mutableStateOf("1234567")).`when`(mainActivityVM).heatNum

            assertFalse(viewModel.displaySearchButton.value)
            viewModel.setSearchButtonVis()
            assertTrue(viewModel.displaySearchButton.value)
        }
    }


}