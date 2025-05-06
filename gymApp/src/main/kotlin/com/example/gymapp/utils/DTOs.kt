package com.example.gymapp.utils

import java.time.LocalDateTime

data class RegistrationDTO(
    val turnId: Long?,
    val activityName: String,
    val startTime: LocalDateTime?
)

data class TurnDTO(
    val id: Long?,
    val datetime: LocalDateTime?,
    val capacity: Int?,
    val enrolled: Int?,
    val activityName: String?
)

data class MemberDTO(
    val name: String?,
    val username: String?,
    val id: Long?,
    val turns: List<Long>,
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