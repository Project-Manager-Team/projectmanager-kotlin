package com.group8.projectmanager.dtos.project

import jakarta.validation.constraints.NotEmpty
import java.sql.Timestamp

data class ProjectUpdateDto (

    @field:NotEmpty
    val name:String,
    val description: String?,

    val startedOn: Timestamp?,
    val deadline: Timestamp?
)