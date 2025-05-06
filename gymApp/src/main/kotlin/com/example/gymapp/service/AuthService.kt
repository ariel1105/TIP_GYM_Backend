package com.example.gymapp.service

import com.example.gymapp.model.Member
import com.example.gymapp.repository.MemberRepository
import com.example.gymapp.utils.LoginDTO
import com.example.gymapp.utils.UsernameAlreadyTakenException
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@Transactional
class AuthService {


    @Autowired
    lateinit var userRepository: MemberRepository
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder
    @Autowired
    lateinit var jwtService: JwtService
    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    fun register(user: Member): String {
        if (user.name.isNullOrBlank() || user.username.isNullOrBlank() || user.password.isNullOrBlank()) {
            throw IllegalArgumentException("Todos los campos son obligatorios")
        }

        if (user.password!!.length < 6) {
            throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres")
        }

        if (!user.username!!.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            throw IllegalArgumentException("El nombre de usuario solo puede contener letras, números y guiones bajos")
        }

        if (userRepository.existsByUsername(user.username!!)) {
            throw UsernameAlreadyTakenException(user.username!!)
        }

        user.password = passwordEncoder.encode(user.password)
        userRepository.save(user)
        return jwtService.generateToken(user)
    }


    fun login(loginDTO: LoginDTO): String {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginDTO.username,
                loginDTO.password
            )
        )
        val user: Member? = userRepository.findByUsername(loginDTO.username).get()
        return jwtService.generateToken(user!!)
    }
}