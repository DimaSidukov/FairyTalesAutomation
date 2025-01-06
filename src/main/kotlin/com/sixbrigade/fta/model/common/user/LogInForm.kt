package com.sixbrigade.fta.model.common.user

import com.fasterxml.jackson.annotation.JsonProperty

data class LogInForm(
    @JsonProperty("login")
    val login: String,
    @JsonProperty("password")
    val password: String
)