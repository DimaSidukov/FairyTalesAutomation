package com.sixbrigade.fta.model.mapping

import com.sixbrigade.fta.model.common.round.Player
import com.sixbrigade.fta.model.common.round.Round
import com.sixbrigade.fta.model.common.round.Wonder
import com.sixbrigade.fta.model.db.round.DBPlayer
import com.sixbrigade.fta.model.db.round.DBRound
import com.sixbrigade.fta.model.db.round.DBWonder

fun Round.toDBType() = DBRound(
    roundId = id,
    name = name,
    players = players.toDBTypes().toHashSet(),
    wonders = wonders.toDBTypes().toMutableList(),
    status = status
)

@JvmName("PlayersToDB")
fun Iterable<Player>.toDBTypes() = map(Player::toDBType)

fun Player.toDBType() = DBPlayer(
    userId = userId,
    roundId = roundId,
    role = role
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

@JvmName("DBRoundsToCommon")
fun Iterable<DBRound>.toCommonTypes() = map(DBRound::toCommonType)

fun DBRound.toCommonType() = Round(
    id = roundId,
    name = name,
    players = players.toCommonTypes(),
    wonders = wonders.toCommonTypes(),
    status = status
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
    role = role
)