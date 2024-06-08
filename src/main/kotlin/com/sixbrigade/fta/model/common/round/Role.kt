package com.sixbrigade.fta.model.common.round

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.sixbrigade.fta.model.serializers.RoleDeserializer
import com.sixbrigade.fta.model.serializers.RoleSerializer

@JsonSerialize(using = RoleSerializer::class)
@JsonDeserialize(using = RoleDeserializer::class)
sealed interface Role {

    // Tzar Saltan
    data object King : Role

    // Grumpy Lady
    data object Auditor : Role

    // Czarevna Swan
    data object PrincessSwan : Role
}