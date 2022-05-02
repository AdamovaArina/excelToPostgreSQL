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

public class ConverterFromExcel {
    public static Table readTableFromExcel(FileInputStream file, int sheetNumber, int nameRow, int fromCol, int toCol,
                                           int fromRow, int toRow, String name) throws IOException {
        Table result;
        HSSFWorkbook myExcelBook = new HSSFWorkbook(file);
        if (myExcelBook.getNumberOfSheets() - 1 < sheetNumber) {
            throw new IOException("Такой лист не существует");
        }
        if (fromCol > toCol){
            throw new IOException("Некорректные номера столбцов");
        }
        if (fromRow > toRow){
            throw new IOException("Некорректные номера строк");
        }
        result = readDataFromExcel(myExcelBook.getSheetAt(sheetNumber), fromRow, toRow, fromCol, toCol);
        result.setColumnNames(readColumnNames(myExcelBook.getSheetAt(sheetNumber), nameRow, fromCol, toCol));
        result.setName(name);
        return result;
    }


    private static Row readColumnNames(HSSFSheet myExcelSheet, int line, int fromCol, int toCol){
        Row row = new Row();
        for (int i = fromCol; i <= toCol; i++){
            row.getRow().add(convertCell(myExcelSheet.getRow(line).getCell(i)));
        }
        return row;
    }

    private static Table readDataFromExcel(HSSFSheet myExcelSheet, int fromRow, int toRow, int fromCol, int toCol){
        Table table = new Table();
        for(int i = fromRow; i <= toRow; i++){
            Row bufferRow = new Row();
            for(int j = fromCol; j <= toCol; j++){
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
