package com.example.gymapp.controller

import com.example.gymapp.model.Registration
import com.example.gymapp.service.MemberService
import com.example.gymapp.utils.RegistrationDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class MemberController {

    @Autowired
    lateinit var memberService: MemberService

    @GetMapping("/member/registrations/{memberId}")
    fun getRegistrations(@PathVariable memberId: String): List<RegistrationDTO> {
        return memberService.getMemberRegistrations(memberId.toLong())
    }
}