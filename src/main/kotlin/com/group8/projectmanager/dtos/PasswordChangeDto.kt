package com.group8.projectmanager.dtos

import jakarta.validation.constraints.NotBlank

data class PasswordChangeDto(

    @field:NotBlank
    val oldPassword: String,

    @field:NotBlank
    val newPassword: String
)