package com.group8.projectmanager.controllers

import com.group8.projectmanager.dtos.DeleteUserDto
import com.group8.projectmanager.dtos.PasswordChangeDto
import com.group8.projectmanager.dtos.UserDto
import com.group8.projectmanager.dtos.project.ProjectCreateDto
import com.group8.projectmanager.dtos.token.TokenRefreshRequestDto
import com.group8.projectmanager.services.JwtsService
import com.group8.projectmanager.services.ProjectService
import com.group8.projectmanager.services.UserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users/")
class UserController
@Autowired constructor(

    val jwtsService: JwtsService,
    val userService: UserService,
    val projectService: ProjectService

) {

    @PostMapping("/register/")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@Valid @RequestBody dto: UserDto) {

        val user = userService.createUser(dto)

        val createDto = ProjectCreateDto(
            name = "Root project for ${user.username}",
            description = null,
            startedOn = null,
            deadline = null,
        )

        projectService.createProject(user, null, createDto)
    }

    @PostMapping("/token/")
    fun obtainToken(@Valid @RequestBody dto: UserDto) =
        jwtsService.tokenObtainPair(dto)

    @PostMapping("/token/refresh/")
    fun refreshToken(@Valid @RequestBody dto: TokenRefreshRequestDto) =
        jwtsService.refreshToken(dto.refresh)

    @PostMapping("/change-password/")
    fun changePassword(@Valid @RequestBody dto: PasswordChangeDto) =
        userService.changePassword(dto)

    @PostMapping("/delete/")
    fun deleteAccount(@RequestBody dto: @Valid DeleteUserDto) =
        userService.deleteUser(dto)
}