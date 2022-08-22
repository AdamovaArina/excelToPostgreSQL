package db;

import beans.TableCell;
import beans.CellType;
import beans.Table;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConverterToPostgreSQL {
    public static void createAndFillTable(Connection dbConnection, Table table) throws  IOException {
        try (Statement statement = dbConnection.createStatement()) {
            if (table.typeCheck()){
                statement.execute(createTable(table));
                statement.execute(fillTable(table));
            } else {
                throw new IOException("Некорректные типы данных в таблице");
            }
        } catch (NullPointerException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static String createTable(Table table){
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append("\"").append(table.getName()).append("\"").append(" ( id serial PRIMARY KEY, ");
        CellType bufferType = null;
        for (int i = 0; i < table.getColumnNames().getRow().size(); i++){
            for (int j = 0; j < table.getTable().size(); j++){
                if (table.getTable().get(j).getRow().get(i).getType() != CellType.BLANK){
                    bufferType = table.getTable().get(j).getRow().get(i).getType();
                    break;
                }
            }
            if (bufferType == null){
                bufferType = CellType.STRING;
            }
            sb.append("\"").append(table.getColumnNames().getRow().get(i).getValue()).append("\"").append(" ")
                    .append(typeToString(bufferType));
            if (i != table.getColumnNames().getRow().size() - 1){
                sb.append(", ");
            }
        }
        sb.append(" ); ");
        return sb.toString();
    }

    private static String fillTable(Table table){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < table.getTable().size(); i++){
            sb.append("insert into \"").append(table.getName()).append("\" values ( default, ");
            for (int j = 0; j < table.getTable().get(i).getRow().size(); j++){
                sb.append(cellToString(table.getTable().get(i).getRow().get(j)));
                if (j != table.getTable().get(i).getRow().size() - 1){
                    sb.append(", ");
                }
            }
            sb.append(" ); ");
        }
        return sb.toString();
    }

    private static String cellToString(TableCell cell){
        StringBuilder sb = new StringBuilder();
        return switch (cell.getType()) {
            case STRING, DATE -> sb.append("'").append(cell.getValue()).append("'").toString();
            default -> sb.append(cell.getValue()).toString();
        };
    }

    private static String typeToString(CellType type){
        return switch (type) {
            case BOOLEAN -> "bool";
            case DATE -> "date";
            case NUMERIC -> "float8";
            default -> "text";
        };

    }
}
