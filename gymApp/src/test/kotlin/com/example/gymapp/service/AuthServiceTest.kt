package com.example.gymapp.service

import com.example.gymapp.model.Member
import com.example.gymapp.repository.MemberRepository
import com.example.gymapp.utils.LoginDTO
import com.example.gymapp.utils.UsernameAlreadyTakenException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class AuthServiceTest {

    private val memberRepository: MemberRepository = mock()
    private val passwordEncoder: PasswordEncoder = mock()
    private val jwtService: JwtService = mock()
    private val authenticationManager: AuthenticationManager = mock()

    private val authService = AuthService().apply {
        this.userRepository = memberRepository
        this.passwordEncoder = this@AuthServiceTest.passwordEncoder
        this.jwtService = this@AuthServiceTest.jwtService
        this.authenticationManager = this@AuthServiceTest.authenticationManager
    }

    @Test
    fun `register should encode password, save user and return token`() {
        val member = Member().apply {
            usernameField = "cami"
            passwordField = "secure123"
        }

        whenever(passwordEncoder.encode("secure123")).thenReturn("encodedPass")
        whenever(jwtService.generateToken(member)).thenReturn("jwt-token")

        val result = authService.register(member)

        assertEquals("jwt-token", result)
        assertEquals("encodedPass", member.passwordField)
    }

    @Test
    fun `register should throw UsernameAlreadyTakenException on DataIntegrityViolation`() {
        val member = Member().apply {
            usernameField = "cami"
            passwordField = "secure123"
        }

        whenever(passwordEncoder.encode("secure123")).thenReturn("encodedPass")
        whenever(memberRepository.save(member)).thenThrow(DataIntegrityViolationException::class.java)

        val exception = assertThrows<UsernameAlreadyTakenException> {
            authService.register(member)
        }

        assertEquals("El usuario 'cami' ya está registrado", exception.message)
    }

    @Test
    fun `login should authenticate and return token`() {
        val loginDTO = LoginDTO(username = "cami", password = "secure123")
        val member = Member().apply {
            usernameField = loginDTO.username
        }

        whenever(authenticationManager.authenticate(any())).thenReturn(null) // no lanza excepción = OK
        whenever(memberRepository.findByUsernameField("cami")).thenReturn(Optional.of(member))
        whenever(jwtService.generateToken(member)).thenReturn("jwt-token")

        val result = authService.login(loginDTO)

        assertEquals("jwt-token", result)
    }

    @Test
    fun `login should throw IllegalArgumentException on authentication failure`() {
        val loginDTO = LoginDTO(username = "cami", password = "wrongpass")

        whenever(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException("Bad creds"))

        val exception = assertThrows<IllegalArgumentException> {
            authService.login(loginDTO)
        }

        assertEquals("Credenciales inválidas", exception.message)
    }
}
