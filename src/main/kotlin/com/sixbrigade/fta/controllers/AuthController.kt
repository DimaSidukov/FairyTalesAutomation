package com.sixbrigade.fta.controllers

import com.sixbrigade.fta.services.LoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    @Autowired private val loginService: LoginService
) {

    @PostMapping("/login")
    fun logIn(login: String, password: String) = loginService.logIn(login, password)

}