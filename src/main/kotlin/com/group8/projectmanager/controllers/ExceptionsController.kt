package com.group8.projectmanager.controllers

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.ErrorResponse
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionsController {

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(e: EntityNotFoundException): ProblemDetail {

        val response = ProblemDetail.forStatus(HttpStatus.NOT_FOUND)
        response.detail = e.message

        return response
    }

    @ExceptionHandler(ErrorResponseException::class)
    fun errorResponse(e: ErrorResponseException): ErrorResponse {
        return e
    }
}