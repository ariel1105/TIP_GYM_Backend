package com.example.gymapp.service

import com.example.gymapp.model.Member
import com.example.gymapp.model.Registration

interface MemberService {

    fun getMember(memberId: Long): Member
    fun reserveASpot(memberId: Long, turnId: Long): Registration
}