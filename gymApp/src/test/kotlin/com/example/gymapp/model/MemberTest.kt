package com.example.gymapp.model

import com.example.gymapp.utils.MemberBuilder
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MemberTest {

    @Test
    fun `when a member is registered, there is no classes to register for`(){
        val member = MemberBuilder().build()
        val classAvailability = member.classAvailability.size

        assertEquals(classAvailability, 0, "A new member should start with 0 classes available")
    }
}