package com.sixbrigade.fta.model.db.round

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable

@Table(name = "ROUND")
data class DBRound(
    @Id
    val roundId: String,
    val name: String,
    @Transient
    val participants: List<DBPlayer>,
    val status: Int
) : Serializable, Persistable<String> {

    override fun getId(): String {
        return roundId
    }

    override fun isNew(): Boolean {
        return roundId.isNotEmpty()
    }
}