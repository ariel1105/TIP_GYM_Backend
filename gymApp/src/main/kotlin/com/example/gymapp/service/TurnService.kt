package com.example.gymapp.service

import com.example.gymapp.model.Turn
import com.example.gymapp.repository.ActivityRepository
import com.example.gymapp.repository.TurnRepository
import com.example.gymapp.utils.NoTurnsForActivityException
import com.example.gymapp.utils.ScheduleTurnDTO
import com.example.gymapp.utils.TurnBuilder
import com.example.gymapp.utils.TurnDTO
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Transactional
class TurnService {
    
    @Autowired
    lateinit var turnRepository: TurnRepository

    @Autowired
    lateinit var activityRepository: ActivityRepository

    fun getTurnsActivity(activityId: Long): List<Turn> {
        val result = turnRepository.findByActivityIdAndDatetimeAfter(activityId)
        if(result.isEmpty()){ throw NoTurnsForActivityException() }
        return result
    }

    fun getTurnsByWeek(startDate: LocalDate): List<Turn> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = startDate.plusDays(7).atTime(23, 59, 59)
        return turnRepository.findTurnsInDateRange(startDateTime, endDateTime)
    }

    fun scheduleTurns(activityiD: Long, toSchedule: List<ScheduleTurnDTO>): List<Turn> {
        val activity = activityRepository.findById(activityiD).orElseThrow()
        val turns = toSchedule.map {
            TurnBuilder()
                .withDatetime(it.dateTime!!)
                .withCapacity(it.capacity!!)
                .withActivity(activity)
                .build()
        }
        return turnRepository.saveAll(turns)
    }

}