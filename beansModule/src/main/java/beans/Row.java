package beans;

import java.util.ArrayList;

public class Row {
    private final ArrayList<TableCell> row;

    public Row(){
        row = new ArrayList<>();
    }

    public ArrayList<TableCell> getRow(){
        return row;
    }

    public boolean isEmpty(){
        for (int i = 0; i < getRow().size(); i++){
            if (getRow().get(i).getValue() != null){
                return false;
            }
        }
        return true;
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (TableCell cell : row) {
            stringBuilder.append(cell.getValue()).append(" ");
        }
        return stringBuilder.toString();
    }
}
