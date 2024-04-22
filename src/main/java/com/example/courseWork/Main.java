package com.example.courseWork;

import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;


public class Main extends Application {
    private final MainController mainController = new MainController();

    @Override
    public void start(Stage stage) {
        stage.setScene(mainController.createScene());
        stage.setTitle("Перевірка справності вогнегасників");
        stage.show();
    }


    public static void main(String[] args) throws SQLException {
        launch(args);
    }
}