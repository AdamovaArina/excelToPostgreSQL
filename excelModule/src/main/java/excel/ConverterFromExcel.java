package excel;

import beans.TableCell;
import beans.TableCellType;
import beans.Row;
import beans.Table;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

public class ConverterFromExcel {
    public static Table readTableFromExcel(Sheet sheet, TreeSet<Integer> rows, TreeSet<Integer> columns){
        Table result = new Table();
        for(Integer row : rows){
            Row bufferRow = new Row();
            for(Integer column : columns){
                bufferRow.getRow().add(convertCell(sheet.getRow(row).getCell(column), column));
            }
            if(!bufferRow.isEmpty()){
                result.getTable().add(bufferRow);
            }
        }
        return result;
    }

    private static TableCell convertCell(Cell initCell, int colNum){
        String value = null;
        TableCellType type = null;
        if (initCell == null){
            type = TableCellType.BLANK;
        } else {
            switch (initCell.getCellType()) {
                case _NONE, BLANK, ERROR -> type = TableCellType.BLANK;
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(initCell)) {
                        type = TableCellType.DATE;
                        value = initCell.getDateCellValue().toString();
                    } else {
                        type = TableCellType.NUMERIC;
                        DataFormatter df = new DataFormatter();
                        value = df.formatCellValue(initCell);
                    }
                }
                case STRING -> {
                    type = TableCellType.STRING;
                    value = initCell.getStringCellValue();
                    if(new TableCell(colNum, value, type).isDate()){
                        type = TableCellType.DATE;
                        try{
                            value = stringToDate(value).toString();
                        } catch (IOException ex){
                            ex.printStackTrace();
                        }
                    } else if(new TableCell(colNum, value, type).isNumeric()){
                        type = TableCellType.NUMERIC;
                    }
                }
                case FORMULA -> {
                    type = TableCellType.FORMULA;
                    value = initCell.getCellFormula();
                }
                case BOOLEAN -> {
                    type = TableCellType.BOOLEAN;
                    if (initCell.getBooleanCellValue()) {
                        value = "true";
                    } else {
                        value = "false";
                    }
                }
            }
        }
        return new TableCell(colNum, value, type);
    }

    private static Date stringToDate(String string) throws IOException{
        String[] parts = new String[0];
        String regex = "";
        ArrayList<Integer> partsNum = new ArrayList<>();
        if(string.contains(".")){
            parts = string.split("\\.");
            regex = ".";
        } else if(string.contains("-")){
            parts = string.split("-");
            regex = "-";
        } else if(string.contains(" ")){
            parts = string.split(" ");
            regex = " ";
        } else if(string.contains("/")){
            parts = string.split("/");
            regex = "/";
        }
        if(parts.length == 0){
            throw new IOException("Не удается преобразовать строковое значение к типу DATE");
        }
        for(String part : parts){
            try{
                partsNum.add(Integer.parseInt(part));
            } catch(NumberFormatException ex){
                throw new IOException("Не удается преобразовать строковое значение к типу DATE", ex);
            }
        }
        String format;
        if(partsNum.get(0) < 32 && partsNum.get(1) < 13 && partsNum.get(2) < 2100){
            format = "dd" + regex + "MM" + regex;
            if(partsNum.get(2) < 100){
                format += "yy";
            } else{
                format += "yyyy";
            }
        } else if(partsNum.get(0) < 2100 && partsNum.get(1) < 13 && partsNum.get(2) < 32){
            if(partsNum.get(0) < 100){
                format = "yy";
            } else{
                format = "yyyy";
            }
            format += regex + "MM" + regex + "dd";
        } else{
            throw new IOException("Не удается преобразовать строковое значение к типу DATE");
        }
        try{
            return new SimpleDateFormat(format).parse(string);
        } catch (ParseException ex){
            throw new IOException("Не удается преобразовать строковое значение к типу DATE", ex);
        }
    }
}
