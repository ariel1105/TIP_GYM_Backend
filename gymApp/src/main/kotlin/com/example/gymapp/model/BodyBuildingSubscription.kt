package com.example.gymapp.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
class BodyBuildingSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "body_building_subscription_id")
    var id:Long? = null

    @ManyToOne
    @JoinColumn(name = "member_id")
    var member: Member? = null

    var acquisitionDate: LocalDate? = null
    var dueDate: LocalDate? = null
    var daysPerWeek: Int? = null

}