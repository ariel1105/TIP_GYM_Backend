package com.example.gymapp.service

import com.example.gymapp.model.Activity
import com.example.gymapp.repository.ActivityRepository
import com.example.gymapp.utils.ActivityBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ActivityServiceTest {

    private val activityRepository: ActivityRepository = mock()

    private val activityService = ActivityService().apply {
        this.activityRepository = this@ActivityServiceTest.activityRepository
    }

    @Test
    fun `getActivities should return all activities from repository`() {

        val activities = listOf(
            ActivityBuilder().withId(1L).withName("Funcional").build(),
            ActivityBuilder().withId(2L).withName("Yoga").build()
        )

        whenever(activityRepository.findAll()).thenReturn(activities)

        val result = activityService.getActivities()

        assertEquals(2, result.size)
        assertEquals("Funcional", result[0].name)
        assertEquals("Yoga", result[1].name)
    }
}