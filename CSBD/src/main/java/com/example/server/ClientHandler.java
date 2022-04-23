package com.example.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

//Обработчик клиента
public class ClientHandler {
    Socket socket;
    ServerMain server;
    DataOutputStream out;
    DataInputStream in;
    private String nickname;
    public void sendMSG(String str){
        try {
            System.out.println(nickname +"- "+ str);//печатает в консоль сервера
            if(str.equals("/authok"))out.writeUTF(str + "\n");
            out.writeUTF(str + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("SendMSG exception");
        }
    }
    public String getNickname() {
        return nickname;
    }
    public ClientHandler(Socket socket,ServerMain serverMain) {
        this.socket = socket;
        this.server = serverMain;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true){
                            //авторизация
                                String str = in.readUTF(); // */auth log pass
                            //Задание нового пользователя
                            if(str.startsWith("/new")){
                                String[] fmt = str.split(" ");
                                AuthServer.setLoginPasswordNickname(fmt[1],fmt[2],fmt[3]);
                                System.out.println("Зарегистрирован новый пользователь");
                            }
                            if(str.startsWith("/auth")){
                                    String[] creds = str.split(" ");
                                    String nick = AuthServer.getNickByLogPassSQL(creds[1],creds[2]);
                                    if(nick != null){
                                        //задали никнейм
                                        nickname = nick;
                                        if(isUserCorrect(nickname,server))break;///
                                    }else{
                                        sendMSG("Wrong log/pass\n");
                                    }
                                }
                        }
                        //////////////
                        while (true) {
                            //Обработка служебных команд
                                String str;
                                str = in.readUTF();
                                if (str.equals("/end")) {
                                    out.writeUTF("/end");
                                    /*server.sendOnlineUsers();////!!!*/
                                    break;
                                }
                            /*if (str.startsWith("/show")){
                                server.sendOnlineUsers();
                            }*/
                                ////исправление ошибок имён, отправка форматированного текста
                                String strF;
                                if(str.startsWith("/w")){

                                    strF = "/w " + getNickname() + ": " + str;
                                }else{
                                    strF = getNickname() + ": " + str;
                                }
                                server.sendToAll(strF);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    server.unsubscribe(ClientHandler.this);
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isUserCorrect (String nickname, ServerMain server) {
        if(server.isNickFree(nickname)){
            server.subscribe(ClientHandler.this);
            sendMSG("/authok");
            server.sendOnlineUsers();
            return true;

        }else{
            sendMSG("Wrong log/pass (  isUserCorrect()  )");
            return false;
        }


    }


}
