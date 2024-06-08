package com.sixbrigade.fta

import com.sixbrigade.fta.data.repository.UserRepository
import com.sixbrigade.fta.model.db.DBUser
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class FairyTaleAutomationApplication

fun main(args: Array<String>) {
	runApplication<FairyTaleAutomationApplication>(*args)
}
