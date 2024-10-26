package com.group8.projectmanager.services

import com.group8.projectmanager.dtos.invitation.InvitationDto
import com.group8.projectmanager.dtos.invitation.InvitationViewDto
import com.group8.projectmanager.models.Invitation
import com.group8.projectmanager.models.Invitation.InvitationStatus
import com.group8.projectmanager.repositories.InvitationRepository
import com.group8.projectmanager.repositories.ProjectRepository
import com.group8.projectmanager.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.ErrorResponseException
import java.sql.Timestamp

@Service
class InvitationService
@Autowired constructor(

    val userService: UserService,
    val userRepository: UserRepository,

    val projectService: ProjectService,
    val projectRepository: ProjectRepository,

    val invitationRepository: InvitationRepository

) {

    private fun convertToDto(invitation: Invitation) = InvitationViewDto(

        id = invitation.id,

        title = invitation.title,
        description = invitation.description,

        sender = invitation.sender.username,
        receiver = invitation.receiver.username,

        sentOn = invitation.sentOn,

        status = invitation.status,
    )

    @Transactional
    fun invite(projectId: Long, dto: InvitationDto) {

        val sender = userService.getUserByContext().orElseThrow()
        val targetProject =
            projectService.retrieveProjectAndCheck(projectId, sender)

        if (targetProject.manager != null) {
            throw ErrorResponseException(HttpStatus.CONFLICT)
        }

        val invitedUser = userRepository.findByUsername(dto.receiver)
            .orElseThrow { ErrorResponseException(HttpStatus.NOT_FOUND) }

        val senderIsInvited = sender.id == invitedUser.id

        if (senderIsInvited) {
            throw ErrorResponseException(HttpStatus.BAD_REQUEST)
        }

        val now = Timestamp(System.currentTimeMillis())

        val invitation = Invitation(
            title = dto.title,
            description = dto.description,
            sentOn = now,
            status = InvitationStatus.PENDING,
            project = targetProject,
            sender = sender,
            receiver = invitedUser
        )

        invitationRepository.save(invitation)
    }

    @Transactional
    fun changeInvitationStatus(id: Long, isAccept: Boolean) {

        val target = invitationRepository.getReferenceById(id)

        val user = userService.getUserByContext().orElseThrow()
        val userIsReceiver = user.id == target.receiver.id

        if (!userIsReceiver) {
            throw ErrorResponseException(HttpStatus.FORBIDDEN)
        }

        if (isAccept) {

            val targetProject = target.project
            targetProject.manager = user

            projectRepository.save(targetProject)

            target.status = InvitationStatus.ACCEPTED
            invitationRepository.save(target)

        } else {

            target.status = InvitationStatus.REJECTED
            invitationRepository.save(target)
        }
    }

    @Transactional(readOnly = true)
    fun listInvitations(): List<InvitationViewDto> {

        val user = userService.getUserByContext().orElseThrow()

        return invitationRepository.findByReceiverId(user.id)
            .map { invitation -> convertToDto(invitation) }
            .toList()
    }

    @Transactional(readOnly = true)
    fun listMyInvitations(): List<InvitationViewDto> {

        val user = userService.getUserByContext().orElseThrow()

        return invitationRepository.findBySenderId(user.id)
            .map { invitation -> convertToDto(invitation) }
            .toList()
    }

    @Transactional
    fun deleteInvitation(id: Long) {

        val target = invitationRepository.getReferenceById(id)

        val user = userService.getUserByContext().orElseThrow()

        val userIsSender = user.id == target.sender.id
        val userIsReceiver = user.id == target.receiver.id

        if (!userIsSender && !userIsReceiver) {
            throw ErrorResponseException(HttpStatus.FORBIDDEN)
        }

        invitationRepository.delete(target)
    }
}