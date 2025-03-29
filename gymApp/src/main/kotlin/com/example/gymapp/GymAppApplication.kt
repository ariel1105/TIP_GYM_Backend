package com.example.gymapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GymAppApplication

fun main(args: Array<String>) {
	runApplication<GymAppApplication>(*args)
}
