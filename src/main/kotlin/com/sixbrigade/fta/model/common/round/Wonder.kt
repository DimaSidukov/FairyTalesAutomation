package com.sixbrigade.fta.model.common.round

import com.fasterxml.jackson.annotation.JsonProperty

data class Wonder(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("round_id")
    val roundId: String,
    @JsonProperty("is_verified")
    val isVerified: Boolean,
    @JsonProperty("is_approved")
    val isApproved: Boolean,
    @JsonProperty("created_for_stage")
    val createdForStage: Int?
)
