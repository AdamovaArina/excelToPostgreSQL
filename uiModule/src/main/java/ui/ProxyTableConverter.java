package ui;

import beans.CellType;
import javafx.scene.layout.GridPane;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class ProxyTableConverter {
    /*public static ProxyTable createProxyTable(HSSFSheet sheet, int rowLast, int columnLast){
        ProxyTable pt = new ProxyTable();

        for (int i = 0; i <= rowLast; ++i) {
            var row = sheet.getRow(i);
            for (int j = 0; j <= columnLast; ++j) {
                //когда ячейка или строка не определена
                if (row == null || row.getCell(j) == null) {
                    int height;
                    if (row == null) {
                        height = sheet.getDefaultRowHeight();
                    } else {
                        height = row.getHeight();
                    }
                    var proxyCell = new ProxyCell(j, i, sheet.getColumnWidth(j), height, 1, 1,"", CellType.BLANK);
                    pt.addCell(proxyCell);
                }
                //когда определена
                else {
                    var c = row.getCell(j);
                    CellRangeAddress r = null;
                    //проверяем, не принадлежит ли ячейка объединению ячеек
                    for (int k = 0; k < sheet.getMergedRegions().size(); ++k) {
                        if (sheet.getMergedRegions().get(k).isInRange(c)) {
                            r = sheet.getMergedRegions().get(k);
                        }
                    }
                    //если не принадлежит - добавляем
                    if (r == null) {
                        var proxyCell = new ProxyCell(j, i, sheet.getColumnWidth(j), row.getHeight(), 1, 1, c.toString(), convertCellType(c));
                        pt.addCell(proxyCell);
                    } else {
                        if (r.getFirstRow() == i && r.getFirstColumn() == j) {
                            int width = 0;
                            int height = 0;
                            for (int k = r.getFirstColumn(); k <= r.getLastColumn(); ++k) {
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
                            var proxyCell = new ProxyCell(j, i, width, height, r.getLastColumn() - j + 1, r.getLastRow() - i + 1, c.toString(), convertCellType(c));
                            pt.addCell(proxyCell);
                        }
                    }

                }
            }
        }
        return pt;
    }*/

    public static ProxyTable createProxyTable(Sheet sheet, int rowLast, int columnLast){
        ProxyTable pt = new ProxyTable();

        for (int i = 0; i <= rowLast; ++i) {
            var row = sheet.getRow(i);
            for (int j = 0; j <= columnLast; ++j) {
                //когда ячейка или строка не определена
                if (row == null || row.getCell(j) == null) {
                    int height;
                    if (row == null) {
                        height = sheet.getDefaultRowHeight();
                    } else {
                        height = row.getHeight();
                    }
                    var proxyCell = new ProxyCell(j, i, sheet.getColumnWidth(j), height, 1, 1,"", CellType.BLANK);
                    pt.addCell(proxyCell);
                }
                //когда определена
                else {
                    var c = row.getCell(j);
                    CellRangeAddress r = null;
                    //проверяем, не принадлежит ли ячейка объединению ячеек
                    for (int k = 0; k < sheet.getMergedRegions().size(); ++k) {
                        if (sheet.getMergedRegions().get(k).isInRange(c)) {
                            r = sheet.getMergedRegions().get(k);
                        }
                    }
                    //если не принадлежит - добавляем
                    if (r == null) {
                        var proxyCell = new ProxyCell(j, i, sheet.getColumnWidth(j), row.getHeight(), 1, 1, c.toString(), convertCellType(c));
                        pt.addCell(proxyCell);
                    } else {
                        if (r.getFirstRow() == i && r.getFirstColumn() == j) {
                            int width = 0;
                            int height = 0;
                            for (int k = r.getFirstColumn(); k <= r.getLastColumn(); ++k) {
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
                            var proxyCell = new ProxyCell(j, i, width, height, r.getLastColumn() - j + 1, r.getLastRow() - i + 1, c.toString(), convertCellType(c));
                            pt.addCell(proxyCell);
                        }
                    }

                }
            }
        }
        return pt;
    }

    /*public static CellType convertCellType(HSSFCell initCell){
        if (initCell == null){
            return CellType.BLANK;
        } else {
            switch (initCell.getCellType()){
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(initCell)){
                        return CellType.DATE;
                    } else {
                        return CellType.NUMERIC;
                    }
                case STRING:
                    return CellType.STRING;
                case FORMULA:
                    return CellType.FORMULA;
                case BOOLEAN:
                    return CellType.BOOLEAN;
                default:
                    return CellType.BLANK;
            }
        }
    }*/

    public static CellType convertCellType(Cell initCell){
        if (initCell == null){
            return CellType.BLANK;
        } else {
            switch (initCell.getCellType()){
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(initCell)){
                        return CellType.DATE;
                    } else {
                        return CellType.NUMERIC;
                    }
                case STRING:
                    return CellType.STRING;
                case FORMULA:
                    return CellType.FORMULA;
                case BOOLEAN:
                    return CellType.BOOLEAN;
                default:
                    return CellType.BLANK;
            }
        }
    }
}
