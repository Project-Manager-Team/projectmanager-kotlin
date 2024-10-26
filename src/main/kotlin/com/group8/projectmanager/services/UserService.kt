package com.group8.projectmanager.services

import com.group8.projectmanager.dtos.DeleteUserDto
import com.group8.projectmanager.dtos.PasswordChangeDto
import com.group8.projectmanager.dtos.UserDto
import com.group8.projectmanager.models.User
import com.group8.projectmanager.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.ErrorResponseException
import java.util.*

@Service
class UserService
@Autowired constructor(

    val repository: UserRepository,
    val passwordEncoder: PasswordEncoder

) {

    fun createUser(dto: UserDto): User {

        if (repository.existsByUsername(dto.username)) {

            val e = ErrorResponseException(HttpStatus.CONFLICT)
            e.setTitle("Username already exists. Please try a different one.")

            throw e
        }

        val hashedPassword = passwordEncoder.encode(dto.password)

        val user = User(
            username = dto.username,
            password = hashedPassword
        )

        return repository.save(user)
    }

    fun getUserByAuthentication(authentication: Authentication?): Optional<User> {

        if (authentication == null) {
            return Optional.empty()
        }

        val principal = authentication.principal

        return if (principal is User) {
            Optional.of(principal)
        } else {
            Optional.empty()
        }
    }

    fun getUserByContext(): Optional<User> {

        val authentication = SecurityContextHolder
            .getContext().authentication

        return this.getUserByAuthentication(authentication)
    }

    fun changePassword(dto: PasswordChangeDto) {

        val user = getUserByContext().orElseThrow()
        if (!passwordEncoder.matches(dto.oldPassword, user.password)) {
            throw ErrorResponseException(HttpStatus.UNAUTHORIZED)
        }

        val newHashedPassword = passwordEncoder.encode(dto.newPassword)
        user.password = newHashedPassword

        repository.save(user)
    }

    fun deleteUser(dto: DeleteUserDto) {

        val user = getUserByContext().orElseThrow()

        if (!passwordEncoder.matches(dto.password, user.password)) {
            throw ErrorResponseException(HttpStatus.UNAUTHORIZED)
        }

        repository.delete(user)
    }
}