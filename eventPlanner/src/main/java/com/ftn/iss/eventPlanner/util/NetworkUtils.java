package com.ftn.iss.eventPlanner.util;

import java.net.*;

public class NetworkUtils {
    public static String getLocalIpAddress() throws SocketException {
        try(final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            return "localhost";
        }
    }
}
