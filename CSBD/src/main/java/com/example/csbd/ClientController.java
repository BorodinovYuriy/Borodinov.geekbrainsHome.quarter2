package com.example.csbd;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


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

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    String IP_ADDRESS = "localhost";
    int PORT = 8189;

    @FXML
    public void keyListener(KeyEvent keyEvent) {
        if (keyEvent.getCode().getCode() == 10) {
            sendMessage();
        }
    }
    public void setActive(boolean isAuthorized){
        this.isAuthorized = isAuthorized;
        if(!isAuthorized){
            topVBox.setVisible(true);
            topVBox.setManaged(true);//добавляет или убирает панель вообще
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
                        while (true) {
                            String str = in.readUTF();
                            if(str.startsWith("/authok")){
                                setActive(true);
                                break;
                            }
                            textArea.appendText(str + "\n");
                        }

                        while (true) {
                            String str = in.readUTF();
                            if(str.equals("/end")){
                                textArea.appendText("Disconnect!!!");
                                break;
                            }
                            textArea.appendText(str + "\n");
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
            System.out.println("Server not found!");
        }

    }
    public void auth(){
        if(socket == null || socket.isClosed()){
            connect();
        }
        try {
            out.writeUTF("/auth "+ logTF.getText()+" "+ passPF.getText());
            logTF.clear();
            passPF.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}