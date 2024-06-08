package com.sixbrigade.fta.model.mapping

import com.sixbrigade.fta.model.common.User
import com.sixbrigade.fta.model.db.DBUser

fun User.toDBType(login: String, password: String) = DBUser(
    id = id,
    name = name,
    email = email,
    createdAt = createdAt,
    login = login,
    password = password
)

fun DBUser.toCommonType() = User(
    id = id,
    name = name,
    email = email,
    createdAt = createdAt
)