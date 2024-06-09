package com.sixbrigade.fta.model.mapping

import com.sixbrigade.fta.model.common.round.*
import com.sixbrigade.fta.model.db.round.DBPlayer
import com.sixbrigade.fta.model.db.round.DBRound
import com.sixbrigade.fta.model.db.round.DBWonder

fun Round.toDBType() = DBRound(
    roundId = id,
    name = name,
    players = players.toDBTypes().toHashSet(),
    wonders = wonders.toDBTypes().toMutableList(),
    status = status.toDBType()
)

@JvmName("PlayersToDB")
fun Iterable<Player>.toDBTypes() = map(Player::toDBType)

fun Player.toDBType() = DBPlayer(
    userId = userId,
    roundId = roundId,
    role = role.toDBType()
)

@JvmName("WondersToDB")
fun Iterable<Wonder>.toDBTypes(): List<DBWonder> = map(Wonder::toDBType)

fun Wonder.toDBType() = DBWonder(
    wonderId = id,
    name = name,
    roundId = roundId,
    isVerified = isVerified,
    isApproved = isApproved,
    createdForStage = createdForStage
)

fun Role.toDBType() = when (this) {
    Role.Auditor -> 0
    Role.King -> 1
    Role.PrincessSwan -> 2
}

fun Status.toDBType() = when (this) {
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

@JvmName("DBRoundsToCommon")
fun Iterable<DBRound>.toCommonTypes() = map(DBRound::toCommonType)

fun DBRound.toCommonType() = Round(
    id = roundId,
    name = name,
    players = players.toCommonTypes(),
    wonders = wonders.toCommonTypes(),
    status = status.toRoundStatus()
)

@JvmName("DBWondersToCommon")
fun Iterable<DBWonder>.toCommonTypes() = map(DBWonder::toCommonType)

fun DBWonder.toCommonType() = Wonder(
    id = wonderId,
    name = name,
    roundId = roundId,
    isVerified = isVerified,
    isApproved = isApproved,
    createdForStage = createdForStage
)

fun HashSet<DBPlayer>.toCommonTypes() = map(DBPlayer::toCommonType)

fun DBPlayer.toCommonType() = Player(
    userId = userId,
    roundId = roundId,
    role = role.toRoundRole()
)

fun Int.toRoundRole() = when (this) {
    0 -> Role.King
    1 -> Role.Auditor
    else -> Role.PrincessSwan
}

fun Int.toRoundStatus() = when (this) {
    0 -> Status.NotStarted
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
    else -> Status.Finished
}