package com.example.gymapp.controller

import com.example.gymapp.model.Turn
import com.example.gymapp.service.TurnService
import com.example.gymapp.utils.ScheduleTurnDTO
import com.example.gymapp.utils.SubscriptionRequestDTO
import com.example.gymapp.utils.TurnDTO
import com.example.gymapp.websocket.TurnNotificationSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class TurnController (
    private val turnService: TurnService,
) {

    @GetMapping("/turns/{activityId}")
    fun getTurns(@PathVariable activityId: String): List<TurnDTO>{
        return turnService.getTurnsActivity(activityId.toLong()).map {
            TurnDTO(it.id, it.datetime, it.capacity, it.enrolled, it.activity!!.name, it.activity!!.id)
        }
    }

    @GetMapping("/turns/week")
    fun getTurnsByWeek(@RequestParam startDate: LocalDate): List<TurnDTO>{
        return turnService.getTurnsByWeek(startDate).map{
            TurnDTO(it.id, it.datetime, it.capacity, it.enrolled, it.activity!!.name, it.activity!!.id)
        }
    }


}