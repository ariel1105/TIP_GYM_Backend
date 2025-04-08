package com.example.gymapp.service

import com.example.gymapp.model.Activity
import com.example.gymapp.model.Turn

interface ActivityService {

    fun getActivities(): List<Activity>
    fun getTurnsActivity(activityId: Long): List<Turn>
}