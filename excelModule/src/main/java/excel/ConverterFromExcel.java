package excel;

import beans.Cell;
import beans.CellType;
import beans.Row;
import beans.Table;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConverterFromExcel {
    public static ArrayList<Table> readFromExcel(FileInputStream file) throws IOException {
        HSSFWorkbook myExcelBook = new HSSFWorkbook(file);
        ArrayList<Table> tables = new ArrayList<>();
        ArrayList<Integer> bounds;
        ArrayList<Integer> columnNamesBounds;
        Row columnNames;
        Scanner sc = new Scanner(System.in);
        String answer;
        String tableName;
        Table bufferTable;
        for (int i = 0; i < myExcelBook.getNumberOfSheets(); i++){
            HSSFSheet myExcelSheet = myExcelBook.getSheetAt(i);
            System.out.println("Выберете строку с заголовками столбцов");
            columnNamesBounds = readNameBounds();
            columnNames = readColumnNames(myExcelSheet, columnNamesBounds.get(0), columnNamesBounds.get(1), columnNamesBounds.get(2));
            while (true){
                System.out.println("Выбрать еще одну таблицу на данном листе? (д/н)");
                answer = sc.nextLine();
                if (answer.equals("д")){
                    System.out.println("Введите имя таблицы");
                    tableName = sc.nextLine();
                    bounds = readBounds();
                    bufferTable = readTableFromExcel(myExcelSheet, bounds.get(0), bounds.get(1), bounds.get(2), bounds.get(3));
                    bufferTable.setColumnNames(columnNames);
                    bufferTable.setName(tableName);
                    tables.add(bufferTable);
                } else if (answer.equals("н")){
                    break;
                }
            }

        }
        myExcelBook.close();
        return tables;
    }

    private static ArrayList<Integer> readBounds(){
        ArrayList<Integer> bounds = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        System.out.println("fromRow: ");
        bounds.add(sc.nextInt());
        System.out.println("toRow: ");
        bounds.add(sc.nextInt());
        System.out.println("fromColumn: ");
        bounds.add(sc.nextInt());
        System.out.println("toColumn: ");
        bounds.add(sc.nextInt());
        return bounds;
    }

    private static ArrayList<Integer> readNameBounds(){
        ArrayList<Integer> bounds = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        System.out.println("fromRow: ");
        bounds.add(sc.nextInt());
        System.out.println("fromColumn: ");
        bounds.add(sc.nextInt());
        System.out.println("toColumn: ");
        bounds.add(sc.nextInt());
        return bounds;
    }

    private static Row readColumnNames(HSSFSheet myExcelSheet, int line, int fromColumn, int toColumn){
        Row row = new Row();
        for (int i = fromColumn; i <= toColumn; i++){
            row.getRow().add(convertCell(myExcelSheet.getRow(line).getCell(i)));
        }
        return row;
    }

    private static Table readTableFromExcel(HSSFSheet myExcelSheet, int fromRow, int toRow, int fromColumn, int toColumn){
        Table table = new Table();
        for(int i = fromRow; i <= toRow; i++){
            Row bufferRow = new Row();
            for(int j = fromColumn; j <= toColumn; j++){
                bufferRow.getRow().add(convertCell(myExcelSheet.getRow(i).getCell(j)));
            }
            if (!bufferRow.isEmpty()){
                table.getTable().add(bufferRow);
            }

        }
        return table;
    }

    private static Cell convertCell(HSSFCell initCell){
        String value = null;
        CellType type = null;
        if (initCell == null){
            type = CellType.BLANK;
        } else {
            switch (initCell.getCellType()){
                case _NONE:
                case BLANK:
                case ERROR:
                    type = CellType.BLANK;
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(initCell)){
                        type = CellType.DATE;
                        value = initCell.getDateCellValue().toString();
                    } else {
                        type = CellType.NUMERIC;
                        double buffer = initCell.getNumericCellValue();
                        value = Double.toString(buffer);
                    }
                    break;
                case STRING:
                    type = CellType.STRING;
                    value = initCell.getStringCellValue();
                    break;
                case FORMULA:
                    type = CellType.FORMULA;
                    value = initCell.getCellFormula();
                    break;
                case BOOLEAN:
                    type = CellType.BOOLEAN;
                    if (initCell.getBooleanCellValue()){
                        value = "true";
                    } else {
                        value = "false";
                    }
                    break;
            }
        }
        return new Cell(value, type);
    }
}
