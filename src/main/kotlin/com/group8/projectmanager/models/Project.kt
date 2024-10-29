package com.group8.projectmanager.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp

@Entity
class Project(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    var description: String? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: ProjectType,

    @ColumnDefault("false")
    var isCompleted: Boolean = false,

    @CreationTimestamp
    @Column(nullable = false)
    var createdOn: Timestamp,

    var startedOn: Timestamp? = null,

    var deadline: Timestamp? = null,

    @ManyToOne(optional = false)
    var creator: User,

    @ManyToOne
    var manager: User? = null,

    @ManyToOne
    var parentProject: Project? = null,

    @OneToMany(
        mappedBy = "parentProject",
        targetEntity = Project::class,
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    var subProjects: List<Project> = listOf()

) {
    enum class ProjectType {
        PROJECT, ROOT, TASK
    }
}