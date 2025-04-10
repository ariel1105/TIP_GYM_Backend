package com.example.gymapp.service.impl

import com.example.gymapp.model.Activity
import com.example.gymapp.model.Turn
import com.example.gymapp.repository.ActivityRepository
import com.example.gymapp.repository.TurnRepository
import com.example.gymapp.service.ActivityService
import com.example.gymapp.utils.NoTurnsForActivityException
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Transactional
class ActivityServiceImpl : ActivityService{

    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Autowired
    lateinit var turnRepository: TurnRepository

    override fun getActivities(): List<Activity> {
        return activityRepository.findAll()
    }

    override fun getTurnsActivity(activityId: Long): List<Turn> {
        var result = turnRepository.findByActivityId(activityId)
        if(result.isEmpty()){ throw NoTurnsForActivityException()}
        return result
    }
}