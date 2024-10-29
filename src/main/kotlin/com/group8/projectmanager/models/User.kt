package com.group8.projectmanager.models

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true, nullable = false)
    private var username: String,

    @Column(nullable = false)
    private var password: String

) : UserDetails {

    override fun equals(other: Any?) =
        other is User && this.id == other.id

    override fun hashCode() = id.hashCode()

    override fun getUsername() = username
    override fun getPassword() = password

    override fun getAuthorities()
        : MutableCollection<out GrantedAuthority> = ArrayList()

    fun setPassword(password: String) {
        this.password = password
    }
}