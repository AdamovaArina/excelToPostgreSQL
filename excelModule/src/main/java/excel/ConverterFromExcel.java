package excel;

import beans.TableCell;
import beans.CellType;
import beans.Row;
import beans.Table;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ConverterFromExcel {
    public static Table readTableFromExcel(Sheet sheet, int nameRow, int fromCol, int toCol,
                                           int fromRow, int toRow, String name) throws IOException {
        Table result;

        /*String ext = getFileExtension(file);
        Workbook myExcelBook = switch (ext) {
            case "xlsx" -> new XSSFWorkbook(new FileInputStream(file));
            case "xls" -> new HSSFWorkbook(new FileInputStream(file));
            default -> null;
        };

        if (myExcelBook == null){
            throw new IOException("Некорректное расширение файла");
        }

        if (myExcelBook.getNumberOfSheets() - 1 < sheetNumber) {
            throw new IOException("Такой лист не существует");
        }*/
        if (fromCol > toCol){
            throw new IOException("Некорректные номера столбцов");
        }
        if (fromRow > toRow){
            throw new IOException("Некорректные номера строк");
        }
        result = readDataFromExcel(sheet, fromRow, toRow, fromCol, toCol);
        result.setColumnNames(readColumnNames(sheet, nameRow, fromCol, toCol));
        result.setName(name);
        return result;
    }

    private static Table readDataFromExcel(Sheet myExcelSheet, int fromRow, int toRow, int fromCol, int toCol){
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

    private static TableCell convertCell(Cell initCell){
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
        return new TableCell(value, type);
    }

    private static Row readColumnNames(Sheet myExcelSheet, int line, int fromCol, int toCol){
        Row row = new Row();
        for (int i = fromCol; i <= toCol; i++){
            row.getRow().add(convertCell(myExcelSheet.getRow(line).getCell(i)));
        }
        return row;
    }

    private static String getFileExtension(File file) {
        if (file == null) {
            return "";
        }
        String name = file.getName();
        int i = name.lastIndexOf('.');
        return i > 0 ? name.substring(i + 1) : "";
    }

}
