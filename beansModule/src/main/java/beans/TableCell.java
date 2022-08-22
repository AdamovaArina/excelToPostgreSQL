package beans;

public class TableCell {
    private final String value;
    private final CellType type;

    public TableCell(String value, CellType type){
        this.value = value;
        this.type = type;
    }

    public String getValue(){
        return value;
    }

    public CellType getType(){
        return type;
    }
}
