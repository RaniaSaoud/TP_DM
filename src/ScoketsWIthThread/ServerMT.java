package ScoketsWIthThread; 


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ServerMT extends Thread {
    private static final int PORT = 1900;
    private int clientCount = 0;
    private final List<Repartitor> clientHandlers = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept(); 
                    clientCount++;
                    System.out.println("Client connected! Total clients: " + clientCount);
                    Repartitor clientHandler = new Repartitor(clientSocket, clientCount, this);
                    clientHandlers.add(clientHandler);
                    clientHandler.start();
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server failed to start: " + e.getMessage());
        }
    }


    public void broadcastMessage(String message) {
        synchronized (clientHandlers) {
            for (Repartitor handler : clientHandlers) {
                handler.sendMessage(message);
            }
        }
    }

  
    public void removeClientHandler(Repartitor handler) {
        clientHandlers.remove(handler);
    }

   
    public void shutDown() {
        broadcastMessage("Server is shutting down. You will be disconnected shortly.");
    try {
        Thread.sleep(5000); 
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); 
    }
    synchronized (clientHandlers) {
        for (Repartitor handler : clientHandlers) {
            handler.closeConnection();
        }
    }
    }
    public static void main(String[] args) {
        new ServerMT().start();
    }
}
