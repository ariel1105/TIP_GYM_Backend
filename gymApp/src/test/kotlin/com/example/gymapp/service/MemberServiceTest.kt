import com.example.gymapp.model.*
import com.example.gymapp.repository.*
import com.example.gymapp.service.MemberService
import com.example.gymapp.utils.*
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class MemberServiceTest {

    @Mock lateinit var memberRepository: MemberRepository
    @Mock lateinit var turnRepository: TurnRepository
    @Mock lateinit var registrationRepository: RegistrationRepository
    @Mock lateinit var activityRepository: ActivityRepository
    @Mock lateinit var voucherRepository: VoucherRepository
    @Mock lateinit var bodyBuildingSubscriptionRepository: BodyBuildingSubscriptionRepository
    @Mock lateinit var bodyBuildingSectorEntryRepository: BodyBuildingSectorEntryRepository

    @InjectMocks
    lateinit var memberService: MemberService

    @Test
    fun `getMember should return MemberDTO with registrations, vouchers and subscription`() {
        val memberId = 1L
        val member = MemberBuilder().withId(memberId).withName("Camila").withUsername("cami").build()
        val registrationsIds = listOf(100L, 101L)
        val vouchers = listOf(
            VoucherBuilder().withActivity(ActivityBuilder().withId(1).withName("Yoga").build())
                .withAmount(5).withRemainingClasses(3).withAcquisitionDate(LocalDate.now()).withAcquisitionWay("COMPRA").build()
        )
        val subscription = BodyBuildingSubscriptionBuilder()
            .withMember(member)
            .withDaysPerWeek(3)
            .withAcquisitionDate(LocalDate.now())
            .withDueDate(LocalDate.now().plusMonths(1))
            .build()

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        whenever(memberRepository.getMemberRegistrations(memberId)).thenReturn(
            registrationsIds.map { RegistrationBuilder().withTurn(TurnBuilder().withId(it).build()).build() }
        )
        whenever(voucherRepository.getActiveVouchersByMemberId(memberId)).thenReturn(vouchers)
        whenever(bodyBuildingSubscriptionRepository.findActiveSubscriptionByMemberId(memberId)).thenReturn(subscription)

        val dto = memberService.getMember(memberId)

        assertEquals(member.name, dto.name)
        assertEquals(member.usernameField, dto.username)
        assertEquals(member.id, dto.id)
        assertEquals(registrationsIds, dto.turns)
        assertEquals(vouchers.size, dto.vouchers.size)
        assertNotNull(dto.bodyBuildingSubscription)
    }

    @Test
    fun `getMemberRegistrations returns list of registrations`() {
        val memberId = 1L
        val regs = listOf(RegistrationBuilder().withId(1).build(), RegistrationBuilder().withId(2).build())
        whenever(memberRepository.getMemberRegistrations(memberId)).thenReturn(regs)
        val result = memberService.getMemberRegistrations(memberId)
        assertEquals(regs, result)
    }

    @Test
    fun `getMemberVouchers returns list of vouchers`() {
        val memberId = 1L
        val vouchers = listOf(VoucherBuilder().withId(1).build())
        whenever(voucherRepository.getActiveVouchersByMemberId(memberId)).thenReturn(vouchers)
        val result = memberService.getMemberVouchers(memberId)
        assertEquals(vouchers, result)
    }

    @Test
    fun `subscribeToMultipleTurns subscribes member to turns and returns registrations`() {
        val memberId = 1L
        val activity = ActivityBuilder().withId(10L).withName("Yoga").build()
        val turn1 = TurnBuilder().withId(100L).withActivity(activity).withCapacity(100).build()
        val turn2 = TurnBuilder().withId(101L).withActivity(activity).withCapacity(100).build()

        val member = MemberBuilder().withId(memberId).withName("Test User").build()

        val voucher1 = VoucherBuilder()
            .withActivity(activity)
            .withMember(member)
            .withRemainingClasses(5)
            .withAmount(5)
            .build()

        val voucher2 = VoucherBuilder()
            .withActivity(activity)
            .withMember(member)
            .withRemainingClasses(3)
            .withAmount(3)
            .build()

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        whenever(turnRepository.findAllById(listOf(turn1.id!!, turn2.id!!))).thenReturn(listOf(turn1, turn2))
        whenever(voucherRepository.getActiveVouchersByMemberId(memberId)).thenReturn(mutableListOf(voucher1, voucher2))

        val registration1 = RegistrationBuilder().withId(1L).withMember(member).withTurn(turn1).build()
        val registration2 = RegistrationBuilder().withId(2L).withMember(member).withTurn(turn2).build()

        whenever(registrationRepository.saveAll(any<List<Registration>>())).thenReturn(listOf(registration1, registration2))

        val result = memberService.subscribeToMultipleTurns(memberId, listOf(turn1.id!!, turn2.id!!))

        assertEquals(2, result.size)
        assertEquals(registration1.id, result[0].id)
        assertEquals(registration2.id, result[1].id)
    }

    @Test
    fun `subscribeToMultipleTurns throws NoRemainingClassesException if no valid voucher`() {
        val memberId = 1L
        val activity = ActivityBuilder().withId(1).build()
        val turn = TurnBuilder().withId(10L).withActivity(activity).build()
        val turns = listOf(turn)

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(Member()))
        whenever(turnRepository.findAllById(listOf(turn.id!!))).thenReturn(turns)
        whenever(voucherRepository.getActiveVouchersByMemberId(memberId)).thenReturn(emptyList())

        assertThrows<NoRemainingClassesException> {
            memberService.subscribeToMultipleTurns(memberId, listOf(turn.id!!))
        }
    }

    @Test
    fun `unsubscribeFromTurn deletes registration and returns voucher`() {
        val memberId = 1L
        val turnId = 10L

        val member = MemberBuilder()
            .withId(memberId)
            .withName("Test Member")
            .withUsername("testuser")
            .withPassword("password")
            .build()

        val activity = ActivityBuilder()
            .withId(100L)
            .withName("Yoga")
            .build()

        val futureDateTime = LocalDateTime.now().plusDays(2)
        val turn = TurnBuilder()
            .withId(turnId)
            .withActivity(activity)
            .withDatetime(futureDateTime)
            .withCapacity(10)
            .withEnrolled(1)
            .build()

        val registration = RegistrationBuilder()
            .withId(99L)
            .withMember(member)
            .withTurn(turn)
            .build()
        turn.registrations.add(registration)

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        whenever(turnRepository.findById(turnId)).thenReturn(Optional.of(turn))

        val voucher = memberService.unsubscribeFromTurn(memberId, turnId)

        verify(registrationRepository).deleteByMemberIdAndTurnId(memberId, turnId)
        verify(memberRepository).save(member)

        assertNotNull(voucher)
        assertEquals(activity, voucher.activity)
        assertEquals(member, voucher.member)
        assertEquals(1, voucher.amount)
        assertEquals("CANCELACIÓN DE TURNO", voucher.acquisitionWay)
    }

    @Test
    fun `subscribeForNotification subscribes member to activity notifications`() {
        val memberId = 1L
        val activityId = 1L
        val member = MemberBuilder().withId(memberId).build()
        val activity = ActivityBuilder().withId(activityId).build()

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        whenever(activityRepository.findById(activityId)).thenReturn(Optional.of(activity))
        whenever(memberRepository.save(member)).thenReturn(member)

        val result = memberService.subscribeForNotification(memberId, activityId)

        assertTrue(result.contains(activityId))
        verify(memberRepository).save(member)
    }

    @Test
    fun `acquireVoucher adds vouchers to member`() {
        val memberId = 1L
        val member = MemberBuilder().withId(memberId).build()
        val activity1 = ActivityBuilder().withId(1).build()
        val activity2 = ActivityBuilder().withId(2).build()

        val vouchersDto = listOf(
            VoucherRequestDTO(activityId = 1, amount = 5),
            VoucherRequestDTO(activityId = 2, amount = 10)
        )

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        whenever(activityRepository.findAllById(setOf(1L, 2L))).thenReturn(listOf(activity1, activity2))
        whenever(memberRepository.save(member)).thenReturn(member)

        memberService.acquireVoucher(memberId, vouchersDto)

        verify(memberRepository).save(member)

    }

    @Test
    fun `subscribeBodyBuilding creates new subscription when none active`() {
        val memberId = 1L
        val member = MemberBuilder().withId(memberId).build()
        val currentDate = LocalDate.now()
        val subscription =  BodyBuildingSubscriptionBuilder()
            .withMember(member)
            .withAcquisitionDate(currentDate)
            .withDaysPerWeek(3)
            .withDueDate(currentDate)
            .build()

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        whenever(bodyBuildingSubscriptionRepository.findActiveSubscriptionByMemberId(memberId)).thenReturn(null)
        whenever(bodyBuildingSubscriptionRepository.save(any<BodyBuildingSubscription>()))
            .thenReturn(subscription)

        val savedSubscription = memberService.subscribeBodyBuilding(memberId, 3)

        assertEquals(member, savedSubscription.member)
        assertEquals(3, subscription.daysPerWeek)
    }

    @Test
    fun `subscribeBodyBuilding throws IllegalStateException if active subscription exists`() {
        val memberId = 1L
        val member = MemberBuilder().withId(memberId).build()
        val activeSubscription = BodyBuildingSubscriptionBuilder()
            .withMember(member)
            .withDaysPerWeek(3)
            .build()

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        whenever(bodyBuildingSubscriptionRepository.findActiveSubscriptionByMemberId(memberId)).thenReturn(activeSubscription)

        assertThrows<IllegalStateException> {
            memberService.subscribeBodyBuilding(memberId, 2)
        }
    }

    @Test
    fun `registerEntryBodyBuildingSector registers entry if valid`() {
        val memberId = 1L
        val member = MemberBuilder().withId(memberId).withName("Camila").withUsername("cami").build()
        val subscription = BodyBuildingSubscriptionBuilder()
            .withMember(member)
            .withDaysPerWeek(3)
            .build()
        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.MONDAY).atStartOfDay()
        val entries = emptyList<BodyBuildingSectorEntry>()

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        whenever(bodyBuildingSubscriptionRepository.findActiveSubscriptionByMemberId(memberId)).thenReturn(subscription)
        whenever(bodyBuildingSectorEntryRepository.findByMemberAndDateTimeAfterOrderByDateTimeDesc(eq(member), eq(startOfWeek))).thenReturn(entries)

        val result = memberService.registerEntryBodyBuildingSector(memberId)

        assertEquals(member, result)
        verify(bodyBuildingSectorEntryRepository).save(any())
    }

    @Test
    fun `registerEntryBodyBuildingSector throws NonActiveBodyBuildingSubscriptionException if no active subscription`() {
        val memberId = 1L
        val member = MemberBuilder().withId(memberId).withUsername("cami").build()
        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        whenever(bodyBuildingSubscriptionRepository.findActiveSubscriptionByMemberId(memberId)).thenReturn(null)

        assertThrows<NonActiveBodyBuildingSubscriptionException> {
            memberService.registerEntryBodyBuildingSector(memberId)
        }
    }

    @Test
    fun `registerEntryBodyBuildingSector throws HaveAlreadyEntryBodyBuildingException if already has entry today`() {
        val memberId = 1L
        val member = MemberBuilder().withId(memberId).withUsername("cami").build()
        val subscription = BodyBuildingSubscriptionBuilder()
            .withMember(member)
            .withDaysPerWeek(3)
            .build()
        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.MONDAY).atStartOfDay()
        val entryToday = BodyBuildingSectorEntryBuilder()
            .withMember(member)
            .withDateTime(LocalDateTime.now())
            .build()

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        whenever(bodyBuildingSubscriptionRepository.findActiveSubscriptionByMemberId(memberId)).thenReturn(subscription)
        whenever(bodyBuildingSectorEntryRepository.findByMemberAndDateTimeAfterOrderByDateTimeDesc(eq(member), eq(startOfWeek))).thenReturn(listOf(entryToday))

        assertThrows<HaveAlreadyEntryBodyBuildingException> {
            memberService.registerEntryBodyBuildingSector(memberId)
        }
    }

    @Test
    fun `registerEntryBodyBuildingSector throws NoDaysLeftInBodyBuildingSubscriptionException if reached limit`() {
        val memberId = 1L
        val member = MemberBuilder().withId(memberId).withUsername("cami").build()
        val subscription = BodyBuildingSubscriptionBuilder()
            .withMember(member)
            .withDaysPerWeek(1)
            .build()
        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.MONDAY).atStartOfDay()
        val entry = BodyBuildingSectorEntryBuilder()
            .withMember(member)
            .withDateTime(LocalDateTime.now().minusDays(1))
            .build()

        whenever(memberRepository.findById(memberId)).thenReturn(Optional.of(member))
        whenever(bodyBuildingSubscriptionRepository.findActiveSubscriptionByMemberId(memberId)).thenReturn(subscription)
        whenever(bodyBuildingSectorEntryRepository.findByMemberAndDateTimeAfterOrderByDateTimeDesc(eq(member), eq(startOfWeek))).thenReturn(listOf(entry))

        assertThrows<NoDaysLeftInBodyBuildingSubscriptionException> {
            memberService.registerEntryBodyBuildingSector(memberId)
        }
    }

    @Test
    fun `getMemberBodyBuildingEntries returns list of entries`() {
        val memberId = 1L
        val month = 7
        val dates = listOf(LocalDateTime.now(), LocalDateTime.now().minusDays(2))
        whenever(bodyBuildingSectorEntryRepository.findByMemberIdAndMonth(memberId, month)).thenReturn(dates)
        val result = memberService.getMemberBodyBuildingEntries(memberId, month)
        assertEquals(dates, result)
    }

    @Test
    fun `loadUserByUsername returns UserDetails when member found`() {
        val username = "cami"
        val member = MemberBuilder().withUsername(username).withPassword("pwd").withName("Camila").build()
        whenever(memberRepository.findByUsernameField(username)).thenReturn(Optional.of(member))

        val userDetails = memberService.loadUserByUsername(username)
        assertNotNull(userDetails)
        assertEquals(username, userDetails?.username)
    }

    @Test
    fun `loadUserByUsername throws UsernameNotFoundException when member not found`() {
        val username = "notfound"
        whenever(memberRepository.findByUsernameField(username)).thenReturn(Optional.empty())

        assertThrows<org.springframework.security.core.userdetails.UsernameNotFoundException> {
            memberService.loadUserByUsername(username)
        }
    }
}
