package com.sixbrigade.fta.data.repository

import com.sixbrigade.fta.model.db.round.DBRound
import org.springframework.data.repository.CrudRepository

interface RoundRepository : CrudRepository<DBRound, String>