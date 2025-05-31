package com.example.gymapp.controller


import com.example.gymapp.service.TurnService
import com.example.gymapp.utils.ActivityBuilder
import com.example.gymapp.utils.TurnBuilder
import com.example.gymapp.utils.NoTurnsForActivityException
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
}