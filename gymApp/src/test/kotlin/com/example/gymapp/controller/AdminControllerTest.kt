package com.example.gymapp.controller

import com.example.gymapp.model.Member
import com.example.gymapp.service.MemberService
import com.example.gymapp.service.TurnService
import com.example.gymapp.utils.ActivityBuilder
import com.example.gymapp.utils.ScheduleTurnDTO
import com.example.gymapp.utils.TurnBuilder
import com.example.gymapp.websocket.TurnNotificationSender
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class AdminControllerTest {

    @Mock
    lateinit var turnService: TurnService

    @Mock
    lateinit var memberService: MemberService

    @Mock
    lateinit var turnNotificationSender: TurnNotificationSender

    @InjectMocks
    lateinit var adminController: AdminController

    @Test
    fun `scheduleTurns should create and return DTOs`() {
        val activityId = 5L
        val activity = ActivityBuilder().withId(activityId).withName("Funcional").build()

        val inputDTOs = listOf(
            ScheduleTurnDTO(dateTime = LocalDateTime.of(2025, 6, 10, 9, 0), capacity = 12),
            ScheduleTurnDTO(dateTime = LocalDateTime.of(2025, 6, 11, 10, 0), capacity = 15)
        )

        val turns = listOf(
            TurnBuilder()
                .withId(100L)
                .withDatetime(inputDTOs[0].dateTime!!)
                .withCapacity(12)
                .withEnrolled(0)
                .withActivity(activity)
                .build(),
            TurnBuilder()
                .withId(101L)
                .withDatetime(inputDTOs[1].dateTime!!)
                .withCapacity(15)
                .withEnrolled(0)
                .withActivity(activity)
                .build()
        )

        whenever(turnService.scheduleTurns(activityId, inputDTOs)).thenReturn(turns)

        val result = adminController.scheduleTurns(activityId.toString(), inputDTOs)

        assertEquals(2, result.size)

        assertEquals(turns[0].id, result[0].id)
        assertEquals(turns[0].datetime, result[0].datetime)
        assertEquals(turns[0].capacity, result[0].capacity)
        assertEquals(turns[0].enrolled, result[0].enrolled)
        assertEquals(activity.name, result[0].activityName)
        assertEquals(activity.id, result[0].activityId)

        assertEquals(turns[1].id, result[1].id)
        assertEquals(turns[1].datetime, result[1].datetime)
    }

    @Test
    fun `registerEntry should return welcome message when entry is valid`() {
        val memberId = 1L
        val member = Member().apply {
            id = memberId
            name = "Camila"
            usernameField = "cami"
        }

        whenever(memberService.registerEntryBodyBuildingSector(memberId)).thenReturn(member)

        val manualAdminController = AdminController(turnService, turnNotificationSender).apply {
            this.memberService = this@AdminControllerTest.memberService
        }

        val result = manualAdminController.registerEntry(memberId)

        assertEquals("Bienvenido Camila", result)
    }
}
