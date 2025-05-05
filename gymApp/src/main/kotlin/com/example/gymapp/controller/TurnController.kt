package com.example.gymapp.controller

import com.example.gymapp.model.Turn
import com.example.gymapp.service.TurnService
import com.example.gymapp.utils.TurnDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class TurnController {

    @Autowired
    lateinit var turnService: TurnService

    @GetMapping("/turns/{activityId}")
    fun getTurns(@PathVariable activityId: String): List<Turn>{
        return turnService.getTurnsActivity(activityId.toLong())
    }

    @GetMapping("/turns/week")
    fun getTurnsByWeek(@RequestParam startDate: LocalDate): List<TurnDTO>{
        return turnService.getTurnsByWeek(startDate).map{
            TurnDTO(it.id, it.datetime, it.capacity, it.enrolled, it.activity!!.name)
        }
    }
}