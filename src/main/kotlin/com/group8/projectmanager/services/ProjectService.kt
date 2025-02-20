package com.group8.projectmanager.services

import com.group8.projectmanager.dtos.project.ProjectCreateDto
import com.group8.projectmanager.dtos.project.ProjectDetailDto
import com.group8.projectmanager.dtos.project.ProjectUpdateDto
import com.group8.projectmanager.models.Project
import com.group8.projectmanager.models.Project.ProjectType
import com.group8.projectmanager.models.User
import com.group8.projectmanager.repositories.ProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.ErrorResponseException
import java.sql.Timestamp
import java.util.*

@Service
class ProjectService
@Autowired constructor(

    val userService: UserService,
    val repository: ProjectRepository

) {

    private fun computeCompleted(project: Project): Boolean {

        if (project.type == ProjectType.TASK) {
            return project.isCompleted
        }

        if (project.isCompleted) {
            return true
        }

        val allCompleted = project.subProjects
            .stream()
            .allMatch(this::computeCompleted)

        if (allCompleted) {

            project.isCompleted = true
            repository.save(project)

            return true

        } else {
            return false
        }
    }

    private fun convertToDetailDto(project: Project): ProjectDetailDto {

        computeCompleted(project)

        return ProjectDetailDto(

            id = project.id,

            name = project.name,
            description = project.description,

            creator = project.creator.username,
            manager = project.manager?.username,

            type = project.type,
            parentProjectId = project.parentProject?.id,

            isCompleted = project.isCompleted,

            completedCount = repository.countCompletedSubproject(project.id),
            subProjectCount = project.subProjects.size,

            createdOn = project.createdOn,
            deadline = project.deadline,
        )
    }

    private fun invalidateCompletedStatus(parent: Project) {

        var ptr: Project? = parent

        while (ptr != null) {

            ptr.isCompleted = false
            repository.save(ptr)

            ptr = ptr.parentProject
        }
    }

    private fun findHighestNodeVisible(
        disjointSet: MutableMap<Long, Project?>,
        proj: Project?, user: User
    ): Project? {

        if (proj == null) return null

        return disjointSet.computeIfAbsent(proj.id, { _ ->

            val parent = proj.parentProject
            val highestNode = findHighestNodeVisible(disjointSet, parent, user)

            when {
                highestNode != null -> highestNode
                user == proj.creator || user == proj.manager -> proj
                else -> null
            }
        })
    }

    private fun ableToView(project: Project?, user: User): Boolean {
        val disjointSet = HashMap<Long, Project?>()
        return findHighestNodeVisible(disjointSet, project, user) != null
    }

    fun retrieveProjectAndCheck(id: Long, user: User): Project {

        val target = repository.getReferenceById(id)

        if (!ableToView(target, user)) {
            throw ErrorResponseException(HttpStatus.FORBIDDEN)
        }

        return target
    }

    fun retrieveProjectAndCheck(id: Long): Project {
        val user = userService.getUserByContext().orElseThrow()
        return retrieveProjectAndCheck(id, user)
    }

    fun createProject(creator: User, parent: Project?, dto: ProjectCreateDto) {

        var type = ProjectType.TASK

        if (parent == null) {
            type = ProjectType.ROOT
        } else if (parent.type == ProjectType.TASK) {
            parent.type = ProjectType.PROJECT
            invalidateCompletedStatus(parent)
        }

        val now = Timestamp(System.currentTimeMillis())

        val project = Project(
            type = type,
            name = dto.name,
            description = dto.description,
            isCompleted = false,
            createdOn = now,
            startedOn = dto.startedOn,
            deadline = dto.deadline,
            creator = creator,
            manager = null,
            parentProject = parent
        )

        repository.save(project)
    }

    @Transactional
    fun changeProjectInfo(id: Long, dto: ProjectUpdateDto) {

        val project = retrieveProjectAndCheck(id)

        project.name = dto.name
        project.description = dto.description

        project.startedOn = dto.startedOn
        project.deadline = dto.deadline

        repository.save(project)
    }

    @Transactional(readOnly = true)
    fun retrieveProjectDetail(id: Long): ProjectDetailDto {
        val target = retrieveProjectAndCheck(id)
        return convertToDetailDto(target)
    }

    @Transactional(readOnly = true)
    fun listAllVisibleProjects(): List<ProjectDetailDto> {

        val user = userService.getUserByContext().orElseThrow()

        val disjointSet = HashMap<Long, Project?>()
        val results = TreeSet<Project>(Comparator.comparing { proj -> proj.id })

        repository
            .findVisibleProjects(user.id)
            .map { proj -> findHighestNodeVisible(disjointSet, proj, user) }
            .forEach { proj -> if (proj != null) results.add(proj) }

        return results.stream()
            .map { proj ->

                val subprojects = ArrayList(proj.subProjects)
                subprojects.add(proj)

                subprojects
            }
            .flatMap { list -> list.stream() }
            .map { project -> convertToDetailDto(project) }
            .toList()
    }

    @Transactional(readOnly = true)
    fun listSubProjects(id: Long): List<ProjectDetailDto> {

        val target = retrieveProjectAndCheck(id)
        val subprojects = target.subProjects

        return subprojects.stream()
            .map { project -> convertToDetailDto(project) }
            .toList()
    }

    @Transactional
    fun newSubProject(parentId: Long, dto: ProjectCreateDto) {

        val user = userService.getUserByContext().orElseThrow()

        val parentProject = retrieveProjectAndCheck(parentId, user)
        createProject(user, parentProject, dto)
    }

    @Transactional
    fun markCompleted(id: Long) {

        val target = retrieveProjectAndCheck(id)

        if (target.type != ProjectType.TASK) {

            val e = ErrorResponseException(HttpStatus.CONFLICT)
            e.setTitle("Only task can mark completed.")

            throw e
        }

        target.isCompleted = true
        repository.save(target)
    }

    @Transactional
    fun deleteProject(id: Long) {

        val user = userService.getUserByContext().orElseThrow()
        val target = retrieveProjectAndCheck(id, user)

        if (user == target.manager) {
            target.manager = null
            repository.save(target)
        } else {
            repository.delete(target)
        }
    }
}