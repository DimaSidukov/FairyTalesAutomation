package com.sixbrigade.fta.data.source

import com.sixbrigade.fta.data.repository.UserRepository
import com.sixbrigade.fta.model.db.DBUser
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

    /*
    commands to test DB:

    INSERT INTO `User` (version, name, email, createdAt, login, password)
    VALUES (1, 'John Doe', 'john.doe@example.com', '2024-06-05 12:00:00', 'admin', 'admin');

    SELECT * FROM `User` WHERE login LIKE 'admin' AND password LIKE 'admin' LIMIT 1;
     */
    fun logIn(login: String, password: String): ResponseEntity<Any> = try {
        val query = QUERY_LOG_IN_PASSWORD.format(login, password)
        val result = jdbcTemplate.query(query) { rs, _ ->
            DBUser(
                userId = rs.getString(1),
                name = rs.getString(2),
                email = rs.getString(3),
                createdAt = rs.getString(4).toString(),
                login = rs.getString(5),
                password = rs.getString(6)
            )
        }
        if (result.isEmpty()) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                mapOf(
                    "code" to HttpStatus.NOT_FOUND.value(),
                    "message" to "There is no user with given credentials."
                )
            )
        }
        ResponseEntity.ok(result.first())
    } catch (e: Exception) {
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            mapOf(
                "code" to HttpStatus.NOT_FOUND.value(),
                "message" to "There is no user with given credentials."
            )
        )
    }

    fun signUp(login: String, password: String, name: String, email: String): ResponseEntity<Any> = try {
        val query = QUERY_LOG_IN.format(login)
        val result = jdbcTemplate.query(query) { rs, _ ->
            DBUser(
                userId = rs.getString(1),
                name = rs.getString(2),
                email = rs.getString(3),
                createdAt = rs.getString(4).toString(),
                login = rs.getString(5),
                password = rs.getString(6)
            )
        }
        if (result.size == 0) {
            val user = saveNewUser(login, password, name, email)
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf(
                    "code" to HttpStatus.BAD_REQUEST.value(),
                    "message" to "User with given login already exists!"
                )
            )
        }
    } catch (e: Exception) {
        val user = saveNewUser(login, password, name, email)
        ResponseEntity.ok(user)
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
            password = password
        )
        repository.save(user)
        return user
    }

}