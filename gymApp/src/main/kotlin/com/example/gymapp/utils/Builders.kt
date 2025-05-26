package com.example.gymapp.utils

import com.example.gymapp.model.Activity
import com.example.gymapp.model.Voucher
import com.example.gymapp.model.Member
import com.example.gymapp.model.Turn
import java.time.LocalDate
import java.time.LocalDateTime

class MemberBuilder(){

    private var id: Long? = null
    private var name: String? = null
    private var username: String? = null
    private var password: String? = null

    fun build(): Member{
        val member = Member()
        member.id = id
        member.name = name
        member.password = password
        member.username = username
        return member
    }

    fun withId(id: Long) = apply { this.id = id }
    fun withName(name: String) = apply { this.name = name }
    fun withUsername(username: String) = apply{this.username = username}
    fun withPassword(password: String) = apply{this.password = password}
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
    private var capacity: Int = 0
    private var activity: Activity? = null
    private var enrolled: Int = 0

    fun build(): Turn {
        val turn = Turn()
        turn.id = id
        turn.datetime = datetime
        turn.capacity = capacity
        turn.activity = activity
        turn.enrolled = enrolled
        return turn
    }

    fun withId(id: Long?) = apply { this.id = id }
    fun withDatetime(datetime: LocalDateTime) = apply { this.datetime = datetime }
    fun withCapacity(capacity: Int) = apply { this.capacity = capacity }
    fun withActivity(activity: Activity) = apply { this.activity = activity }
    fun withEnrolled(enrolled: Int) = apply { this.enrolled = enrolled }
}

class VoucherBuilder {
    private var id: Long? = null
    private var member: Member? = null
    private var activity: Activity? = null
    private var amount: Int = 0
    private var remainingClasses: Int = 0
    private var acquisitionDate: LocalDate = LocalDate.now()
    private var acquisitionWay: String? = null

    fun build(): Voucher {
        val voucher = Voucher()
        voucher.id = id
        voucher.member = member
        voucher.activity = activity
        voucher.amount = amount
        voucher.remainingClasses = remainingClasses
        voucher.acquisitionWay = acquisitionWay
        voucher.acquisitionDate = acquisitionDate
        return  voucher
    }

    fun withId(id: Long) = apply { this.id = id }
    fun withMember(member: Member) = apply { this.member = member }
    fun withActivity(activity: Activity) = apply { this.activity = activity }
    fun withAmount(amount: Int) = apply { this.amount = amount }
    fun withRemainingClasses(amount: Int) = apply { this.remainingClasses = amount }
    fun withAcquisitionDate(date: LocalDate) = apply { this.acquisitionDate = date }
    fun withAcquisitionWay(way: String) = apply { this.acquisitionWay = way }

}