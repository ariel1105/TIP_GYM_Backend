package com.example.gymapp.service.impl

import com.example.gymapp.model.Member
import com.example.gymapp.model.Registration
import com.example.gymapp.repository.MemberRepository
import com.example.gymapp.repository.RegistrationRepository
import com.example.gymapp.repository.TurnRepository
import com.example.gymapp.service.MemberService
import com.example.gymapp.utils.MemberDTO
import com.example.gymapp.utils.RegistrationDTO
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Transactional
@Service
class MemberServiceImpl : MemberService{

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var turnRepository: TurnRepository

    @Autowired
    lateinit var registrationRepository: RegistrationRepository

    override fun getMember(memberId: Long): MemberDTO {
        val member = memberRepository.findById(memberId).getOrNull()
        val registrations = memberRepository.getMemberRegistrations(memberId).map {
            it.turn!!.id!!.toLong()
        }
        return MemberDTO(member!!.name.toString(), registrations)
    }

    override fun reserveASpot(memberId: Long, turnId: Long): Registration {
        TODO("Not yet implemented")
    }

    override fun getMemberRegistrations(memberId: Long): List<Registration> {
        return memberRepository.getMemberRegistrations(memberId)
    }

    override fun subscribe(memberId: Long, turnId: Long): Registration {
        val member = memberRepository.findById(memberId).getOrNull()
        val turn = turnRepository.findById(turnId).getOrNull()
        val registration = member!!.subscribe(turn!!)
        registrationRepository.save(registration)
        return registration
    }

    override fun subscribeToMultipleTurns(memberId: Long, turnIds: List<Long>): List<Registration> {
        val member = memberRepository.findById(memberId).orElseThrow()
        val turns = turnRepository.findAllById(turnIds)

        val registrations = turns.map { turn ->
            member.subscribe(turn)
        }

        return registrationRepository.saveAll(registrations)
    }
}