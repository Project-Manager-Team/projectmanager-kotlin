package com.group8.projectmanager.dtos

import jakarta.validation.constraints.NotBlank

data class DeleteUserDto(

    @field:NotBlank
    val password: String

)