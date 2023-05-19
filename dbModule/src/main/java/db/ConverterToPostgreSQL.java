package db;

import beans.Table;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ConverterToPostgreSQL {
    private static final String[] colNamesInDatabase = {"NUM1", "NUM2", "NUM3", "NUM4", "NUM5", "NUM6",
            "NUM7", "NUM8", "NUM9", "NUM10", "VAR1", "VAR2", "VAR3", "VAR4", "VAR5", "VAR6", "VAR7",
            "VAR8", "VAR9", "VAR10", "DATE1", "DATE2", "DATE3", "DATE4", "DATE5", "DATE6", "DATE7",
            "DATE8", "DATE9", "DATE10"};

    public static void executeCommand(Connection dbConnection, String command) throws SQLException{
        try (Statement statement = dbConnection.createStatement()){
            System.out.println(command);
            statement.execute(command);
            statement.execute("commit");
        } catch(Exception ex){
            ex.printStackTrace();
            throw new SQLException("Не удалось подключиться к базе данных", ex);
        }
    }
    public static String insertIntoTemporaryTable(Table data){
        StringBuilder sb = new StringBuilder("""
                insert into public.temporary_table\s
                values
                (default,\s""");
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
                sb.append(", \n (default, ");
            } else{
                sb.append(";");
            }
        }
        return sb.toString();
    }

    public static ArrayList<String> findColNames(Table table, int maxColNum){
        ArrayList<String> columnNames = new ArrayList<>();
        for(int i = 0; i <= maxColNum; i++){
            if(table.findColNum(i) != null){
                columnNames.add(colNamesInDatabase[table.findColNum(i)]);
            }else{
                columnNames.add("");
            }
        }
        return columnNames;
    }
}
