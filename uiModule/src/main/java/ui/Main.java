package ui;

import beans.Table;
import db.ConnectorToPostgreSQL;
import db.ConverterToPostgreSQL;
import excel.ConverterFromExcel;
import excel.XlsConverterFromExcel;
import excel.XlsxConverterFromExcel;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main extends Application{
    private static Stage previewStage;
    private static Label data1;
    private static Label data2;
    private static Label header1;

    private static int fromCol;
    private static int toCol;
    private static int fromRow;
    private static int toRow;
    private static int nameRow;
    private static String tableName;
    //private static int sheetNumber;
    private static Sheet currentSheet;

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Приложение для импорта данных");
        primaryStage.setWidth(450);
        primaryStage.setHeight(300);

        Label loadLabel = createLoadLabel();

        Button loadButton = createLoadButton(primaryStage);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(loadLabel, loadButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);

        Scene primaryScene = new Scene(vbox);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    private static void showFilePreview(Stage parent) throws IOException {

        //HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(file));
        //HSSFSheet sheet = book.getSheetAt(sheetNumber);

        //Workbook book = new Workbook(new FileInputStream(file));
        //Sheet sheet = book.getSheetAt(sheetNumber);

        int rowLast = currentSheet.getLastRowNum();
        int columnLast = getLastColNum(currentSheet);

        VBox vb = new VBox();

        long bufferTime = System.nanoTime(); // для тестирования
        ProxyTable pt = ProxyTableConverter.createProxyTable(currentSheet, rowLast, columnLast);
        System.out.println("Считывание данных из файла");
        System.out.println((System.nanoTime() - bufferTime)/1000000 + " мс"); // для тестирования

        bufferTime = System.nanoTime(); // для тестирования
        GridPane gp = createGridPane(pt, currentSheet);
        System.out.println("Создание GridPane");
        System.out.println((System.nanoTime() - bufferTime)/1000000 + " мс"); // для тестирования

        HBox hb = new HBox();
        hb.getChildren().addAll(createOkButton(), createCancelButton());
        hb.setSpacing(10);
        vb.getChildren().addAll(gp, createTableNameInput(), hb);
        vb.setSpacing(10);

        ScrollPane sp = new ScrollPane(vb);
        sp.setPrefViewportHeight(600);
        sp.setPrefViewportWidth(800);

        Scene scene = new Scene(sp);
        previewStage = new Stage();
        previewStage.initModality(Modality.WINDOW_MODAL);
        previewStage.initOwner(parent);
        previewStage.setTitle("Выбор диапазона");

        previewStage.setScene(scene);
        previewStage.show();


    }

    /*public static GridPane createGridPane(ProxyTable pt, HSSFSheet sheet){
        GridPane gp = new GridPane();
        gp.setStyle("-fx-background-color: white");
        for (int idx = 0; idx < pt.getCells().size(); idx++){
            var current = pt.getCells().get(idx);
            var l = createLabel(current.getValue(), current.getWidth(), current.getHeight(),
                    current.getX(), current.getY());
            gp.add(l, current.getX(), current.getY(), current.getWidthCount(), current.getHeightCount());
        }

        int rowLast = sheet.getLastRowNum();
        int columnLast = getLastColNum(sheet);
        //установка ширины столбцов
        for (int i = 0; i <= columnLast; i++) {
            gp.getColumnConstraints().add(new ColumnConstraints(2.0 * sheet.getColumnWidth(i) / 72));
        }
        //и строк
        for (int j = 0; j <= rowLast; j++) {
            if (sheet.getRow(j) != null) {
                gp.getRowConstraints().add(new RowConstraints(6.0 * sheet.getRow(j).getHeight() / 72));
            } else {
                gp.getRowConstraints().add(new RowConstraints(6.0 * sheet.getDefaultRowHeight() / 72));
            }
        }

        return gp;
    }*/

    public static GridPane createGridPane(ProxyTable pt, Sheet sheet){
        GridPane gp = new GridPane();
        gp.setStyle("-fx-background-color: white");
        for (int idx = 0; idx < pt.getCells().size(); idx++){
            var current = pt.getCells().get(idx);
            var l = createLabel(current.getValue(), current.getWidth(), current.getHeight(),
                    current.getX(), current.getY());
            gp.add(l, current.getX(), current.getY(), current.getWidthCount(), current.getHeightCount());
        }

        int rowLast = sheet.getLastRowNum();
        int columnLast = getLastColNum(sheet);
        //установка ширины столбцов
        for (int i = 0; i <= columnLast; i++) {
            gp.getColumnConstraints().add(new ColumnConstraints(2.0 * sheet.getColumnWidth(i) / 72));
        }
        //и строк
        for (int j = 0; j <= rowLast; j++) {
            if (sheet.getRow(j) != null) {
                gp.getRowConstraints().add(new RowConstraints(6.0 * sheet.getRow(j).getHeight() / 72));
            } else {
                gp.getRowConstraints().add(new RowConstraints(6.0 * sheet.getDefaultRowHeight() / 72));
            }
        }

        return gp;
    }

    private static Stage createSheetChooser(Stage parent, File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        //HSSFWorkbook book = new HSSFWorkbook(is);

        String name = file.getName();
        int i = name.lastIndexOf('.');
        String ext =  i > 0 ? name.substring(i + 1) : "";
        Workbook book = switch (ext) {
            case "xlsx" -> new XSSFWorkbook(new FileInputStream(file));
            case "xls" -> new HSSFWorkbook(new FileInputStream(file));
            default -> null;
        };

        if (book == null){
            throw new IOException("Некорректное расширение");
        }

        VBox vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        vb.setSpacing(20);

        Scene scene = new Scene(vb);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parent);
        stage.setTitle("Выбор листа");
        stage.setWidth(450);
        stage.setHeight(300);

        stage.setScene(scene);

        ComboBox<Integer> cb = new ComboBox<>();
        ObservableList<Integer> listNames = FXCollections.observableArrayList();
        for (int j = 1; j <= book.getNumberOfSheets(); j++){
            listNames.add(j);
        }
        cb.setItems(listNames);
        cb.setOnAction(event -> currentSheet = book.getSheetAt(cb.getValue() - 1));

        Label label = new Label("Выберите страницу файла");
        label.setStyle("-fx-font-size: 16px");

        HBox hb = new HBox();
        hb.getChildren().addAll(createBackButton(stage), createSheetChooseButton(stage, file));
        hb.setSpacing(40);
        hb.setAlignment(Pos.CENTER);
        vb.getChildren().addAll(label, cb, hb);

        return stage;
    }

    /*private static int getLastColNum(HSSFSheet sheet) {
        int columnLast = 0;
        int rowLast = sheet.getLastRowNum();
        for (int i = 0; i <= rowLast; ++i) {
            var row = sheet.getRow(i);
            if (row != null) {
                if (row.getLastCellNum() > columnLast) {
                    columnLast = row.getLastCellNum();
                }
            }
        }
        return columnLast;
    }*/

    private static int getLastColNum(Sheet sheet) {
        int columnLast = 0;
        int rowLast = sheet.getLastRowNum();
        for (int i = 0; i <= rowLast; ++i) {
            var row = sheet.getRow(i);
            if (row != null) {
                if (row.getLastCellNum() > columnLast) {
                    columnLast = row.getLastCellNum();
                }
            }
        }
        return columnLast;
    }

    private static Label createLabel(String text, double width, double height, int col, int row) {
        var l = new Label(text);
        l.setMinWidth(width * 2.0 / 72);
        l.setMinHeight(height * 6.0 / 72);
        l.setWrapText(true);
        l.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-background-color: white;");
        createContextMenu(l, col, row);
        return l;
    }

    private static void createContextMenu(Label l, int col, int row) {
        ContextMenu cm = new ContextMenu();
        MenuItem d1 = new MenuItem("Установить левую верхнюю ячейку диапазона данных");
        d1.setOnAction(actionEvent -> {
            if (data1 != null) {
                data1.setStyle(l.getStyle() + "-fx-background-color: white;");
            }
            l.setStyle(l.getStyle() + "-fx-background-color: #b6d7a8;");
            fromCol = col;
            fromRow = row;
            data1 = l;
        });
        MenuItem d2 = new MenuItem("Установить правую нижнюю ячейку диапазона данных");
        d2.setOnAction(actionEvent -> {
            if (data2 != null) {
                data2.setStyle(l.getStyle() + "-fx-background-color: white;");
            }
            l.setStyle(l.getStyle() + "-fx-background-color: #a7ca99;");
            toCol = col;
            toRow = row;
            data2 = l;
        });
        MenuItem h1 = new MenuItem("Установить любую ячейку диапазона заголовков");
        h1.setOnAction(actionEvent -> {
            if (header1 != null) {
                header1.setStyle(l.getStyle() + "-fx-background-color: white;");
            }
            l.setStyle(l.getStyle() + "-fx-background-color: #91b9df;");
            nameRow = row;
            header1 = l;
        });
        cm.getItems().addAll(d1, d2, h1);
        l.setContextMenu(cm);
    }

    private static Label createLoadLabel() {
        Label loadLabel = new Label("Загрузите файл");
        loadLabel.setAlignment(Pos.CENTER);
        loadLabel.setStyle("-fx-font-size:24");
        return loadLabel;
    }

    private static Button createLoadButton(Stage parent) {
        Button btn = new Button("Загрузить");
        btn.setStyle("-fx-font-size:16");
        btn.setMinWidth(60);
        btn.setMinHeight(20);
        btn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберете файл");
            //fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XLS", "*.xls"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XLSX", "*.xlsx", "XLS", "*.xls"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) { //если файл открылся
                try {
                    createSheetChooser(parent, file).show();
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
        return btn;
    }

    private static Button createOkButton() {
        Button btn = new Button("Ок");
        btn.setStyle("-fx-font-size:16");
        btn.setMinWidth(60);
        btn.setMinHeight(20);
        btn.setOnAction(event -> {
            if (data1 != null && data2 != null && header1 != null) { //если выбраны все нужные значения
                try {
                    if (tableName == null || tableName.equals("")) {
                        throw new IOException("Не введено название таблицы");
                    }
                    long bufferTime = System.nanoTime(); // для тестирования
                    Table buffer = ConverterFromExcel.readTableFromExcel(currentSheet,
                            nameRow, fromCol, toCol, fromRow, toRow, tableName);
                    ConverterToPostgreSQL.createAndFillTable(ConnectorToPostgreSQL.getDBConnection(), buffer);
                    System.out.println("Загрузка в бд");
                    System.out.println((System.nanoTime() - bufferTime)/1000000 + " мс"); // для тестирования
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Сообщение");
                    a.setHeaderText("Успешно");
                    a.setContentText("Таблица добавлена в базу данных");
                    a.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Ошибка");
                    a.setHeaderText("Некорректные данные");
                    a.setContentText(e.getMessage());
                    a.show();
                }
            } else {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Предупреждение");
                a.setHeaderText("Не выбраны ячейки диапазона");
                a.setContentText("Клинете правой кнопкой мыши по выбранным ячейкам");
                a.show();
            }
        });
        return btn;
    }

    private static Button createSheetChooseButton(Stage stage, File file) {
        Button button = new Button("Ок");
        button.setStyle("-fx-font-size:16");
        button.setMinWidth(60);
        button.setMinHeight(20);
        button.setAlignment(Pos.CENTER);
        button.setOnAction(event -> {
            try {
                showFilePreview(stage);
            } catch (IOException e) {
                e.printStackTrace();
                Alert al = new Alert(Alert.AlertType.ERROR);
                al.setTitle("Ошибка");
                al.setHeaderText("Внутренняя ошибка");
                al.setContentText(e.getMessage());
                al.show();
            }
        });
        return button;
    }

    private static Button createCancelButton() {
        Button btn = new Button("Отмена");
        btn.setStyle("-fx-font-size:16");
        btn.setMinWidth(60);
        btn.setMinHeight(20);
        btn.setOnAction(event -> previewStage.close());
        return btn;
    }

    private static Button createBackButton(Stage stage) {
        Button btn = new Button("Назад");
        btn.setStyle("-fx-font-size:16");
        btn.setMinWidth(60);
        btn.setMinHeight(20);
        btn.setOnAction(event -> stage.close());
        return btn;
    }

    private static TextField createTableNameInput() {
        TextField textField = new TextField();
        textField.setMaxWidth(200);
        textField.setMinHeight(20);
        textField.setPromptText("Введите название таблицы");
        textField.textProperty().addListener((observable, oldValue, newValue) -> tableName = newValue);
        return textField;
    }


}
