package com.group8.projectmanager.dtos.token

import jakarta.validation.constraints.NotEmpty

data class TokenRefreshRequestDto(

    @field:NotEmpty
    val refresh: String
)