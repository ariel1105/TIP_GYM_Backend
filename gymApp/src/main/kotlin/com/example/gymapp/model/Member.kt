package com.example.gymapp.model

import com.example.gymapp.utils.VoucherBuilder
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


@Entity
class Member : UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    var id:Long? = null

    @Column(nullable = false)
    var name:String? = null
    @Column(name="username", nullable = false, unique = true)
    var usernameField:String? = null
    @Column(name="password")
    var passwordField:String? = null
    @Column
    var role: String = "USER"

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var vouchers: MutableList<Voucher> = mutableListOf()


    @ManyToMany
    @JoinTable(
        name = "member_notification_subscriptions",
        joinColumns = [JoinColumn(name = "member_id")],
        inverseJoinColumns = [JoinColumn(name = "activity_id")]
    )
    val notificationSubscriptions: MutableSet<Activity> = mutableSetOf()


    fun acquire(activity: Activity, amount: Int, acquisitionWay: String = "COMPRA"): Voucher {
        val voucher = VoucherBuilder()
            .withMember(this)
            .withActivity(activity)
            .withAmount(amount)
            .withRemainingClasses(amount)
            .withAcquisitionWay(acquisitionWay)
            .build()
        vouchers.add(voucher)
        return voucher
    }

    fun subscribeToNotification(activity: Activity) {
        if (!notificationSubscriptions.remove(activity)) {
            notificationSubscriptions.add(activity)
        }
    }

    fun subscribe(turn: Turn): Registration {
        val registration = turn.register(this)
        return registration
    }

    fun unsubscribe(turn: Turn): Voucher{
        turn.remove(this)
        return this.acquire(turn.activity!!, 1, acquisitionWay = "CANCELACIÓN DE TURNO")
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        mutableListOf(SimpleGrantedAuthority(role))

    override fun getPassword(): String? {
        return passwordField
    }

    override fun getUsername(): String? {
        return usernameField
    }


}