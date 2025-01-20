package com.sixbrigade.fta.controllers

import com.sixbrigade.fta.model.common.user.LogInForm
import com.sixbrigade.fta.model.common.user.SignUpForm
import com.sixbrigade.fta.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["http://localhost:8080"], maxAge = 3600)
@RestController
@RequestMapping("/users")
class UserController(
    @Autowired private val userService: UserService
) {

    @PostMapping("/login")
    fun logIn(@RequestBody logInForm: LogInForm) = userService.logIn(logInForm.login, logInForm.password)

    @PostMapping("/signup")
    fun signUp(@RequestBody signUpForm: SignUpForm) = signUpForm.run {
        userService.signUp(login, password, login, email)
    }

    @PostMapping("{userId}/set_role")
    fun setRole(@PathVariable userId: String, @RequestParam("preferred_role") preferredRole: String) =
        userService.setRole(userId, preferredRole)

    @GetMapping("/all")
    fun getAllUsers() = userService.getAllUsers()

    @GetMapping("/{userId}")
    fun getUserInfo(@PathVariable userId: String) = userService.getUser(userId)

    @PostMapping("{userId}/delete")
    fun delete(@PathVariable userId: String) = userService.delete(userId)

    @PostMapping("{userId}/block")
    fun blockUser(@PathVariable userId: String) = userService.blockUser(userId)

    @PostMapping("{userId}/unblock")
    fun unblockUser(@PathVariable userId: String) = userService.unblockUser(userId)

}