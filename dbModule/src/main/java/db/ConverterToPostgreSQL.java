package db;

import beans.CellType;
import beans.Table;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConverterToPostgreSQL {
    public static void createAndFillTable(Connection dbConnection, Table table) throws SQLException {
        if (dbConnection == null){
            throw new SQLException("Соединение с базой данных не установлено");
        }
        Statement statement = null;

        try {
            statement = dbConnection.createStatement();
            statement.execute(createTable(table));
            System.out.println("Table is created!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static String createTable(Table table){
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

    private static String typeToString(CellType type){
        return switch (type) {
            case BOOLEAN -> "bool";
            case DATE -> "date";
            case NUMERIC -> "float8";
            default -> "text";
        };

    }
}
