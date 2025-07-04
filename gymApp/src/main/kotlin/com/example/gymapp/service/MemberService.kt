package com.example.gymapp.service

import com.example.gymapp.model.BodyBuildingSectorEntry
import com.example.gymapp.model.BodyBuildingSubscription
import com.example.gymapp.model.Member
import com.example.gymapp.model.Registration
import com.example.gymapp.model.Voucher
import com.example.gymapp.repository.ActivityRepository
import com.example.gymapp.repository.BodyBuildingSectorEntryRepository
import com.example.gymapp.repository.BodyBuildingSubscriptionRepository
import com.example.gymapp.repository.MemberRepository
import com.example.gymapp.repository.RegistrationRepository
import com.example.gymapp.repository.TurnRepository
import com.example.gymapp.repository.VoucherRepository
import com.example.gymapp.utils.BodyBuildingSubscriptionBuilder
import com.example.gymapp.utils.BodyBuildingSubscriptionDTO
import com.example.gymapp.utils.HaveAlreadyEntryBodyBuildingException
import com.example.gymapp.utils.MemberDTO
import com.example.gymapp.utils.NoDaysLeftInBodyBuildingSubscriptionException
import com.example.gymapp.utils.NoRemainingClassesException
import com.example.gymapp.utils.NonActiveBodyBuildingSubscriptionException
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

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

    @Autowired
    lateinit var bodyBuildingSubscriptionRepository: BodyBuildingSubscriptionRepository

    @Autowired
    lateinit var bodyBuildingSectorEntryRepository : BodyBuildingSectorEntryRepository

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
        val activeBodyBuildingSubscriptionDTO = bodyBuildingSubscriptionRepository
            .findActiveSubscriptionByMemberId(memberId)?.let {
                BodyBuildingSubscriptionDTO(
                    member!!.name.toString(),
                    it.acquisitionDate!!,
                    it.dueDate!!,
                    it.daysPerWeek!!
                )
            }
        return MemberDTO(
            member!!.name,
            member.usernameField,
            member.id,
            registrations,
            vouchers,
            member.notificationSubscriptions.map{it.id!!}.toSet(),
            activeBodyBuildingSubscriptionDTO
        )
    }

    fun getMemberRegistrations(memberId: Long): List<Registration> {
        return memberRepository.getMemberRegistrations(memberId)
    }

    fun getMemberVouchers(memberId: Long): List<Voucher> {
        return voucherRepository.getActiveVouchersByMemberId(memberId)
    }

    @Transactional
    fun subscribeToMultipleTurns(memberId: Long, turnIds: List<Long>): List<Registration> {
        val member = memberRepository.findById(memberId).orElseThrow()
        val turns = turnRepository.findAllById(turnIds)

        val activeVouchers = voucherRepository.getActiveVouchersByMemberId(memberId).toMutableList()

        val registrations = turns.map { turn ->
            val voucher = activeVouchers.firstOrNull {
                it.activity?.id == turn.activity?.id && it.remainingClasses > 0
            } ?: throw NoRemainingClassesException(turn.activity!!.name.toString())

            voucher.validate(turn, memberId)
            member.subscribe(turn)
        }

        return registrationRepository.saveAll(registrations)
    }

    @Transactional
    fun unsubscribeFromTurn(memberId: Long, turnId: Long): Voucher {
        val member = memberRepository.findById(memberId).orElseThrow()
        val turn = turnRepository.findById(turnId).orElseThrow()
        registrationRepository.deleteByMemberIdAndTurnId(memberId, turnId)
        val voucher = member.unsubscribe(turn)
        memberRepository.save(member)

        return voucher
    }

    @Transactional
    fun subscribeForNotification(memberId: Long, activityId: Long): Set<Long>{
        val member = memberRepository.findById(memberId).orElseThrow()
        val activity = activityRepository.findById(activityId).orElseThrow()
        member.subscribeToNotification(activity)
        memberRepository.save(member)
        return member.notificationSubscriptions.map{it.id!!}.toSet()
    }

    @Transactional
    fun acquireVoucher(memberId: Long, vouchers: List<VoucherRequestDTO>) {
        val member = memberRepository.findById(memberId).orElseThrow()
        val activityIdMap = vouchers.map { it.activityId }.toSet()
        val activities = activityRepository.findAllById(activityIdMap)
        val activityMap = activities.associateBy { it.id }

        vouchers.forEach { dto ->
            val activity = activityMap[dto.activityId]
                ?: throw IllegalArgumentException("Actividad con id ${dto.activityId} no encontrada")
            member.acquire(activity, dto.amount)
        }
        memberRepository.save(member)
    }

    @Transactional
    fun subscribeBodyBuilding(memberId: Long, daysPerWeek: Int): BodyBuildingSubscription {
        val member = memberRepository.findById(memberId).orElseThrow()
        val currentDate = LocalDate.now()
        val activeSubscription = bodyBuildingSubscriptionRepository
            .findActiveSubscriptionByMemberId(memberId)
        if (activeSubscription != null) {
            throw IllegalStateException("Ya existe una suscripción activa para este miembro.")
        }
        val dueDate = currentDate.plusMonths(1)
        return bodyBuildingSubscriptionRepository.save(
            BodyBuildingSubscriptionBuilder()
                .withMember(member)
                .withAcquisitionDate(currentDate)
                .withDaysPerWeek(daysPerWeek)
                .withDueDate(dueDate)
                .build()
        )
    }

