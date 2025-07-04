package com.example.gymapp.repository

import com.example.gymapp.utils.ActivityBuilder
import com.example.gymapp.utils.MemberBuilder
import com.example.gymapp.utils.RegistrationBuilder
import com.example.gymapp.utils.TurnBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var registrationRepository: RegistrationRepository

    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Autowired
    lateinit var turnRepository: TurnRepository

    @Test
    fun `findByUsername should return member if exists`() {
        val member = MemberBuilder()
            .withName("name")
            .withUsername("username")
            .build()
        memberRepository.save(member)

        val found = memberRepository.findByUsernameField("username")

        assertThat(found).isPresent
        assertThat(found.get().usernameField).isEqualTo("username")
    }

    @Test
    fun `getMemberRegistrations should return registrations with related turn and activity`() {
        val member = MemberBuilder()
            .withName("name")
            .withUsername("username")
            .build()
        memberRepository.save(member)

        val activity = ActivityBuilder()
            .withName("Yoga")
            .build()
        activityRepository.save(activity)

        val turn = TurnBuilder()
            .withDatetime(LocalDateTime.now().plusDays(1))
            .withCapacity(10)
            .withActivity(activity)
            .withEnrolled(1)
            .build()
        turnRepository.save(turn)

        val registration = RegistrationBuilder()
            .withMember(member)
            .withTurn(turn)
            .build()
        registrationRepository.save(registration)

        val registrations = memberRepository.getMemberRegistrations(member.id!!)

        assertThat(registrations).hasSize(1)
        val reg = registrations.first()
        assertThat(reg.member?.id).isEqualTo(member.id)
        assertThat(reg.turn?.id).isEqualTo(turn.id)
        assertThat(reg.turn?.activity?.id).isEqualTo(activity.id)
    }
}