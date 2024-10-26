package com.group8.projectmanager.controllers

import com.group8.projectmanager.dtos.invitation.InvitationDto
import com.group8.projectmanager.services.InvitationService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/")
@SecurityRequirements(
    SecurityRequirement(name = "basicAuth"),
    SecurityRequirement(name = "bearerAuth")
)
class InvitationController
@Autowired constructor(val invitationService: InvitationService) {

    @PostMapping("/projects/{id}/invite/")
    @ResponseStatus(HttpStatus.CREATED)
    fun invite(
        @PathVariable("id") projectId: Long,
        @Valid @RequestBody dto: InvitationDto
    ) = invitationService.invite(projectId, dto)

    @GetMapping("/invitations/")
    fun listInvitations() = invitationService.listInvitations()

    @GetMapping("/myInvitations/")
    fun listMyInvitations() = invitationService.listMyInvitations()

    @DeleteMapping("/invitations/{id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteInvitation(@PathVariable id: Long) =
        invitationService.deleteInvitation(id)

    @PostMapping("/invitations/{id}/accept/")
    fun acceptInvitation(@PathVariable id: Long) =
        invitationService.changeInvitationStatus(id, true)

    @PostMapping("/invitations/{id}/reject/")
    fun rejectInvitation(@PathVariable id: Long) =
        invitationService.changeInvitationStatus(id, false)
}