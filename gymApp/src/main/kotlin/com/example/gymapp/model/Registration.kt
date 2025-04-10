package com.example.gymapp.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "registrations")
class Registration() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    var id:Long? = null

    @ManyToOne
    @JoinColumn(name = "member_id")
    var member: Member? = null

    @ManyToOne
    @JoinColumn(name = "turn_id")
    var turn: Turn? = null

    constructor(member: Member, turn: Turn): this(){
        this.member = member
        this.turn = turn
    }
}