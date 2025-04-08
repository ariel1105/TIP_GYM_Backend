package com.example.gymapp.repository

import com.example.gymapp.model.Turn
import org.springframework.data.jpa.repository.JpaRepository

interface TurnRepository : JpaRepository<Turn, Long> {

    fun findByActivityId(activityId: Long): List<Turn>
}