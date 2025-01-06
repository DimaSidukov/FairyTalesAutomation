package com.sixbrigade.fta.model.common.user

data class User(
    val id: String,
    val name: String,
    val email: String,
    val createdAt: String,
    val preferredRole: String?,
    val isBanned: Boolean
)