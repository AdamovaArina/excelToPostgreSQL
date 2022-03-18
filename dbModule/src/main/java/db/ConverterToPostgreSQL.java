package db;

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

        String createTableSQL = "CREATE TABLE DBUSER("
                + "USER_ID REAL NOT NULL, "
                + "USERNAME VARCHAR(20) NOT NULL, "
                + "CREATED_BY VARCHAR(20) NOT NULL, "
                + "CREATED_DATE DATE NOT NULL, " + "PRIMARY KEY (USER_ID) "
                + ")";

        try {
            statement = dbConnection.createStatement();
            statement.execute(createTableSQL);
            System.out.println("Table \"dbuser\" is created!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
}
