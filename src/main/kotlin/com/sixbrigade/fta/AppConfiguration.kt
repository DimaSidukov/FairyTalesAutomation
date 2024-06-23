package com.sixbrigade.fta

import org.apache.catalina.connector.Connector
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class AppConfiguration {
    @Bean
    fun cookieProcessorCustomizer(): WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
        return WebServerFactoryCustomizer { factory: TomcatServletWebServerFactory ->
            // also listen on http
            val connector = Connector()
            connector.port = httpPort
            factory.addAdditionalTomcatConnectors(connector)
        }
    }

    @Value("\${server.http.port}")
    private val httpPort = 0
}
