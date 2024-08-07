package com.dal.asdc.reconnect.configs;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Configuration class for setting up Socket.IO server.
 * This class handles the configuration, initialization, and shutdown of the Socket.IO server.
 */

@CrossOrigin
@Component
@Log4j2
public class SocketIOConfig {

    @Value("${socket.host}")
    private String socketHost;
    @Value("${socket.port}")
    private int socketPort;
    private SocketIOServer server;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(socketHost);
        config.setPort(socketPort);
        server = new SocketIOServer(config);
        server.start();
        server.addConnectListener(client -> log.info("new user connected with socket " + client.getSessionId()));

        server.addDisconnectListener(client -> client.getNamespace().getAllClients().forEach(data -> log.info("user disconnected " + data.getSessionId().toString())));
        return server;
    }

    /**
     * Method to stop the SocketIOServer gracefully before the application shuts down.
     * Annotated with @PreDestroy to ensure this method is called when the bean is destroyed.
     */

    @PreDestroy
    public void stopSocketIOServer() {
        this.server.stop();
    }

}
