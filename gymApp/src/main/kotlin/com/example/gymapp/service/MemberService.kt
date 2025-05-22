package com.example.gymapp.service

import com.example.gymapp.model.Member
import com.example.gymapp.model.Registration
import com.example.gymapp.repository.ActivityRepository
import com.example.gymapp.repository.MemberRepository
import com.example.gymapp.repository.RegistrationRepository
import com.example.gymapp.repository.TurnRepository
import com.example.gymapp.utils.MemberDTO
import com.example.gymapp.utils.VoucherRequestDTO
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

    @Autowired
    lateinit var activityRepository: ActivityRepository

    fun getMember(memberId: Long): MemberDTO {
        val member = memberRepository.findById(memberId).orElseThrow()
        //agregar manejo de error si el usuario no existe
        val registrations = memberRepository.getMemberRegistrations(memberId).map {
            it.turn!!.id!!.toLong()
        }
        return MemberDTO(member!!.name, member.username, member.id, registrations)
    }

    fun getMemberRegistrations(memberId: Long): List<Registration> {
        return memberRepository.getMemberRegistrations(memberId)
    }

//    fun subscribe(memberId: Long, turnId: Long): Registration {
//        val member = memberRepository.findById(memberId).getOrNull()
//        val turn = turnRepository.findById(turnId).getOrNull()
//        val registration = member!!.subscribe(turn!!)
//        registrationRepository.save(registration)
//        return registration
//    }

    fun subscribeToMultipleTurns(memberId: Long, turnIds: List<Long>): List<Registration> {
        val member = memberRepository.findById(memberId).orElseThrow()
        val turns = turnRepository.findAllById(turnIds)

        val registrations = turns.map { turn ->
            member.subscribe(turn)
        }

        return registrationRepository.saveAll(registrations)
    }

    fun unsubscribeFromTurn(memberId: Long, turnId: Long) {
        val member = memberRepository.findById(memberId).orElseThrow()
        val turn = turnRepository.findById(turnId).orElseThrow()

        member.unsubscribe(turn)
        registrationRepository.deleteByMemberIdAndTurnId(memberId, turnId)
    }

    fun acquireVoucher(memberId: Long, vouchers: List<VoucherRequestDTO>) {
        val member = memberRepository.findById(memberId).orElseThrow()
        val activityIdMap = vouchers.mapNotNull { it.activityId }.toSet()
        val activities = activityRepository.findAllById(activityIdMap)
        val activityMap = activities.associateBy { it.id }

        vouchers.forEach { dto ->
            val activity = activityMap[dto.activityId]
                ?: throw IllegalArgumentException("Actividad con id ${dto.activityId} no encontrada")
            member.acquire(activity, dto.amount ?: 0)
        }
        memberRepository.save(member)
    }

    override fun loadUserByUsername(username: String?): UserDetails? {
        val member: Member = memberRepository.findByUsername(username!!).getOrNull()
            ?: throw UsernameNotFoundException("User with user $username does not exist.")

        val authorities: Set<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_USER")).toSet()
        return User(member.username, member.password, true, true, true, true, authorities)
    }
}