package com.sixbrigade.fta.model.db.round

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable

@Table(name = "WONDER")
data class DBWonder(
    @Id
    val wonderId: String,
    val name: String,
    val roundId: String,
    val isVerified: Boolean,
    val isApproved: Boolean,
    val createdForStage: Int?
) : Serializable, Persistable<String> {
    override fun getId(): String {
        return wonderId
    }

    override fun isNew(): Boolean {
        return wonderId.isNotEmpty()
    }
}