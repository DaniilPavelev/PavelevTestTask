package com.example.pavelevtesttask;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import static com.example.pavelevtesttask.Helper.addToArrayTelemetricDate;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        //Объявление и инициализация необходимых элементов
        IntegerProperty countPackagesInTable = new SimpleIntegerProperty(10);
        IntegerProperty countPackagesInTextFild = new SimpleIntegerProperty(10);
        countPackagesInTable.bind(countPackagesInTextFild);
        Button startButton = new Button("Start");
        TextField textField = new TextField("10");
        Label label = new Label("Количество пакетов за раз: ");
        BorderPane root = new BorderPane();
        FlowPane topPane = new FlowPane();
        StackPane bottomnPane = new StackPane();
        ObservableList observableList = FXCollections.observableArrayList();
        TableView<TelemetricDate> table = new TableView<TelemetricDate>(observableList);
        Scene scene = new Scene(root, 400, 500);

        //Добавление колонки с номером пакета
        TableColumn<TelemetricDate, Integer> counterPackagesCol = new TableColumn<>("Номер пакета");
        //Добавление колонки с временем в секундах
        TableColumn<TelemetricDate, Double> timeCol = new TableColumn<>("Время");
        //Добавление колонки с полезной информацией(синус угла)
        TableColumn<TelemetricDate, Double> dateCol = new TableColumn<>("Полезная информация");
        //Тут задаются необходимые зависимости, чтобы столбцы могли из объекта класса брать соответствующие данные
        counterPackagesCol.setCellValueFactory(new PropertyValueFactory<>("counterPackages"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        //Добавление заданных ранее столбцов в таблицу
        table.getColumns().addAll(counterPackagesCol, timeCol, dateCol);
        //Handle для заполнения таблицы
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                addToArrayTelemetricDate(observableList, countPackagesInTable.get());
            }
        });
        //Handle для изменения числа выводимых за раз строк таблицы
        textField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                countPackagesInTextFild.set(Integer.valueOf(textField.getText()));
            }
        });
        //Handle для изменения размеров элементов при измении размеров окна
        root.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observableValue, Bounds bounds, Bounds t1) {
                topPane.setMinSize(t1.getWidth(),t1.getHeight()*0.1);
                bottomnPane.setMinSize(t1.getWidth(),t1.getHeight()*0.9);
                counterPackagesCol.setMinWidth(t1.getWidth()*0.333);
                timeCol.setMinWidth(t1.getWidth()*0.333);
                dateCol.setMinWidth(t1.getWidth()*0.333);
                counterPackagesCol.setMaxWidth(t1.getWidth()*0.333);
                timeCol.setMaxWidth(t1.getWidth()*0.333);
                dateCol.setMaxWidth(t1.getWidth()*0.333);
            }
        });
        //Размеры и смещения элементов
        startButton.setMinSize(80,50);
        label.setMinSize(20,20);
        textField.setMinSize(20,25);
        textField.setMaxSize(40,25);
        label.setTranslateX(20);
        textField.setTranslateX(20);
        //Раздилительная строка для удобства
        topPane.setMinSize(root.getWidth(),root.getHeight()*0.2);
        //Раздилительная строка для удобства
        bottomnPane.setMinSize(root.getWidth(),root.getHeight()*0.8);
        counterPackagesCol.setMinWidth(0.333*root.getWidth());
        timeCol.setMinWidth(0.333*root.getWidth());
        dateCol.setMinWidth(0.333*root.getWidth());
        counterPackagesCol.setMaxWidth(0.333*root.getWidth());
        timeCol.setMaxWidth(0.333*root.getWidth());
        dateCol.setMaxWidth(0.333*root.getWidth());
        //Добавление элементов на соответствующие Pane'ы
        topPane.getChildren().addAll(startButton,label, textField);
        bottomnPane.getChildren().add(table);
        //Добавление в корень
        root.setLeft(topPane);
        root.setBottom(bottomnPane);
        stage.setTitle("Test-task");
        //Сцена
        stage.setScene(scene);
        stage.show();
    }
}
