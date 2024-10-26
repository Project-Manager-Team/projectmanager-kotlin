package com.group8.projectmanager.repositories

import com.group8.projectmanager.models.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.stream.Stream

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {

    @Query("""
        SELECT p FROM Project p
        WHERE p.creator.id = ?1 OR p.manager.id = ?1
    """)
    fun findVisibleProjects(userId: Long): Stream<Project>

    @Query("""
        SELECT COUNT(p) FROM Project p
        INNER JOIN p.subProjects subs
        WHERE p.id = ?1 AND subs.isCompleted = true
    """)
    fun countCompletedSubproject(projectId: Long): Long
}