package com.sixbrigade.fta.data.source

import com.sixbrigade.fta.data.repository.PlayerRepository
import com.sixbrigade.fta.data.repository.RoundRepository
import com.sixbrigade.fta.data.repository.UserRepository
import com.sixbrigade.fta.data.repository.WondersRepository
import com.sixbrigade.fta.model.common.User
import com.sixbrigade.fta.model.common.round.*
import com.sixbrigade.fta.model.db.DBUser
import com.sixbrigade.fta.model.db.round.DBRound
import com.sixbrigade.fta.model.db.round.DBWonder
import com.sixbrigade.fta.model.mapping.toCommonType
import com.sixbrigade.fta.model.mapping.toCommonTypes
import com.sixbrigade.fta.model.mapping.toDBType
import com.sixbrigade.fta.model.mapping.toDBTypes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
            Status.STARTED,
            Status.FIRST_WONDER_APPROVED,
            Status.FIRST_WONDER_REJECTED,
            Status.SECOND_WONDER_APPROVED,
            Status.SECOND_WONDER_REJECTED,
            Status.THIRD_WONDER_APPROVED,
            Status.THIRD_WONDER_REJECTED,
            Status.LAST_WONDER_REJECTED
        )

        const val ROUND_BY_NAME = "SELECT * FROM ROUND WHERE name LIKE '%s' LIMIT 1"
    }

    sealed interface RoundsQueryType {
        data object All : RoundsQueryType
        data object Active : RoundsQueryType
        data object WithWondersAwaitingApproval : RoundsQueryType
    }

    fun createRound(name: String): ResponseEntity<Any> {
        val result = jdbcTemplate.query(ROUND_BY_NAME.format(name)) { _, _ -> }
        if (result.isNotEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf(
                    "code" to HttpStatus.BAD_REQUEST.value(),
                    "message" to "Round with this name already exists! Please, try another name!"
                )
            )
        }
        val round = DBRound(
            roundId = UUID.randomUUID().toString(),
            name = name,
            status = Status.NOT_STARTED,
            players = hashSetOf(),
            wonders = mutableListOf()
        )
        roundRepository.save(round)
        return ResponseEntity.ok(round.toCommonType())
    }

    fun selectUsers(roundId: String, players: List<Player>): ResponseEntity<Any> {
        // Check whether roundIds are present in database
        val foundRoundsIds = jdbcTemplate.query(
            "SELECT ROUND_ID FROM ROUND WHERE ROUND_ID = '$roundId'"
        ) { _, _ -> }
        if (foundRoundsIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                mapOf(
                    "code" to HttpStatus.NOT_FOUND.value(),
                    "message" to "Round id is not found"
                )
            )
        }

        // Check whether userIds are present in database
        val userIds = players.map(Player::userId)
        val foundUserIds = jdbcTemplate.query(
            "SELECT USER_ID FROM `User` WHERE USER_ID IN (${userIds.joinToString(prefix="'", postfix="'", separator="' ,'")})"
        ) { _, _ -> }

        if (userIds.size != foundUserIds.size) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                mapOf(
                    "code" to HttpStatus.NOT_FOUND.value(),
                    "message" to "Provided user ids are not found"
                )
            )
        }

        val dbPlayers = players.map { player ->
            player.copy(
                roundId = roundId
            )
        }.toDBTypes()
        playerRepository.saveAll(dbPlayers)
        jdbcTemplate.execute(
            "UPDATE ROUND SET STATUS = '${Status.STARTED}' WHERE ROUND_ID LIKE '$roundId'"
        )
        return ResponseEntity.ok(getRounds().first { round -> round.id == roundId })
    }

    fun getRound(roundId: String): Round = getRounds().first { round -> round.id == roundId }

    fun getRounds(roundsQueryType: RoundsQueryType = RoundsQueryType.Active): List<Round> {
        val dbRounds = jdbcTemplate.query(
            when (roundsQueryType) {
                RoundsQueryType.Active -> "SELECT * FROM ROUND WHERE STATUS != '${Status.FINISHED}'"
                RoundsQueryType.All -> "SELECT * FROM ROUND"
                RoundsQueryType.WithWondersAwaitingApproval -> "SELECT * FROM ROUND WHERE STATUS IN (" +
                        "'${Status.AWAIT_FIRST_WONDER_APPROVAL}', " +
                        "'${Status.AWAIT_SECOND_WONDER_APPROVAL}', " +
                        "'${Status.AWAIT_THIRD_WONDER_APPROVAL}', " +
                        "'${Status.AWAIT_LAST_WONDER_APPROVAL}')"
            }
        ) { rs, _ ->
            DBRound(
                roundId = rs.getString(1),
                name = rs.getString(2),
                status = rs.getString(3),
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

    fun getActiveRounds(): List<Round> = getRounds(roundsQueryType = RoundsQueryType.Active)

    fun getAvailablePlayers(): List<User> {
        return jdbcTemplate.query("SELECT * FROM `User` WHERE PREFERRED_ROLE != '${Role.PRINCE_GUIDON}'") { rs, _ ->
            DBUser(
                userId = rs.getString(1),
                name = rs.getString(2),
                email = rs.getString(3),
                createdAt = rs.getString(4),
                login = rs.getString(5),
                password = rs.getString(6),
                preferredRole = rs.getString(7),
                isBanned = rs.getBoolean(8)
            )
        }.toCommonTypes()
    }

    fun getRoundsAwaitingWonder() = getRounds().filter { round ->
        StatusesThatRequireWonder.contains(round.status)
    }

    // TODO: optimize it
    fun makeWonder(roundId: String, wonderName: String): Round {
        val currentRound = getRounds().firstOrNull { round ->
            round.id == roundId
        }?.toDBType() ?: throw NotFoundException()
        return if (StatusesThatRequireWonder.contains(currentRound.status)) {
            val wonder = DBWonder(
                wonderId = UUID.randomUUID().toString(),
                name = wonderName,
                roundId = roundId,
                isVerified = false,
                isApproved = false,
                createdForStage = when (currentRound.status) {
                    Status.STARTED, Status.FIRST_WONDER_REJECTED -> 1
                    Status.FIRST_WONDER_APPROVED, Status.SECOND_WONDER_REJECTED -> 2
                    Status.SECOND_WONDER_APPROVED, Status.THIRD_WONDER_REJECTED -> 3
                    Status.THIRD_WONDER_APPROVED, Status.LAST_WONDER_REJECTED -> 4
                    else -> null
                }
            )
            wonderRepository.save(wonder)

            val wonderList = currentRound.wonders
            wonderList.add(wonder)
            val updatedRound = currentRound.copy(
                status = when (currentRound.status) {
                    Status.STARTED, Status.FIRST_WONDER_REJECTED ->
                        Status.AWAIT_FIRST_WONDER_APPROVAL

                    Status.FIRST_WONDER_APPROVED, Status.SECOND_WONDER_REJECTED ->
                        Status.AWAIT_SECOND_WONDER_APPROVAL

                    Status.SECOND_WONDER_APPROVED, Status.THIRD_WONDER_REJECTED ->
                        Status.AWAIT_THIRD_WONDER_APPROVAL

                    Status.THIRD_WONDER_APPROVED, Status.LAST_WONDER_REJECTED ->
                        Status.AWAIT_LAST_WONDER_APPROVAL

                    else -> currentRound.status
                },
                wonders = wonderList
            )
            jdbcTemplate.execute(
                "UPDATE ROUND SET STATUS = '${updatedRound.status}' WHERE ROUND_ID LIKE '${updatedRound.roundId}'"
            )
            updatedRound
        } else {
            currentRound
        }.toCommonType()
    }

    fun getWonders() = wonderRepository.findAll().toCommonTypes()

    fun getWondersAwaitingApproval(): List<Wonder> {
        return getRounds(roundsQueryType = RoundsQueryType.WithWondersAwaitingApproval).mapNotNull { round ->
            round.wonders.lastOrNull()
        }
    }

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
            status = when (currentRound.status) {
                Status.AWAIT_FIRST_WONDER_APPROVAL -> Status.FIRST_WONDER_APPROVED
                Status.AWAIT_SECOND_WONDER_APPROVAL -> Status.SECOND_WONDER_APPROVED
                Status.AWAIT_THIRD_WONDER_APPROVAL -> Status.THIRD_WONDER_APPROVED
                Status.AWAIT_LAST_WONDER_APPROVAL -> Status.LAST_WONDER_APPROVED
                else -> currentRound.status
            }
        )
        jdbcTemplate.execute(
            "UPDATE ROUND SET STATUS = '${updatedRound.status}' WHERE ROUND_ID LIKE '${updatedRound.roundId}'"
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
            status = when (currentRound.status) {
                Status.AWAIT_FIRST_WONDER_APPROVAL -> Status.FIRST_WONDER_REJECTED
                Status.AWAIT_SECOND_WONDER_APPROVAL -> Status.SECOND_WONDER_REJECTED
                Status.AWAIT_THIRD_WONDER_APPROVAL -> Status.THIRD_WONDER_REJECTED
                Status.AWAIT_LAST_WONDER_APPROVAL -> Status.LAST_WONDER_REJECTED
                else -> currentRound.status
            }
        )
        jdbcTemplate.execute(
            "UPDATE ROUND SET STATUS = '${updatedRound.status}' WHERE ROUND_ID LIKE '${updatedRound.roundId}'"
        )

        return updatedWonder.toCommonType()
    }

}