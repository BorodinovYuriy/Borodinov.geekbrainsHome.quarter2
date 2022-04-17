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
    String nickname;

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
                            String str = in.readUTF(); // */auth log pass
                            if(str.startsWith("/auth")){
                                String[] creds = str.split(" ");
                                String nick = AuthServer.getNickByLogPassSQL(creds[1],creds[2]);
                                if(nick != null){
                                    //задали никнейм
                                    nickname = nick;
                                    sendMSG("/authok");
                                    server.subscribe(ClientHandler.this);
                                    break;
                                }else{
                                    sendMSG("Wrong log/pass");
                                }

                            }
                        }


                        while (true) {
                            String str;
                            str = in.readUTF();
                            if (str.equals("/end")) {
                                out.writeUTF("/end");
                                break;
                            }
                            ////исправление ошибок имён, отправка форматированного текста
                            String strF;
                            if(str.startsWith("/w ")){
                                strF = "/w " + getNickname() + ": " + str;
                            }else{
                                strF = getNickname() + ": " + str;
                            }
                            serverMain.sendToAll(strF);
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
//                            out.writeUTF("/end");
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
    public void sendMSG(String str){

        try {
            System.out.println(nickname +": "+ str);//печатает в консоль сервера
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
}
