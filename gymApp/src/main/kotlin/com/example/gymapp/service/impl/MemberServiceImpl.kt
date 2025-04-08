package com.example.gymapp.service.impl

import com.example.gymapp.model.Member
import com.example.gymapp.model.Registration
import com.example.gymapp.repository.MemberRepository
import com.example.gymapp.service.MemberService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Transactional
@Service
class MemberServiceImpl : MemberService{

    @Autowired
    lateinit var memberRepository: MemberRepository
    override fun getMember(memberId: Long): Member {
        TODO("Not yet implemented")
    }

    override fun reserveASpot(memberId: Long, turnId: Long): Registration {
        TODO("Not yet implemented")
    }
}