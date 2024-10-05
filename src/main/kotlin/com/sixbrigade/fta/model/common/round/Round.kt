package com.sixbrigade.fta.model.common.round

import com.fasterxml.jackson.annotation.JsonProperty


data class Round(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("participants")
    val players: List<Player>,
    @JsonProperty("status")
    val status: String,
    @JsonProperty("wonders")
    val wonders: List<Wonder>
)