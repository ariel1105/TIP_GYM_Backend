package com.example.gymapp.service

import com.example.gymapp.model.Member
import com.example.gymapp.model.Registration
import com.example.gymapp.utils.RegistrationDTO

interface MemberService {

    fun getMember(memberId: Long): Member
    fun reserveASpot(memberId: Long, turnId: Long): Registration
    fun getMemberRegistrations(memberId: Long): List<RegistrationDTO>
}