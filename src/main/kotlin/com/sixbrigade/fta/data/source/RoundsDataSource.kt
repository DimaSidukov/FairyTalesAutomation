package com.sixbrigade.fta.data.source

import com.sixbrigade.fta.data.repository.PlayerRepository
import com.sixbrigade.fta.data.repository.RoundRepository
import com.sixbrigade.fta.model.common.round.Round
import com.sixbrigade.fta.model.db.round.DBRound
import com.sixbrigade.fta.model.db.round.DBStatus
import com.sixbrigade.fta.model.mapping.toCommonType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class RoundsDataSource(
    @Autowired private val jdbcTemplate: JdbcTemplate,
    @Autowired private val roundRepository: RoundRepository,
    @Autowired private val playerRepository: PlayerRepository
) {

    fun createRound(name: String): Round {
        val round = DBRound(
            roundId = UUID.randomUUID().toString(),
            name = name,
            status = DBStatus.NOT_STARTED.code,
            participants = listOf()
        )

        roundRepository.save(round)

        return round.toCommonType()
    }

}