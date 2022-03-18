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

public class ConverterFromExcel {
    public static ArrayList<Table> readFromExcel(FileInputStream file) throws IOException {
        HSSFWorkbook myExcelBook = new HSSFWorkbook(file);
        ArrayList<Table> tables = new ArrayList<>();
        for (int i = 0; i < myExcelBook.getNumberOfSheets(); i++){
            HSSFSheet myExcelSheet = myExcelBook.getSheetAt(i);
            Table table = new Table();
            HSSFCell bufferCell;
            int realNumberOfRows = myExcelSheet.getPhysicalNumberOfRows();
            for(int j = 0; j < realNumberOfRows; j++){
                Row bufferRow = new Row();
                if (myExcelSheet.getRow(j) == null){
                    realNumberOfRows++;
                } else {
                    int realNumberOfCells = myExcelSheet.getRow(j).getPhysicalNumberOfCells();
                    for(int k = 0; k < realNumberOfCells; k++){
                        bufferCell = myExcelSheet.getRow(j).getCell(k);
                        if (bufferCell == null){
                            realNumberOfCells++;
                        }
                        bufferRow.getRow().add(convertCell(bufferCell));
                    }
                    if (!bufferRow.isEmpty()){
                        table.getTable().add(bufferRow);
                    }
                }
            }
            tables.add(table);
        }
        myExcelBook.close();
        return tables;
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
