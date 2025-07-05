package com.example.gymapp.service

import com.example.gymapp.model.Activity
import com.example.gymapp.model.Turn
import com.example.gymapp.repository.ActivityRepository
import com.example.gymapp.repository.TurnRepository
import com.example.gymapp.utils.NoTurnsForActivityException
import com.example.gymapp.utils.ScheduleTurnDTO
import com.example.gymapp.utils.TurnBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class TurnServiceTest {

    @Mock
    lateinit var turnRepository: TurnRepository

    @Mock
    lateinit var activityRepository: ActivityRepository

    @InjectMocks
    lateinit var turnService: TurnService

    @Test
    fun `getTurnsActivity returns list when activity has turns`() {
        val activityId = 1L
        val turns = listOf(TurnBuilder().withId(100L).build())

        whenever(turnRepository.findByActivityIdAndDatetimeAfter(eq(activityId), any()))
            .thenReturn(turns)

        val result = turnService.getTurnsActivity(activityId)

        assertEquals(turns, result)
    }

    @Test
    fun `getTurnsActivity throws exception when no turns found`() {
        val activityId = 1L
        whenever(turnRepository.findByActivityIdAndDatetimeAfter(eq(activityId), any())).thenReturn(emptyList())

        assertThrows<NoTurnsForActivityException> {
            turnService.getTurnsActivity(activityId)
        }
    }

    @Test
    fun `getTurnsByWeek returns turns in date range`() {
        val startDate = LocalDate.of(2025, 7, 1)
        val expectedTurns = listOf(
            TurnBuilder().withId(1L).withDatetime(startDate.atStartOfDay()).build()
        )
        whenever(turnRepository.findTurnsInDateRange(any<LocalDateTime>(), any<LocalDateTime>())).thenReturn(expectedTurns)

        val result = turnService.getTurnsByWeek(startDate)

        assertEquals(expectedTurns, result)
    }

    @Test
    fun `scheduleTurns saves and returns new turns`() {
        val activityId = 1L
        val activity = Activity().apply { id = activityId }

        val scheduleDTOs = listOf(
            ScheduleTurnDTO(dateTime = LocalDateTime.of(2025, 7, 10, 10, 0), capacity = 10),
            ScheduleTurnDTO(dateTime = LocalDateTime.of(2025, 7, 11, 12, 0), capacity = 12)
        )

        val turns = scheduleDTOs.map {
            TurnBuilder()
                .withDatetime(it.dateTime!!)
                .withCapacity(it.capacity!!)
                .withActivity(activity)
                .build()
        }

        whenever(activityRepository.findById(activityId)).thenReturn(Optional.of(activity))
        whenever(turnRepository.saveAll(any<List<Turn>>())).thenReturn(turns)

        val result = turnService.scheduleTurns(activityId, scheduleDTOs)

        assertEquals(2, result.size)
        assertEquals(10, result[0].capacity)
        assertEquals(12, result[1].capacity)
        assertEquals(activity, result[0].activity)
    }
}
