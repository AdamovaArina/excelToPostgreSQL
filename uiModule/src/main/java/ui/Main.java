package ui;

import beans.Table;
import excel.Converter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            ArrayList<Table> buffer = Converter.readFromExcel(openFile());
            for (Table table : buffer) {
                table.printTable();
            }
        } catch (IOException e) {
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
            } catch(FileNotFoundException fnfe){
                System.out.println("Файл не найден");
                fnfe.printStackTrace();
            }
        }
    }
}
