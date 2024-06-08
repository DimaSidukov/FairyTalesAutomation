package com.sixbrigade.fta.data.repository

import com.sixbrigade.fta.model.db.round.DBPlayer
import org.springframework.data.repository.CrudRepository

interface PlayerRepository: CrudRepository<DBPlayer, Int>