package com.group8.projectmanager.dtos.project

import jakarta.validation.constraints.NotEmpty
import java.sql.Timestamp

data class ProjectCreateDto(

    @field:NotEmpty
    val name: String,

    val description: String?,

    val startedOn: Timestamp?,
    val deadline: Timestamp?
)