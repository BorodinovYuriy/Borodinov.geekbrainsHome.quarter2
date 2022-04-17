package com.example.server;

import java.sql.*;

public class AuthServer {
    //классы-драйверы для sql что бы могли
    // посылать запросы к базе в формате sql...
    //static - должны быть доступны во всём проекте
    private static Connection connection;
    private static Statement statement;

    public static void connect() {
        //находим класс драйвера
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:users.db");
            statement = connection.createStatement();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("SQL exception");
        }
    }
    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("disconnect exception");
        }
    }
    public static String getNickByLogPassSQL(String log, String pass){
        String sql = String.format( "select nickname from users where login = '%s' and password = '%s'", log, pass);
        //просим statement выполнить запрос
        try {
            //resultSet - таблица с возвращаемым от sql результатом
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
