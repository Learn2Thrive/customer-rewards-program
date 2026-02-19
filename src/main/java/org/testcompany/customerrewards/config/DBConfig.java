package org.testcompany.customerrewards.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class DBConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server createH2DBServer() throws SQLException {
        return Server.createTcpServer(
                "-tcp", "-tcpAllowOthers", "-tcpPort", "8043");
    }
}
