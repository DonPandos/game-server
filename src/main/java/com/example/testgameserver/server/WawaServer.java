package com.example.testgameserver.server;

import com.example.testgameserver.exception.MachineNotFoundException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

@Slf4j
public class WawaServer {
    private Thread newThread;
    Map<String, MachineInfo> all_machines;
    ServerSocket listenSocket;
    boolean showldStop = false;
    int nport = 0;

    @SneakyThrows
    public WawaServer() {
        // START OF MOCK DATA
//        all_machines = new HashMap<>();
//        MachineInfo mi = new MachineInfo();
//        mi.mac = "86:9b:16:91:cb:7a";
//        try {
//            mi.socket = new Socket("0.0.0.0", 7771);
//        } catch (ConnectException e) {
//            log.info("Mocked socket are failed to create");
//        }
//        all_machines.put("86:9b:16:91:cb:7a", mi);
        // END OF MOCK DATA
    }

    public void Start(int np) {
        this.showldStop = false;
        this.nport = np;
        this.all_machines = new HashMap();
        this.newThread = new Thread(() -> {
            try {
                WawaServer.this.listenSocket = new ServerSocket();
                WawaServer.this.listenSocket.bind(new InetSocketAddress("0.0.0.0", WawaServer.this.nport));

                Socket cur_socket;
                for (; !WawaServer.this.showldStop; WawaServer.this.new HandlerThread(cur_socket)) {
                    cur_socket = WawaServer.this.listenSocket.accept();
                    String ip = cur_socket.getRemoteSocketAddress().toString();
                    log.info("wawaji ip" + ip + "has connected.");

                    try {
                        DataOutputStream out = new DataOutputStream(cur_socket.getOutputStream());
                        out.write("aa".getBytes(), 0, 2);
                        out.flush();
                    } catch (IOException var4) {
                        System.out.println("server new DataOutputStream Failed.");
                    }
                }

                System.out.println("listen is exit at" + WawaServer.this.nport);
            } catch (Exception var5) {
                System.out.println("listen thread is exit at" + WawaServer.this.nport);
            }

        });
        this.newThread.start();
        this.CheckTimeout();
    }

    void CheckTimeout() {
        Thread thTimer = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        if (!WawaServer.this.showldStop) {
                            Thread.sleep(5000L);
                            if (!WawaServer.this.showldStop) {
                                WawaServer.this.processTimeOut();
                                continue;
                            }
                        }
                    } catch (InterruptedException var2) {
                    }

