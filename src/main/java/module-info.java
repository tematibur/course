module com.example.trytosmth {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires junit;
    requires mockito.all;
    requires commons.dbcp;

    opens com.example.courseWork to javafx.fxml;
    exports com.example.courseWork;
    exports com.example.courseWork.dao;
    opens com.example.courseWork.dao to javafx.fxml, mockito.all;
    exports com.example.courseWork.model;
    opens com.example.courseWork.model to javafx.fxml;
    exports com.example.courseWork.service;
    opens com.example.courseWork.service to javafx.fxml;
}