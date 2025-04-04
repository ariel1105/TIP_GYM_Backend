package com.example.gymapp.model

import com.example.gymapp.utils.TurnBuilder
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "activity_id")
    var id:Long? = null

    var name:String? = null

    @OneToMany(mappedBy = "activity", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var scheduledTurns: MutableList<Turn> = mutableListOf()

    fun scheduleSingleTurn(turn: Turn) {
        scheduledTurns.add(turn)
    }

}