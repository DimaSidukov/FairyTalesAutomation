package com.sixbrigade.fta.data.source

import com.sixbrigade.fta.data.repository.PlayerRepository
import com.sixbrigade.fta.data.repository.RoundRepository
import com.sixbrigade.fta.data.repository.WondersRepository
import com.sixbrigade.fta.model.common.round.Player
import com.sixbrigade.fta.model.common.round.Round
import com.sixbrigade.fta.model.common.round.Status
import com.sixbrigade.fta.model.common.round.Wonder
import com.sixbrigade.fta.model.db.round.DBRound
import com.sixbrigade.fta.model.db.round.DBWonder
import com.sixbrigade.fta.model.mapping.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class RoundsDataSource(
    @Autowired private val roundRepository: RoundRepository,
    @Autowired private val playerRepository: PlayerRepository,
    @Autowired private val wonderRepository: WondersRepository
) {

    private companion object {
        val StatusesThatRequireWonder = listOf(
            Status.Started,
            Status.FirstWonderApproved,
            Status.FirstWonderRejected,
            Status.SecondWonderApproved,
            Status.SecondWonderRejected,
            Status.ThirdWonderApproved,
            Status.ThirdWonderRejected,
            Status.LastWonderRejected
        )
    }

    fun createRound(name: String): Round {
        val round = DBRound(
            roundId = UUID.randomUUID().toString(),
            name = name,
            status = Status.NotStarted.toDBType(),
            players = hashSetOf(),
            wonders = mutableListOf()
        )
        roundRepository.save(round)
        return round.toCommonType()
    }

    fun selectUsers(roundId: String, players: List<Player>): Round {
        val dbPlayers = players.map { player ->
            player.copy(
                roundId = roundId
            )
        }.toDBTypes()
        playerRepository.saveAll(dbPlayers)

        val currentRound = roundRepository.findById(roundId).get()
        roundRepository.save(
            currentRound.copy(
                status = Status.Started.toDBType()
            )
        )
        return roundRepository.findById(roundId).get().toCommonType()
    }

    fun getRound(roundId: String): Round {
        return roundRepository.findById(roundId).get().toCommonType()
    }

    fun getRounds(): List<Round> {
        return roundRepository.findAll().toList().toCommonTypes()
    }

    fun getRoundsAwaitingWonder() = getRounds().filter { round ->
        StatusesThatRequireWonder.contains(round.status)
    }

    fun makeWonder(roundId: String, wonderName: String): Round {
        val currentRound = roundRepository.findById(roundId).get()
        return if (StatusesThatRequireWonder.contains(currentRound.status.toRoundStatus())) {
            val wonder = DBWonder(
                wonderId = UUID.randomUUID().toString(),
                name = wonderName,
                roundId = roundId,
                isVerified = false,
                isApproved = false,
                createdForStage = when (currentRound.status.toRoundStatus()) {
                    Status.Started, Status.FirstWonderRejected -> 1
                    Status.FirstWonderApproved, Status.SecondWonderRejected -> 2
                    Status.SecondWonderApproved, Status.ThirdWonderRejected -> 3
                    Status.ThirdWonderApproved, Status.LastWonderRejected -> 4
                    else -> null
                }
            )
            wonderRepository.save(wonder)

            val wonderList = currentRound.wonders
            wonderList.add(wonder)
            val updatedRound = currentRound.copy(
                status = when (currentRound.status.toRoundStatus()) {
                    Status.Started, Status.FirstWonderRejected ->
                        Status.AwaitFirstWonderApproval.toDBType()

                    Status.FirstWonderApproved, Status.SecondWonderRejected ->
                        Status.AwaitSecondWonderApproval.toDBType()

                    Status.SecondWonderApproved, Status.ThirdWonderRejected ->
                        Status.AwaitThirdWonderApproval.toDBType()

                    Status.ThirdWonderApproved, Status.LastWonderRejected ->
                        Status.AwaitLastWonderApproval.toDBType()

                    else -> currentRound.status
                },
                wonders = wonderList
            )
            roundRepository.save(updatedRound)
            updatedRound
        } else {
            currentRound
        }.toCommonType()
    }

    fun getWonders() = wonderRepository.findAll().toCommonTypes()

    fun verifyWonder(wonderId: String): Wonder {
        val wonder = wonderRepository.findById(wonderId).get()
        val updatedWonder = wonder.copy(
            isVerified = true
        )
        return wonderRepository.save(updatedWonder).toCommonType()
    }

    fun approveWonder(wonderId: String): Wonder {
        val wonder = wonderRepository.findById(wonderId).get()
        val updatedWonder = wonder.copy(
            isApproved = true
        )
        return wonderRepository.save(updatedWonder).toCommonType()
    }

}