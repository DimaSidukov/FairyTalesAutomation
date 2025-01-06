package com.sixbrigade.fta.model.common.user

import com.fasterxml.jackson.annotation.JsonProperty

data class SignUpForm(
    @JsonProperty("login")
    val login: String,
    @JsonProperty("email")
    val email: String,
    @JsonProperty("password")
    val password: String,
    @JsonProperty("isBanned")
    val isBanned: Boolean
)