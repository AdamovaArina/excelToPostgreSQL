package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectorToPostgreSQL {
    public static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        try {
            dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/excelToPostgreSQL",
                    "postgres", "postgress");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return dbConnection;
    }
}
