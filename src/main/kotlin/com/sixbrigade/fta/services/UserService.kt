package com.sixbrigade.fta.services

import com.sixbrigade.fta.data.source.UserDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired private val dataSource: UserDataSource
) {

    fun logIn(login: String, password: String) = dataSource.logIn(login, password)

    fun signUp(login: String, password: String, name: String, email: String) =
        dataSource.signUp(login, password, name, email)

    fun setRole(userId: String, preferredRole: String) = dataSource.setRole(userId, preferredRole)

    fun getAllUsers() = dataSource.getAllUsers()

    fun getUser(userId: String) = dataSource.getUser(userId)

    fun delete(userId: String) = dataSource.delete(userId)

    fun blockUser(userId: String) = dataSource.blockUser(userId)

    fun unblockUser(userId: String) = dataSource.unblockUser(userId)

}