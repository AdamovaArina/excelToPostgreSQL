package ui;

import beans.Table;
import db.ConnectorToPostgreSQL;
import db.ConverterToPostgreSQL;
import excel.ConverterFromExcel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main extends Application{
    private static final int MAX_CANVAS_HEIGHT = 6000;
    private static final int NUMBER_COLUMN_WIDTH = 2300;
    private static final TreeSet<Integer> colNums = new TreeSet<>();
    private static final TreeSet<Integer> rowNums = new TreeSet<>();
    private static final TreeSet<Integer> colNames = new TreeSet<>();
    private static ArrayList<String> colTitles = new ArrayList<>();
    private static ProxyTable pt;
    private static Sheet currentSheet;
    private static int currentPage = 1;
    private static Canvas canvas;
    private static ScrollPane sp;
    private static Double x; //текущее положение мыши
    private static Double y;
    private static final ProxyTable columnNamesFirstLine = new ProxyTable();
    private static final ProxyTable columnNamesFirstLineToDelete = new ProxyTable();
    private static Logger LOGGER;
    static{
        try(FileInputStream ins = new FileInputStream("C:\\Users\\Арина\\IdeaProjects\\excelToPostgreSQL\\uiModule\\src\\main\\resources\\logging.properties")){
            LogManager.getLogManager().readConfiguration(ins);
            LOGGER = Logger.getLogger(Main.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Приложение для импорта данных");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(600);

        canvas = new Canvas(600, 400);

        //Основной канвас + названия столбцов
        VBox canvases = new VBox();
        canvases.getChildren().add(canvas);

        sp = new ScrollPane(canvases);
        sp.setMinViewportHeight(400);
        sp.setMinViewportWidth(750);
        sp.setMaxWidth(1600);

        VBox leftBox = new VBox();
        leftBox.setSpacing(10);
        leftBox.getChildren().add(sp);

        HBox menuBox = new HBox();
        menuBox.setSpacing(10);
        TextField sheetNumber = new TextField();
        sheetNumber.setMaxWidth(25);
        sheetNumber.setEditable(false);

        MenuButton menu = new MenuButton("Лист");
        menu.setMinWidth(100);
        menuBox.getChildren().addAll(menu, sheetNumber);

        VBox loadButton = Buttons.createLoadButton(menu, leftBox, sheetNumber);
        Button okButton = Buttons.createOkButton(canvases);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(loadButton, menuBox, okButton);
        vbox.setSpacing(20);

        HBox hbox = new HBox();
        hbox.getChildren().addAll(leftBox, vbox);
        hbox.setSpacing(20);

        Scene primaryScene = new Scene(hbox);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    private static void createContextMenu(Canvas canvas){
        ContextMenu cm = new ContextMenu();
        MenuItem i1 = new MenuItem("Выбрать все");
        i1.setOnAction(actionEvent -> selectAll());
        MenuItem i2 = new MenuItem("Отменить все");
        i2.setOnAction(actionEvent -> cancelAll());
        MenuItem i3 = new MenuItem("Выбрать строку заголовков");
        i3.setOnAction(actionEvent -> chooseColumnNames());
        MenuItem i4 = new MenuItem("Отменить строку заголовков");
        i4.setOnAction(actionEvent -> cancelChooseColumnNames());
        cm.getItems().addAll(i1, i2, i3, i4);
        canvas.setOnContextMenuRequested(contextMenuEvent -> {
            cm.show(canvas, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
            x = contextMenuEvent.getX();
            y = contextMenuEvent.getY();
        });
    }

    public static void fillSheetChooser(MenuButton menu, File file, VBox parent, TextField sheetNumber) throws IOException {
        colNums.clear();
        rowNums.clear();
        colNames.clear();
        currentSheet = null;

        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');
        String ext = i > 0 ? fileName.substring(i + 1) : "";
        Workbook book;

        if(ext.equals("xlsx")){
            book = new XSSFWorkbook(new FileInputStream(file));
        } else if(ext.equals("xls")){
            book = new HSSFWorkbook(new FileInputStream(file));
        } else{
            throw new IOException("Некорректное расширение файла");
        }
        MenuItem buffer;
        for(int j = 1; j <= book.getNumberOfSheets(); j++){
            buffer = new MenuItem(j + " (" + book.getSheetAt(j - 1).getSheetName() + ")");
            int num = j;
            int finalJ = j;
            MenuItem finalBuffer = buffer;
            buffer.setOnAction(actionEvent -> {
                menu.setText(finalBuffer.getText());
                currentSheet = book.getSheetAt(num - 1);
                showFilePreview(parent, finalJ, sheetNumber);
            });
            menu.getItems().add(buffer);
        }
        if(book.getNumberOfSheets() > 0){
            currentSheet = book.getSheetAt(0);
            menu.setText(currentSheet.getSheetName());
            showFilePreview(parent,1, sheetNumber);
        } else{
            canvas = new Canvas(600, 400);
            sp = new ScrollPane(canvas);
        }
    }

    public static MenuButton createPageChooser(){
        MenuButton menu = new MenuButton("Раздел страницы");
        MenuItem buffer;
        for(int j = 1; j <= pt.getCells().get(pt.getCells().size() - 1).getPage(); j++){
            buffer = new MenuItem(j + "");
            int finalJ = j;
            buffer.setOnAction(actionEvent -> {
                currentPage = finalJ;
                System.out.println(currentPage);
                createCanvas(currentPage);
            });
            menu.getItems().add(buffer);
        }
        return menu;
    }

    private static void showFilePreview(VBox parent, int j, TextField sheetNumber){
        int rowLast = currentSheet.getLastRowNum();
        int columnLast = getLastColNum(currentSheet);
        sheetNumber.setText(j + "");

        pt = ProxyTableConverter.createProxyTable(currentSheet, rowLast, columnLast);
        numeratePages();
        createCanvas(currentPage);
        pt.fillRowNums(rowNums);
        sp.setContent(canvas);
        parent.getChildren().clear();
        parent.getChildren().addAll(sp, createPageChooser());
    }

    private static int getLastColNum(Sheet sheet) {
        int columnLast = 0;
        int rowLast = sheet.getLastRowNum();
        for (int i = 0; i <= rowLast; ++i) {
            Row row = sheet.getRow(i);
            if (row != null) {
                if (row.getLastCellNum() - 1 > columnLast) {
                    columnLast = row.getLastCellNum() - 1;
                }
            }
        }
        return columnLast;
    }

    public static void createCanvas(int page){
        int height = 0;
        int width = 0;
        Integer first = null;
        Integer last = null;
        Integer firstRow = null;
        for(int i = 0; i < pt.getCells().size(); i++){
            if(pt.getCells().get(i).getPage() == page){
                if(firstRow == null){
                    firstRow = pt.getCells().get(i).getY();
                }
                if(first == null){
                    first = i;
                }
                if(last == null && (i == pt.getCells().size() - 1) || pt.getCells().get(i + 1).getPage() != page){
                    last = i;
                }
                if(pt.getCells().get(i).getX() == 0){
                    height += pt.getCells().get(i).getHeight() * pt.getCells().get(i).getHeightCount();
                }
                if(pt.getCells().get(i).getY() == firstRow){
                    width += pt.getCells().get(i).getWidth() * pt.getCells().get(i).getWidthCount();
                }
            } else if(pt.getCells().get(i).getPage() > page){
                break;
            }
        }
        canvas = new Canvas(((width + NUMBER_COLUMN_WIDTH) * 2.0 / 72) + 5, (height * 6.0 / 72) + 5);
        canvas.setStyle("-fx-background-color:white");
        sp.setContent(canvas);
        int curHeight = 0;
        int curWidth = 0;

        if(first != null && last != null){
            for(int j = first; j <= last; j++){
                if(pt.getCells().get(j).getPage() == page){
                    if(pt.getCells().get(j).getX() == 0){
                        ProxyCell bufferCell = new ProxyCell(0, pt.getCells().get(j + 1).getY(),
                                NUMBER_COLUMN_WIDTH, pt.getCells().get(j + 1).getHeight(), 1, 1,
                                pt.getCells().get(j + 1).getY() + "");
                        bufferCell.setColor(Color.LIGHTGREY);
                        drawCell(curWidth, curHeight, bufferCell);
                        curWidth += (bufferCell.getWidth() * bufferCell.getWidthCount() * 2.0 / 72);
                    }
                    drawCell(curWidth, curHeight, pt.getCells().get(j));
                    if(j + 1 != pt.getCells().size()){
                        if(pt.getCells().get(j + 1).getX() == 0){
                            curWidth = 0;
                            curHeight += (pt.getCells().get(j).getHeight() * pt.getCells().get(j).getHeightCount()
                                    * 6.0 / 72);
                        } else{
                            curWidth += (pt.getCells().get(j).getWidth() * pt.getCells().get(j).getWidthCount()
                                    * 2.0 / 72);
                        }
                    }
                }
            }
        }
        createContextMenu(canvas);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, Main::clickColumns);
    }

    private static void numeratePages(){
        pt.getCells().sort((o1, o2) -> {
            if(o1.getY() == o2.getY()){
                return o1.getX() - o2.getX();
            } else{
                return o1.getY() - o2.getY();
            }
        });
        int page = 1;
        int curHeight = 0;
        int curRow = 0;
        for(ProxyCell cell : pt.getCells()){
            if(cell.getY() != curRow){
                curRow = cell.getY();
                if((curHeight + (cell.getHeightCount() * cell.getHeight() * 6.0 / 72)) >= MAX_CANVAS_HEIGHT){
                    page++;
                    curHeight = 0;
                } else{
                    curHeight += cell.getHeight() * cell.getHeightCount() * 6.0 / 72;
                }
            }
            cell.setPage(page);
        }
    }

    private static void drawCell(int curWidth, int curHeight, ProxyCell cell){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = cell.getWidth() * cell.getWidthCount() * 2.0 / 72;
        double height = cell.getHeight() * cell.getHeightCount() * 6.0 / 72;
        gc.setStroke(Color.BLACK);
        gc.strokeRect(curWidth, curHeight, width, height);
        gc.setFill(cell.getColor());
        gc.fillRect(curWidth, curHeight, width, height);
        cell.setXPix(curWidth);
        cell.setYPix(curHeight);
        gc.setFill(Color.BLACK);
        if(cell.getValue().length() == 0 || cell.getWidth() * cell.getWidthCount() /
                cell.getValue().length() >= 301){ //если влезло в одну строку
            gc.fillText(cell.getValue(), curWidth + 3, curHeight + (height / 2) + 3, width - 6);
        } else{
            writeText(curWidth, curHeight, width, height, cell);
        }
    }

    private static void writeText(int curWidth, int curHeight, double width, double height, ProxyCell cell){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //делим на строчки
        int symbPerLine = (cell.getWidth() * cell.getWidthCount()) / 301;
        ArrayList<String> partsToPrint = new ArrayList<>();
        String[] parts = cell.getValue().split(" ");
        StringBuilder curPart = new StringBuilder(parts[0]);
        for(int i = 1; i < parts.length; i++){
            if(curPart.length() + parts[i].length() + 1 > symbPerLine){
                partsToPrint.add(curPart.toString());
                curPart = new StringBuilder(parts[i]);
            }else{
                curPart.append(" ").append(parts[i]);
            }
        }
        if(!curPart.toString().equals("")){
            partsToPrint.add(curPart.toString());
        }
        //пишем строчки в ячейку
        double padding = height / (partsToPrint.size() + 1);
        for(int j = 0; j < partsToPrint.size(); j++){
            gc.fillText(partsToPrint.get(j), curWidth + 3, curHeight + (padding * (j + 1)) + 3, width - 6);
        }
    }

    private static void chooseColumn(int x){
        colNums.add(x);
        System.out.println(colNums);
        for(ProxyCell cell : pt.getCells()){
            if(cell.getX() == x && rowNums.contains(cell.getY()) && !colNames.contains(cell.getY())){
                cell.setColor(Color.LIGHTGREEN);
                if(cell.getPage() == currentPage){
                    drawCell(cell.getXPix(), cell.getYPix(), cell);
                }
            }
        }
    }

    private static void clickColumns(MouseEvent e){
        if(e.isPrimaryButtonDown()){
            double width;
            double height;
            int x;
            int y;
            for(ProxyCell cell : pt.getCells()){
                width = cell.getWidth() * cell.getWidthCount() * 2.0 / 72;
                height = cell.getHeight() * cell.getHeightCount() * 6.0 / 72;
                if(cell.getXPix() <= e.getX() && (cell.getXPix() + width) >= e.getX() &&
                        cell.getYPix() <= e.getY() && (cell.getYPix() + height) >= e.getY() &&
                        currentPage == cell.getPage() && rowNums.contains(cell.getY())){
                    y = cell.getY();
                    x = cell.getX();
                    if(colNames.contains(y) || rowNums.contains(y)){
                        if(colNums.contains(x)){
                            cancelChooseColumn(x);
                        } else{
                            chooseColumn(x);
                        }
                    }
                    break;
                }
            }
        }
    }

    private static void chooseColumnNames(){
        double width;
        double height;
        int number = -1;
        for(ProxyCell cell : pt.getCells()){
            width = cell.getWidth() * cell.getWidthCount() * 2.0 / 72;
            height = cell.getHeight() * cell.getHeightCount() * 6.0 / 72;
            if(cell.getXPix() <= x && (cell.getXPix() + width) >= x &&
                    cell.getYPix() <= y && (cell.getYPix() + height) >= y &&
                    currentPage == cell.getPage()){
                number = cell.getY();
                break;
            }
        }
        System.out.println("Номер строки заголовоков: " + number);
        boolean add = false;
        int cntr = 0;
        if(number >= 0){
            for(ProxyCell cell : pt.getCells()){
                if(cell.getY() == number){
                    columnNamesFirstLine.addCell(cell);
                }
                if(cell.getY() > number){
                    break;
                }
            }
            for(int i = 0; i < pt.getCells().size(); i++){
                if(!add && pt.getCells().get(i).equalsValue(columnNamesFirstLine.getCells().get(0))){
                    add = true;
                    cntr = 1;
                }
                if(add){
                    if(cntr == columnNamesFirstLine.getCells().size()){
                        colNames.add(pt.getCells().get(i - 1).getY());
                        cntr = 0;
                        add = false;
                    } else if(pt.getCells().get(i).equalsValue(columnNamesFirstLine.getCells().get(cntr))){
                        cntr += 1;
                    }
                }
            }
        }
        //раскрашивание
        for(ProxyCell cell : pt.getCells()){
            if(colNames.contains(cell.getY())){
                cell.setColor(Color.LIGHTBLUE);
                if(cell.getPage() == currentPage){
                    drawCell(cell.getXPix(), cell.getYPix(), cell);
                }
            }
        }
        columnNamesFirstLine.getCells().clear();
    }

    private static void cancelChooseColumnNames(){
        double width;
        double height;
        int number = -1;
        for(ProxyCell cell : pt.getCells()){
            width = cell.getWidth() * cell.getWidthCount() * 2.0 / 72;
            height = cell.getHeight() * cell.getHeightCount() * 6.0 / 72;
            if(cell.getXPix() <= x && (cell.getXPix() + width) >= x &&
                    cell.getYPix() <= y && (cell.getYPix() + height) >= y &&
                    currentPage == cell.getPage()){
                number = cell.getY();
                break;
            }
        }
        boolean add = false;
        int cntr = 0;
        if(number >= 0){
            for(ProxyCell cell : pt.getCells()){
                if(cell.getY() == number){
                    columnNamesFirstLineToDelete.addCell(cell);
                }
                if(cell.getY() > number){
                    break;
                }
            }
            for(int i = 0; i < pt.getCells().size(); i++){
                if(!add && pt.getCells().get(i).equals(columnNamesFirstLineToDelete.getCells().get(0))){
                    add = true;
                    cntr = 1;
                }
                if(add){
                    if(cntr == columnNamesFirstLineToDelete.getCells().size()){
                        colNames.remove(pt.getCells().get(i - 1).getY());
                        cntr = 0;
                        add = false;
                    } else if(pt.getCells().get(i).equals(columnNamesFirstLineToDelete.getCells().get(cntr))){
                        cntr += 1;
                    }
                }
            }
        }
        //раскрашивание
        for(ProxyCell cell : pt.getCells()){
            if(!colNames.contains(cell.getY()) && colNums.contains(cell.getX()) && rowNums.contains(cell.getY())){
                cell.setColor(Color.LIGHTGREEN);
            } else if(!colNames.contains(cell.getY())){
                cell.setColor(Color.WHITE);
            }
            if(cell.getPage() == currentPage){
                drawCell(cell.getXPix(), cell.getYPix(), cell);
            }
        }
        columnNamesFirstLineToDelete.getCells().clear();
    }

    private static void cancelChooseColumn(int x){
        colNums.remove(x);
        System.out.println(colNums);
        for(ProxyCell cell : pt.getCells()){
            if(cell.getX() == x && rowNums.contains(cell.getY()) && cell.getColor() == Color.LIGHTGREEN){
                cell.setColor(Color.WHITE);
                if(cell.getPage() == currentPage){
                    drawCell(cell.getXPix(), cell.getYPix(), cell);
                }
            }
        }
    }

    private static void cancelAll(){
        pt.paintAll(Color.WHITE, rowNums);
        for(ProxyCell cell : pt.getCells()){
            if(cell.getPage() == currentPage){
                drawCell(cell.getXPix(), cell.getYPix(), cell);
            }
            if(cell.getPage() > currentPage){
                break;
            }
        }
        colNums.clear();
    }

    private static void clearAll(){
        pt.paintAll(Color.WHITE);
        for(ProxyCell cell : pt.getCells()){
            if(cell.getPage() == currentPage){
                drawCell(cell.getXPix(), cell.getYPix(), cell);
            }
            if(cell.getPage() > currentPage){
                break;
            }
        }
        colNums.clear();
    }

    private static void selectAll(){
        pt.paintAll(Color.LIGHTGREEN, rowNums);
        for(int i = 0; i < pt.getCells().size(); i++){
            if(pt.getCells().get(i).getPage() == currentPage){
                drawCell(pt.getCells().get(i).getXPix(), pt.getCells().get(i).getYPix(), pt.getCells().get(i));
                if(i + 1 == pt.getCells().size() || pt.getCells().get(i).getX() != pt.getCells().get(i + 1).getX()){
                    colNums.add(pt.getCells().get(i).getX());
                }
            }
            if(pt.getCells().get(i).getPage() > currentPage){
                break;
            }
        }
    }

    public static Canvas drawColumnNames(){
        int height = pt.getCells().get(1).getHeight() * pt.getCells().get(1).getHeightCount();
        int width = 0;
        for(ProxyCell cell : pt.getCells()){
            if(cell.getY() == 0){
                width += cell.getWidth() * cell.getWidthCount();
            } else{
                break;
            }
        }
        Canvas canvas1 = new Canvas(((width + NUMBER_COLUMN_WIDTH) * 2.0 / 72) + 5, (height * 6.0 / 72) + 5);
        GraphicsContext gc = canvas1.getGraphicsContext2D();
        double curWidth = NUMBER_COLUMN_WIDTH * 2.0 / 72;
        ProxyCell cell;
        for(int i = 0; i < pt.getCells().size(); i++){
            cell = pt.getCells().get(i);
            if(cell.getY() == 0){
                gc.strokeRect(curWidth, 0, (cell.getWidth() * cell.getWidthCount() * 2.0 / 72) - 1,
                        cell.getHeight() * cell.getHeightCount() * 6.0 / 72);
                if(colNums.contains(cell.getX())){
                    gc.fillText(colTitles.get(i), curWidth + 3,
                            (cell.getHeight() * cell.getHeightCount() * 6.0 / 144) + 3);
                }
                curWidth += (cell.getWidth() * cell.getWidthCount() * 2.0 / 72) - 1;
            } else{
                break;
            }
        }
        return canvas1;
    }

    public static void sendTableToDB(VBox parent){
        if(!rowNums.isEmpty() && !colNums.isEmpty()){ //если выбраны все нужные значения
            try{
                System.out.println(rowNums);
                for (Integer num : colNames){
                    rowNums.remove(num);
                }
                System.out.println(rowNums);
                Table buffer = ConverterFromExcel.readTableFromExcel(currentSheet, rowNums, colNums)
                        .typeFix().toTempTable();
                Connection connection = ConnectorToPostgreSQL.getDBConnection();
                //todo:
                colTitles = ConverterToPostgreSQL.findColNames(buffer, pt.maxColNum());
                ConverterToPostgreSQL.executeCommand(connection, ConverterToPostgreSQL.insertIntoTemporaryTable(buffer));

                parent.getChildren().clear();
                Canvas titles = drawColumnNames();
                parent.getChildren().addAll(titles, canvas);
                sp.setContent(parent);
                clearAll();

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Сообщение");
                a.setHeaderText("Успешно");
                a.setContentText("Данные добавлены в таблицу");
                a.show();
            } catch (Exception e){
                clearAll();
                e.printStackTrace();
                LOGGER.log(Level.SEVERE, "Exception: ", e);
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Ошибка");
                a.setHeaderText("Некорректные данные");
                a.setContentText(e.getMessage());
                a.show();
            } finally {
                colNums.clear();
            }
        } else{
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Предупреждение");
            a.setHeaderText("Не выбраны добавляемые столбцы");
            a.setContentText("Кликнете левой кнопкой мыши по выбранным столбцам");
            a.show();
        }
    }
}
