package com.example.gymapp.controller


import com.example.gymapp.service.TurnService
import com.example.gymapp.utils.ActivityBuilder
import com.example.gymapp.utils.TurnBuilder
import com.example.gymapp.utils.NoTurnsForActivityException
import com.example.gymapp.utils.ScheduleTurnDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class TurnControllerTest {

    @Mock
    lateinit var turnService: TurnService

    @InjectMocks
    lateinit var turnController: TurnController

    @Test
    fun `should return turns by activity id`() {
        val activityId = 2L
        val activity = ActivityBuilder().withId(activityId).withName("Yoga").build()
        val turn = TurnBuilder()
            .withId(1L)
            .withDatetime(LocalDateTime.of(2025, 5, 30, 10, 0))
            .withCapacity(20)
            .withEnrolled(5)
            .withActivity(activity)
            .build()

        whenever(turnService.getTurnsActivity(activityId)).thenReturn(listOf(turn))

        val result = turnController.getTurns(activityId.toString())

        assertEquals(1, result.size)
        val dto = result[0]
        assertEquals(turn.id, dto.id)
        assertEquals(turn.datetime, dto.datetime)
        assertEquals(turn.capacity, dto.capacity)
        assertEquals(turn.enrolled, dto.enrolled)
        assertEquals("Yoga", dto.activityName)
        assertEquals(activityId, dto.activityId)
    }

    @Test
    fun `should return turns by week`() {
        val startDate = LocalDate.of(2025, 6, 3)
        val activity = ActivityBuilder().withId(3L).withName("Spinning").build()
        val turn = TurnBuilder()
            .withId(2L)
            .withDatetime(LocalDateTime.of(2025, 6, 4, 18, 0))
            .withCapacity(15)
            .withEnrolled(10)
            .withActivity(activity)
            .build()

        whenever(turnService.getTurnsByWeek(startDate)).thenReturn(listOf(turn))

        val result = turnController.getTurnsByWeek(startDate)

        assertEquals(1, result.size)
        val dto = result[0]
        assertEquals("Spinning", dto.activityName)
        assertEquals(activity.id, dto.activityId)
    }

    @Test
    fun `should throw exception when no turns for activity`() {
        val activityId = 99L
        whenever(turnService.getTurnsActivity(activityId)).thenThrow(NoTurnsForActivityException())

        val exception = assertThrows(NoTurnsForActivityException::class.java) {
            turnController.getTurns(activityId.toString())
        }

        assertEquals("NoTurnsForActivityException", exception::class.simpleName)
    }
/*
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

        val result = turnController.scheduleTurns(activityId, inputDTOs)

        assertEquals(2, result.size)

        assertEquals(turns[0].id, result[0].id)
        assertEquals(turns[0].datetime, result[0].datetime)
        assertEquals(turns[0].capacity, result[0].capacity)
        assertEquals(turns[0].enrolled, result[0].enrolled)
        assertEquals(activity.name, result[0].activityName)
        assertEquals(activity.id, result[0].activityId)

        assertEquals(turns[1].id, result[1].id)
        assertEquals(turns[1].datetime, result[1].datetime)
    }*/
}