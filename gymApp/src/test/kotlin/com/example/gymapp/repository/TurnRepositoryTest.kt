package com.example.gymapp.repository

import com.example.gymapp.utils.ActivityBuilder
import com.example.gymapp.utils.TurnBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDate
import java.time.LocalDateTime

@DataJpaTest
class TurnRepositoryTest {

    @Autowired
    lateinit var turnRepository: TurnRepository

    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Test
    fun `findByActivityIdAndDatetimeAfter should return future turns for given activity`() {
        val activity = activityRepository.save(ActivityBuilder().withName("Pilates").build())
        val pastTurn = TurnBuilder()
            .withDatetime(LocalDateTime.now().minusDays(2))
            .withActivity(activity)
            .build()
        val futureTurn = TurnBuilder()
            .withDatetime(LocalDateTime.now().plusDays(1))
            .withActivity(activity)
            .build()
        turnRepository.saveAll(listOf(pastTurn, futureTurn))

        val result = turnRepository.findByActivityIdAndDatetimeAfter(activity.id!!)

        assertThat(result).hasSize(1)
        assertThat(result[0].datetime).isAfter(LocalDateTime.now().minusDays(1))
    }

    @Test
    fun `findTurnsInDateRange should return turns within the specified week`() {
        val activity = activityRepository.save(ActivityBuilder().withName("Zumba").build())
        val start = LocalDate.now().atStartOfDay()
        val end = start.plusDays(7).withHour(23).withMinute(59)

        val insideTurn = TurnBuilder()
            .withDatetime(start.plusDays(2).withHour(10))
            .withActivity(activity)
            .build()
        val outsideTurn = TurnBuilder()
            .withDatetime(start.plusDays(8))
            .withActivity(activity)
            .build()
        turnRepository.saveAll(listOf(insideTurn, outsideTurn))

        val result = turnRepository.findTurnsInDateRange(start, end)

        assertThat(result).hasSize(1)
        assertThat(result[0].datetime).isAfterOrEqualTo(start)
        assertThat(result[0].datetime).isBeforeOrEqualTo(end)
    }
}