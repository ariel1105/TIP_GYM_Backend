package com.example.gymapp.repository

import com.example.gymapp.model.Activity
import org.springframework.data.jpa.repository.JpaRepository

interface ActivityRepository : JpaRepository<Activity, Long> {
}