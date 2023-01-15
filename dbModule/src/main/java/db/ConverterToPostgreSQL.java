package db;

import beans.Table;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConverterToPostgreSQL {
    public static void executeCommand(Connection dbConnection, String command) throws SQLException{
        try (Statement statement = dbConnection.createStatement()){
            statement.execute(command);
            statement.execute("commit");
        } catch(Exception ex){
            ex.printStackTrace();
            throw new SQLException("Не удалось подключиться к базе данных", ex);
        }
    }
    public static String insertIntoTemporaryTable(Table data){
        StringBuilder sb = new StringBuilder("insert into public.temporary_table \n" +
                "values\n" +
                "(");
        for(int i = 0; i < data.getTable().size(); i++){
            for(int j = 0; j < data.getTable().get(i).getRow().size(); j++){
                sb.append(data.getTable().get(i).getRow().get(j).toString());
                if(j + 1 == data.getTable().get(i).getRow().size()){
                    sb.append(")");
                } else{
                    sb.append(", ");
                }
            }
            if(i + 1 != data.getTable().size()){
                sb.append(", \n (");
            } else{
                sb.append(";");
            }
        }
        return sb.toString();
    }
}
