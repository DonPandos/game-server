package com.example.testgameserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@SpringBootApplication
public class TestGameServerApplication {

        public static void main(String[] args) {
        SpringApplication.run(TestGameServerApplication.class, args);
    }
}
