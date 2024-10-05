package com.sixbrigade.fta.model.db

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable

@Table(name = "USER")
data class DBUser(
    @Id
    val userId: String,
    val name: String,
    val email: String,
    val createdAt: String,
    val login: String,
    val password: String
) : Serializable, Persistable<String> {
    override fun getId(): String {
        return userId
    }

    override fun isNew(): Boolean {
        return userId.isNotEmpty()
    }

}
