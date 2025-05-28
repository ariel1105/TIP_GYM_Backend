package com.example.gymapp.repository

import com.example.gymapp.model.Voucher
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VoucherRepository : JpaRepository<Voucher, Long> {

    @Query("""
        SELECT v FROM Voucher v 
        WHERE v.member.id = :memberId AND v.remainingClasses > 0 
        ORDER BY v.acquisitionDate ASC
    """)
    fun getActiveVouchersByMemberId(@Param("memberId") memberId: Long): List<Voucher>
}