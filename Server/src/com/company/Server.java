package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server extends Thread {
    private final int serverPort;
    private ArrayList<ServerWorker> workerList = new ArrayList<>();

    /**
     * Server Constructor
     * @param serverPort server port
     */
    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Returns list of workers
     * @return list of workers
     */
    public List<ServerWorker> getWorkerList() {
        return workerList;
    }

    //threading
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while (true) {
                //System.out.println("About to accept client connection...");
                Socket clientSocket = serverSocket.accept();
                //System.out.println("Accepted connection from " + clientSocket);
                ServerWorker worker = new ServerWorker(this, clientSocket);
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes worker from workerList
     * @param serverWorker worker
     */
    public void removeWorker(ServerWorker serverWorker) {
        workerList.remove(serverWorker);
    }
}
