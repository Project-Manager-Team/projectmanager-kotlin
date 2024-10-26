package com.group8.projectmanager.dtos.project

import com.group8.projectmanager.models.Project.ProjectType
import java.sql.Timestamp

data class ProjectDetailDto(

    val id: Long,

    val name: String,
    val description: String?,

    val creator: String,
    val manager: String?,

    val parentProjectId: Long?,
    val type: ProjectType,

    val isCompleted: Boolean,

    val completedCount: Long,
    val subProjectCount: Int,

    val createdOn: Timestamp,
    val deadline: Timestamp?,
)
