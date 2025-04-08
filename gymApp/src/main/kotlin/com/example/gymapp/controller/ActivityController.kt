package com.example.gymapp.controller

import com.example.gymapp.model.Activity
import com.example.gymapp.model.Turn
import com.example.gymapp.service.ActivityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ActivityController {

    @Autowired
    lateinit var activityService: ActivityService

    @GetMapping("/activities")
    fun getActivities(): List<Activity>{
        return activityService.getActivities()
    }

    @GetMapping("/turns/{activityId}")
    fun getTurns(@PathVariable activityId: String): List<Turn>{
        return return activityService.getTurnsActivity(activityId.toLong())
    }
}