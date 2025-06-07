package com.example.gymapp.controller

import com.example.gymapp.model.Registration
import com.example.gymapp.service.JwtService
import com.example.gymapp.service.MemberService
import com.example.gymapp.utils.*
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class MemberControllerTest {

    @InjectMocks
    lateinit var memberController: MemberController

    @Mock
    lateinit var memberService: MemberService

    @Mock
    lateinit var jwtService: JwtService

    @Mock
    lateinit var request: HttpServletRequest

    @Test
    fun `should return member info`() {
        val memberId = 1L
        val expectedDTO = MemberDTO("member", "member123", memberId, listOf(), listOf())
        whenever(request.getHeader("Authorization")).thenReturn("Bearer token")
        whenever(jwtService.extractId("Bearer token")).thenReturn(memberId.toString())
        whenever(memberService.getMember(memberId)).thenReturn(expectedDTO)

        val result = memberController.getMember(request)

        assertEquals(expectedDTO, result)
    }

    @Test
    fun `should return member vouchers`() {
        val memberId = 1L
        val voucher = VoucherResponseDTO(2L, 10, 10, "Yoga", LocalDate.now(), "COMPRA")
        whenever(request.getHeader("Authorization")).thenReturn("Bearer token")
        whenever(jwtService.extractId("Bearer token")).thenReturn(memberId.toString())
        whenever(memberService.getMemberVouchers(memberId)).thenReturn(listOf(
            VoucherBuilder()
                .withActivity(ActivityBuilder().withId(2L).withName("Yoga").build())
                .withAmount(10)
                .withRemainingClasses(10)
                .withAcquisitionDate(LocalDate.now())
                .withAcquisitionWay("COMPRA")
                .build()
        ))

        val result = memberController.getVouchers(request)

        assertEquals(1, result.size)
        assertEquals(voucher.activityName, result[0].activityName)
    }
    @Test
    fun `should return member registrations`() {
        val memberId = 1L
        val activity = ActivityBuilder().withName("Spinning").build()
        val turn = TurnBuilder().withId(10L).withDatetime(LocalDateTime.now()).withActivity(activity).build()
        val registration = Registration().apply { this.turn = turn }

        whenever(request.getHeader("Authorization")).thenReturn("Bearer token")
        whenever(jwtService.extractId("Bearer token")).thenReturn(memberId.toString())
        whenever(memberService.getMemberRegistrations(memberId)).thenReturn(listOf(registration))

        val result = memberController.getRegistrations(request)

        assertEquals(1, result.size)
        assertEquals("Spinning", result[0].activityName)
    }

    @Test
    fun `should subscribe member to turns`() {
        val memberId = 1L
        val turnId = 5L
        val activity = ActivityBuilder().withName("Pilates").build()
        val turn = TurnBuilder().withId(turnId).withDatetime(LocalDateTime.now()).withActivity(activity).build()
        val registration = Registration().apply { this.turn = turn }
        val requestDTO = SubscriptionRequestDTO(listOf(turnId))

        whenever(request.getHeader("Authorization")).thenReturn("Bearer token")
        whenever(jwtService.extractId("Bearer token")).thenReturn(memberId.toString())
        whenever(memberService.subscribeToMultipleTurns(memberId, listOf(turnId))).thenReturn(listOf(registration))

        val result = memberController.subscribe(request, requestDTO)

        assertEquals(1, result.size)
        assertEquals("Pilates", result[0].activityName)
    }

    @Test
    fun `should unsubscribe member from turn`() {
        val memberId = 1L
        val turnId = 99L
        val voucher = VoucherBuilder()
            .withActivity(ActivityBuilder().withId(5L).withName("Zumba").build())
            .withAmount(5)
            .withRemainingClasses(4)
            .withAcquisitionDate(LocalDate.now())
            .withAcquisitionWay("COMPRA")
            .build()

        whenever(request.getHeader("Authorization")).thenReturn("Bearer token")
        whenever(jwtService.extractId("Bearer token")).thenReturn(memberId.toString())
        whenever(memberService.unsubscribeFromTurn(memberId, turnId)).thenReturn(voucher)

        val result = memberController.unsubscribe(request, turnId)

        assertEquals("Zumba", result.activityName)
        assertEquals(4, result.remainingClasses)
    }

    @Test
    fun `should acquire vouchers`() {
        val memberId = 1L
        val voucherRequests = listOf(VoucherRequestDTO(2L, 3))

        whenever(request.getHeader("Authorization")).thenReturn("Bearer token")
        whenever(jwtService.extractId("Bearer token")).thenReturn(memberId.toString())

        val result = memberController.acquireVoucher(request, voucherRequests)

        verify(memberService).acquireVoucher(memberId, voucherRequests)
        assertEquals("ya tienes tus vouchers!", result)
    }





}