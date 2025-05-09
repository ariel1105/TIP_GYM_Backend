package com.example.gymapp.repository

import com.example.gymapp.model.Member
import com.example.gymapp.model.Registration
import com.example.gymapp.model.Turn
import org.springframework.data.jpa.repository.JpaRepository

interface RegistrationRepository : JpaRepository<Registration, Long> {

    fun deleteByMemberIdAndTurnId(memberId: Long, turnId: Long)
}