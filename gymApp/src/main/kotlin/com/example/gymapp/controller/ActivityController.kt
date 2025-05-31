package com.example.gymapp.controller

import com.example.gymapp.model.Activity
import com.example.gymapp.model.Turn
import com.example.gymapp.service.ActivityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

@RestController
class ActivityController {

    @Autowired
    lateinit var activityService: ActivityService

    @GetMapping("/activities")
    fun getActivities(): List<Activity>{
        return activityService.getActivities()
    }
}