package com.example.gymapp.model

import com.example.gymapp.utils.MemberAlreadyRegisteredException
import com.example.gymapp.utils.MemberNotRegisteredInTurnException
import com.example.gymapp.utils.PassedTimeOnTurnException
import com.example.gymapp.utils.TurnAlreadyFullException
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Entity
class Turn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "turn_id")
    var id: Long? = null

    var datetime: LocalDateTime? = null
    var capacity: Int = 0
    var enrolled: Int = 0

    @ManyToOne
    @JoinColumn(name = "activity_id") // FK en la tabla Turn
    var activity: Activity? = null

    @OneToMany(mappedBy = "turn", cascade = [CascadeType.ALL], orphanRemoval = true)
    var registrations: MutableList<Registration> = mutableListOf()

    fun register(member: Member): Registration {
        if (enrolled == capacity) { throw TurnAlreadyFullException() }
        if (registrations.any { it.member?.id == member.id }) {
            throw MemberAlreadyRegisteredException()
        }
        val registration = Registration(member, this)
        registrations.add(registration)
        enrolled++
        return registration
    }

    fun remove(member: Member){
        val registration = registrations.find { it.member?.id == member.id }
            ?: throw MemberNotRegisteredInTurnException()
        val now = LocalDateTime.now()
        if (datetime!!.isBefore(now) || ChronoUnit.HOURS.between(now, datetime) < 24) {
            throw PassedTimeOnTurnException("No se puede cancelar el turno con menos de 24 horas de anticipación.")
        }
        registrations.remove(registration)
        enrolled--
    }
}