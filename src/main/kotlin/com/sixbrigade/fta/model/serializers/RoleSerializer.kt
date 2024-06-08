package com.sixbrigade.fta.model.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.sixbrigade.fta.model.common.round.Role

class RoleSerializer : StdSerializer<Role>(Role::class.java) {

    override fun serialize(value: Role, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeNumber(
            when (value) {
                Role.King -> 0
                Role.Auditor -> 1
                Role.PrincessSwan -> 2
            }
        )
    }
}

class RoleDeserializer : StdDeserializer<Role>(Role::class.java) {

    override fun deserialize(jsonParser: JsonParser, context: DeserializationContext): Role {
        return when (jsonParser.numberValue) {
            0 -> Role.King
            1 -> Role.Auditor
            else -> Role.PrincessSwan
        }
    }
}