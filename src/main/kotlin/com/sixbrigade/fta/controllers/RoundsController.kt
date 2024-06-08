package com.sixbrigade.fta.controllers

import com.sixbrigade.fta.model.common.round.Round
import com.sixbrigade.fta.services.RoundsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rounds")
class RoundsController(
    @Autowired private val roundsService: RoundsService
) {

    @PostMapping("/create")
    fun createRound(name: String) : Round = roundsService.createRound(name)

}