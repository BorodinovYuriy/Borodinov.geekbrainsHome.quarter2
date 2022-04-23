package com.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

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

            while (true){
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
    public void sendToAll(String str){
                if(!str.startsWith("/w ")) {
            for (ClientHandler client :
                    clientHandlers) {
                client.sendMSG(str);////ошибочка
            }
        }else {
                    System.out.println("SECRET: "+ str);
                    String[] wsendstr = str.split(" ");
                    sendToOnly(str, wsendstr[3]);
        }
    }
    public void sendToOnly(String str, String nick){
        for (ClientHandler client :
                clientHandlers) {
            if(nick.equals(client.getNickname())){
                client.sendMSG(str);
            }

        }
    }

    public void subscribe (ClientHandler client){
        clientHandlers.add(client);
    }
    public void unsubscribe (ClientHandler client){
        clientHandlers.remove(client);
    }
}
