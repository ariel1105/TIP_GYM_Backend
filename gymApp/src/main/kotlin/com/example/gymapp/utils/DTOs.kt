package com.example.gymapp.utils

import java.time.LocalDateTime
import jakarta.validation.constraints.*

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

data class VoucherRequestDTO(
    val activityId: Long,
    val amount: Int
)

data class VoucherResponseDTO(
    val activityId: Long?,
    val amount: Int,
    val remainingClasses: Int,
    val activityName: String
)

data class LoginDTO(
    val username: String,
    val password: String
)

data class RegisterDTO(
    @field:NotBlank(message = "El nombre no puede estar vacío")
    val name: String,
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "El usuario debe tener caracteres alfanuméricos")
    val username: String,
    @field:Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    val password: String
)