package com.example.gymapp.model

import com.example.gymapp.utils.TurnBuilder
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    var id:Long? = null

    var name:String? = null
    var img: String? = null
    var description: String? = null

    @OneToMany(mappedBy = "activity", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonIgnore
    var scheduledTurns: MutableList<Turn> = mutableListOf()

    fun scheduleSingleTurn(turn: Turn) {
        scheduledTurns.add(turn)
    }

}