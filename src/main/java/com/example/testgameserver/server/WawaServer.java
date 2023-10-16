package com.example.testgameserver.server;

import com.example.testgameserver.exception.MachineNotFoundException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.*;

@Component
@Slf4j
public class WawaServer {
    Map<String, MachineInfo> all_machines;

    @SneakyThrows
    public WawaServer() {
        // START OF MOCK DATA
        all_machines = new HashMap<>();
        MachineInfo mi = new MachineInfo();
        mi.mac = "86:9b:16:91:cb:7a";
        try {
            mi.socket = new Socket("0.0.0.0", 7771);
        } catch (ConnectException e) {
            log.info("Mocked socket are failed to create");
        }
        all_machines.put("86:9b:16:91:cb:7a", mi);
        // END OF MOCK DATA
    }

    public boolean processPlayerStartNewGame(String mac) {
        if (this.all_machines.size() == 0) {
            // if no machine found - throw an exception
            log.info("No machine with mac=" + mac + " found");
            throw new MachineNotFoundException(mac);
        } else {
            MachineInfo wawaji;
            synchronized (this.all_machines) {
                wawaji = this.all_machines.get(mac);
            }

            return wawaji != null;
        }
    }

    public List<String> getRoomList() {
        List<String> roomList;
        log.info("Get room list server processing");
        synchronized (this.all_machines) {
            roomList = new ArrayList<>(this.all_machines.keySet());
        }

        log.info("Room list result: " + roomList);
        return roomList;
    }

    public Boolean tranlsateToWawaji(String mac, byte[] da) {
        MachineInfo destMac = this.all_machines.get(mac);
        if (destMac == null) {
            // throw exception if no machine with given mac found
            log.info("No machine with mac=" + mac + " found");
            throw new MachineNotFoundException(mac);
        } else {
            try {
                DataOutputStream out = new DataOutputStream(destMac.socket.getOutputStream());
                out.write(da, 0, da.length);
                out.flush();
                ++destMac.sendCount;
            } catch (Exception ex) {
                // if socket is not open return false
                log.info("Socket is closed, can't start a game.");
                return false;
            }
        }
        // if everything is okay - return true
        log.info("Run a game on machine with mac=" + mac);
        return true;
    }

}
