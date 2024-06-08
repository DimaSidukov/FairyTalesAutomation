package com.sixbrigade.fta.services

import com.sixbrigade.fta.data.source.RoundsDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RoundsService(
    @Autowired private val dataSource: RoundsDataSource
) {

    fun createRound(name: String) = dataSource.createRound(name)

}