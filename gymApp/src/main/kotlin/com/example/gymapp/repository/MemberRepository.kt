package com.example.gymapp.repository

import com.example.gymapp.model.Member
import com.example.gymapp.model.Registration
import com.example.gymapp.utils.RegistrationDTO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MemberRepository : JpaRepository<Member, Long> {

    @Query("SELECT r FROM Registration r " +
            "JOIN FETCH r.turn t " +
            "JOIN FETCH t.activity a " +
            "WHERE r.member.id = :memberId")
    fun getMemberRegistrations(memberId: Long): List<Registration>
}