package com.group8.projectmanager.controllers

import com.group8.projectmanager.dtos.project.ProjectCreateDto
import com.group8.projectmanager.services.ProjectService
import com.group8.projectmanager.services.UserService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects/")
@SecurityRequirements(
    SecurityRequirement(name = "basicAuth"),
    SecurityRequirement(name = "bearerAuth")
)
class ProjectController
@Autowired constructor(

    val userService: UserService,
    val projectService: ProjectService

) {

    @GetMapping
    fun listRoots() = projectService.listAllVisibleProjects()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProject(@Valid @RequestBody dto: ProjectCreateDto) {
        val user = userService.getUserByContext().orElseThrow()
        projectService.createProject(user, null, dto)
    }
}