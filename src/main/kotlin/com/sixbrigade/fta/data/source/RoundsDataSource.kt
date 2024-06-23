package com.sixbrigade.fta.data.source

import com.sixbrigade.fta.data.repository.PlayerRepository
import com.sixbrigade.fta.data.repository.RoundRepository
import com.sixbrigade.fta.data.repository.UserRepository
import com.sixbrigade.fta.data.repository.WondersRepository
import com.sixbrigade.fta.model.common.User
import com.sixbrigade.fta.model.common.round.Player
import com.sixbrigade.fta.model.common.round.Round
import com.sixbrigade.fta.model.common.round.Status
import com.sixbrigade.fta.model.common.round.Wonder
import com.sixbrigade.fta.model.db.DBUser
import com.sixbrigade.fta.model.db.round.DBRound
import com.sixbrigade.fta.model.db.round.DBWonder
import com.sixbrigade.fta.model.mapping.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class RoundsDataSource(
    @Autowired private val jdbcTemplate: JdbcTemplate,
    @Autowired private val userRepository: UserRepository,
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
        jdbcTemplate.execute(
            "UPDATE ROUND SET STATUS = ${Status.Started.toDBType()} WHERE ROUND_ID LIKE '$roundId}'"
        )
        return getRounds().first { round -> round.id == roundId }
    }

    fun getRound(roundId: String): Round = getRounds().first { round -> round.id == roundId }

    fun getRounds(): List<Round> {
        val dbRounds = jdbcTemplate.query("SELECT * FROM ROUND") { rs, _ ->
            DBRound(
                roundId = rs.getString(1),
                name = rs.getString(2),
                status = rs.getInt(3),
                players = hashSetOf(),
                wonders = mutableListOf()
            )
        }
        val dbPlayers = playerRepository.findAll().toList()
        val wonders = wonderRepository.findAll().toList()

        val rounds = dbRounds.map { round ->
            round.copy(
                players = dbPlayers.filter { player ->
                    player.roundId == round.id
                }.toHashSet(),
                wonders = wonders.filter { wonder ->
                    wonder.roundId == round.id
                }.toMutableList()
            )
        }

        return rounds.toCommonTypes()
    }

    fun getAvailablePlayers(): List<User> {
        return jdbcTemplate.query("SELECT * FROM `User`") { rs, _ ->
            DBUser(
                id = rs.getInt(1),
                name = rs.getString(2),
                email = rs.getString(3),
                createdAt = rs.getString(4),
                login = rs.getString(5),
                password = rs.getString(6)
            )
        }.toCommonTypes()
    }

    fun getRoundsAwaitingWonder() = getRounds().filter { round ->
        StatusesThatRequireWonder.contains(round.status)
    }

    fun makeWonder(roundId: String, wonderName: String): Round {
        val currentRound = getRounds().firstOrNull { round ->
            round.id == roundId
        }?.toDBType() ?: throw NotFoundException()
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
            jdbcTemplate.execute(
                "UPDATE ROUND SET STATUS = ${updatedRound.status} WHERE ROUND_ID LIKE '${updatedRound.roundId}'"
            )
            updatedRound
        } else {
            currentRound
        }.toCommonType()
    }

    fun getWonders() = wonderRepository.findAll().toCommonTypes()

    fun verifyWonder(wonderId: String): Wonder {
        val wonder = wonderRepository.findById(wonderId).get()
        jdbcTemplate.execute(
            "UPDATE WONDER SET IS_VERIFIED = TRUE WHERE WONDER_ID LIKE '${wonder.wonderId}'"
        )
        val updatedWonder = wonder.copy(
            isVerified = true
        )
        return updatedWonder.toCommonType()
    }

    fun approveWonder(wonderId: String): Wonder {
        // updating wonder
        val wonder = wonderRepository.findById(wonderId).get()
        jdbcTemplate.execute(
            "UPDATE WONDER SET IS_APPROVED = TRUE WHERE WONDER_ID LIKE '${wonder.wonderId}'"
        )
        val updatedWonder = wonder.copy(
            isApproved = true
        )

        // updating round
        val currentRound = getRounds().firstOrNull { round ->
            round.id == wonder.roundId
        }?.toDBType() ?: throw NotFoundException()
        val updatedRound = currentRound.copy(
            status = when (currentRound.status.toRoundStatus()) {
                Status.AwaitFirstWonderApproval ->
                    Status.FirstWonderApproved.toDBType()

                Status.AwaitSecondWonderApproval ->
                    Status.SecondWonderApproved.toDBType()

                Status.AwaitThirdWonderApproval ->
                    Status.ThirdWonderApproved.toDBType()

                Status.AwaitLastWonderApproval ->
                    Status.LastWonderApproved.toDBType()

                else -> currentRound.status
            }
        )
        jdbcTemplate.execute(
            "UPDATE ROUND SET STATUS = ${updatedRound.status} WHERE ROUND_ID LIKE '${updatedRound.roundId}'"
        )

        return updatedWonder.toCommonType()
    }

    fun rejectWonder(wonderId: String): Wonder {
        // updating wonder
        val wonder = wonderRepository.findById(wonderId).get()
        jdbcTemplate.execute(
            "UPDATE WONDER SET IS_APPROVED = FALSE WHERE WONDER_ID LIKE '${wonder.wonderId}'"
        )
        val updatedWonder = wonder.copy(
            isApproved = false
        )

        // updating round
        val currentRound = getRounds().firstOrNull { round ->
            round.id == wonder.roundId
        }?.toDBType() ?: throw NotFoundException()
        val updatedRound = currentRound.copy(
            status = when (currentRound.status.toRoundStatus()) {
                Status.AwaitFirstWonderApproval ->
                    Status.FirstWonderRejected.toDBType()

                Status.AwaitSecondWonderApproval ->
                    Status.SecondWonderRejected.toDBType()

                Status.AwaitThirdWonderApproval ->
                    Status.ThirdWonderRejected.toDBType()

                Status.AwaitLastWonderApproval ->
                    Status.LastWonderRejected.toDBType()

                else -> currentRound.status
            }
        )
        jdbcTemplate.execute(
            "UPDATE ROUND SET STATUS = ${updatedRound.status} WHERE ROUND_ID LIKE '${updatedRound.roundId}'"
        )

        return updatedWonder.toCommonType()
    }

}