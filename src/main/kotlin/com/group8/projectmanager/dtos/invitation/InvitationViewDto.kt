package com.group8.projectmanager.dtos.invitation

import com.group8.projectmanager.models.Invitation.InvitationStatus
import java.sql.Timestamp

data class InvitationViewDto(

    val id: Long,

    val title: String,
    val description: String?,

    val sender: String,
    val receiver: String,

    val sentOn: Timestamp,

    val status: InvitationStatus
)