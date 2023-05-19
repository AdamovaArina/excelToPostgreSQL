package ui;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class Buttons {
    public static VBox createLoadButton(MenuButton menu, VBox parent,TextField sheetNumber) {
        Button btn = new Button("...");
        //btn.setStyle("-fx-font-size:16");
        btn.setMinWidth(15);
        btn.setMinHeight(15);
        TextField text = new TextField();
        text.setEditable(false);
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().addAll(text, btn);
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.getChildren().addAll(new Label("Файл"), hbox);
        btn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберете файл");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XLSX", "*.xlsx",
                    "XLS", "*.xls"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) { //если файл открылся
                try {
                    menu.getItems().clear();
                    Main.fillSheetChooser(menu, file, parent, sheetNumber);
                    text.setText(file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Ошибка");
                    a.setHeaderText("Ошибка работы с файлом");
                    a.setContentText(e.getMessage());
                    a.show();
                }
            } else {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Предупреждение");
                a.setHeaderText("Файл не выбран");
                a.setContentText("Выберете файл");
                a.show();
            }
        });
        return vbox;
    }

    public static Button createOkButton(VBox parent) {
        Button btn = new Button("Загрузка в БД");
        //btn.setStyle("-fx-font-size:16");
        btn.setMinWidth(100);
        btn.setMinHeight(15);
        btn.setOnAction(event -> Main.sendTableToDB(parent));
        return btn;
    }
}
