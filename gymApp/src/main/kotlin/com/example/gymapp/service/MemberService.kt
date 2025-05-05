package com.example.gymapp.service

import com.example.gymapp.model.Member
import com.example.gymapp.model.Registration
import com.example.gymapp.repository.MemberRepository
import com.example.gymapp.repository.RegistrationRepository
import com.example.gymapp.repository.TurnRepository
import com.example.gymapp.utils.MemberDTO
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Transactional
@Service
class MemberService: UserDetailsService{

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var turnRepository: TurnRepository

    @Autowired
    lateinit var registrationRepository: RegistrationRepository

    fun getMember(memberId: Long): MemberDTO {
        val member = memberRepository.findById(memberId).getOrNull()
        //agregar manejo de error si el usuario no existe
        val registrations = memberRepository.getMemberRegistrations(memberId).map {
            it.turn!!.id!!.toLong()
        }
        return MemberDTO(member!!.name.toString(), registrations)
    }

    fun reserveASpot(memberId: Long, turnId: Long): Registration {
        TODO("Not yet implemented")
    }

    fun getMemberRegistrations(memberId: Long): List<Registration> {
        return memberRepository.getMemberRegistrations(memberId)
    }

    fun subscribe(memberId: Long, turnId: Long): Registration {
        val member = memberRepository.findById(memberId).getOrNull()
        val turn = turnRepository.findById(turnId).getOrNull()
        val registration = member!!.subscribe(turn!!)
        registrationRepository.save(registration)
        return registration
    }

    fun subscribeToMultipleTurns(memberId: Long, turnIds: List<Long>): List<Registration> {
        val member = memberRepository.findById(memberId).orElseThrow()
        val turns = turnRepository.findAllById(turnIds)

        val registrations = turns.map { turn ->
            member.subscribe(turn)
        }

        return registrationRepository.saveAll(registrations)
    }

    fun findUserById(id: Long): Member {
        return memberRepository.findById(id).get()
    }

    fun findMemberByUsername(username: String): Member {
        return memberRepository.findByUsername(username).get()
    }

    override fun loadUserByUsername(username: String?): UserDetails? {
        val member: Member = memberRepository.findByUsername(username!!).getOrNull()
            ?: throw UsernameNotFoundException("User with user $username does not exist.")

        val authorities: Set<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_USER")).toSet()
        return User(member.username, member.password, true, true, true, true, authorities)
    }
}