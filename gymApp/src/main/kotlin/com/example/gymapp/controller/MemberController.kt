package com.example.gymapp.controller

import com.example.gymapp.model.Registration
import com.example.gymapp.service.MemberService
import com.example.gymapp.utils.MemberDTO
import com.example.gymapp.utils.RegistrationDTO
import com.example.gymapp.utils.SubscriptionRequestDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberController {

    @Autowired
    lateinit var memberService: MemberService

    @GetMapping("/member/registrations/{memberId}")
    fun getRegistrations(@PathVariable memberId: String): List<RegistrationDTO> {
        val registrations = memberService.getMemberRegistrations(memberId.toLong())
        return registrations.map{
            RegistrationDTO(
                turnId = it.turn!!.id,
                activityName = it.turn!!.activity!!.name.toString(),
                startTime = it.turn!!.datetime
            )
        }
    }

    @PostMapping("/member/subscribe/{memberId}")
    fun subscribe(@PathVariable memberId: String, @RequestBody request: SubscriptionRequestDTO): List<RegistrationDTO> {
        //val registration = memberService.subscribe(memberId.toLong())
        val registrations = memberService.subscribeToMultipleTurns(memberId.toLong(), request.turnIds)
        return registrations.map {
            RegistrationDTO(
                it.turn!!.id,
                it.turn!!.activity!!.name.toString(),
                it.turn!!.datetime
            )
        }
    }

    @GetMapping("/member/{memberId}")
    fun getMember(@PathVariable memberId: String): MemberDTO{
        return memberService.getMember(memberId.toLong())
    }


}