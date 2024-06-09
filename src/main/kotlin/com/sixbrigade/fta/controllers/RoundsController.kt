package com.sixbrigade.fta.controllers

import com.sixbrigade.fta.model.common.round.Player
import com.sixbrigade.fta.model.common.round.Round
import com.sixbrigade.fta.services.RoundsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rounds")
class RoundsController(
    @Autowired private val roundsService: RoundsService
) {

    @PostMapping("/create")
    fun createRound(name: String): Round = roundsService.createRound(name)

    @PostMapping("/{id}/select_users")
    fun selectUsers(@PathVariable roundId: String, @RequestBody players: List<Player>) =
        roundsService.selectUsers(roundId, players)

    @GetMapping("/{id}")
    fun getRound(@PathVariable roundId: String) = roundsService.getRound(roundId)

    @GetMapping()
    fun getRounds() = roundsService.getRounds()

    @GetMapping("/need_wonder")
    fun getRoundsAwaitingWonder() = roundsService.getRoundsAwaitingWonder()

    @PostMapping("/{id}/make_wonder")
    fun makeWonder(@PathVariable roundId: String, wonderName: String) =
        roundsService.makeWonder(roundId, wonderName)

    @PostMapping("/verify_wonder")
    fun verifyWonder(wonderId: String) = roundsService.verifyWonder(wonderId)

    @PostMapping("/approve_wonder")
    fun approveWonder(wonderId: String) = roundsService.approveWonder(wonderId)

}