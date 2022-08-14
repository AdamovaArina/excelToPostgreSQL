package ui;

import java.util.ArrayList;

public class ProxyTable {
    private final ArrayList<ProxyCell> cells = new ArrayList<>();

    public ArrayList<ProxyCell> getCells(){
        return cells;
    }

    public void addCell(ProxyCell newCell){
        cells.add(newCell);
    }
}
