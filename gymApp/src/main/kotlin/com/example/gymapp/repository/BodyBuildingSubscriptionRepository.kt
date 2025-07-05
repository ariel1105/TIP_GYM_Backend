package com.example.gymapp.repository

import com.example.gymapp.model.BodyBuildingSubscription
import com.example.gymapp.model.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface BodyBuildingSubscriptionRepository : JpaRepository<BodyBuildingSubscription, Long> {

    @Query("""
        SELECT b FROM BodyBuildingSubscription b
        WHERE b.member.id = :memberId
        AND b.dueDate > :today
    """)
    fun findActiveSubscriptionByMemberId(
        @Param("memberId") memberId: Long,
        @Param("today") today: LocalDate = LocalDate.now()
    ): BodyBuildingSubscription?



}
