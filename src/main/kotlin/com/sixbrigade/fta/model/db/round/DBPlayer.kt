package com.sixbrigade.fta.model.db.round

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable

@Table(name = "PLAYER")
data class DBPlayer(
    @Id
    val userId: String,
    val role: String,
    val roundId: String
) : Serializable, Persistable<String> {
    override fun getId(): String {
        return userId
    }

    override fun isNew(): Boolean {
        return userId.isNotEmpty()
    }

}