package com.example.gymapp.model

import com.example.gymapp.utils.ActivityBuilder
import com.example.gymapp.utils.IncorrectVoucherException
import com.example.gymapp.utils.VoucherBuilder
import com.example.gymapp.utils.MemberBuilder
import com.example.gymapp.utils.NoRemainingClassesException
import com.example.gymapp.utils.NonOwnVoucherException
import com.example.gymapp.utils.TurnAlreadyFullException
import com.example.gymapp.utils.TurnBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class MemberTest {

    @Test
    fun `when a member is registered, there is no vouchers to use`(){
        val member = MemberBuilder().build()
        val classAvailability = member.vouchers.size

        assertEquals(classAvailability, 0, "A new member should start with 0 classes available")
    }

    @Test
    fun `when a member acquire a pack of classes, increases his amount of vouchers`(){
        val member = MemberBuilder().build()
        val activity = ActivityBuilder().build()

        member.acquire(activity, 10)
        val vouchers = member.vouchers.size

        assertEquals(vouchers, 1, "member should have 1 voucher")
    }


}