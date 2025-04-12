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