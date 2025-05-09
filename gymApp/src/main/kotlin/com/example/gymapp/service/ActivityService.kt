package com.example.gymapp.service

import com.example.gymapp.model.Activity
import com.example.gymapp.model.Turn
import com.example.gymapp.repository.ActivityRepository
import com.example.gymapp.repository.TurnRepository
import com.example.gymapp.utils.NoTurnsForActivityException
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ActivityService{

    @Autowired
    lateinit var activityRepository: ActivityRepository

    fun getActivities(): List<Activity> {
        return activityRepository.findAll()
    }

}