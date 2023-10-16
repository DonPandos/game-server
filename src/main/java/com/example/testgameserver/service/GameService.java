package com.example.testgameserver.service;

import com.example.testgameserver.dto.GetRoomResponseDTO;
import com.example.testgameserver.dto.StartGameRequestDTO;

public interface GameService {
    GetRoomResponseDTO getRoomList();

    Boolean startGame(StartGameRequestDTO request);
}
