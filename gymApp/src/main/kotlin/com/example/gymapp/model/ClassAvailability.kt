package com.example.gymapp.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "member_class_availability")
class ClassAvailability {

    @Id
    @GeneratedValue
    var id:Long? = null
}
