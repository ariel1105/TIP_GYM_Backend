package com.example.gymapp.utils

import com.example.gymapp.model.Activity
import com.example.gymapp.model.Member
import com.example.gymapp.model.Turn
import java.time.LocalDateTime

class MemberBuilder(){

    private var id: Long? = null
    private var name: String? = null

    fun build(): Member{
        val member = Member()
        member.id = id
        member.name = name
        return member
    }

    fun withId(id: Long) = apply { this.id = id }
    fun withName(name: String) = apply { this.name = name }
}

class ActivityBuilder(){
    private var id: Long? = null
    private var name: String? = null

    fun build(): Activity{
        val activity = Activity()
        activity.id = id
        activity.name = name
        return activity
    }

    fun withId(id: Long) = apply { this.id = id }
    fun withName(name: String) = apply { this.name = name }

}

class TurnBuilder {
    private var id: Long? = null
    private var datetime: LocalDateTime? = null
    private var capacity: Int? = null

    fun build(): Turn {
        val turn = Turn()
        turn.id = id
        turn.datetime = datetime
        turn.capacity = capacity
        return turn
    }

    fun withId(id: Long?) = apply { this.id = id }
    fun withDatetime(datetime: LocalDateTime) = apply { this.datetime = datetime }
    fun withCapacity(capacity: Int) = apply { this.capacity = capacity }

}