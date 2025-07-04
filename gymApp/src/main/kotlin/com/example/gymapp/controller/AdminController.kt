package com.example.gymapp.controller

import com.example.gymapp.service.MemberService
import com.example.gymapp.service.TurnService
import com.example.gymapp.utils.ScheduleTurnDTO
import com.example.gymapp.utils.TurnDTO
import com.example.gymapp.websocket.TurnNotificationSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController (
    private val turnService: TurnService,
    private val turnNotificationSender: TurnNotificationSender
) {

    @Autowired
    lateinit var memberService: MemberService

    @PostMapping("/bodyBuilding/registerEntry")
    fun registerEntry(@RequestParam memberId: Long): String{
        val member = memberService.registerEntryBodyBuildingSector(memberId)
        return "Bienvenido ${member.name}"
    }

    @PostMapping("/turns/schedule/{activityId}")
    fun scheduleTurns(@PathVariable activityId: String, @RequestBody body: List<ScheduleTurnDTO>): List<TurnDTO>{
        val turns = turnService.scheduleTurns(activityId.toLong(), body).map {
            TurnDTO(it.id!!, it.datetime!!, it.capacity, it.enrolled, it.activity!!.name!!, it.activity!!.id)
        }
        turnNotificationSender.notifyTurnsScheduled(activityId.toLong(), turns)
        return turns
    }
}