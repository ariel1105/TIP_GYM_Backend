package com.example.gymapp.controller

import com.example.gymapp.model.Member
import com.example.gymapp.model.Registration
import com.example.gymapp.service.JwtService
import com.example.gymapp.service.MemberService
import com.example.gymapp.utils.MemberDTO
import com.example.gymapp.utils.RegistrationDTO
import com.example.gymapp.utils.SubscriptionRequestDTO
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member")
class MemberController {

    @Autowired
    lateinit var memberService: MemberService
    @Autowired
    lateinit var jwtService: JwtService

    @GetMapping
    fun getMember(request: HttpServletRequest): MemberDTO{
        val memberId = getMemberIdFromRequest(request)
        return memberService.getMember(memberId.toLong())
    }

    @GetMapping("/registrations")
    fun getRegistrations(request: HttpServletRequest): List<RegistrationDTO> {
        val memberId = getMemberIdFromRequest(request)

        val registrations = memberService.getMemberRegistrations(memberId.toLong())
        return registrations.map{
            RegistrationDTO(
                turnId = it.turn!!.id,
                activityName = it.turn!!.activity!!.name.toString(),
                startTime = it.turn!!.datetime
            )
        }
    }

    @PostMapping("/subscribe")
    fun subscribe(request: HttpServletRequest, @RequestBody body: SubscriptionRequestDTO): List<RegistrationDTO> {
        val memberId = getMemberIdFromRequest(request)
        println(memberId)
        val registrations = memberService.subscribeToMultipleTurns(memberId.toLong(), body.turnIds)
        return registrations.map {
            RegistrationDTO(
                it.turn!!.id,
                it.turn!!.activity!!.name.toString(),
                it.turn!!.datetime
            )
        }
    }

    @DeleteMapping("/unsubscribe/{turnId}")
    fun unsubscribe(request: HttpServletRequest, @PathVariable turnId: Long): String{
        val memberId = getMemberIdFromRequest(request)
        memberService.unsubscribeFromTurn(memberId, turnId)
        return "Has sido removido de este turno"
    }

    private fun getMemberIdFromRequest(request: HttpServletRequest): Long {
        return jwtService.extractId(request.getHeader("Authorization")).toLong()
    }

}