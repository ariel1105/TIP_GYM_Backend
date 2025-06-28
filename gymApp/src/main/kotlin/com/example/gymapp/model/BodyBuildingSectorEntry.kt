package com.example.gymapp.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
class BodyBuildingSectorEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "body_building_entries_id")
    var id:Long? = null

    @ManyToOne
    @JoinColumn(name = "member_id")
    var member: Member? = null

    @Column(name = "datetime")
    var dateTime: LocalDateTime? = null

}