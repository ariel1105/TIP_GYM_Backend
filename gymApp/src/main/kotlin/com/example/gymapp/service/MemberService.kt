package com.example.gymapp.service

import com.example.gymapp.model.Member
import com.example.gymapp.model.Registration
import com.example.gymapp.utils.MemberDTO
import com.example.gymapp.utils.RegistrationDTO
import org.springframework.security.core.userdetails.UserDetailsService

interface MemberService : UserDetailsService {

    fun getMember(memberId: Long): MemberDTO
    fun reserveASpot(memberId: Long, turnId: Long): Registration
    fun getMemberRegistrations(memberId: Long): List<Registration>
    fun subscribe(memberId: Long, turnId: Long): Registration
    fun subscribeToMultipleTurns(memberId: Long, turnIds: List<Long>): List<Registration>
    fun findUserById(id: Long): Member
    fun findMemberByUsername(username: String): Member
}