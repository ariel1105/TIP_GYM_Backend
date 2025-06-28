package com.example.gymapp.repository

import com.example.gymapp.model.BodyBuildingSectorEntry
import org.springframework.data.jpa.repository.JpaRepository

interface BodyBuildingSectorEntryRepository : JpaRepository<BodyBuildingSectorEntry, Long> {
}