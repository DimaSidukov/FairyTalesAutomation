package com.sixbrigade.fta.data.source

import com.sixbrigade.fta.data.repository.UserRepository
import com.sixbrigade.fta.model.common.User
import com.sixbrigade.fta.model.common.round.Status
import com.sixbrigade.fta.model.db.DBUser
import com.sixbrigade.fta.model.db.round.DBPlayer
import com.sixbrigade.fta.model.mapping.toCommonType
import com.sixbrigade.fta.model.mapping.toCommonTypes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Repository
class UserDataSource(
    @Autowired private val repository: UserRepository,
    @Autowired private val jdbcTemplate: JdbcTemplate
) {

    private companion object {
        const val QUERY_LOG_IN_PASSWORD = "SELECT * FROM `User` WHERE login LIKE '%s' AND password LIKE '%s' LIMIT 1"
        const val QUERY_LOG_IN = "SELECT * FROM `User` WHERE login LIKE '%s' LIMIT 1"
    }

    fun logIn(login: String, password: String): ResponseEntity<Any> {
        val query = QUERY_LOG_IN_PASSWORD.format(login, password)
        val result = jdbcTemplate.query(query) { rs, _ ->
            DBUser(
                userId = rs.getString(1),
                name = rs.getString(2),
                email = rs.getString(3),
                createdAt = rs.getString(4).toString(),
                login = rs.getString(5),
                password = rs.getString(6),
                preferredRole = rs.getString(7),
                isBanned = rs.getBoolean(8)
            )
        }
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                mapOf(
                    "code" to HttpStatus.NOT_FOUND.value(),
                    "message" to "There is no user with given credentials."
                )
            )
        }
        return ResponseEntity.ok(result.first().toCommonType())
    }

    fun signUp(login: String, password: String, name: String, email: String): ResponseEntity<Any> {
        val query = QUERY_LOG_IN.format(login)
        val result = jdbcTemplate.query(query) { rs, _ ->
            DBUser(
                userId = rs.getString(1),
                name = rs.getString(2),
                email = rs.getString(3),
                createdAt = rs.getString(4).toString(),
                login = rs.getString(5),
                password = rs.getString(6),
                preferredRole = rs.getString(7),
                isBanned = rs.getBoolean(8)
            )
        }
        if (result.size == 0) {
            val user = saveNewUser(login, password, name, email)
            return ResponseEntity.ok(user.toCommonType())
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf(
                    "code" to HttpStatus.BAD_REQUEST.value(),
                    "message" to "User with given login already exists!"
                )
            )
        }
    }

    fun setRole(userId: String, preferredRole: String): ResponseEntity<Any> {
        val isUserInUnfinishedRounds = isUserInUnfinishedRounds(userId)
        if (isUserInUnfinishedRounds) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                mapOf(
                    "code" to HttpStatus.METHOD_NOT_ALLOWED.value(),
                    "message" to "Not possible to change role at the moment. Wait for all of your pending games to finish"
                )
            )
        }

        jdbcTemplate.execute(
            "UPDATE `User` SET PREFERRED_ROLE = '$preferredRole' WHERE USER_ID LIKE '$userId'"
        )
        val users = jdbcTemplate.query("SELECT * FROM `User` WHERE USER_ID LIKE '$userId'") { rs, _ ->
            DBUser(
                userId = rs.getString(1),
                name = rs.getString(2),
                email = rs.getString(3),
                createdAt = rs.getString(4).toString(),
                login = rs.getString(5),
                password = rs.getString(6),
                preferredRole = rs.getString(7),
                isBanned = rs.getBoolean(8)
            )
        }
        return ResponseEntity.ok(users.first().toCommonType())
    }

    fun getAllUsers() = repository.findAll().toList().toCommonTypes()

    fun delete(userId: String) = repository.deleteById(userId)

    fun blockUser(userId: String): User {
        jdbcTemplate.execute("UPDATE `User` SET IS_BANNED = TRUE WHERE USER_ID LIKE '$userId'")
        return repository.findById(userId).get().toCommonType()
    }

    fun unblockUser(userId: String): User {
        jdbcTemplate.execute("UPDATE `User` SET IS_BANNED = FALSE WHERE USER_ID LIKE '$userId'")
        return repository.findById(userId).get().toCommonType()
    }

    private fun saveNewUser(login: String, password: String, name: String, email: String): DBUser {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedNow = now.format(formatter)
        val user = DBUser(
            userId = UUID.randomUUID().toString(),
            name = name,
            email = email,
            createdAt = formattedNow,
            login = login,
            password = password,
            preferredRole = null,
            isBanned = false
        )
        repository.save(user)
        return user
    }

    private fun isUserInUnfinishedRounds(userId: String) : Boolean {
        val roundIds = jdbcTemplate.query(
            "SELECT * FROM PLAYER WHERE user_id LIKE '$userId'"
        ) { rs, _ ->
            DBPlayer(
                userId = rs.getString(1),
                role = rs.getString(2),
                roundId = rs.getString(3)
            )
        }.map(DBPlayer::roundId)
        println("ROUND IDS: $roundIds")
        if (roundIds.isEmpty()) {
            return false
        }
        val rounds = jdbcTemplate.query(
            "SELECT STATUS FROM ROUND WHERE ROUND_ID IN (${roundIds.joinToString(prefix="'", postfix="'", separator="' ,'")})"
        ) { rs, _ ->
            rs.getString(1)
        }
        println("ROUNDS FOUND STATUSES: $rounds")
        return rounds.any { status -> status != Status.FINISHED }
    }

}

//{
//    "id": "475ef5d9-1eba-4153-88bd-ccdfe0774f09",
//    "name": "SarahConnor",
//    "email": "sarah@resistance.com\n",
//    "createdAt": "2024-10-05 16:22:38",
//    "preferredRole": "king"
//}
//
//d02d62e9-19f6-4fb4-964b-93d96ff05a47