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
class AuthService {


    @Autowired
    lateinit var userRepository: MemberRepository
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder
    @Autowired
    lateinit var jwtService: JwtService
    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Transactional
    fun register(user: Member): String {
        try{
            user.passwordField = passwordEncoder.encode(user.passwordField)
            userRepository.save(user)
            return jwtService.generateToken(user)
        }catch (e : DataIntegrityViolationException){
            throw UsernameAlreadyTakenException(user.usernameField!!)
        }
    }


    fun login(loginDTO: LoginDTO): String {
        try{
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginDTO.username,
                    loginDTO.password
                )
            )
        }catch (e: Exception){
            throw IllegalArgumentException("Credenciales inválidas")
        }
        val user: Member = userRepository.findByUsernameField(loginDTO.username).get()
        return jwtService.generateToken(user)
    }
}