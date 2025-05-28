package com.example.gymapp.service

import com.example.gymapp.model.Member
import com.example.gymapp.model.Registration
import com.example.gymapp.model.Voucher
import com.example.gymapp.repository.ActivityRepository
import com.example.gymapp.repository.MemberRepository
import com.example.gymapp.repository.RegistrationRepository
import com.example.gymapp.repository.TurnRepository
import com.example.gymapp.repository.VoucherRepository
import com.example.gymapp.utils.MemberDTO
import com.example.gymapp.utils.VoucherRequestDTO
import com.example.gymapp.utils.VoucherResponseDTO
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

    @Autowired
    lateinit var voucherRepository: VoucherRepository

    fun getMember(memberId: Long): MemberDTO {
        val member = memberRepository.findById(memberId).orElseThrow()
        val registrations = memberRepository.getMemberRegistrations(memberId).map {
            it.turn!!.id!!.toLong()
        }
        val vouchers = voucherRepository.getActiveVouchersByMemberId(memberId).map {
            VoucherResponseDTO(
                it.activity!!.id,
                it.amount,
                it.remainingClasses,
                it.activity!!.name.toString(),
                it.acquisitionDate,
                it.acquisitionWay.toString()
            )
        }
        return MemberDTO(
            member!!.name,
            member.username,
            member.id,
            registrations,
            vouchers)
    }

    fun getMemberRegistrations(memberId: Long): List<Registration> {
        return memberRepository.getMemberRegistrations(memberId)
    }

    fun getMemberVouchers(memberId: Long): List<Voucher> {
        return voucherRepository.getActiveVouchersByMemberId(memberId)
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

        val activeVouchers = voucherRepository.getActiveVouchersByMemberId(memberId).toMutableList()

        val registrations = turns.map { turn ->
            val voucher = activeVouchers.firstOrNull {
                it.activity?.id == turn.activity?.id && it.remainingClasses > 0
            } ?: throw IllegalStateException("No hay voucher válido para la actividad ${turn.activity?.name}")

            voucher.validate(turn, memberId)
            member.subscribe(turn)
        }

        return registrationRepository.saveAll(registrations)
    }

    fun unsubscribeFromTurn(memberId: Long, turnId: Long): Voucher {
        val member = memberRepository.findById(memberId).orElseThrow()
        val turn = turnRepository.findById(turnId).orElseThrow()
        registrationRepository.deleteByMemberIdAndTurnId(memberId, turnId)
        val voucher = member.unsubscribe(turn)
        memberRepository.save(member)

        return voucher
    }

    fun acquireVoucher(memberId: Long, vouchers: List<VoucherRequestDTO>) {
        val member = memberRepository.findById(memberId).orElseThrow()
        val activityIdMap = vouchers.map { it.activityId }.toSet()
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