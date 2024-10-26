package com.group8.projectmanager.controllers

import com.group8.projectmanager.dtos.project.ProjectCreateDto
import com.group8.projectmanager.dtos.project.ProjectUpdateDto
import com.group8.projectmanager.services.ProjectService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects/{id}/")
@SecurityRequirements(
    SecurityRequirement(name = "basicAuth"),
    SecurityRequirement(name = "bearerAuth")
)
class ProjectRetrieveController
@Autowired constructor(val projectService: ProjectService) {

    @GetMapping
    fun retrieveProject(@PathVariable id: Long) =
        projectService.retrieveProjectDetail(id)

    @GetMapping("/subprojects/")
    fun listSubProjects(@PathVariable id: Long) =
        projectService.listSubProjects(id)

    @PostMapping("/subprojects/")
    @ResponseStatus(HttpStatus.CREATED)
    fun newSubProject(
        @PathVariable("id") parentId: Long,
        @Valid @RequestBody dto: ProjectCreateDto
    ) = projectService.newSubProject(parentId, dto)

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeProjectInfo(
        @PathVariable id: Long,
        @Valid @RequestBody dto: ProjectUpdateDto
    ) = projectService.changeProjectInfo(id, dto)

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun markCompleted(@PathVariable id: Long) =
        projectService.markCompleted(id)

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProject(@PathVariable id: Long) =
        projectService.deleteProject(id)
}