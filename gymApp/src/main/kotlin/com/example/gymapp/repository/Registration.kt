package com.example.gymapp.repository

import com.example.gymapp.model.Registration
import org.springframework.data.jpa.repository.JpaRepository

interface Registration : JpaRepository<Registration, Long> {
}