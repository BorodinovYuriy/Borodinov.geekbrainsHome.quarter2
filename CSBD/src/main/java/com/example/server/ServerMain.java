package com.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class ServerMain {

    private Vector<ClientHandler> clientHandlers;

    public void start() {
        ServerSocket server;
        Socket socket;


        clientHandlers = new Vector<>();

        try {
            //Соединение с БД
            AuthServer.connect();
            //Старт сервера
            server = new ServerSocket(8189); //localhost:8189 -- 0 - 65000
            System.out.println("Старт сервера");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(socket, this);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        AuthServer.disconnect();
    }

    /*
     * sendToAll отправляет так же личные сообщения написанные в формате (/w "nickname"....)
     * через метод sendToOnly
     */
    public void sendToAll(String str) {
         if (!str.startsWith("/w")) {
            for (ClientHandler client :
                    clientHandlers) {
                client.sendMSG(str);
            }
        } else if(str.startsWith("/w")) {
            System.out.println("SECRET: " + str);
            String[] wsendstr = str.split(" ");
            System.out.println(wsendstr[3]);
            sendToOnly(str, wsendstr[3].trim());
        }
    }

    public void sendToOnly(String str, String nick) {
        for (ClientHandler client :
                clientHandlers)
            if (nick.equals(client.getNickname())) {
                client.sendMSG(str);
            }
    }

    public void subscribe(ClientHandler client) {
        clientHandlers.add(client);
    }

    public void unsubscribe(ClientHandler client) {
        clientHandlers.remove(" "+client.getNickname());
        sendOnlineUsers();//
    }

    public void sendOnlineUsers(){
        ///При выходе пользователя, его ник добавляет но не удаляет!!!
        /**
         * Воспользовались стримом:
         * Взяли наш вектор, получили поток/стрим элементов
         * и по одому методом МАП обьекта клиентхендлера получили ник 
         * и сложили в список стрингов....
         * */
        List<String> list = clientHandlers.stream().map(ClientHandler::getNickname).toList();
        //Отправляем никнеймы клиентам
        StringBuilder sb = new StringBuilder();
        for (String s:
             list) {
            sb.append(s);
            sb.append(" ");
        }
        sendToAll("/show "+sb.toString().trim());
    }

    public boolean isNickFree(String nick){
        if(clientHandlers.isEmpty())return true;
        for (ClientHandler client:
             clientHandlers) {
            if(client.getNickname().equals(nick)){
                return false;
            }
        }
        return true;
    }

















}