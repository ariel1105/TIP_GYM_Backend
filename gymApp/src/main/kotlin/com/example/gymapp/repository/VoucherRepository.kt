package com.example.gymapp.repository

import com.example.gymapp.model.Voucher
import org.springframework.data.jpa.repository.JpaRepository

interface VoucherRepository : JpaRepository<Voucher, Long> {
}