//    fun registerEntryBodyBuildingSector(memberId: Long): Member {
//        val member = memberRepository.findById(memberId).orElseThrow()
//        bodyBuildingSectorEntryRepository.save(
//            BodyBuildingSectorEntryBuilder()
//                .withMember(member)
//                .withDateTime(LocalDateTime.now())
//                .build()
//        )
//        return member
//    }
    fun registerEntryBodyBuildingSector(memberId: Long): Member {
        val member = memberRepository.findById(memberId).orElseThrow()
        val today = LocalDate.now()
        //valida suscripcion activa existente
        val subscription = bodyBuildingSubscriptionRepository.findActiveSubscriptionByMemberId(memberId)
            ?:throw NonActiveBodyBuildingSubscriptionException()

        val startOfWeek = today.with(DayOfWeek.MONDAY).atStartOfDay()
        val entries = bodyBuildingSectorEntryRepository.findByMemberAndDateTimeAfterOrderByDateTimeDesc(member, startOfWeek)
        val haveAlreadyEntryToday = entries.firstOrNull()?.dateTime?.toLocalDate() == today
        //valida el ingreso en el dia (no debe registrar dos ingresos en el mismo dia)
        if (haveAlreadyEntryToday) {
            throw HaveAlreadyEntryBodyBuildingException()
        }
        //valida que no haya llegado al limite
        if (entries.size == (subscription.daysPerWeek ?: 0)) {
            throw NoDaysLeftInBodyBuildingSubscriptionException()
        }
        bodyBuildingSectorEntryRepository.save(
            BodyBuildingSectorEntry().apply {
                this.member = member
                this.dateTime = LocalDateTime.now()
            }
        )
        return member
    }


    fun getMemberBodyBuildingEntries(memberId: Long, monthNumber: Int): List<LocalDateTime> {
        return bodyBuildingSectorEntryRepository.findByMemberIdAndMonth(memberId, monthNumber)
    }


    override fun loadUserByUsername(username: String?): UserDetails? {
        val member: Member = memberRepository.findByUsernameField(username!!).getOrNull()
            ?: throw UsernameNotFoundException("User with user $username does not exist.")

        val authorities: Set<GrantedAuthority> = listOf(SimpleGrantedAuthority(member.role)).toSet()
        return User(member.usernameField, member.passwordField, true, true, true, true, authorities)
    }

}