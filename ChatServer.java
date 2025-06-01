import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;
    // Thread-safe set to hold all client handlers
    private static Set<ClientHandler> clientHandlers = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        System.out.println("Chat server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler handler = new ClientHandler(clientSocket);
                clientHandlers.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Error in server: " + e.getMessage());
        }
    }

    // Broadcast message to all clients except the sender
    public static void broadcastMessage(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clientHandlers) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    // Remove client from set
    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        System.out.println("Client disconnected. Total clients: " + clientHandlers.size());
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Enter your name:");
            this.clientName = in.readLine();
            out.println("Welcome " + clientName + "! You can start typing messages.");
            ChatServer.broadcastMessage(clientName + " has joined the chat.", this);
        } catch (IOException e) {
            System.err.println("Error initializing client handler: " + e.getMessage());
            closeEverything();
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
                String fullMessage = clientName + ": " + message;
                System.out.println(fullMessage);
                ChatServer.broadcastMessage(fullMessage, this);
            }
        } catch (IOException e) {
            System.err.println("Connection lost with client " + clientName);
        } finally {
            closeEverything();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void closeEverything() {
        try {
            ChatServer.removeClient(this);
            if (clientName != null) {
                ChatServer.broadcastMessage(clientName + " has left the chat.", this);
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing client resources: " + e.getMessage());
        }
    }
}
