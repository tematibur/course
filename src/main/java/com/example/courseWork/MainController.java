package com.example.courseWork;

import com.example.courseWork.dao.exception.DbException;
import com.example.courseWork.model.FireExtinguisherData;
import com.example.courseWork.service.FireExtinguisherService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;


public class MainController {

    private final Button checkButton = new Button("Перевірити термін придатності");
    private final Button replaceButton = new Button("Замінити");
    private final Button addButton = new Button("Додати");
    private final TextArea resultTextArea = new TextArea();
    private TableView<FireExtinguisherData> tableView; // Поле для TableView

    private final FireExtinguisherService fireExtinguisherService = new FireExtinguisherService();

    public Scene createScene() {
        // Створення панелі вкладок
        TabPane tabPane = new TabPane();
        // Створення вкладки "Info"
        Tab infoTab = new Tab("Info");

        // Створення контейнера для вмісту вкладки "Info"
        VBox infoContent = new VBox(createInfoLabel());
        infoContent.setAlignment(Pos.CENTER); // Центрування вмісту

        // Встановлення вмісту вкладки "Info"
        infoTab.setContent(infoContent);

        // Створення вкладки "Menu"
        Tab menuTab = new Tab("Menu");
        menuTab.setContent(createMenuContent());

        // Створення вкладки "List"
        Tab listTab = new Tab("List");
        listTab.setContent(createListContent());

        // Додавання вкладок до панелі
        tabPane.getTabs().addAll(infoTab, menuTab, listTab);

        // Створення основної панелі
        BorderPane root = new BorderPane();
        root.setCenter(tabPane);

        // Створення сцени та відображення вікна
        Scene scene = new Scene(root, 500, 450);
        scene.getStylesheets().add(Paths.get("src/styles.css").toUri().toString());
        return scene;
    }

    public Label createInfoLabel() {
        // Встановлення текстового вмісту для вкладки "Info"
        Label infoLabel = new Label("Курсова робота з ІПЗ\nВиконав студент 2-го курсу з групи 5\nБурих Тимур");
        infoLabel.setStyle("-fx-font-size: 16px;"); // Задання розміру шрифту
        infoLabel.setPadding(new Insets(20)); // Задання відступу
        return infoLabel;
    }

    private Node createMenuContent() {
        // Створення панелі з кнопками
        VBox menuPane = new VBox(10);
        menuPane.setPadding(new Insets(10));

        // Додавання кнопок та результуючого поля
        menuPane.getChildren().addAll(checkButton, replaceButton, addButton, resultTextArea);

        // Налаштування обробників подій кнопок
        addButton.setOnAction(event -> createAddFireExtinguisherWindow());
        checkButton.setOnAction(event -> checkExpirationDate());
        replaceButton.setOnAction(event -> replaceExpiredFireExtinguishers());

        return menuPane;
    }

    private BorderPane createListContent() {

        // Створення таблиці
        tableView = new TableView<>();
        tableView.setItems(FXCollections.observableArrayList(fireExtinguisherService.getAll()));

        // Створення стовпців
        TableColumn<FireExtinguisherData, String> locationColumn = new TableColumn<>("Місцезнаходження");
        locationColumn.setCellValueFactory(new PropertyValueFactory<FireExtinguisherData, String>("location"));
        locationColumn.setPrefWidth(200);

        TableColumn<FireExtinguisherData, String> expirationDateColumn = new TableColumn<>("Термін придатності");
        expirationDateColumn.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
        expirationDateColumn.setPrefWidth(150);

        // Створення стовпця для кнопки видалення
        TableColumn<FireExtinguisherData, Void> deleteColumn = new TableColumn<>("Видалити");
        deleteColumn.setPrefWidth(150);
        deleteColumn.setCellFactory(getDeleteFireExtinguisherCallback());

        tableView.getColumns().addAll(locationColumn, expirationDateColumn, deleteColumn);

        // Додавання таблиці до панелі
        BorderPane listPane = new BorderPane();
        listPane.setCenter(tableView);

        return listPane;
    }

