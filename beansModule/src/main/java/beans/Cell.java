package beans;

public class Cell {
    private String value;
    private CellType type;

    public Cell(String value, CellType type){
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
