package com.example.gymapp.model

import com.example.gymapp.utils.NoRemainingClassesException
import com.example.gymapp.utils.NonOwnVoucherException
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.*

@Entity
class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    var id:Long? = null

    var name:String? = null

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var vouchers: MutableList<Voucher> = mutableListOf()

    fun acquire(availability: Voucher) {
        vouchers.add(availability)
    }

    fun useVoucher(voucher: Voucher?, turn:Turn): Registration {
        voucher!!.validate(turn, id)
        val registration = turn.register(this)
        return registration
    }
}