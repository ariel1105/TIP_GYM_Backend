package com.example.gymapp.service

import com.example.gymapp.model.Member
import io.jsonwebtoken.JwtException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.User
import java.util.Date

class JwtServiceTest {

    private val jwtService = JwtService()

    @Test
    fun `generateToken should create a valid token`() {
        val member = Member().apply {
            id = 123L
            usernameField = "cami"
            role = "USER"
        }

        val token = jwtService.generateToken(member)

        assertNotNull(token)
        assertTrue(token.isNotEmpty())
    }

    @Test
    fun `extractUsername should return the correct username`() {
        val member = Member().apply {
            id = 1L
            usernameField = "testuser"
            role = "ADMIN"
        }

        val token = jwtService.generateToken(member)

        val username = jwtService.extractUsername(token)

        assertEquals("testuser", username)
    }

    @Test
    fun `extractId should return the correct id`() {
        val member = Member().apply {
            id = 42L
            usernameField = "camilatest"
            role = "USER"
        }

        val token = jwtService.generateToken(member)
        val tokenWithBearer = "Bearer $token"

        val id = jwtService.extractId(tokenWithBearer)

        assertEquals("42", id)
    }

    @Test
    fun `isValid should return true for valid token and user`() {
        val member = Member().apply {
            id = 5L
            usernameField = "user1"
            role = "USER"
        }

        val token = jwtService.generateToken(member)

        val userDetails = User(member.usernameField, "password", emptyList())

        val valid = jwtService.isValid(token, userDetails)

        assertTrue(valid)
    }


    @Test
    fun `extractAllClaims should throw exception for invalid token`() {
        val invalidToken = "invalid.token.value"

        assertThrows(JwtException::class.java) {
            jwtService.extractAllClaims(invalidToken)
        }
    }
}