                    System.out.println("[AppServer] heartbeat thread exit.");
                    return;
                }
            }
        });
        thTimer.start();
    }


    void processTimeOut() {
        if (this.all_machines.size() > 0) {
            synchronized (this.all_machines) {
                Iterator<Map.Entry<String, MachineInfo>> iter = this.all_machines.entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry<String, MachineInfo> me = (Map.Entry) iter.next();
                    long now_tw = System.currentTimeMillis();
                    if (now_tw - ((MachineInfo) me.getValue()).last_heartbeattime > 30000L) {
                        System.out.println("[AppServer]" + (String) me.getKey() + "Timeout Remove:");
                        ((MachineInfo) me.getValue()).Clear();
                        iter.remove();
                    }
                }

            }
        }
    }

    public boolean processPlayerStartPlay(String strMAC, Socket sclient) {
        MachineInfo macInfo = (MachineInfo) this.all_machines.get(strMAC);
        if (macInfo == null) {
            return false;
        } else {
            macInfo.current_player = sclient;
            return true;
        }
    }

    void processMsgtoPlayer(String MAC, byte[] data) {
        MachineInfo macInfo = (MachineInfo) this.all_machines.get(MAC);
        if (macInfo != null) {
//            SimpleApp.cserver.TranlsateToPlayer(macInfo.current_player, data);
        }
    }

    public void processPlayerLeave(String MAC, Socket client) {
        if (this.all_machines.size() > 0) {
            MachineInfo wawaji = null;
            synchronized (this.all_machines) {
                wawaji = (MachineInfo) this.all_machines.get(MAC);
            }

            if (wawaji != null) {
                if (wawaji.current_player == client) {
                    wawaji.current_player = null;
                }

            }
        }
    }

    private int ReadDataUnti(byte[] datas, int expect_len, InputStream is) {
        int readCount = 0;

        while (readCount < expect_len) {
            try {
                int recv_len = is.read(datas, readCount, expect_len - readCount);
                if (recv_len <= 0) {
                    System.out.println(this.getClass().getName() + "ReadDataUnti. return -1.beacuse:recv_len=" + recv_len);
                    return -1;
                }

                readCount += recv_len;
            } catch (IOException var6) {
                System.out.println("ReadDataUnti Exception. return -1.");
                return -1;
            }
        }

        return readCount;
    }

    boolean check_com_data(byte[] data, int len) {
        int check_total = 0;

        for (int i = 0; i < len; ++i) {
            if (i >= 6 && i < len - 1) {
                check_total += data[i] & 255;
            }
        }

        if (check_total % 100 != data[len - 1]) {
            return false;
        } else {
            return true;
        }
    }

    public void Stop() {
        this.showldStop = true;

        try {
            this.listenSocket.close();
            this.listenSocket = null;
        } catch (IOException var4) {
        }

        if (this.newThread != null) {
            this.newThread.interrupt();
            this.newThread = null;
        }

        synchronized (this.all_machines) {
            Iterator<Map.Entry<String, MachineInfo>> iter = this.all_machines.entrySet().iterator();

            while (true) {
                if (!iter.hasNext()) {
                    break;
                }

                Map.Entry<String, MachineInfo> me = (Map.Entry) iter.next();
                ((MachineInfo) me.getValue()).Clear();
            }
        }

        this.all_machines.clear();
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

    private class HandlerThread implements Runnable {
        MachineInfo me = null;

        public HandlerThread(Socket client) {
            log.info("Handler initialized");
            this.me = new MachineInfo();
            this.me.socket = client;
            this.me.runningThread = new Thread(this);
            this.me.runningThread.start();
        }

        public void run() {
            while (true) {
                if (!WawaServer.this.showldStop) {
                    try {
                        InputStream reader = this.me.socket.getInputStream();
                        byte[] bHead = new byte[7];
                        int count = WawaServer.this.ReadDataUnti(bHead, 7, reader);
                        if (count != 7) {
                            System.out.println("Room recv Read head != 7.Socket close.");
                        } else {
                            if ((bHead[0] & 255) == 254) {
                                if (bHead[0] != (byte) (~bHead[3] & 255) && bHead[1] != (byte) (~bHead[4] & 255) && bHead[2] != (byte) (~bHead[5] & 255)) {
                                    continue;
                                }

                                int data_length = bHead[6] & 255;
                                byte[] datas = new byte[data_length - 7];
                                int data_recved_len = WawaServer.this.ReadDataUnti(datas, data_length - 7, reader);
                                if (data_recved_len != data_length - 7) {
                                }

                                byte[] total_data = new byte[data_length];
                                System.arraycopy(bHead, 0, total_data, 0, 7);
                                System.arraycopy(datas, 0, total_data, 7, data_length - 7);
                                if (!WawaServer.this.check_com_data(total_data, data_length)) {
                                    System.out.println("Checksum Data Failed. skip.");
                                    continue;
                                }

                                int data_cmd = total_data[7] & 255;
                                System.out.printf("cmd:%02X\n", data_cmd);
                                ++this.me.recvCount;
                                if (data_cmd == 49) {
                                    if (this.me.current_player != null && !this.me.current_player.isClosed()) {
//                                        SimpleApp.cserver.OnGameStartOK(this.me.current_player);
                                        ++this.me.recvCount;
                                    }
                                    continue;
                                }

                                int frontCamstate;
                                if (data_cmd == 51) {
                                    if (WawaServer.this.all_machines.size() <= 0) {
                                        return;
                                    }

                                    if (this.me.current_player != null && !this.me.current_player.isClosed()) {
                                        frontCamstate = total_data[8] & 255;
//                                        SimpleApp.cserver.OnGameEnd(this.me.current_player, frontCamstate);
                                    }

                                    --this.me.recvCount;
                                    this.me.current_player = null;
                                    continue;
                                }

                                String strMAC;
                                if (data_cmd == 53) {
                                    --this.me.recvCount;
                                    strMAC = new String(total_data, 8, 12);
                                    long t1 = System.currentTimeMillis();
                                    System.out.println("[" + t1 + "] wawa heartbeat." + strMAC);
                                    long now_tw = System.currentTimeMillis();
                                    MachineInfo tmp = (MachineInfo) WawaServer.this.all_machines.get(strMAC);
                                    if (tmp == null) {
                                        this.me.last_heartbeattime = now_tw;
                                        this.me.mac = strMAC;
                                        synchronized (WawaServer.this.all_machines) {
                                            WawaServer.this.all_machines.put(strMAC, this.me);
                                        }
                                    } else {
                                        this.me.last_heartbeattime = now_tw;
                                    }

                                    try {
                                        DataOutputStream out = new DataOutputStream(this.me.socket.getOutputStream());
                                        out.write(total_data, 0, total_data.length);
                                        out.flush();
                                    } catch (IOException var17) {
                                    }
                                    continue;
                                }

                                if (data_cmd == 55) {
                                    --this.me.recvCount;
                                    System.out.println("收到娃娃机故障");
                                    continue;
                                }

                                if (data_cmd == 137) {
                                    --this.me.recvCount;
                                    frontCamstate = total_data[8] & 255;
                                    int backCamstate = total_data[9] & 255;
                                    String st_txt = "收到即将重启命令.";
                                    if (frontCamstate == 0) {
                                        st_txt = st_txt + "前置正常.";
                                    } else if (frontCamstate == 1) {
                                        st_txt = st_txt + "前置推流故障.";
                                    } else if (frontCamstate == 2) {
                                        st_txt = st_txt + "前置缺失.";
                                    }

                                    if (backCamstate == 0) {
                                        st_txt = st_txt + "后置正常.";
                                    } else if (backCamstate == 1) {
                                        st_txt = st_txt + "后置推流故障.";
                                    } else if (backCamstate == 2) {
                                        st_txt = st_txt + "后置缺失.";
                                    }

                                    System.out.println(st_txt);
                                    continue;
                                }

                                if (data_cmd == 146) {
                                    --this.me.recvCount;

                                    try {
                                        DataOutputStream outx = new DataOutputStream(this.me.socket.getOutputStream());
                                        outx.write(total_data, 0, total_data.length);
                                        outx.flush();
                                    } catch (IOException var19) {
                                    }
                                    continue;
                                }

                                if (data_cmd != 160) {
                                    continue;
                                }

                                --this.me.recvCount;
                                strMAC = new String(total_data, 8, 12);
                                if ((total_data[20] & 255) == 0) {
                                    System.out.println("娃娃机:" + strMAC + "前置推流失败.");
                                    continue;
                                }

                                if ((total_data[20] & 255) == 1) {
                                    System.out.println("娃娃机:" + strMAC + "前置推流成功.");
                                    continue;
                                }

                                if ((total_data[20] & 255) == 2) {
                                    System.out.println("娃娃机:" + strMAC + "前置推流关闭.");
                                    continue;
                                }

                                if ((total_data[20] & 255) == 16) {
                                    System.out.println("娃娃机:" + strMAC + "后置推流失败.");
                                    continue;
                                }

                                if ((total_data[20] & 255) == 17) {
                                    System.out.println("娃娃机:" + strMAC + "后置推流成功.");
                                    continue;
                                }

                                if ((total_data[20] & 255) == 18) {
                                    System.out.println("娃娃机:" + strMAC + "后置推流关闭.");
                                }
                                continue;
                            }

                            System.out.println("Invalid Head.Socket close.");
                        }
                    } catch (Exception var20) {
                        System.out.println("[AppServer] Exception!===" + this.me.mac);
                    }
                }

                synchronized (WawaServer.this.all_machines) {
                    WawaServer.this.all_machines.remove(this.me.mac);
                }

                System.out.println("[AppServer] " + this.me.mac + "thread exit.");
                this.me.Clear();
                this.me = null;
                return;
            }
        }
    }

}
