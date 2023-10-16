package com.example.testgameserver.service.impl;

import com.example.testgameserver.dto.GetRoomResponseDTO;
import com.example.testgameserver.dto.StartGameRequestDTO;
import com.example.testgameserver.server.WawaServer;
import com.example.testgameserver.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final WawaServer wawaServer;

    private int g_packget_id = 1;

    @Override
    public GetRoomResponseDTO getRoomList() {
        // create response and set data
        GetRoomResponseDTO response = new GetRoomResponseDTO();
        response.setRooms(wawaServer.getRoomList());
        return response;
    }

    @Override
    public Boolean startGame(StartGameRequestDTO request) {
        // there will be additional field which will recognize user which is playing, but in scope of test we don't have user identifier
        // start new game
        wawaServer.processPlayerStartNewGame(request.getRoomMac());
        // set game settings
        return wawaServer.tranlsateToWawaji(request.getRoomMac(), make_com(49, request.getTimeout(), request.getResult(), request.getGraspPower(),
                request.getTopPower(), request.getMovePower(), request.getMaxPower(), request.getTopHeight(), request.getLineLength(),
                request.getXSpeed(), request.getYSpeed(), request.getYSpeed()));
    }

    // convert game params to array of bytes
    private byte[] make_com(int... params) {
        byte[] send_buf = new byte[8 + params.length];
        send_buf[0] = -2;
        send_buf[1] = (byte) this.g_packget_id;
        send_buf[2] = (byte) (this.g_packget_id >> 8);
        send_buf[3] = (byte) (~send_buf[0]);
        send_buf[4] = (byte) (~send_buf[1]);
        send_buf[5] = (byte) (~send_buf[2]);
        send_buf[6] = (byte) (8 + params.length);

        int sum;
        for (sum = 0; sum < params.length; ++sum) {
            send_buf[7 + sum] = (byte) params[sum];
        }

        sum = 0;

        for (int i = 6; i < 8 + params.length - 1; ++i) {
            sum += send_buf[i] & 255;
        }

        send_buf[8 + params.length - 1] = (byte) (sum % 100);
        ++this.g_packget_id;
        return send_buf;
    }
}
