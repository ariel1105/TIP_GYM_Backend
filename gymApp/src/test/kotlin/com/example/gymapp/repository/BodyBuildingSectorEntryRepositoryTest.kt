package com.example.gymapp.repository

import com.example.gymapp.model.BodyBuildingSectorEntry
import com.example.gymapp.model.Member
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime
import java.time.Month

@DataJpaTest
class BodyBuildingSectorEntryRepositoryTest {

    @Autowired
    lateinit var entryRepository: BodyBuildingSectorEntryRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Test
    fun `findByMemberIdAndMonth should return entries for the given month`() {

        val member = Member().apply {
            name = "Camila"
            usernameField = "cami"
            passwordField = "pass123"
        }
        memberRepository.save(member)

        val now = LocalDateTime.of(2025, Month.JULY, 5, 10, 0)
        val lastMonth = now.minusMonths(1)

        val entry1 = BodyBuildingSectorEntry().apply {
            this.member = member
            this.dateTime = now
        }

        val entry2 = BodyBuildingSectorEntry().apply {
            this.member = member
            this.dateTime = lastMonth
        }

        entryRepository.saveAll(listOf(entry1, entry2))

        val results = entryRepository.findByMemberIdAndMonth(member.id!!, 7)

        assertEquals(1, results.size)
        assertEquals(now, results.first())
    }
}