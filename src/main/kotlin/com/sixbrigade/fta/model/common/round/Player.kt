package com.sixbrigade.fta.model.common.round

import com.fasterxml.jackson.annotation.JsonProperty

data class Player(
    @JsonProperty("user_id")
    val userId: String,
    @JsonProperty("round_id")
    val roundId: String,
    @JsonProperty("role")
    val role: String
)