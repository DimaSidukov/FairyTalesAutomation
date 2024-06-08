package com.sixbrigade.fta.model.common.round

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.sixbrigade.fta.model.serializers.StatusDeserializer
import com.sixbrigade.fta.model.serializers.StatusSerializer

@JsonSerialize(using = StatusSerializer::class)
@JsonDeserialize(using = StatusDeserializer::class)
sealed interface Status {

    data object NotStarted: Status

    data object Started: Status

    data object AwaitFirstWonderApproval: Status

    data object FirstWonderApproved: Status

    data object FirstWonderRejected: Status

    data object AwaitSecondWonderApproval: Status

    data object SecondWonderApproved: Status

    data object SecondWonderRejected: Status

    data object AwaitThirdWonderApproval: Status

    data object ThirdWonderApproved: Status

    data object ThirdWonderRejected: Status

    data object AwaitLastWonderApproval: Status

    data object LastWonderApproved: Status

    data object LastWonderRejected: Status

    data object Finished: Status

}