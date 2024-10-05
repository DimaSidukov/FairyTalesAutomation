package com.sixbrigade.fta.services

import com.sixbrigade.fta.data.source.UserDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LoginService(
    @Autowired private val dataSource: UserDataSource
) {

    fun logIn(login: String, password: String) = dataSource.logIn(login, password)

    fun signUp(login: String, password: String, name: String, email: String) =
        dataSource.signUp(login, password, name, email)

}