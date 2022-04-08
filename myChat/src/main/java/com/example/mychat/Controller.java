package com.example.mychat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {
    @FXML
    TextArea textArea;

    @FXML
    Button button;

    @FXML
    TextField textField;

    @FXML
    Label usersLabel;

    @FXML
    private ListView<String> listView;
    //позже можно добавить функций, пока только листнер и отображение инфы...
    String[] userDemo = {"Кирилл","Мифодий","Карл","Марксcccccccccc","Шмаркс","Кот Василий"};
    String tmp;

    @FXML
    MenuBar menuBar;
    //send
    public void sendMessage(){
        textArea.appendText("-"+textField.getText() +"\n");
        textField.clear();
        textField.requestFocus();
    }
    //Некоторые betta-функции панели ListView
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listView.getItems().addAll(userDemo);
        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            tmp = listView.getSelectionModel().getSelectedItem();
            usersLabel.setText("Информация о пользователе: "+tmp);
        });
    }
    //Закрытие приложения
    public void closeApp(ActionEvent event){
        Platform.exit();
        System.exit(0);
    }
    //Сохраняет историю чата (пока только в паку проекта append)
    public void saveToFile(ActionEvent event){
        File myFile = new File("chatText.txt");
        try{
            PrintWriter writer =
                    new PrintWriter(new FileWriter(myFile,true));
            String tmp = textArea.getText();
            writer.println(tmp+"__________________"+"\n");
            writer.flush();
            writer.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    //Чистит историю textArea
    public void clearHistory(ActionEvent event){
        textArea.setText("");
    }
    //Открывает историю чата
    public  void openChat(ActionEvent event) throws FileNotFoundException {
        File file = new File("chatText.txt");
        Scanner scanner = new Scanner(file);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append("\n"+scanner.nextLine());
            scanner.close();
        }
        textArea.setText(sb.toString());
        textArea.appendText("\n"+"================");
    }

}