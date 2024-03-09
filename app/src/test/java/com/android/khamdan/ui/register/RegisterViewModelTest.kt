package com.android.khamdan.ui.register

import com.android.khamdan.data.user.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RegisterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
        viewModel = RegisterViewModel(userRepository)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun register_withValidData_triggersSuccessRegisterEvent() = runTest {
        // Arrange
        coEvery { userRepository.insertUser(any()) } returns flowOf(Result.success(Unit))
        val validState = RegisterState(
            username = "khamdannahari",
            email = "khamdannahari.id@gmail.com",
            password = "12345678",
            role = "Admin"
        )

        // Act
        viewModel.updateState(validState)
        viewModel.register()

        // Advance until idle
        advanceUntilIdle()

        // Assert
        val state = viewModel.registerState.value
        checkNotNull(state.successRegisterEvent.getContentIfNotHandled())
    }

    @Test
    fun register_withInvalidData_triggersErrorMessageEvent() = runTest {
        // Arrange
        coEvery { userRepository.insertUser(any()) } returns flowOf(Result.success(Unit))
        val invalidState = RegisterState(
            username = "",
            email = "",
            password = "",
            role = ""
        )

        // Act
        viewModel.updateState(invalidState)
        viewModel.register()

        // Advance until idle
        advanceUntilIdle()

        // Assert
        val state = viewModel.registerState.value
        checkNotNull(state.errorMessageEvent.getContentIfNotHandled())
    }

}
