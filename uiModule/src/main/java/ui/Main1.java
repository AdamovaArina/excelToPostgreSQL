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

public class Main1 {
    public static void main(String[] args) {
        Table buffer;
        try {
            buffer = ConverterFromExcel.readTableFromExcel(openFile(), 0, 0,
                    0, 11, 7, 11, "table111");
            buffer.printTable();
            ConverterToPostgreSQL.createAndFillTable(ConnectorToPostgreSQL.getDBConnection(), buffer);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
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
