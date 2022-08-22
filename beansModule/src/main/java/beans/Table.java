package beans;

import java.util.ArrayList;

public class Table {
    private String name;
    private Row columnNames;
    private final ArrayList<Row> table;

    public Table(){
        name = null;
        columnNames = new Row();
        table = new ArrayList<>();
    }

    public ArrayList<Row> getTable(){
        return table;
    }

    public String getName(){
        return name;
    }

    public Row getColumnNames(){
        return columnNames;
    }

    public void setColumnNames(Row columnNames){
        this.columnNames = columnNames;
    }

    public void setName(String name){
        this.name = name;
    }

    private boolean columnTypeCheck(int idx){
        CellType baseType = CellType.BLANK;
        for (int j = 1; j <getTable().size(); j++){
            if(getTable().get(j).getRow().get(idx).getType() != CellType.BLANK){
                baseType = getTable().get(j).getRow().get(idx).getType();
                break;
            }
        }
        if (getTable().size() > 1){
            for (int i = 1; i < getTable().size(); i++){
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
        for (int i = 0; i < getTable().get(0).getRow().size(); i++){
            if (!columnTypeCheck(i)){
                return false;
            }
        }
        return true;
    }

    /*public void printTable(){
        System.out.println(name);
        System.out.println(columnNames.toString());
        for (Row row : table) {
            System.out.println(row.toString());
        }
    }*/
}
