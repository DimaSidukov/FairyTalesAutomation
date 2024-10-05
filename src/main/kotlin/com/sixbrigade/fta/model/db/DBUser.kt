package com.sixbrigade.fta.model.db

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable

@Table(name = "USER")
data class DBUser(
    @Id
    val id: String,
    val name: String,
    val email: String,
    val createdAt: String,
    val login: String,
    val password: String
) : Serializable
