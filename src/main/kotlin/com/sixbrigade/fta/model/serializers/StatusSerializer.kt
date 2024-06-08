package com.sixbrigade.fta.model.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.sixbrigade.fta.model.common.round.Status

class StatusSerializer : StdSerializer<Status>(Status::class.java) {

    override fun serialize(value: Status, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeNumber(
            when (value) {
                Status.NotStarted -> 0
                Status.Started -> 1
                Status.AwaitFirstWonderApproval -> 2
                Status.FirstWonderApproved -> 3
                Status.FirstWonderRejected -> 4
                Status.AwaitSecondWonderApproval -> 5
                Status.SecondWonderApproved -> 6
                Status.SecondWonderRejected -> 7
                Status.AwaitThirdWonderApproval -> 8
                Status.ThirdWonderApproved -> 9
                Status.ThirdWonderRejected -> 10
                Status.AwaitLastWonderApproval -> 11
                Status.LastWonderApproved -> 12
                Status.LastWonderRejected -> 13
                Status.Finished -> 14
            }
        )
    }
}


class StatusDeserializer : StdDeserializer<Status>(Status::class.java) {

    override fun deserialize(jsonParser: JsonParser, context: DeserializationContext): Status {
        return when (jsonParser.numberValue) {
            1 -> Status.Started
            2 -> Status.AwaitFirstWonderApproval
            3 -> Status.FirstWonderApproved
            4 -> Status.FirstWonderRejected
            5 -> Status.AwaitSecondWonderApproval
            6 -> Status.SecondWonderApproved
            7 -> Status.SecondWonderRejected
            8 -> Status.AwaitThirdWonderApproval
            9 -> Status.ThirdWonderApproved
            10 -> Status.ThirdWonderRejected
            11 -> Status.AwaitLastWonderApproval
            12 -> Status.LastWonderApproved
            13 -> Status.LastWonderRejected
            14 -> Status.Finished
            else -> Status.NotStarted
        }
    }

}