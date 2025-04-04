package com.example.gymapp.model

import com.example.gymapp.utils.ActivityBuilder
import com.example.gymapp.utils.TurnBuilder
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ActivityTest {

    @Test
    fun `when an activity is registered, there is no turns available for that activity`(){
        val activity = ActivityBuilder().build()
        val scheduledActivities = activity.scheduledTurns.size

        assertEquals(scheduledActivities, 0, "a new activity should start with 0 scheduled turns")
    }

    @Test
    fun `when an activity schedule a single turn, it has 1 turn`(){
        val activity = ActivityBuilder().build()
        val dateTime = LocalDateTime.parse("2024-04-05T14:30")
        val capacity = 10
        val turn = TurnBuilder().withDatetime(dateTime).withCapacity(capacity).build()

        activity.scheduleSingleTurn(turn)

        val scheduledActivities = activity.scheduledTurns.size
        assertEquals(scheduledActivities, 1, "the activity should have 1 scheduled turns")
    }
}