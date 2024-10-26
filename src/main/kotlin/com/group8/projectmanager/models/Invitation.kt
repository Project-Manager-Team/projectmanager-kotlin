package com.group8.projectmanager.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp


@Entity
class Invitation(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var title: String,

    var description: String?,

    @CreationTimestamp
    var sentOn: Timestamp,

    @Enumerated(EnumType.STRING)
    var status: InvitationStatus,

    @ManyToOne(optional = false)
    var project: Project,

    @ManyToOne(optional = false)
    var sender: User,

    @ManyToOne(optional = false)
    var receiver: User

) {
    enum class InvitationStatus {
        PENDING, ACCEPTED, REJECTED
    }
}