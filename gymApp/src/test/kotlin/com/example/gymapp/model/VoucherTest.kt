package com.example.gymapp.model

import com.example.gymapp.utils.ActivityBuilder
import com.example.gymapp.utils.IncorrectVoucherException
import com.example.gymapp.utils.MemberBuilder
import com.example.gymapp.utils.NoRemainingClassesException
import com.example.gymapp.utils.NonOwnVoucherException
import com.example.gymapp.utils.TurnAlreadyFullException
import com.example.gymapp.utils.TurnBuilder
import com.example.gymapp.utils.VoucherBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class VoucherTest {

    @Test
    fun `when a voucher does not match a turn or member does not have a voucher, an exception is raised`(){
        val member = MemberBuilder()
            .withId(1L)
            .build()
        val activityVoucher = ActivityBuilder()
            .withId(1L)
            .build()
        val activityTurn = ActivityBuilder()
            .withId(2L)
            .build()
        val voucher = VoucherBuilder()
            .withActivity(activityVoucher)
            .withMember(member)
            .build()
        val turn = TurnBuilder()
            .withActivity(activityTurn)
            .build()

        val expectedErrorMsg = "Este voucher no es para esta actividad"
        val errorMsgInvalidVoucher = assertThrows<IncorrectVoucherException> { voucher.validate(turn, 1L)}.message

        assertEquals(errorMsgInvalidVoucher, expectedErrorMsg)

    }

    @Test
    fun `a member cannot use a non own voucher`(){
        val voucherMember = MemberBuilder()
            .withId(2L)
            .build()
        val activity = ActivityBuilder()
            .withId(1L)
            .build()
        val voucher = VoucherBuilder()
            .withActivity(activity)
            .withMember(voucherMember)
            .build()
        val turn = TurnBuilder()
            .withActivity(activity)
            .build()
        val expectedErrorMsg = "No puede usar este voucher"
        val errorMsgInvalidVoucher = assertThrows<NonOwnVoucherException> { voucher.validate(turn, 1L)}.message

        assertEquals(errorMsgInvalidVoucher, expectedErrorMsg)
    }

    @Test
    fun `a member cannot use a voucher with 0 remaining classes`(){
        val member = MemberBuilder()
            .withId(1L)
            .build()
        val activity = ActivityBuilder()
            .withId(1L)
            .build()
        val voucher = VoucherBuilder()
            .withActivity(activity)
            .withMember(member)
            .withRemainingClasses(0)
            .build()
        val turn = TurnBuilder()
            .withActivity(activity)
            .build()
        val expectedErrorMsg = "Has utilizado todas las clases de tu voucher"
        val errorNoClassesRemaining = assertThrows<NoRemainingClassesException> { voucher.validate(turn, 1L)}.message

        assertEquals(errorNoClassesRemaining, expectedErrorMsg)
    }

}