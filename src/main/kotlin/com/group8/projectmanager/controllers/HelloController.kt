package com.group8.projectmanager.controllers

import com.group8.projectmanager.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController
@Autowired constructor(val userService: UserService) {

    data class GreetingDto(val message: String)

    @GetMapping("/api/hello/")
    fun greeting(): GreetingDto {

        val user = userService.getUserByContext()

        var message = "Hello, world!"
        if (user.isPresent) {
            val username = user.get().username
            message = "Hello, your username is ${username}"
        }

        return GreetingDto(message)
    }
}