package com.sixbrigade.fta.controllers

import com.sixbrigade.fta.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["http://localhost:8080"], maxAge = 3600)
@RestController
@RequestMapping("/user")
class UserController(
    @Autowired private val userService: UserService
) {

    @PostMapping("/login")
    fun logIn(login: String, password: String) = userService.logIn(login, password)

    @PostMapping("/signup")
    fun signUp(login: String, password: String, name: String, email: String) =
        userService.signUp(login, password, name, email)

    @PostMapping("{userId}/set_role")
    fun setRole(@PathVariable userId: String, @RequestParam("preferred_role") preferredRole: String) =
        userService.setRole(userId, preferredRole)

    @GetMapping("/all")
    fun getAllUsers() = userService.getAllUsers()

    @PostMapping("{userId}/delete")
    fun delete(@PathVariable userId: String) = userService.delete(userId)

}