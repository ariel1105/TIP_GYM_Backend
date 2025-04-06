package com.example.gymapp.model

import com.example.gymapp.utils.TurnAlreadyFullException
import jakarta.persistence.*
import java.time.LocalDateTime

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

    fun register(member: Member): Registration {
        if(enrolled == capacity){ throw TurnAlreadyFullException() }
        enrolled++
        return Registration(member, this)
    }
}