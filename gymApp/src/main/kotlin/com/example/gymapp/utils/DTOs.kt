package com.example.gymapp.utils

import java.time.LocalDateTime

data class RegistrationDTO(
    val turnId: Long?,
    val activityName: String,
    val startTime: LocalDateTime?
)

data class MemberDTO(
    val name: String,
    val turns: List<Long>
)

data class SubscriptionRequestDTO(
    val turnIds: List<Long>
)

data class LoginDTO(
    val username: String,
    val password: String
)

data class RegisterDTO(
    val name: String,
    val username: String,
    val password: String
)