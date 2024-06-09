package com.sixbrigade.fta.model.db.round

import org.springframework.data.relational.core.mapping.Table

@Table(name = "PLAYER")
data class DBPlayer(
    val userId: Int,
    val role: Int,
    val roundId: String
)