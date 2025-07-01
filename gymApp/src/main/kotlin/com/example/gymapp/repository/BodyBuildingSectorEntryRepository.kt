package com.example.gymapp.repository

import com.example.gymapp.model.BodyBuildingSectorEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface BodyBuildingSectorEntryRepository : JpaRepository<BodyBuildingSectorEntry, Long> {

    @Query("""
        SELECT b.dateTime 
        FROM BodyBuildingSectorEntry b 
        WHERE b.member.id = :memberId 
          AND FUNCTION('MONTH', b.dateTime) = :month
    """)
    fun findByMemberIdAndMonth(@Param("memberId") memberId: Long, @Param("month") month: Int): List<LocalDateTime>
}