package com.example.gymapp.controller

import com.example.gymapp.model.Activity
import com.example.gymapp.service.ActivityService
import com.example.gymapp.utils.ActivityBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class ActivityControllerTest {

    @InjectMocks
    lateinit var activityController: ActivityController

    @Mock
    lateinit var activityService: ActivityService

    @Test
    fun `should return list of activities`() {
        val activity1 = ActivityBuilder().withId(1L).withName("Yoga").build()
        val activity2 = ActivityBuilder().withId(2L).withName("Spinning").build()
        val activities = listOf(activity1, activity2)

        whenever(activityService.getActivities()).thenReturn(activities)

        val result = activityController.getActivities()

        assertEquals(2, result.size)
        assertEquals("Yoga", result[0].name)
        assertEquals("Spinning", result[1].name)
    }
}