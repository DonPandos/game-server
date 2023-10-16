package com.example.testgameserver.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StartGameRequestDTO {
    private String roomMac;
    private int timeout;
    private int result;
    private int graspPower;
    private int topPower;
    private int movePower;
    private int maxPower;
    private int topHeight;
    private int lineLength;
    private int xSpeed;
    private int ySpeed;
    private int zSpeed;
}
