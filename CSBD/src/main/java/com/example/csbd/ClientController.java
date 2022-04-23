package com.example.csbd;

import com.example.server.AuthServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController {

    private boolean isAuthorized;
    @FXML
    TextArea textArea;
    @FXML
    TextField textField;
    @FXML
    Button button;
    @FXML
    VBox topVBox;
    @FXML
    HBox botHBox;
    @FXML
    TextField logTF;
    @FXML
    PasswordField passPF;
    @FXML
    Button authB;
    @FXML
    ListView listView;
    ///////
    @FXML
            Button addNewUser;
    @FXML
            VBox addBox;
    @FXML
            TextField newLog;
    @FXML
            TextField newPass;
    @FXML
            TextField newNick;
    @FXML
            Button addButton;


    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    String IP_ADDRESS = "localhost";
    int PORT = 8189;


    @FXML

    public void addUser(){
        addBox.setManaged(true);
        addBox.setVisible(true);
        topVBox.setManaged(false);
        topVBox.setVisible(false);
    }

    public void addUserFinish(){
        if(newLog.getText().isEmpty() || newPass.getText().isEmpty() || newNick.getText().isEmpty()){
            textArea.appendText("Заполните пустые поля\n");
        }else {
            if(socket == null || socket.isClosed()){
                connect();
            }
            try {
                out.writeUTF("/new "+ newLog.getText() +" "+ newPass.getText() +" "+ newNick.getText());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Не удалось отослать запрос на добавление серверу");
            }

            textArea.appendText("Логин принят и добавлен\n");
            addBox.setManaged(false);
            addBox.setVisible(false);
            topVBox.setManaged(true);
            topVBox.setVisible(true);
            try {
                out.writeUTF("/end");
            } catch (IOException e) {
                System.out.println("не получилось отослать /end (addUserFinish)");
            }
        }
    }

    public void keyListener(KeyEvent keyEvent) {
        if (keyEvent.getCode().getCode() == 10) {
            sendMessage();
        }
    }
    public void setActive(boolean isAuthorized){
        this.isAuthorized = isAuthorized;
        if(!isAuthorized){
            topVBox.setVisible(true);
            topVBox.setManaged(true);//добавляет или убирает панель вообще в сочитани с setVisible
            botHBox.setVisible(false);
            botHBox.setManaged(false);
        }else{
            topVBox.setVisible(false);
            topVBox.setManaged(false);
            botHBox.setVisible(true);
            botHBox.setManaged(true);

        }
    }

    public void sendMessage() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Send message error!!! (server offline)");
        }
    }

    public void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //авторизация окна по /authok
                        while (true) {
                            try {
                                String str = in.readUTF();
                                if (str.startsWith("/authok")) {
                                    setActive(true);
                                    textArea.appendText("Oneline\n");
                                    break;
                                }
                            }catch (SocketException e){
                                System.out.println("Connection lost!");
                                break;
                            }
                        }
                        //show
                        while (true) {
                            try {
                                String str = in.readUTF();
                                if(str.startsWith("/show")){
                                    String[] nicknames = str.split(" ");
                                    //что то типа микропотока для малых задач
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            //очистка массива вьюшки
                                            listView.getItems().clear();
                                            //обход с 1
                                            for (int i = 1; i < nicknames.length; i++) {
                                                listView.getItems().add(nicknames[i]);
                                            }

                                        }
                                    });
                                }
                                //печать текста на textArea
                                    textArea.appendText(str + "\n");
                                if(str.equals("/end")){
                                    textArea.appendText("Disconnect!!!\n");
                                    break;//доработать
                                }

                            }catch (SocketException e){
                                System.out.println("Connection lost! (server is off)");
                                break;
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            in.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }).start();
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server not found!:))");
        }
    }
    public void auth(){
        if(socket == null || socket.isClosed()){
            connect();
        }
        try {

            //что бы не выпадал в ошибку при null на вводе - (заглушка)
            if(logTF.getText().isEmpty() || passPF.getText().isEmpty()){
                //можно отослать (не регистрируемое значение, пока так)
                out.writeUTF("/auth "+ " WRONG_LOG "+ "WRONG_PASS");
            }else{

                out.writeUTF("/auth "+ logTF.getText()+" "+ passPF.getText());
            }
            logTF.clear();
            passPF.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}