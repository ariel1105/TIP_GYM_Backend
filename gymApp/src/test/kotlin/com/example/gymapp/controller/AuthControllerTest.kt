package com.example.gymapp.controller

import com.example.gymapp.service.AuthService
import com.example.gymapp.utils.LoginDTO
import com.example.gymapp.utils.RegisterDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.ResponseEntity
import kotlin.test.Test

class AuthControllerTest {

    private val authService: AuthService = mock()

    private val authController = AuthController().apply {
        this.authService = this@AuthControllerTest.authService
    }

    @Test
    fun `login should return token when credentials are valid`() {
        val loginDTO = LoginDTO(username = "cami", password = "secure123")
        val expectedToken = "jwt-token"

        whenever(authService.login(loginDTO)).thenReturn(expectedToken)

        val response: ResponseEntity<String> = authController.login(loginDTO)

        assertEquals(200, response.statusCode.value())
        assertEquals(expectedToken, response.body)
    }

    @Test
    fun `register should return token when registration is successful`() {
        val registerDTO = RegisterDTO(name = "Camila", username = "cami", password = "secure123")
        val expectedToken = "jwt-token"

        whenever(authService.register(org.mockito.kotlin.any())).thenReturn(expectedToken)

        val response: ResponseEntity<String> = authController.register(registerDTO)

        assertEquals(200, response.statusCode.value())
        assertEquals(expectedToken, response.body)
    }
}