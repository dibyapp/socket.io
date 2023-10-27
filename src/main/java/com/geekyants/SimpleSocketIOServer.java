package com.geekyants;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import jakarta.annotation.PostConstruct;

public class SimpleSocketIOServer {

	@PostConstruct
    void main() {
        Configuration config = new Configuration();
        config.setHostname("localhost"); // Server hostname
        config.setPort(9092); // Port on which the server will run

        final SocketIOServer server = new SocketIOServer(config);

        // Event listener for a simple chat message
        server.addEventListener("chatMessage", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackSender) {
                // Broadcast the received message to all connected clients
                server.getBroadcastOperations().sendEvent("chatMessage", data);
            }
        });

        // Disconnect listener
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                System.out.println("Client disconnected: " + client.getSessionId());
            }
        });

        server.start();
        System.out.println("Socket.IO server started on " + config.getHostname() + ":" + config.getPort());

        // Handle shutdown gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                server.stop();
                System.out.println("Socket.IO server stopped.");
            }
        }));
    }
}
