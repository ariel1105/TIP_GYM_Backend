package com.example.gymapp.repository
import com.example.gymapp.utils.ActivityBuilder
import com.example.gymapp.utils.MemberBuilder
import com.example.gymapp.utils.VoucherBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDate

@DataJpaTest
class VoucherRepositoryTest {

    @Autowired
    lateinit var voucherRepository: VoucherRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var activityRepository: ActivityRepository

    @Test
    fun `getActiveVouchersByMemberId should return only active vouchers sorted by acquisition date`() {
        val member = memberRepository.save(MemberBuilder().withUsername("username").withName("name").build())
        val activity = activityRepository.save(ActivityBuilder().withName("Yoga").build())

        val voucher1 = VoucherBuilder()
            .withMember(member)
            .withActivity(activity)
            .withAmount(5)
            .withRemainingClasses(0)
            .withAcquisitionDate(LocalDate.of(2024, 1, 1))
            .build()

        val voucher2 = VoucherBuilder()
            .withMember(member)
            .withActivity(activity)
            .withAmount(10)
            .withRemainingClasses(3)
            .withAcquisitionDate(LocalDate.of(2024, 2, 1))
            .build()

        val voucher3 = VoucherBuilder()
            .withMember(member)
            .withActivity(activity)
            .withAmount(10)
            .withRemainingClasses(5)
            .withAcquisitionDate(LocalDate.of(2023, 12, 15))
            .build()

        voucherRepository.saveAll(listOf(voucher1, voucher2, voucher3))

        val result = voucherRepository.getActiveVouchersByMemberId(member.id!!)

        assertThat(result).hasSize(2)
        assertThat(result[0].acquisitionDate).isEqualTo(LocalDate.of(2023, 12, 15))
        assertThat(result[1].acquisitionDate).isEqualTo(LocalDate.of(2024, 2, 1))
    }
}