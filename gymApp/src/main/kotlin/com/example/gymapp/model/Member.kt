package com.example.gymapp.model

import com.example.gymapp.utils.NoRemainingClassesException
import com.example.gymapp.utils.NonOwnVoucherException
import com.example.gymapp.utils.VoucherBuilder
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.*
import jdk.jfr.DataAmount


@Entity
class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    var id:Long? = null

    @Column(nullable = false)
    var name:String? = null
    @Column(nullable = false, unique = true)
    var username:String? = null
    var password:String? = null

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var vouchers: MutableList<Voucher> = mutableListOf()

    fun acquire(activity: Activity, amount: Int, acquisitionWay: String = "COMPRA"): Voucher {
        val voucher = VoucherBuilder()
            .withMember(this)
            .withActivity(activity)
            .withAmount(amount)
            .withRemainingClasses(amount)
            .withAcquisitionWay(acquisitionWay)
            .build()
        vouchers.add(voucher)
        return voucher
    }

//    fun useVoucher(voucher: Voucher?, turn:Turn): Registration {
//        voucher!!.validate(turn, id)
//        val registration = turn.register(this)
//        return registration
//    }

    fun subscribe(turn: Turn): Registration {
        val registration = turn.register(this)
        return registration
    }

    fun unsubscribe(turn: Turn): Voucher{
        turn.remove(this)
        return this.acquire(turn.activity!!, 1, acquisitionWay = "CANCELACIÓN DE TURNO")
    }
}