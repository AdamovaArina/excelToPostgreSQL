package beans;

import java.util.ArrayList;

public class Table {
    private final ArrayList<Row> table;

    public Table(){
        table = new ArrayList<>();
    }

    public ArrayList<Row> getTable(){
        return table;
    }

    private TableCellType columnTypeCheck(int idx){
        TableCellType baseType = TableCellType.BLANK;
        for (int j = 0; j < getTable().size(); j++){
            if(getTable().get(j).getRow().get(idx).getType() != TableCellType.BLANK){
                baseType = getTable().get(j).getRow().get(idx).getType();
                break;
            }
        }
        if (getTable().size() > 1){
            for (int i = 1; i < getTable().size(); i++){
                if (baseType != getTable().get(i).getRow().get(idx).getType() &&
                        getTable().get(i).getRow().get(idx).getType() != TableCellType.BLANK &&
                        getTable().get(i).getRow().get(idx).getType() != null){
                    baseType = TableCellType.STRING;
                    break;
                }
            }
        }
        return baseType;
    }

    private void columnTypeFix(int idx) throws Exception{
        try{
            TableCellType baseType = this.columnTypeCheck(idx);
            if(baseType != TableCellType.NUMERIC && baseType != TableCellType.DATE){
                baseType = TableCellType.STRING;
            }
            for(Row row : table){
                row.getRow().get(idx).setType(baseType);
            }
        } catch (Exception ex){
            throw new Exception("Столбец №" + idx + " содержит неприводимые типы данных", ex);
        }
    }

    public Table typeFix() throws Exception{
        for(int i = 0 ; i < table.get(0).getRow().size(); i++){
            this.columnTypeFix(i);
        }
        return this;
    }

    private void addColumn(Table other, int idx){
        TableCell el;
        while (table.size() < other.getTable().size()){
            table.add(new Row());
        }
        for(int i = 0; i < other.getTable().size(); i++){
            el = other.getTable().get(i).getRow().get(idx);
            table.get(i).getRow().add(el);
        }
    }

    private Table sortTable(){
        Table result = new Table();
        ArrayList<TableCellType> types = new ArrayList<>();
        types.add(TableCellType.NUMERIC);
        types.add(TableCellType.STRING);
        types.add(TableCellType.DATE);
        for(TableCellType type : types){
            for(int j = 0; j < table.get(0).getRow().size(); j++){
                if(table.get(0).getRow().get(j).getType().equals(type)){
                    result.addColumn(this, j);
                }
            }
        }
        return result;
    }

    private void addNullCol(TableCellType type, int idx){
        for(Row row : table){
            row.getRow().add(idx, new TableCell(-1, null, type));
        }
    }

    public boolean isNullColumn(int idx){
        for(Row row : table){
            if(row.getRow().get(idx).getValue() != null && !row.getRow().get(idx).getValue().equals("null") &&
                    !row.getRow().get(idx).getValue().equals("")){
                return false;
            }
        }
        return true;
    }

    public TableCell firstNotNullInCol(int colNum){
        for(Row row : table){
            if(row.getRow().get(colNum).getValue() != null && !row.getRow().get(colNum).getValue().equals("null") &&
                    !row.getRow().get(colNum).getValue().equals("")){
                return row.getRow().get(colNum);
            }
        }
        return null;
    }

    private Table colCount() throws Exception{
        int cntr = 0;
        ArrayList<TableCellType> types = new ArrayList<>();
        types.add(TableCellType.NUMERIC);
        types.add(TableCellType.STRING);
        types.add(TableCellType.DATE);
        for(TableCellType type : types){
            for(int i = 0; i < table.get(0).getRow().size(); i++){
                if(table.get(0).getRow().get(i).getType() == type){
                    cntr++;
                }
            }
            if(cntr > 10){
                throw new Exception("Выбрано слишком много столбцов типа " + type);
            } else if (cntr < 10){
                if(type == TableCellType.NUMERIC){
                    for(int j = 0; j < 10 - cntr; j++){
                        this.addNullCol(type, cntr + j);
                    }
                } else if (type == TableCellType.STRING){
                    for(int j = 0; j < 10 - cntr; j++){
                        this.addNullCol(type, 10 + cntr + j);
                    }
                } else if (type == TableCellType.DATE){
                    for(int j = 0; j < 10 - cntr; j++){
                        this.addNullCol(type, 20 + cntr + j);
                    }
                }
            }
            cntr = 0;
        }
        return this;
    }

    public Integer findColNum(int colNum){
        for(int i = 0; i < table.get(0).getRow().size(); i++){
            if(table.get(0).getRow().get(i).getColNum() == colNum){
                return i;
            }
        }
        return null;
    }

    public Table toTempTable() throws Exception{
        return this.typeFix().sortTable().colCount();
    }

    /*public void printTable(){
        System.out.println(name);
        System.out.println(columnNames.toString());
        for (Row row : table) {
            System.out.println(row.toString());
        }
    }*/
}
