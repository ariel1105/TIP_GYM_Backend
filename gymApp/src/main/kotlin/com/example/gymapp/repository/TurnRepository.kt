package com.example.gymapp.repository

import com.example.gymapp.model.Turn
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.time.LocalDateTime

interface TurnRepository : JpaRepository<Turn, Long> {

    fun findByActivityIdAndDatetimeAfter(activity_id: Long, datetime: LocalDateTime = LocalDateTime.now().minusDays(1)): List<Turn>
}