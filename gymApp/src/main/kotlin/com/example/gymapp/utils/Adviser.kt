package com.example.gymapp.utils

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class Adviser {

    @ExceptionHandler(NoTurnsForActivityException::class)
    fun handleNoTurnsException(e: NoTurnsForActivityException): ResponseEntity<String>{
        return ResponseEntity.status(404).body(e.message)
    }
}