package com.sixbrigade.fta.services

import com.sixbrigade.fta.data.source.RoundsDataSource
import com.sixbrigade.fta.model.common.round.Player
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RoundsService(
    @Autowired private val dataSource: RoundsDataSource
) {

    fun createRound(name: String) = dataSource.createRound(name)

    fun selectUsers(roundId: String, players: List<Player>) = dataSource.selectUsers(roundId, players)

    fun getRound(roundId: String) = dataSource.getRound(roundId)

    fun getAllRounds() = dataSource.getRounds()

    fun getActiveRounds() = dataSource.getActiveRounds()

    fun getAvailablePlayers() = dataSource.getAvailablePlayers()

    fun getRoundsAwaitingWonder() = dataSource.getRoundsAwaitingWonder()

    fun makeWonder(roundId: String, wonderName: String) = dataSource.makeWonder(roundId, wonderName)

    fun getWonders() = dataSource.getWonders()

    fun getWondersAwaitingApproval() = dataSource.getWondersAwaitingApproval()

    fun verifyWonder(wonderId: String) = dataSource.verifyWonder(wonderId)

    fun approveWonder(wonderId: String) = dataSource.approveWonder(wonderId)

    fun rejectWonder(wonderId: String) = dataSource.rejectWonder(wonderId)

}