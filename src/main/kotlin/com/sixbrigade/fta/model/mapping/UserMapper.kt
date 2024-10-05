package com.sixbrigade.fta.model.mapping

import com.sixbrigade.fta.model.common.User
import com.sixbrigade.fta.model.db.DBUser

fun User.toDBType(login: String, password: String) = DBUser(
    userId = id,
    name = name,
    email = email,
    createdAt = createdAt,
    login = login,
    password = password,
    preferredRole = preferredRole
)

fun Iterable<DBUser>.toCommonTypes() = map(DBUser::toCommonType)

fun DBUser.toCommonType() = User(
    id = userId,
    name = name,
    email = email,
    createdAt = createdAt,
    preferredRole = preferredRole
)