    private Callback<TableColumn<FireExtinguisherData, Void>, TableCell<FireExtinguisherData, Void>> getDeleteFireExtinguisherCallback() {
        Callback<TableColumn<FireExtinguisherData, Void>, TableCell<FireExtinguisherData, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<FireExtinguisherData, Void> call(final TableColumn<FireExtinguisherData, Void> param) {
                final TableCell<FireExtinguisherData, Void> cell = new TableCell<>() {
                    private final Button deleteButton = new Button("Видалити");

                    {
                        deleteButton.setOnAction((event) -> deleteFireExtinguisherAction());
                    }

                    private void deleteFireExtinguisherAction() {
                        FireExtinguisherData extinguisher = getTableView().getItems().get(getIndex());
                        try {
                            List<FireExtinguisherData> updatedList = fireExtinguisherService.delete(extinguisher); // Видалення вогнегасника з бази даних
                            tableView.setItems(FXCollections.observableArrayList(updatedList)); // Оновлення таблиці
                            tableView.refresh();
                        } catch (DbException e) {
                            e.printStackTrace();
                            showErrorAlert("Помилка", "Помилка при видаленні вогнегасника з бази даних.");
                        }
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
                return cell;
            }
        };
        return cellFactory;
    }

    private void showErrorAlert(String title, String description) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(description);
        alert.showAndWait();
    }

    private void checkExpirationDate() {
        // Оновлення тексту у TextArea
        List<String> expiredFireExtinguishers =  fireExtinguisherService.getExpiredFireExtinguishersName();
        if (!expiredFireExtinguishers.isEmpty()) {
            resultTextArea.setText("Прострочені вогнегасники: \n" + String.join("\n", expiredFireExtinguishers));
        } else {
            resultTextArea.setText("Прострочених вогнегасників немає.");
        }
    }

    private void replaceExpiredFireExtinguishers() {
        // Оновлення таблиці
        if (tableView != null) {
            // Оновлення тексту у TextArea
            resultTextArea.setText("Вогнегасники замінені.");
            tableView.setItems(FXCollections.observableArrayList(fireExtinguisherService.replaceExpiredFireExtinguisher()));
        }
    }

    private void createAddFireExtinguisherWindow() {
        Stage stage = new Stage();
        stage.setTitle("Додати вогнегасник");

        // Створення полів для введення даних
        TextField locationField = new TextField();
        DatePicker expirationDatePicker = new DatePicker();

        // Створення кнопки "Ок"
        Button addButton = new Button("Додати");
        addButton.setOnAction(event -> createNewFireExtinguisherAction(stage, locationField, expirationDatePicker));

        // Створення макету та встановлення його для вікна
        VBox layout = new VBox(10);
        layout.getChildren().addAll(
                new Label("Місцезнаходження:"),
                locationField,
                new Label("Термін придатності:"),
                expirationDatePicker,
                addButton
        );
        layout.setPadding(new Insets(10));
        stage.setScene(new Scene(layout));
        stage.show();
    }

    private void createNewFireExtinguisherAction(Stage stage, TextField locationField, DatePicker expirationDatePicker) {
        // Отримання даних з полів
        String location = locationField.getText();
        LocalDate expirationDate = expirationDatePicker.getValue();

        // Валідація введених даних
        if (location.isEmpty() || expirationDate == null) {
            // Повідомлення про помилку
            showErrorAlert("Помилка", "Будь ласка, заповніть всі поля.");
        } else {
            // Створення нового вогнегасника та додавання його до бази даних
            FireExtinguisherData newExtinguisher = new FireExtinguisherData(location, expirationDate);
            try {
                fireExtinguisherService.insert(newExtinguisher);

                // Оновлення таблиці
                tableView.setItems(FXCollections.observableArrayList(fireExtinguisherService.getAll()));
                tableView.refresh();

                // Закриття вікна
                stage.close();
            } catch (DbException e) {
                // Обробка помилки бази даних
                e.printStackTrace();
                showErrorAlert("Помилка бази даних", "Помилка при додаванні вогнегасника.");
            }
        }
    }
}