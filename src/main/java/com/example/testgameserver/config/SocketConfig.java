package com.example.testgameserver.config;

import com.example.testgameserver.server.WawaServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketConfig {

    @Value("${wawa-server.port}")
    private int wawaServerPort;

    @Bean
    public WawaServer wawaServer() {
        WawaServer wawaServer = new WawaServer();
        wawaServer.Start(wawaServerPort);

        return wawaServer;
    }
}
