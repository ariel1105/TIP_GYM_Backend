package com.example.gymapp.utils

import java.time.LocalDateTime

data class RegistrationDTO(
    val activityName: String,
    val startTime: LocalDateTime?
)