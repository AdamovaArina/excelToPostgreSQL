package beans;

import java.util.ArrayList;

public class Table {
    private ArrayList<Row> table;

    public Table(){
        table = new ArrayList<>();
    }

    public ArrayList<Row> getTable(){
        return table;
    }

    private boolean isRectangle(){
        if (getTable() == null){
            return false;
        }
        int numberOfCells = getTable().get(0).getRow().size();
        for (int i = 1; i < this.getTable().size(); i++){
            if (numberOfCells != getTable().get(i).getRow().size()){
                return false;
            }
        }
        return true;
    }

    private boolean columnTypeCheck(int idx){
        CellType baseType = getTable().get(1).getRow().get(idx).getType();
        if (getTable().size() > 1){
            for (int i = 2; i < getTable().size(); i++){
                if (baseType != getTable().get(i).getRow().get(idx).getType() &&
                        getTable().get(i).getRow().get(idx).getType() != CellType.BLANK &&
                        getTable().get(i).getRow().get(idx).getType() != null){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean typeCheck(){
        if (!isRectangle()){
            return false;
        }
        for (int i = 0; i < getTable().get(0).getRow().size(); i++){
            if (!columnTypeCheck(i)){
                return false;
            }
        }
        return true;
    }

    public void printTable(){
        for (Row row : table) {
            System.out.println(row.toString());
        }
    }
}
