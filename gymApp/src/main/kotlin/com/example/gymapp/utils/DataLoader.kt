package com.example.gymapp.utils

import com.example.gymapp.model.Member
import com.example.gymapp.repository.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataLoader(
    @Autowired val memberRepository: MemberRepository,
    @Autowired val passwordEncoder: PasswordEncoder
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        if (!memberRepository.existsByUsernameField("admin")) {
            val admin = Member().apply {
                usernameField = "admin"
                passwordField = passwordEncoder.encode("admin123")
                role = "ADMIN"
                name = "admin"
            }
            memberRepository.save(admin)
        }
    }
}