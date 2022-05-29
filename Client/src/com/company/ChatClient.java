package com.company;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

//import com.company.engine.ResponseListener;


public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    /**
     * ChatClient constructor
     * @param serverName name of server
     * @param serverPort server port
     */
    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    /**
     * Main for ChatClient
     * @param args args
     * @throws IOException exception
     */
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE " + login);
            }
        });

        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String msgBody) {
                System.out.println("Mesage from " + fromLogin + ": " + msgBody);
            }
        });

        if (!client.connect()) {
            System.err.println("Couldn't connect");
        } else {
            System.out.println("Connected");
            //client.logoff();
        }

        //TESTING
        /**int port = 8818; //random port
         Server server = new Server(port);

         server.start();**/
    }

    /**
     * Send a message
     * @param user username
     * @param body text
     * @throws IOException exception
     */
    public void msg(String user, String body) throws IOException {
        String cmd = "msg " + user + " " + body + "\n";
        serverOut.write(cmd.getBytes());
    }

    /**
     * Remove user from online list
     * @throws IOException exception
     */
    private void logoff() throws IOException {
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }

    /**
     * Check if login combination is valid
     * @param login username
     * @param password password
     * @return Login Sucess/Fail
     * @throws IOException exception
     */
    boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        //System.out.println("Response Line: " + response);

        if ("login success".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Starts message loop
     */
    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    /**
     * Constantly checks for online and offline status
     */
    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = StringUtils.split(line);
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String [] tokensMsg = StringUtils.split(line, null, 3);
                        handleMessage(tokensMsg);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * Sends message
     * @param tokensMsg array to parse login name and message body
     */
    private void handleMessage(String[] tokensMsg) {
        for (MessageListener listener : messageListeners) {
            listener.onMessage(tokensMsg[1], tokensMsg[2]);

        }
    }

    /**
     * Sends command to set status as offline
     * @param tokens array to parse login name
     */
    private void handleOffline(String[] tokens) {
        for (UserStatusListener listener: userStatusListeners) {
            listener.offline(tokens[1]);
        }
    }
    /**
     * Sends command to set status as online
     * @param tokens array to parse login name
     */
    private void handleOnline(String[] tokens) {
        for (UserStatusListener listener: userStatusListeners) {
            listener.online(tokens[1]);
        }
    }

    /**
     * Connect to server
     * @return successful connection
     */
    boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            //System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Add user status listener to user in userlist
     * @param listener listener
     */
    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    /**
     * Removes user status listener to user in userlist
     * @param listener listener
     */
    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    /**
     * Add message listener to messagelisteners
     * @param listener listener
     */
    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    /**
     * removes message listener from messagelisteners
     * @param listener listener
     */
    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }
}