package com.example.gymapp.repository

import com.example.gymapp.model.Turn
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalDateTime

interface TurnRepository : JpaRepository<Turn, Long> {

    fun findByActivityIdAndDatetimeAfter(activity_id: Long, datetime: LocalDateTime = LocalDateTime.now().minusDays(1)): List<Turn>
    @Query("SELECT t FROM Turn t WHERE t.datetime BETWEEN :start AND :end")
    fun findTurnsInDateRange(
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime
    ): List<Turn>
}