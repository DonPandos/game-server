package com.example.testgameserver.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetRoomResponseDTO {
    private List<String> rooms;
}
