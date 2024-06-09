package com.sixbrigade.fta.services

import com.sixbrigade.fta.data.source.RoundsDataSource
import com.sixbrigade.fta.model.common.round.Player
import com.sixbrigade.fta.model.common.round.Round
import com.sixbrigade.fta.model.common.round.Wonder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RoundsService(
    @Autowired private val dataSource: RoundsDataSource
) {

    fun createRound(name: String) = dataSource.createRound(name)

    fun selectUsers(roundId: String, players: List<Player>) = dataSource.selectUsers(roundId, players)

    fun getRound(roundId: String) = dataSource.getRound(roundId)

    fun getRounds() = dataSource.getRounds()

    fun getRoundsAwaitingWonder() = dataSource.getRoundsAwaitingWonder()

    fun makeWonder(roundId: String, wonderName: String) = dataSource.makeWonder(roundId, wonderName)

    fun getWonders() = dataSource.getWonders()

    fun verifyWonder(wonderId: String) = dataSource.verifyWonder(wonderId)

    fun approveWonder(wonderId: String) = dataSource.approveWonder(wonderId)

}