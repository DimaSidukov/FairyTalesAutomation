package com.sixbrigade.fta.controllers

import com.sixbrigade.fta.services.LoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["http://localhost:8080"], maxAge = 3600)
@RestController
@RequestMapping("/auth")
class AuthController(
    @Autowired private val loginService: LoginService
) {

    @PostMapping("/login")
    fun logIn(login: String, password: String) = loginService.logIn(login, password)

    @PostMapping("/signup")
    fun signUp(login: String, password: String, name: String, email: String) =
        loginService.signUp(login, password, name, email)

}