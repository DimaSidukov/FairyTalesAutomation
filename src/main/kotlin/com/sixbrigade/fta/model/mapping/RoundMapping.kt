package com.sixbrigade.fta.model.mapping

import com.sixbrigade.fta.model.common.round.Player
import com.sixbrigade.fta.model.common.round.Role
import com.sixbrigade.fta.model.common.round.Round
import com.sixbrigade.fta.model.common.round.Status
import com.sixbrigade.fta.model.db.round.DBPlayer
import com.sixbrigade.fta.model.db.round.DBRole
import com.sixbrigade.fta.model.db.round.DBRound
import com.sixbrigade.fta.model.db.round.DBStatus

fun Round.toDBType() = DBRound(
    roundId = id,
    name = name,
    participants = participants.toDBTypes(),
    status = status.toDBType().code
)

fun List<Player>.toDBTypes() = map(Player::toDBType)

fun Player.toDBType() = DBPlayer(
    userId = userId,
    roundId = roundId,
    role = role.toDBType()
)

fun Role.toDBType() = when(this) {
    Role.Auditor -> DBRole.AUDITOR
    Role.King -> DBRole.KING
    Role.PrincessSwan -> DBRole.PRINCESS_SWAN
}

fun Status.toDBType() = when(this) {
    Status.AwaitFirstWonderApproval -> DBStatus.AWAIT_FIRST_WONDER_APPROVAL
    Status.AwaitLastWonderApproval -> DBStatus.AWAIT_LAST_WONDER_APPROVAL
    Status.AwaitSecondWonderApproval -> DBStatus.AWAIT_SECOND_WONDER_APPROVAL
    Status.AwaitThirdWonderApproval -> DBStatus.AWAIT_THIRD_WONDER_APPROVAL
    Status.Finished -> DBStatus.FINISHED
    Status.FirstWonderApproved -> DBStatus.FIRST_WONDER_APPROVED
    Status.FirstWonderRejected -> DBStatus.FIRST_WONDER_REJECTED
    Status.LastWonderApproved -> DBStatus.LAST_WONDER_APPROVED
    Status.LastWonderRejected -> DBStatus.LAST_WONDER_REJECTED
    Status.NotStarted -> DBStatus.NOT_STARTED
    Status.SecondWonderApproved -> DBStatus.SECOND_WONDER_APPROVED
    Status.SecondWonderRejected -> DBStatus.SECOND_WONDER_REJECTED
    Status.Started -> DBStatus.STARTED
    Status.ThirdWonderApproved -> DBStatus.THIRD_WONDER_APPROVED
    Status.ThirdWonderRejected -> DBStatus.THIRD_WONDER_REJECTED
}

fun DBRound.toCommonType() = Round(
    id = roundId,
    name = name,
    participants = participants.toCommonTypes(),
    status = DBStatus.entries[status].toCommonType()
)

fun List<DBPlayer>.toCommonTypes() = map(DBPlayer::toCommonType)

fun DBPlayer.toCommonType() = Player(
    userId = userId,
    roundId = roundId,
    role = role.toCommonType()
)

fun DBRole.toCommonType() = when(this) {
    DBRole.KING -> Role.King
    DBRole.AUDITOR -> Role.Auditor
    DBRole.PRINCESS_SWAN -> Role.PrincessSwan
}

fun DBStatus.toCommonType() = when(this) {
    DBStatus.NOT_STARTED -> Status.NotStarted
    DBStatus.STARTED -> Status.Started
    DBStatus.AWAIT_FIRST_WONDER_APPROVAL -> Status.AwaitFirstWonderApproval
    DBStatus.FIRST_WONDER_APPROVED -> Status.FirstWonderApproved
    DBStatus.FIRST_WONDER_REJECTED -> Status.FirstWonderRejected
    DBStatus.AWAIT_SECOND_WONDER_APPROVAL -> Status.AwaitSecondWonderApproval
    DBStatus.SECOND_WONDER_APPROVED -> Status.SecondWonderApproved
    DBStatus.SECOND_WONDER_REJECTED -> Status.SecondWonderRejected
    DBStatus.AWAIT_THIRD_WONDER_APPROVAL -> Status.AwaitThirdWonderApproval
    DBStatus.THIRD_WONDER_APPROVED -> Status.ThirdWonderApproved
    DBStatus.THIRD_WONDER_REJECTED -> Status.ThirdWonderRejected
    DBStatus.AWAIT_LAST_WONDER_APPROVAL -> Status.AwaitLastWonderApproval
    DBStatus.LAST_WONDER_APPROVED -> Status.LastWonderApproved
    DBStatus.LAST_WONDER_REJECTED -> Status.LastWonderRejected
    DBStatus.FINISHED -> Status.Finished
}