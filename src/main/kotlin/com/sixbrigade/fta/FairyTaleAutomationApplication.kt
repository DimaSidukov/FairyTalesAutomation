package com.sixbrigade.fta

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FairyTaleAutomationApplication

// db console - http://localhost:8080/h2-console/
// username - sa
// password -
fun main(args: Array<String>) {
    runApplication<FairyTaleAutomationApplication>(*args)
}
