package com.example.gymapp.service

import com.example.gymapp.model.Turn
import com.example.gymapp.repository.TurnRepository
import com.example.gymapp.utils.NoTurnsForActivityException
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Transactional
class TurnService {
    
    @Autowired
    lateinit var turnRepository: TurnRepository 
    fun getTurnsActivity(activityId: Long): List<Turn> {
        var result = turnRepository.findByActivityIdAndDatetimeAfter(activityId)
        if(result.isEmpty()){ throw NoTurnsForActivityException() }
        return result
    }

    fun getTurnsByWeek(startDate: LocalDate): List<Turn> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = startDate.plusDays(7).atTime(23, 59, 59)
        return turnRepository.findTurnsInDateRange(startDateTime, endDateTime)
    }

}