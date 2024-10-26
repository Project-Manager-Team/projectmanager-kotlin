package com.group8.projectmanager.dtos.invitation

import jakarta.validation.constraints.NotEmpty

data class InvitationDto(

    @field:NotEmpty
    val receiver: String,

    @field:NotEmpty
    val title: String,

    val description: String?
)