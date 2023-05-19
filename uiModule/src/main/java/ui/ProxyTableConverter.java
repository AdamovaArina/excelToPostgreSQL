package ui;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public class ProxyTableConverter {
    public static ProxyTable createProxyTable(Sheet sheet, int rowLast, int columnLast){
        ProxyTable pt = new ProxyTable();

        for (int i = 0; i <= rowLast; ++i) {
            Row row = sheet.getRow(i);
            for (int j = 0; j <= columnLast; ++j) {
                //когда ячейка или строка не определена
                if (row == null || row.getCell(j) == null) {
                    int height;
                    if (row == null) {
                        height = sheet.getDefaultRowHeight();
                    } else {
                        height = row.getHeight();
                    }
                    ProxyCell proxyCell = new ProxyCell(j, i, sheet.getColumnWidth(j), height, 1, 1,"");
                    pt.addCell(proxyCell);
                }
                //когда определена
                else {
                    Cell c = row.getCell(j);
                    CellRangeAddress r = null;
                    //проверяем, не принадлежит ли ячейка объединению ячеек
                    for (int k = 0; k < sheet.getMergedRegions().size(); ++k) {
                        if (sheet.getMergedRegions().get(k).isInRange(c)) {
                            r = sheet.getMergedRegions().get(k);
                        }
                    }
                    //если не принадлежит - добавляем
                    if (r == null) {
                        ProxyCell proxyCell = new ProxyCell(j, i, sheet.getColumnWidth(j), row.getHeight(), 1, 1, c.toString());
                        pt.addCell(proxyCell);
                    } else {
                        if (r.getFirstRow() == i && r.getFirstColumn() == j) {
                            int width = 0;
                            int height = 0;
                            for (int k = r.getFirstColumn(); k <= r.getLastColumn(); ++k) {
                                width += sheet.getColumnWidth(k);
                            }
                            for (int k = r.getFirstRow(); k <= r.getLastRow(); ++k) {
                                Row tmp = sheet.getRow(k);
                                if (tmp == null) {
                                    height += sheet.getDefaultRowHeight();
                                } else {
                                    height += tmp.getHeight();
                                }
                            }
                            ProxyCell proxyCell = new ProxyCell(j, i, width, height, r.getLastColumn() - j + 1, r.getLastRow() - i + 1, c.toString());
                            pt.addCell(proxyCell);
                        }
                    }

                }
            }
        }
        return pt;
    }

    /*public static TableCellType convertCellType(Cell initCell){
        if (initCell == null){
            return TableCellType.BLANK;
        } else {
            switch (initCell.getCellType()){
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(initCell)){
                        return TableCellType.DATE;
                    } else {
                        return TableCellType.NUMERIC;
                    }
                case STRING:
                    return TableCellType.STRING;
                case FORMULA:
                    return TableCellType.FORMULA;
                case BOOLEAN:
                    return TableCellType.BOOLEAN;
                default:
                    return TableCellType.BLANK;
            }
        }
    }*/
}
