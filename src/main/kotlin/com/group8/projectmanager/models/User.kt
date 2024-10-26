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

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return ArrayList()
    }

    override fun getUsername() = username
    override fun getPassword() = password

    fun setPassword(password: String) {
        this.password = password
    }
}