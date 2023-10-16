package com.example.testgameserver.controller;

import com.example.testgameserver.dto.GetRoomResponseDTO;
import com.example.testgameserver.dto.StartGameRequestDTO;
import com.example.testgameserver.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

    // endpoint which returns available rooms
    @GetMapping("/rooms")
    public GetRoomResponseDTO getRoomList() {
        log.info("{} -> Request 'getRoomList'");
        return gameService.getRoomList();
    }

    // endpoint which starts a new game
    @PostMapping("/start")
    public Boolean startGame(@RequestBody StartGameRequestDTO request) {
        log.info("{} -> Request 'getRoomList': " + request.toString());
        return gameService.startGame(request);
    }
}
