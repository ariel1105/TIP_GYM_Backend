package com.example.gymapp.controller

import com.example.gymapp.service.AuthService
import com.example.gymapp.utils.LoginDTO
import com.example.gymapp.utils.MemberBuilder
import com.example.gymapp.utils.RegisterDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController {

    @Autowired
    lateinit var authService: AuthService

    @PostMapping("/login")
    fun login(@RequestBody loginDTO: LoginDTO): ResponseEntity<String> {
        val result = authService.login(loginDTO)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/register")
    fun register(@RequestBody registerDto: RegisterDTO): ResponseEntity<String> {
        val member = MemberBuilder()
            .withName(registerDto.name)
            .withUsername(registerDto.username)
            .withPassword(registerDto.password)
            .build()
        val result = authService.register(member)
        return ResponseEntity.ok(result)
    }
}