package com.example.gymapp.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.*

@Entity
class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    var id:Long? = null

    var name:String? = null

    @ManyToMany
    @JoinTable(
        name = "member_class",
        joinColumns = [JoinColumn(name = "member_id")],
        inverseJoinColumns = [JoinColumn(name = "turn_id")]
    )
    val enrollments: MutableList<Turn> = mutableListOf()

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var classAvailability: MutableList<ClassAvailability> = mutableListOf()

}