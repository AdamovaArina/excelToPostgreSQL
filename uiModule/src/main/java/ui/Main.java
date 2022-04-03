package ui;

import beans.Table;
import db.ConnectorToPostgreSQL;
import db.ConverterToPostgreSQL;
import excel.ConverterFromExcel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ArrayList<Table> buffer = null;
        try {
            buffer = ConverterFromExcel.readFromExcel(openFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(buffer != null){
            try {
                for (Table table : buffer) {
                    ConverterToPostgreSQL.createAndFillTable(ConnectorToPostgreSQL.getDBConnection(), table);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Все не ок");
        }

    }
    private static FileInputStream openFile(){
        Scanner sc = new Scanner(System.in);
        FileInputStream fis;
        while(true){
            String fileName = sc.nextLine();
            try{
                fis = new FileInputStream(fileName);
                return fis;
            } catch(FileNotFoundException ex){
                System.out.println("Файл не найден");
                ex.printStackTrace();
            }
        }
    }
}
