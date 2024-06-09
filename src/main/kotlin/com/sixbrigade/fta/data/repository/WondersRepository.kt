package com.sixbrigade.fta.data.repository

import com.sixbrigade.fta.model.db.round.DBWonder
import org.springframework.data.repository.CrudRepository

interface WondersRepository : CrudRepository<DBWonder, String>