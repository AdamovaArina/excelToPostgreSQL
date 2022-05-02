package ui;

import beans.Table;
import db.ConnectorToPostgreSQL;
import db.ConverterToPostgreSQL;
import excel.ConverterFromExcel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    private static Stage previewStage;
    private static Label data1;
    private static Label data2;
    private static Label header1;

    private static int fromCol;
    private static int toCol;
    private static int fromRow;
    private static int toRow;
    private static int nameRow;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello world Application");
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

    private static void showFilePreview(Stage parent, File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        HSSFWorkbook book = new HSSFWorkbook(is);
        HSSFSheet sheet = book.getSheetAt(0);

        int rowLast = sheet.getLastRowNum();
        int columnLast = getLastColNum(sheet);

        VBox vb = new VBox();
        GridPane gp = createGridPane(sheet, rowLast, columnLast);
        HBox hb = new HBox();
        hb.getChildren().addAll(createOkButton(file), createCancelButton());
        hb.setPadding(new Insets(10));
        hb.setSpacing(10);
        vb.getChildren().addAll(gp, hb);

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

    private static Label createLabel(String text, double width, double height, int col, int row){
        var l = new Label(text);
        l.setMinWidth(width * 2.0 / 72);
        l.setMinHeight(height * 6.0 / 72);
        l.setWrapText(true);
        l.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-background-color: white;");
        createContextMenu(l, col, row);
        return l;
    }

    private static void createContextMenu(Label l, int col, int row){
        ContextMenu cm = new ContextMenu();
        MenuItem d1 = new MenuItem("Установить первую ячейку диапазона данных");
        d1.setOnAction(actionEvent -> {
            if (data1 != null){
                data1.setStyle(l.getStyle() + "-fx-background-color: white;");
            }
            l.setStyle(l.getStyle() + "-fx-background-color: green;");
            fromCol = col;
            fromRow = row;
            data1 = l;
        });
        MenuItem d2 = new MenuItem("Установить вторую ячейку диапазона данных");
        d2.setOnAction(actionEvent -> {
            if (data2 != null) {
                data2.setStyle(l.getStyle() + "-fx-background-color: white;");
            }
            l.setStyle(l.getStyle() + "-fx-background-color: darkgreen;");
            toCol = col;
            toRow = row;
            data2 = l;
        });
        MenuItem h1 = new MenuItem("Установить любую ячейку диапазона заголовков");
        h1.setOnAction(actionEvent -> {
            if(header1 != null){
                header1.setStyle(l.getStyle() + "-fx-background-color: white;");
            }
            l.setStyle(l.getStyle() + "-fx-background-color: blue;");
            nameRow = row;
            header1 = l;
        });
        cm.getItems().addAll(d1, d2, h1);
        l.setContextMenu(cm);
    }

    private static Label createLoadLabel(){
        Label loadLabel = new Label("Загрузите файл");
        loadLabel.setAlignment(Pos.CENTER);
        loadLabel.setStyle("-fx-font-size:24");
        return loadLabel;
    }

    private static Button createLoadButton(Stage primaryStage){
        Button btn = new Button("Загрузить");
        btn.setStyle("-fx-font-size:16");
        btn.setMinWidth(60);
        btn.setMinHeight(20);
        btn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберете файл");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XLS", "*.xls"));
            File file = fileChooser.showOpenDialog(null);
            if(file != null){ //если файл открылся
                try {
                    showFilePreview(primaryStage, file);
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert a = new Alert(Alert.AlertType.WARNING);
                    a.setTitle("Ошибка");
                    a.setHeaderText("Файл недоступен, попробуйте еще раз");
                    a.setContentText(e.getMessage());
                    a.show();
                }
            }
            else {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Предупреждение");
                a.setHeaderText("Файл не выбран");
                a.setContentText("Выберете файл");
                a.show();
            }
        });
        return btn;
    }

    private static Button createOkButton(File file){
        Button btn = new Button("Ок");
        btn.setStyle("-fx-font-size:16");
        btn.setMinWidth(60);
        btn.setMinHeight(20);
        btn.setOnAction(event -> {
            if(data1 != null && data2 != null && header1 != null){ //если выбраны все нужные значения
                try {
                    Table buffer;
                    buffer = ConverterFromExcel.readTableFromExcel(new FileInputStream(file), 0, nameRow,
                            fromCol, toCol, fromRow, toRow, "table111");
                    ConverterToPostgreSQL.createAndFillTable(ConnectorToPostgreSQL.getDBConnection(), buffer);
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Сообщение");
                    a.setHeaderText("Успешно");
                    a.setContentText("Таблица добавлена в базу данных");
                    a.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Ошибка");
                    a.setHeaderText("Выбраны некорректные ячейки");
                    a.setContentText(e.getMessage());
                    a.show();
                }
            }
            else {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Предупреждение");
                a.setHeaderText("Не выбраны ячейки диапазона");
                a.setContentText("Клинете правой кнопкой мыши по выбранным ячейкам");
                a.show();
            }
        });
        return btn;
    }

    private static Button createCancelButton(){
        Button btn = new Button("Отмена");
        btn.setStyle("-fx-font-size:16");
        btn.setMinWidth(60);
        btn.setMinHeight(20);
        btn.setOnAction(event -> {
            previewStage.close();
        });
        return btn;
    }

    private static int getLastColNum(HSSFSheet sheet){
        int columnLast = 0;
        int rowLast = sheet.getLastRowNum();
        for(int i = 0; i <= rowLast; ++i){
            var row = sheet.getRow(i);
            if(row != null){
                if (row.getLastCellNum() > columnLast){
                    columnLast = row.getLastCellNum();
                }
            }
        }
        return columnLast;
    }

    private static GridPane createGridPane(HSSFSheet sheet, int rowLast, int columnLast){
        GridPane gp = new GridPane();
        gp.setStyle("-fx-background-color: #99FF99");
        //создаем структуру gridpanel аналогичную excel таблице
        for (int i = 0; i <= rowLast; ++i) {
            var row = sheet.getRow(i);
            for(int j = 0; j <= columnLast; ++j) {
                //когда ячейка или строка не определена
                if(row == null || row.getCell(j) == null) {
                    int height;
                    if(row == null){
                        height = sheet.getDefaultRowHeight();
                    }
                    else {
                        height = row.getHeight();
                    }
                    var l = createLabel("", sheet.getColumnWidth(j), height, j, i);
                    gp.add(l, j, i);
                }
                //когда определена
                else {
                    var c = row.getCell(j);
                    CellRangeAddress r = null;
                    //проверяем, не принадлежит ли ячейка объединению ячеек
                    for (int k = 0; k < sheet.getMergedRegions().size(); ++k){
                        if (sheet.getMergedRegions().get(k).isInRange(c)) {
                            r = sheet.getMergedRegions().get(k);
                        }
                    }
                    //если не принадлежит - добавляем
                    if (r == null) {
                        var l = createLabel(c.toString(), sheet.getColumnWidth(j), row.getHeight(), j, i);
                        gp.add(l, j, i);
                    }
                    else {
                        if (r.getFirstRow() == i && r.getFirstColumn() == j) {
                            int width = 0;
                            int height = 0;
                            for(int k = r.getFirstColumn(); k <= r.getLastColumn(); ++k) {
                                width += sheet.getColumnWidth(k);
                            }
                            for (int k = r.getFirstRow(); k <= r.getLastRow(); ++k) {
                                var tmp = sheet.getRow(k);
                                if (tmp == null) {
                                    height += sheet.getDefaultRowHeight();
                                } else {
                                    height += tmp.getHeight();
                                }
                            }
                            var l = createLabel(c.toString(), width, height, j, i);
                            gp.add(l, j, i, r.getLastColumn() - j + 1, r.getLastRow() - i + 1);
                        }
                    }

                }
            }
        }

        //установка ширины столбцов
        for(int i = 0; i <= columnLast; i++) {
            gp.getColumnConstraints().add(new ColumnConstraints(2.0 * sheet.getColumnWidth(i) / 72));
        }
        //и строк
        for(int j = 0; j <= rowLast; j++) {
            if(sheet.getRow(j) != null) {
                gp.getRowConstraints().add(new RowConstraints(6.0 * sheet.getRow(j).getHeight() / 72 ));
            }
            else {
                gp.getRowConstraints().add(new RowConstraints(6.0 * sheet.getDefaultRowHeight() / 72 ));
            }
        }
        //для отладки можно вывести сетку
        //gp.setGridLinesVisible(true);
        return gp;
    }

}
