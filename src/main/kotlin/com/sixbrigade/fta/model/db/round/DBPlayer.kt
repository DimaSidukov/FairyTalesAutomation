package com.sixbrigade.fta.model.db.round

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable

@Table(name = "PLAYER")
data class DBPlayer(
    @Id
    val userId: Int,
    val role: Int,
    val roundId: String
) : Serializable