package com.nishant.payment_router.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class H2TestConfig {

    // This manually starts the H2 Web Console on port 8082
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        System.out.println("H2 Console starting manually on port 8082...");
        return Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
    }
}