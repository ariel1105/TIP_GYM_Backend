package com.example.gymapp.model

import com.example.gymapp.utils.IncorrectVoucherException
import com.example.gymapp.utils.NoRemainingClassesException
import com.example.gymapp.utils.NonOwnVoucherException
import jakarta.persistence.*

@Entity
@Table(name = "vouchers")
class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne
    @JoinColumn(name = "member_id")
    var member: Member? = null

    @ManyToOne
    @JoinColumn(name = "activity_id")
    var activity: Activity? = null

    var amount: Int = 0
    var remainingClasses: Int = 0

    fun validate(turn: Turn, memberId: Long?) {
        val incorrectVoucherForActivity = activity!!.id != turn.activity!!.id
        val nonOwnVoucher = memberId != member!!.id
        val noRemainingClasses = remainingClasses == 0
        if (nonOwnVoucher) { throw NonOwnVoucherException()}
        if (incorrectVoucherForActivity) { throw IncorrectVoucherException() }
        if (noRemainingClasses) { throw NoRemainingClassesException()}
        remainingClasses--
    }
}
