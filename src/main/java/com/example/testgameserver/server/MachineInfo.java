package com.example.testgameserver.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

class MachineInfo {
    public String mac;
    public String name;
    public int state;
    public Map<Socket, Integer> user_in_room;
    public Socket socket;
    public Socket current_player;
    public Thread runningThread;
    public long last_heartbeattime = 0L;
    public long sendCount = 0L;
    public long recvCount = 0L;

    MachineInfo() {
    }

    public void Clear() {
        if (this.socket != null) {
            try {
                this.socket.close();
                this.socket = null;
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

        if (this.runningThread != null) {
            this.runningThread.interrupt();
            this.runningThread = null;
        }

    }
}
