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
    val players: HashSet<DBPlayer>,
    @Transient
    val wonders: MutableList<DBWonder>,
    val status: String
) : Serializable, Persistable<String> {

    override fun getId(): String {
        return roundId
    }

    override fun isNew(): Boolean {
        return roundId.isNotEmpty()
    }

    fun addPlayer(player: DBPlayer) {
        players.add(player)
    }

    fun addWonder(wonder: DBWonder) {
        wonders.add(wonder)
    }
}