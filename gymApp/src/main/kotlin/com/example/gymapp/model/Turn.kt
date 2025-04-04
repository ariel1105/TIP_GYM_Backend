package com.example.gymapp.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class Turn {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "turn_id")
    var id:Long? = null

    var datetime: LocalDateTime? = null
    var capacity: Int? = null
}