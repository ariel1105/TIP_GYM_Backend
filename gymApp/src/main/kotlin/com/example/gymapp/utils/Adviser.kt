package com.example.gymapp.utils

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class Adviser {

    @ExceptionHandler(NoTurnsForActivityException::class)
    fun handleNoTurnsException(e: NoTurnsForActivityException): ResponseEntity<String>{
        return ResponseEntity.status(404).body(e.message)
    }

    @ExceptionHandler(UsernameAlreadyTakenException::class)
    fun handleUsernameTakenException(e: UsernameAlreadyTakenException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario '${e.username}' ya está registrado")
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleIllegalArgumentException(e: Exception): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleIllegalArgumentException(e: RuntimeException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(e: MethodArgumentNotValidException): ResponseEntity<String> {
        val firstErrorMessage = e.bindingResult
            .fieldErrors
            .firstOrNull()?.defaultMessage ?: "Error de validación"
        return ResponseEntity.badRequest().body(firstErrorMessage)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParams(ex: MissingServletRequestParameterException): ResponseEntity<String> {
        val paramName = ex.parameterName
        return ResponseEntity("El parámetro '$paramName' es obligatorio.", HttpStatus.BAD_REQUEST)
    }

}