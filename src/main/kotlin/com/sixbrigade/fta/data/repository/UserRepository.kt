package com.sixbrigade.fta.data.repository

import com.sixbrigade.fta.model.db.DBUser
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<DBUser, Int>