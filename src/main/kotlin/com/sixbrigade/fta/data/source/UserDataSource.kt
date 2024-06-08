package com.sixbrigade.fta.data.source

import com.sixbrigade.fta.data.repository.PlayerRepository
import com.sixbrigade.fta.data.repository.UserRepository
import com.sixbrigade.fta.model.db.DBUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class UserDataSource(
    @Autowired private val repository: UserRepository,
    @Autowired private val jdbcTemplate: JdbcTemplate
) {

    private companion object {
        const val QUERY_LOG_IN = "SELECT * FROM `User` WHERE login LIKE '%s' AND password LIKE '%s' LIMIT 1"
    }

    // TODO: temporary returned type. replace with proper error handling in future
    /*

    commands to test DB:

    INSERT INTO `User` (version, name, email, createdAt, login, password)
    VALUES (1, 'John Doe', 'john.doe@example.com', '2024-06-05 12:00:00', 'admin', 'admin');

    SELECT * FROM `User` WHERE login LIKE 'admin' AND password LIKE 'admin' LIMIT 1;
     */
    fun logIn(login: String, password: String): Boolean = try {
        val query = QUERY_LOG_IN.format(login, password)
        val result = jdbcTemplate.query(query) { rs, rowNum ->
            DBUser(
                id = rs.getInt(1),
                name = rs.getString(2),
                email = rs.getString(3),
                createdAt = rs.getString(4).toString(),
                login = rs.getString(5),
                password = rs.getString(6)
            )
        }
        result.size > 0
    } catch (e: Exception) {
        false
    }

}