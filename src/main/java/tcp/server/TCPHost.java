package tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPHost {

    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final int port;

    public TCPHost(int port) {
        this.threadPool = Executors.newCachedThreadPool();
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            threadPool.execute(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        threadPool.execute(new ClientHandler(clientSocket));
                    } catch (IOException e) {
                        if (!serverSocket.isClosed()) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPool.shutdownNow();
    }
}
